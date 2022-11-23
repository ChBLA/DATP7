package org.UcelParser.TypeChecker;

import org.UcelParser.CodeGeneration.templates.ManualTemplate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.UcelParser.UCELParser_Generated.*;

import org.UcelParser.Util.Type;
import org.UcelParser.Util.*;
import org.UcelParser.Util.Logging.*;

public class TypeCheckerVisitor extends UCELBaseVisitor<Type> {
    private Scope currentScope;
    private ILogger logger;

    public TypeCheckerVisitor() {
            this.currentScope = null;
            this.logger = new Logger();
        }

    public TypeCheckerVisitor(Scope scope) {
            this.currentScope = scope;
            this.logger = new Logger();
        }

    public TypeCheckerVisitor(ILogger logger) {
            this.currentScope = null;
            this.logger = logger;
        }

    //region Type constants
    private static final Type INT_TYPE = new Type(Type.TypeEnum.intType);
    private static final Type DOUBLE_TYPE = new Type(Type.TypeEnum.doubleType);
    private static final Type BOOL_TYPE = new Type(Type.TypeEnum.boolType);
    private static final Type CHAR_TYPE = new Type(Type.TypeEnum.charType);
    private static final Type STRING_TYPE = new Type(Type.TypeEnum.stringType);
    private static final Type CLOCK_TYPE = new Type(Type.TypeEnum.clockType);
    private static final Type ERROR_TYPE = new Type(Type.TypeEnum.errorType);
    private static final Type INT_ARRAY_TYPE = new Type(Type.TypeEnum.intType, 1);
    private static final Type DOUBLE_ARRAY_TYPE = new Type(Type.TypeEnum.doubleType, 1);
    private static final Type BOOL_ARRAY_TYPE = new Type(Type.TypeEnum.boolType, 1);
    private static final Type CHAR_ARRAY_TYPE = new Type(Type.TypeEnum.charType, 1);
    private static final Type INVALID_TYPE = new Type(Type.TypeEnum.invalidType);
    private static final Type VOID_TYPE = new Type(Type.TypeEnum.voidType);
    private static final Type CHAN_TYPE = new Type(Type.TypeEnum.chanType);
    private static final Type STRUCT_TYPE = new Type(Type.TypeEnum.structType);
    private static final Type PROCESS_TYPE = new Type(Type.TypeEnum.processType);
    private static final Type TEMPLATE_TYPE = new Type(Type.TypeEnum.templateType);
    private static final Type SCALAR_TYPE = new Type(Type.TypeEnum.scalarType);
    private static final Type ARRAY_TYPE = new Type(Type.TypeEnum.voidType, 1);
    public DeclarationInfo currentFunction = null;
    //endregion

    // region compCon

    @Override
    public Type visitCompCon(UCELParser.CompConContext ctx) {
        Type result = VOID_TYPE;
        Type argumentsTypes = visit(ctx.arguments());

        DeclarationInfo constructorInfo;
        DeclarationInfo variableCompInfo;

        try {
            constructorInfo = currentScope.get(ctx.constructorReference);
            variableCompInfo = currentScope.get(ctx.compVar().variableReference);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx,
                    "internal error: constructor for component could not be found in scope"));
            return ERROR_TYPE;
        }

        if (!constructorInfo.equals(variableCompInfo)) {
            String conID = ((UCELParser.ComponentContext) constructorInfo.getNode()).ID().getText();
            String varConID = ((UCELParser.ComponentContext) variableCompInfo.getNode()).ID().getText();
            logger.log(new ErrorLog(ctx, "Trying to assign " + conID + " to component of type " + varConID));
            return ERROR_TYPE;
        }

        Type constructorType = constructorInfo.getType();
        if (!(constructorType.getEvaluationType().equals(Type.TypeEnum.componentType))) {
            logger.log(new ErrorLog(ctx,
                    "internal error: constructor for component is not of type component"));
            result = ERROR_TYPE;
        }

        for (int i = 0; i < argumentsTypes.getParameters().length; i++) {
            if (!constructorType.getParameters()[i].equals(argumentsTypes.getParameters()[i])) {
                logger.log(new ErrorLog(ctx,
                        "internal error: constructor for component does not match arguments. expected: "
                                + constructorType.getParameters()[i] + " but got: "
                                + argumentsTypes.getParameters()[i]));
                result = ERROR_TYPE;
            }
        }

        return result;
    }


    // endregion

    // region compVar

    @Override
    public Type visitCompVar(UCELParser.CompVarContext ctx) {
        boolean success = true;
        DeclarationInfo variableRefDecl = null;

        if (ctx.expression() != null) {
            for (var expr : ctx.expression()) {
                Type exprType = visit(expr);
                if (exprType.getEvaluationType() != Type.TypeEnum.intType) {
                    logger.log(new ErrorLog(expr, "type error: expected type int, got type " + exprType));
                    success = false;
                }
            }
        }

        try {
            variableRefDecl = currentScope.get(ctx.variableReference);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "internal error: variable reference could not be found in scope"));
            return ERROR_TYPE;
        }

        if (variableRefDecl.getType().getArrayDimensions() < ctx.expression().size()){
            logger.log(new ErrorLog(ctx, "type error: expected "
                        + variableRefDecl.getType().getArrayDimensions()
                        + " array dimensions, got " + ctx.expression().size()));
            success = false;
        }

        if (variableRefDecl.getType().getEvaluationType() != Type.TypeEnum.componentType
                || variableRefDecl.getType().getEvaluationType() != Type.TypeEnum.templateType){
            logger.log(new ErrorLog(ctx, "type error: expected type component or template, got type "
                    + variableRefDecl.getType()));
            success = false;
        }

        Type result = new Type(Type.TypeEnum.componentType, variableRefDecl.getType().getArrayDimensions() - ctx.expression().size());
        return success ? result : ERROR_TYPE;
    }

    // endregion


    // region linkStatement
    private Type extractInterfaceTypeFromComponent(int number, Scope scope, UCELParser.CompVarContext node) {
        UCELParser.ComponentContext componentNode;
        DeclarationInfo compInfo;
        try {
            componentNode = (UCELParser.ComponentContext) scope.get(node.variableReference).getNode();
            compInfo = currentScope.get(componentNode.reference);
        } catch (Exception e) {
            logger.log(new ErrorLog(node, "Compiler error"));
            return null;
        }

        Type compType = compInfo.getType();
        int counter = 0;
        while (compType.getParameters()[counter].getEvaluationType() != Type.TypeEnum.seperatorType)
            counter++;

        return compType.getParameters()[counter + number + 1];
    }
    @Override
    public Type visitLinkStatement(UCELParser.LinkStatementContext ctx) {
        Type compVar1 = visit(ctx.compVar(0));
        Type compVar2 = visit(ctx.compVar(1));

        var leftInterfaceType = extractInterfaceTypeFromComponent(ctx.leftInterface.getDeclarationId(), currentScope, ctx.compVar(0));
        var rightInterfaceType = extractInterfaceTypeFromComponent(ctx.rightInterface.getDeclarationId(), currentScope, ctx.compVar(1));

        if (compVar1.getEvaluationType().equals(Type.TypeEnum.errorType)
                || compVar2.getEvaluationType().equals(Type.TypeEnum.errorType))
            return ERROR_TYPE;

        if (leftInterfaceType == null || rightInterfaceType == null ||
                !leftInterfaceType.getEvaluationType().equals(rightInterfaceType.getEvaluationType())) {
            logger.log(new ErrorLog(ctx, "Link statement must link interfaces of same type. Got " + leftInterfaceType + " and " + rightInterfaceType));
            return ERROR_TYPE;
        }

        return VOID_TYPE;
    }


    //endregion

    // region interfaceDecl

    // Passes along the type and names of var decls
    // inside the returned Type object
    @Override
    public Type visitInterfaceDecl(UCELParser.InterfaceDeclContext ctx) {
        Type interfaceType = visit(ctx.interfaceVarDecl());
        interfaceType.setEvaluationType(Type.TypeEnum.interfaceType);
        try {
            currentScope.get(ctx.reference).setType(interfaceType);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx,"internal error: could not find reference to interface"));
            interfaceType.setEvaluationType(Type.TypeEnum.errorType);
        }
        return interfaceType;
    }

    //endregion

    //region interfaceVarDecl

    @Override
    public Type visitInterfaceVarDecl(UCELParser.InterfaceVarDeclContext ctx) {
        List<String> names = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        assert ctx.type().size() == ctx.arrayDeclID().size();

        for (int i = 0; i < ctx.type().size(); i++) {
            var varType = visit(ctx.type(i));

            names.add(ctx.arrayDeclID(i).ID().getText());
            types.add(varType);

            if (varType.equals(ERROR_TYPE)) {
                logger.log(new ErrorLog(ctx.arrayDeclID(i), "Error: variable has type error"));
            }
        }

        if (types.contains(ERROR_TYPE)) {
            return new Type(Type.TypeEnum.errorType, names.toArray(new String[0]), types.toArray(new Type[0]));
        }

        return new Type(Type.TypeEnum.voidType, names.toArray(new String[0]), types.toArray(new Type[0]));
    }


    // endregion

    @Override
    public Type visitProject(UCELParser.ProjectContext ctx) {
        var result = VOID_TYPE;

        enterScope(ctx.scope);

        var pdeclarationType = visit(ctx.pdeclaration());
        if (pdeclarationType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (!pdeclarationType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx, "Type error in declaration. Value is not VOID_TYPE"));
            result = ERROR_TYPE;
        }

        for (var template : ctx.ptemplate()) {
            var templateType = visit(template);
            if (templateType.equals(ERROR_TYPE)) {
                result = ERROR_TYPE;
            } else if (!templateType.equals(VOID_TYPE)) {
                logger.log(new ErrorLog(ctx, "Type error in template. Value is not VOID_TYPE"));
                result = ERROR_TYPE;
            }
        }

        var psystemType = visit(ctx.psystem());
        if (psystemType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (!psystemType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx, "Type error in system. Value is not VOID_TYPE"));
            result = ERROR_TYPE;
        }

        exitScope();
        return result;
    }

    @Override
    public Type visitPdeclaration(UCELParser.PdeclarationContext ctx) {
        var result = VOID_TYPE;

        var declarationsType = visit(ctx.declarations());
        if (declarationsType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (!declarationsType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx, "Type error in declarations. Value is not VOID_TYPE"));
            result = ERROR_TYPE;
        }

        return result;
    }

    @Override
    public Type visitPtemplate(UCELParser.PtemplateContext ctx) {
        var result = VOID_TYPE;

        enterScope(ctx.scope);

        var parametersType = visit(ctx.parameters());
        if (parametersType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (parametersType.getEvaluationType() != Type.TypeEnum.voidType) {
            logger.log(new ErrorLog(ctx.parameters(), "Type error in parameters. Value is not VOID_TYPE"));
            result = ERROR_TYPE;
        }

        var declarationsType = visit(ctx.declarations());
        if (declarationsType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (!declarationsType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx, "Type error in declarations. Value is not VOID_TYPE"));
            result = ERROR_TYPE;
        }

        var graphType = visit(ctx.graph());
        if (graphType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (!graphType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx, "Type error in graph. Value is not VOID_TYPE"));
            result = ERROR_TYPE;
        }

        exitScope();

        Type[] paramTypes = parametersType.getParameters();
        Type[] templateTypes = new Type[paramTypes != null ? paramTypes.length + 1 : 1];
        templateTypes[0] = new Type(Type.TypeEnum.processType);
        if (paramTypes != null)
            System.arraycopy(paramTypes, 0, templateTypes, 1, paramTypes.length);
        try {
            currentScope.get(ctx.reference).setType(new Type(Type.TypeEnum.templateType, templateTypes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public Type visitPsystem(UCELParser.PsystemContext ctx) {
        var result = VOID_TYPE;

        var declarationsType = visit(ctx.declarations());
        if (declarationsType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (!declarationsType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx, "Type error in declarations. Value is not VOID_TYPE"));
            result = ERROR_TYPE;
        }


        if (ctx.build() != null) {
            var buildType = visit(ctx.build());
            if (buildType.equals(ERROR_TYPE)) {
                result = ERROR_TYPE;
            } else if (!buildType.equals(VOID_TYPE)) {
                logger.log(new ErrorLog(ctx, "Type error in build. Value is not VOID_TYPE"));
                result = ERROR_TYPE;
            }
        }


        else if (ctx.system() != null) {
            var systemType = visit(ctx.system());
            if (systemType.equals(ERROR_TYPE)) {
                result = ERROR_TYPE;
            } else if (!systemType.equals(VOID_TYPE)) {
                logger.log(new ErrorLog(ctx, "Type error in system. Value is not VOID_TYPE"));
                result = ERROR_TYPE;
            }
        }
        else {
            logger.log(new ErrorLog(ctx, "Compiler error: PSystem: Expected either build or system in type checker"));
            result = ERROR_TYPE;
        }


        return result;
    }

    //region Component
    @Override
    public Type visitComponent(UCELParser.ComponentContext ctx) {
        enterScope(ctx.scope);

        Type parametersType = VOID_TYPE;
        if (ctx.parameters() != null) parametersType = visit(ctx.parameters());
        Type interfacesType = visit(ctx.interfaces());
        Type compBodyType = visit(ctx.compBody());
        exitScope();

        if (parametersType.equals(ERROR_TYPE) || interfacesType.equals(ERROR_TYPE) || compBodyType.equals(ERROR_TYPE))
            return ERROR_TYPE;

        if (!compBodyType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx.compBody(), "Body must be of type void, got: " + compBodyType));
        }

        var componentTypes = new ArrayList<Type>();
        Type[] paramTypes = parametersType.getParameters();
        componentTypes.add(new Type(Type.TypeEnum.componentType));
        if (paramTypes != null) componentTypes.addAll(Arrays.asList(paramTypes));
        componentTypes.add(new Type(Type.TypeEnum.seperatorType));
        if (interfacesType.getParameters() != null) componentTypes.addAll(Arrays.asList(interfacesType.getParameters()));

        var paramNames = parametersType.getParameterNames();
        var interNames = interfacesType.getParameterNames();

        var names = new ArrayList<String>();
        names.add("");
        names.addAll(Arrays.asList(paramNames));
        names.add(":");
        names.addAll(Arrays.asList(interNames));

        var componentType = new Type(Type.TypeEnum.componentType, names.toArray(String[]::new), componentTypes.toArray(new Type[0]));
        try {
            currentScope.get(ctx.reference).setType(componentType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return componentType;
    }

    @Override
    public Type visitCompBody(UCELParser.CompBodyContext ctx) {
        enterScope(ctx.scope);

        var declsType = ctx.declarations() != null ? visit(ctx.declarations()) : VOID_TYPE;
        var buildType = ctx.build() != null ? visit(ctx.build()) : VOID_TYPE;

        exitScope();

        if (declsType.equals(ERROR_TYPE) || buildType.equals(ERROR_TYPE)) {
            return ERROR_TYPE;
        } else if (!declsType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx.declarations(), "Compiler error: declarations must be void, got: " + declsType));
            return ERROR_TYPE;
        } else if (!buildType.equals((VOID_TYPE))) {
            logger.log(new ErrorLog(ctx.build(), "Compiler error: build must be void, got: " + buildType));
            return ERROR_TYPE;
        }

        return VOID_TYPE;
    }

    @Override
    public Type visitInterfaces(UCELParser.InterfacesContext ctx) {
        if (ctx.parameters() == null)
            return VOID_TYPE;

        Type parametersType = visit(ctx.parameters());
        boolean foundError = false;

        for (var paramType : parametersType.getParameters()) {
            if (paramType.equals(ERROR_TYPE))
                foundError = true;
        }

        return foundError ? ERROR_TYPE : new Type(Type.TypeEnum.voidType, parametersType.getParameterNames(), parametersType.getParameters());
    }


    //endregion

    //region Build Region

    @Override
    public Type visitBuild(UCELParser.BuildContext ctx) {
        // build : BUILD COLON LEFTCURLYBRACE buildDecl* buildStmnt+ RIGHTCURLYBRACE;
        boolean hadError = false;

        var decls = ctx.buildDecl();
        if(decls != null) {
            for (var decl : decls) {
                var declType = visit(decl);
                if (!declType.equals(VOID_TYPE)) {
                    hadError = true;
                    if (!decl.equals(ERROR_TYPE))
                        logger.log(new ErrorLog(decl, "Compiler error: Void type expected"));
                }
            }
        }


        for(var stmt: ctx.buildStmnt()) {
            var stmtType = visit(stmt);
            if (!stmtType.equals(VOID_TYPE)) {
                hadError = true;
                if(!stmt.equals(ERROR_TYPE))
                    logger.log(new ErrorLog(stmt, "Compiler error: Void type expected"));
            }
        }


        if(hadError)
            return ERROR_TYPE;

        return VOID_TYPE;
    }

    @Override
    public Type visitBuildBlock(UCELParser.BuildBlockContext ctx) {
        enterScope(ctx.scope);

        var hadError = false;
        for(var stmt: ctx.buildStmnt()) {
            if(visit(stmt).equals(ERROR_TYPE))
                hadError = true;
        }

        exitScope();
        if(hadError)
            return ERROR_TYPE;

        return VOID_TYPE;
    }

    //region Build If
    @Override
    public Type visitBuildIf(UCELParser.BuildIfContext ctx) {
        var exprType = visit(ctx.expression());
        if (!exprType.equals(BOOL_TYPE)) {
            logger.log(new ErrorLog(ctx, "Expression in if-statement must be type bool, got type: " + exprType));
            return ERROR_TYPE;
        }

        for (var stmnt : ctx.buildStmnt()) {
            var stmntType = visit(stmnt);
            if (stmntType.equals(ERROR_TYPE))
                return ERROR_TYPE;
            else if (!stmntType.equals(VOID_TYPE)) {
                logger.log(new ErrorLog(ctx.buildStmnt(0), "Statement must be void-type, got: " + stmntType));
                return ERROR_TYPE;
            }
        }

        return VOID_TYPE;
    }

    @Override
    public Type visitBuildDecl(UCELParser.BuildDeclContext ctx) {
        // buildDecl locals [DeclarationReference typeReference, DeclarationReference reference]
        //    : ID ID (arrayDecl)* END;
        Type[] arrayDeclTypes = null;
        if (ctx.arrayDecl() != null) {
            arrayDeclTypes = ctx.arrayDecl().stream().map(this::visit).toArray(Type[]::new);
        }

        try {
            var typeInfo = currentScope.get(ctx.typeReference);
            var refInfo = currentScope.get(ctx.reference);

            var refType = new Type(typeInfo.getType().getEvaluationType(), arrayDeclTypes != null ? arrayDeclTypes.length : 0);
            refInfo.setType(refType);
        }
        catch {
            logger.log(new ErrorLog(ctx, "Compiler error: Could not find typeReference or reference in scope"));
            return ERROR_TYPE;
        }

        return VOID_TYPE;

    }

    //endregion

    @Override
    public Type visitBuildIteration(UCELParser.BuildIterationContext ctx) {
        DeclarationInfo iteratorInfo;
        try {
            iteratorInfo = currentScope.get(ctx.reference);
        } catch (Exception e) {
            throw new RuntimeException(e); // Should be no way this isn't set by the reference handler
        }
        var lowerBound = visit(ctx.expression(0));
        var upperBound = visit(ctx.expression(1));
        var stmt = visit(ctx.buildStmnt());

        boolean hadError = false;
        if(!iteratorInfo.getType().equals(INT_TYPE)) {
            logger.log(new ErrorLog(ctx, "Compiler error, iterator should have been automatically set to an integer."));
            hadError = true;
        }

        if(!lowerBound.equals(INT_TYPE)) {
            if(!lowerBound.equals(ERROR_TYPE))
                logger.log(new ErrorLog(ctx.expression(0), "Lower bound must be an integer"));
            hadError = true;
        }

        if(!upperBound.equals(INT_TYPE)) {
            if(!upperBound.equals(ERROR_TYPE))
                logger.log(new ErrorLog(ctx.expression(1), "Upper bound must be an integer"));
            hadError = true;
        }

        if(!stmt.equals(VOID_TYPE)) {
            if(!stmt.equals(ERROR_TYPE))
                logger.log(new ErrorLog(ctx.buildStmnt(), "Compiler error: Statements should always return error or void"));
            hadError = true;
        }

        if(hadError)
            return ERROR_TYPE;

        return VOID_TYPE;
    }

    @Override
    public Type visitBuildStmnt(UCELParser.BuildStmntContext ctx) {
        var childType = visit(ctx.children.get(0));
        if (childType.equals(ERROR_TYPE))
            return ERROR_TYPE;
        else if (!childType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx, "Compiler error"));
            return ERROR_TYPE;
        }

        return VOID_TYPE;
    }

    //endregion

    //region Start

    @Override
    public Type visitStart(UCELParser.StartContext ctx) {
        enterScope(ctx.scope);
        var declType = visit(ctx.declarations());
        if (declType.equals(ERROR_TYPE)) {
            //No logging, passing through
            return ERROR_TYPE;
        } else if (!declType.equals(VOID_TYPE)) {
            logger.log(new ErrorLog(ctx.declarations(), "Compiler Error, unexpected type of declarations: " + declType));
            return ERROR_TYPE;
        }

        boolean correct = true;

        for (var stmnt : ctx.statement()) {
            var stmntType = visit(stmnt);
            if (!stmntType.equals(VOID_TYPE)) {
                correct = false;
                if (stmntType.equals(ERROR_TYPE))
                    logger.log(new ErrorLog(ctx,"Compiler error during type checking"));
            }
        }

        var sysType = visit(ctx.system());
        if (!sysType.equals(VOID_TYPE) && !sysType.equals(ERROR_TYPE))
            logger.log(new ErrorLog(ctx, "Compiler error during type checking"));

        exitScope();
        return sysType.equals(VOID_TYPE) && correct ? VOID_TYPE : ERROR_TYPE;
    }


    //endregion

    //region System

    @Override
    public Type visitSystem(UCELParser.SystemContext ctx) {
        boolean success = true;

        for (var expr : ctx.expression()) {
            var exprRes = visit(expr);
            if (!exprRes.isSameBaseType(PROCESS_TYPE) && !exprRes.isSameBaseType(TEMPLATE_TYPE)) {
                success = false;
                if (!expr.equals(ERROR_TYPE))
                    logger.log(new ErrorLog(ctx, "Expression in system must be of type process"));
            }
        }

        return success ? VOID_TYPE : ERROR_TYPE;
    }


    //endregion
    
    //region function
    @Override
    public Type visitFunction(UCELParser.FunctionContext ctx) {
        DeclarationInfo declInfo = null;

        try {
            declInfo = currentScope.get(ctx.reference);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler error: " + e.getMessage()));
            return ERROR_TYPE;
        }

        enterScope(ctx.scope);

        Type type = visit(ctx.type());
        Type parameterType = visit(ctx.parameters());


        if(type.equals(ERROR_TYPE) || parameterType.equals(ERROR_TYPE)) {
            //No logging, passing through
            exitScope();
            return ERROR_TYPE;
        }

        Type[] types = new Type[parameterType.getParameters().length + 1];

        types[0] = type;
        for(int i = 1; i < types.length; i++) {
            types[i] = parameterType.getParameters()[i - 1];
        }

        declInfo.setType(new Type(Type.TypeEnum.functionType, types));

        currentFunction = declInfo;

        Type blockType = visit(ctx.block());

        exitScope();
        return blockType.equals(type) ? VOID_TYPE : ERROR_TYPE;
    }
    //endregion

    //region Instantiation

    @Override
    public Type visitInstantiation(UCELParser.InstantiationContext ctx) {
        DeclarationInfo instantiationInfo = null, constructorInfo = null;

        var parameters = ctx.parameters();
        Type[] parameterTypes = parameters != null ? visit(parameters).getParameters() : new Type[0];

        enterScope(ctx.scope);

        try {
            instantiationInfo = currentScope.getParent().get(ctx.instantiatedReference);
            constructorInfo = currentScope.get(ctx.constructorReference);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
            exitScope();
            return ERROR_TYPE;
        }

        Type[] argumentTypes = visit(ctx.arguments()).getParameters();
        Type constructorType = constructorInfo.getType();

        if(constructorType.getEvaluationType() != Type.TypeEnum.templateType) {
            logger.log(new ErrorLog(ctx, "Expected a template but found: " + constructorType));
            exitScope();
            return ERROR_TYPE;
        }

        Type[] constructorParameters = constructorType.getParameters();
        for (int i = 1; i < constructorParameters.length; i++) {
            if(!constructorParameters[i].equals(argumentTypes[i-1])) {
                logger.log(new ErrorLog(ctx, "Expected argument of type: " + constructorParameters[i] +
                        " but found: " + argumentTypes[i-1]));
                exitScope();
                return ERROR_TYPE;
            }
        }

        Type[] instantiationParameters = new Type[parameterTypes.length + 1];
        instantiationParameters[0] = PROCESS_TYPE;
        for (int i = 1; i < instantiationParameters.length; i++)
            instantiationParameters[i] = parameterTypes[i-1];

        Type instantiationType = new Type(Type.TypeEnum.templateType, instantiationParameters);

        instantiationInfo.setType(instantiationType);

        exitScope();
        return VOID_TYPE;
    }

    //endregion

    //region Parameters
    @Override
    public Type visitParameters(UCELParser.ParametersContext ctx) {
        boolean foundError = false;
        List<Type> parameterTypes = new ArrayList<>();
        var names = new ArrayList<String>();
        for (UCELParser.ParameterContext parameter : ctx.parameter()) {
            Type paramType = visit(parameter);
            if (paramType.equals(ERROR_TYPE))
                foundError = true;
            parameterTypes.add(paramType);
            try {
                var paramName = currentScope.get(parameter.reference).getIdentifier();
                names.add(paramName);
            } catch (Exception e) {
                logger.log(new ErrorLog(parameter, "Cannot get parameter name from context: " + e.getMessage()));
                names.add("error_parameter_name");
                return ERROR_TYPE;
            }
        }

        Type[] parameterTypesArray = {};
        parameterTypesArray = parameterTypes.toArray(parameterTypesArray);

        return foundError ? ERROR_TYPE : new Type(Type.TypeEnum.voidType, names.toArray(String[]::new), parameterTypesArray);
    }

    //endregion

    //region Parameter
    @Override
    public Type visitParameter(UCELParser.ParameterContext ctx) {
        var type = visit(ctx.type());
        Type parameterType = new Type(type.getEvaluationType(), ctx.arrayDecl().size());

        try {
            getCurrentScope().get(ctx.reference).setType(parameterType);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "scope error: could not find symbol " + ctx.getText()));
            return ERROR_TYPE;
        }

        return parameterType;
    }
    //endregion

    //region Statements
    @Override
    public Type visitBlock(UCELParser.BlockContext ctx) {

        Type commonType = null;
        enterScope(ctx.scope);

        for(UCELParser.LocalDeclarationContext ldc : ctx.localDeclaration()) {
            Type declType = visit(ldc);
            if(declType.equals(ERROR_TYPE))
                commonType = declType;
        }

        if(commonType != null && commonType.equals(ERROR_TYPE)) {
            //No logging just passing the error up
            exitScope();
            return ERROR_TYPE;
        } else {
            commonType = null;
        }

        boolean hasFoundError = false;
        boolean hasFoundType = false;

        for(UCELParser.StatementContext sc : ctx.statement()) {
            Type statementType = visit(sc);
            if (hasFoundType) {
                exitScope();
                logger.log(new ErrorLog(sc, "Unreachable code"));
                return ERROR_TYPE;
            }
            if(!(hasFoundError)) {
                if(statementType.equals(ERROR_TYPE)) {
                    hasFoundError = true;
                } else if(!statementType.equals(VOID_TYPE)) {
                    hasFoundType = true;
                    commonType = statementType;
                }
            }
        }

        exitScope();

        if(hasFoundError) return ERROR_TYPE;
        else if(commonType == null) return VOID_TYPE;
        else return commonType;
    }

    @Override
    public Type visitReturnStatement(UCELParser.ReturnStatementContext ctx) {
        var expression = ctx.expression();
        if (expression != null) {
            var expressionType = visit(expression);
            if (currentFunction == null || currentFunction.getType().getEvaluationType() == null) {
                logger.log(new ErrorLog(ctx, "Return statement only valid within function"));
                return ERROR_TYPE;
            }
            Type funcType = currentFunction.getType();
            if(funcType.getEvaluationType() != Type.TypeEnum.functionType ||
                funcType.getParameters() == null || funcType.getParameters().length < 1) {
                logger.log(new ErrorLog(ctx, "Compiler Error: Invalid type for function"));
                return ERROR_TYPE;
            } else if(!expressionType.equals(funcType.getParameters()[0])) {
                logger.log(new ErrorLog(ctx, "Expression in return is of the wrong type"));
                return ERROR_TYPE;
            } else {
                return expressionType;
            }
        } else {
            return VOID_TYPE;
        }
    }

    @Override
    public Type visitWhileLoop(UCELParser.WhileLoopContext ctx) {
        Type condType = visit(ctx.expression());

        if (!condType.equals(BOOL_TYPE)) {
            logger.log(new ErrorLog(ctx.expression(), "Loop condition not a boolean"));
            return ERROR_TYPE;
        }

        return visit(ctx.statement());
    }

    @Override
    public Type visitDowhile(UCELParser.DowhileContext ctx) {
        Type condType = visit(ctx.expression());

        if (!condType.equals(BOOL_TYPE)) {
            logger.log(new ErrorLog(ctx.expression(), "Loop condition not a boolean"));
            return ERROR_TYPE;
        }

        return visit(ctx.statement());
    }

    @Override
    public Type visitForLoop(UCELParser.ForLoopContext ctx) {
        if ((ctx.assignment() != null)) {
            if (visit(ctx.assignment()).equals(ERROR_TYPE)) {
                logger.log(new ErrorLog(ctx.assignment(), "Assignment is not valid"));
                return ERROR_TYPE;
            }
        }

        Type condType = visit(ctx.expression(0));
        if (!condType.equals(BOOL_TYPE)) {
            logger.log(new ErrorLog(ctx.expression(0), "Loop condition not a boolean"));
            return ERROR_TYPE;
        }

        if ((ctx.expression(1) != null)) {
            if (visit(ctx.expression(1)).equals(ERROR_TYPE)) {
                logger.log(new ErrorLog(ctx.expression(1), "Expression not well typed"));
                return ERROR_TYPE;
            }
        }

        return visit(ctx.statement());
    }

    @Override
    public Type visitIteration(UCELParser.IterationContext ctx) {
        Type type = visit(ctx.type());

        if(type == null || !(type.equals(INT_TYPE) || type.equals(SCALAR_TYPE))) {
            logger.log(new ErrorLog(ctx.type(), "Unexpected type for iteration: " + type));
            return ERROR_TYPE;
        }

        try {
            currentScope.get(ctx.reference).setType(INT_TYPE);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: reference not set"));
            return ERROR_TYPE;
        }

        Type statementType = visit(ctx.statement());
        return statementType;
    }

    @Override
    public Type visitIfstatement(UCELParser.IfstatementContext ctx) {
        Type condType = visit(ctx.expression());

        if (!condType.equals(BOOL_TYPE)) {
            logger.log(new ErrorLog(ctx.expression(), "Condition not a boolean"));
            return ERROR_TYPE;
        }

        for (var stmnt : ctx.statement()) {
            var stmntType = visit(stmnt);
            if (stmntType.equals(ERROR_TYPE))
                return ERROR_TYPE;
            else if (!stmntType.equals(VOID_TYPE)) {
                logger.log(new ErrorLog(ctx.statement(0), "Statement must be void-type, got: " + stmntType));
                return ERROR_TYPE;
            }
        }

        return VOID_TYPE;
    }

    //endregion

    //region Type

    @Override
    public Type visitType(UCELParser.TypeContext ctx) {
        String prefix = ctx.prefix() == null ? "" : ctx.prefix().getText();
        Type type = visit(ctx.typeId());
        return switch (prefix) {
            case "urgent" ->  type.deepCopy(Type.TypePrefixEnum.urgent);
            case "broadcast" ->  type.deepCopy(Type.TypePrefixEnum.broadcast);
            case "meta" -> type.deepCopy(Type.TypePrefixEnum.meta);
            case "const" ->  type.deepCopy(Type.TypePrefixEnum.constant);
            default -> type;
        };
    }

    @Override
    public Type visitTypeIDType(UCELParser.TypeIDTypeContext ctx) {
        return switch (ctx.getText()) {
            case "int" -> INT_TYPE;
            case "clock" -> new Type(Type.TypeEnum.clockType);
            case "chan" -> CHAN_TYPE;
            case "bool" -> BOOL_TYPE;
            case "double" -> DOUBLE_TYPE;
            case "string" -> STRING_TYPE;
            default -> ERROR_TYPE;
        };
    }

    @Override
    public Type visitTypeIDID(UCELParser.TypeIDIDContext ctx) {
        try {
            return currentScope.get(ctx.reference).getType();
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
            return ERROR_TYPE;
        }
    }

    @Override
    public Type visitTypeIDInt(UCELParser.TypeIDIntContext ctx) {
        if(ctx.expression().size() == 0) return INT_TYPE;
        else {
            Type left = visit(ctx.expression(0));
            if(left.equals(INT_TYPE)) {
                if(ctx.expression().size() == 1) return INT_TYPE;
                else {
                    Type right = visit(ctx.expression(1));
                    if(right.equals(INT_TYPE)) return INT_TYPE;
                    else {
                        logger.log(new ErrorLog(ctx.expression().get(1), "Expected " +
                                INT_TYPE + " but found " + right));
                        return ERROR_TYPE;
                    }
                }
            } else {
                logger.log(new ErrorLog(ctx.expression().get(0), "Expected " +
                        INT_TYPE + " but found " + left));
                return ERROR_TYPE;
            }
        }
    }

    @Override
    public Type visitTypeIDScalar(UCELParser.TypeIDScalarContext ctx) {
        Type exprType = visit(ctx.expression());
        if(exprType.equals(INT_TYPE))
            return SCALAR_TYPE;
        logger.log(new ErrorLog(ctx.expression(), "Scalar type definition takes an " + INT_TYPE +
                " but found " + exprType));
        return ERROR_TYPE;
    }

    @Override
    public Type visitTypeIDStruct(UCELParser.TypeIDStructContext ctx) {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Type> types = new ArrayList<>();

        for(UCELParser.FieldDeclContext field : ctx.fieldDecl()) {
            Type fieldType = visit(field);
            if(fieldType.equals(ERROR_TYPE)) {
                //No log passing error through
                return fieldType;
            }
            for(String s : fieldType.getParameterNames()) names.add(s);
            for(Type t : fieldType.getParameters()) types.add(t);
        }

        return new Type(Type.TypeEnum.structType,
                names.toArray(new String[names.size()]),
                types.toArray(new Type[types.size()]));
    }

    @Override
    public Type visitFieldDecl(UCELParser.FieldDeclContext ctx) {
        Type type = visit(ctx.type());
        if(type.equals(ERROR_TYPE)) {
            //No logging, passing error through
            return ERROR_TYPE;
        }

        int idCount = ctx.arrayDeclID().size();
        Type[] fieldTypes = new Type[idCount];
        String[] fieldNames = new String[idCount];
        boolean foundErrors = false;

        for(int i = 0; i < idCount; i++) {
            UCELParser.ArrayDeclIDContext aDclID = ctx.arrayDeclID().get(i);
            int arrayDim = aDclID.arrayDecl().size();
            fieldTypes[i] = type.deepCopy(arrayDim);
            fieldNames[i] = aDclID.ID().getText();

            for(int ii = 0; ii < arrayDim; ii++) {
                Type t = visit(aDclID.arrayDecl().get(ii));
                foundErrors = foundErrors || t.equals(ERROR_TYPE);
            }
        }

        if(foundErrors) {
            //No logging, passing error through
            return ERROR_TYPE;
        }

        return new Type(Type.TypeEnum.structType, fieldNames, fieldTypes);
    }

    //endregion

    //region Declarations

    @Override
    public Type visitLocalDeclaration(UCELParser.LocalDeclarationContext ctx) {
        Type t = null;
        if(ctx.typeDecl() != null) t = visit(ctx.typeDecl());
        if(ctx.variableDecl() != null) t = visit(ctx.variableDecl());
        return t == null ? ERROR_TYPE : t;
    }

    @Override
    public Type visitTypeDecl(UCELParser.TypeDeclContext ctx) {
        Type type = visit(ctx.type());

        assert ctx.references.size() == ctx.arrayDeclID().size();
        for (int i = 0; i < ctx.references.size(); i++) {

            Type declType = visit(ctx.arrayDeclID(i));

            if (declType == ERROR_TYPE) {
                logger.log(new ErrorLog(ctx.arrayDeclID(i), "type error: declaration has type error"));
            }

            try {
                getCurrentScope().get(ctx.references.get(i)).setType(declType);
            } catch (Exception e) {
                logger.log(new ErrorLog(ctx.arrayDeclID(i), "reference error: unable to get reference of variable"));
            }
        }

        return type;
    }

    @Override
    public Type visitArrayDecl(UCELParser.ArrayDeclContext ctx) {
        if (ctx.expression() != null) {
            var elementType = visit(ctx.expression());
            return new Type(elementType.getEvaluationType(), 1);
        } else {
            var elementType = visit(ctx.type());
            return new Type(elementType.getEvaluationType(), 1);
        }
    }

    /**
     * Checks that varid types are equivalent to the declared type
     * Edge cases include:
     *      Coercion from int to double
     *      varid is declared as an array
     *      struct to array coercion is handled in the varID visitor
     *
     * @param ctx
     * @return
     */
    @Override
    public Type visitVariableDecl(UCELParser.VariableDeclContext ctx) {
        Type declaredType = null;
        if(ctx.type() != null) declaredType = visit(ctx.type());

        boolean errorFound = false;

        for(UCELParser.VariableIDContext varID : ctx.variableID()) {
            Type varIDType = visit(varID);
            if(declaredType != null && !varIDType.equalsOrIsArrayOf(declaredType)) {
                if(varIDType.equals(VOID_TYPE)) {
                    try {
                        DeclarationInfo declInfo = currentScope.get(varID.reference);
                        declInfo.setType(declaredType);
                    } catch (Exception e) {
                        logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
                        errorFound = true;
                    }
                } else  if(declaredType.equals(DOUBLE_TYPE) &&
                    varIDType.equals(INT_TYPE)) {

                    try {
                        DeclarationInfo declInfo = currentScope.get(varID.reference);
                        declInfo.setType(DOUBLE_TYPE);
                    } catch (Exception e) {
                        logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
                        errorFound = true;
                    }
                } else {
                    logger.log(new ErrorLog(varID, "Does not match declared " + declaredType));
                    errorFound = true;
                }
            }
        }

        if(errorFound) return ERROR_TYPE;
        else return VOID_TYPE;
    }

    @Override
    public Type visitVariableID(UCELParser.VariableIDContext ctx) {
        Type initialiserType = ctx.initialiser() != null ?
                visit(ctx.initialiser()) : VOID_TYPE;
        Type errorType = ERROR_TYPE;

        boolean errorFound = false;
        List<UCELParser.ArrayDeclContext> arrayDecls = ctx.arrayDecl();
        for(UCELParser.ArrayDeclContext arrayDecl : arrayDecls) {
            Type arrayDeclType = visit(arrayDecl);
            if(arrayDeclType.equals(errorType))
                errorFound = true;
        }

        int arrayDim = arrayDecls == null ? 0 : arrayDecls.size();
        if(errorFound) return errorType;

        try {
            Type newType = structToArray(ctx, initialiserType, arrayDim);
            currentScope.get(ctx.reference).setType(newType);
            return newType;
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
            return errorType;
        }
    }

    private Type structToArray(ParserRuleContext ctx, Type type, int arrayDim) {
        if(arrayDim == 0) return type;
        if(type.equals(VOID_TYPE)) return VOID_TYPE.deepCopy(arrayDim);
        if(type.getEvaluationType() != Type.TypeEnum.structType) {
            logger.log(new ErrorLog(ctx, "Array declaration does not match initializer"));
        }
        Type internalType = null;
        for(Type t : type.getParameters()) {
            Type paramType = structToArray(ctx, t, arrayDim - 1);
            if(internalType != null && !internalType.equals(paramType)) {
                logger.log(new ErrorLog(ctx, "Array initializer cannot contain both " +
                        paramType + " and " + internalType));
                return ERROR_TYPE;
            }
            internalType = paramType;
        }
        return internalType.deepCopy(arrayDim);
    }

    @Override
    public Type visitInitialiser(UCELParser.InitialiserContext ctx) {
        if(ctx.expression() != null) return visit(ctx.expression());

        Type[] types = new Type[ctx.initialiser().size()];
        List<UCELParser.InitialiserContext> innerInitialisers = ctx.initialiser();
        for(int i = 0; i < innerInitialisers.size(); i++) {
            types[i] = visit(innerInitialisers.get(i));
        }

        return new Type(Type.TypeEnum.structType, types);
    }

    @Override
    public Type visitAssign(UCELParser.AssignContext ctx) {
        return VOID_TYPE;
    }

    @Override
    public Type visitStatement(UCELParser.StatementContext ctx) {
        return visit(ctx.children.get(0));
    }

    @Override
    public Type visitDeclarations(UCELParser.DeclarationsContext ctx) {
        boolean errorFound = false;
        Type errorType = ERROR_TYPE;

        if(ctx.children != null) {
            for(ParseTree pt : ctx.children)
                if(visit(pt).equals(errorType))
                    errorFound = true;
        }

        if(errorFound) return errorType;
        else return VOID_TYPE;
    }

    //endregion

    //region Expressions
    @Override
    public Type visitAssignExpr(UCELParser.AssignExprContext ctx) {
        Type leftType = visit(ctx.expression(0));
        Type rightType = visit(ctx.expression(1));

        if (leftType.getEvaluationType() == Type.TypeEnum.errorType ||
                rightType.getEvaluationType() == Type.TypeEnum.errorType ||
                leftType.getEvaluationType() == Type.TypeEnum.chanType ||
                rightType.getEvaluationType() == Type.TypeEnum.chanType ||
                leftType.getEvaluationType() == Type.TypeEnum.voidType ||
                rightType.getEvaluationType() == Type.TypeEnum.voidType ||
                leftType.getEvaluationType() == Type.TypeEnum.invalidType ||
                rightType.getEvaluationType() == Type.TypeEnum.invalidType) {
            logger.log(new ErrorLog(ctx, "Type error: cannot assign " + rightType + " to " + leftType));
            return ERROR_TYPE;
        }

        if (leftType.equals(rightType)) {
            return leftType;
        }
        else if ((leftType.getEvaluationType() == Type.TypeEnum.clockType) && (rightType.getEvaluationType() == Type.TypeEnum.intType)
                && (rightType.getArrayDimensions() == 0)) {
            return leftType;
        }
        else {
            logger.log(new ErrorLog(ctx, "Type error: cannot assign " + rightType + " to " + leftType));
            return ERROR_TYPE;
        }
    }

    @Override
    public Type visitAssignment(UCELParser.AssignmentContext ctx) {
        Type leftType = visit(ctx.expression(0));
        Type rightType = visit(ctx.expression(1));

        if (leftType.getEvaluationType() == Type.TypeEnum.errorType ||
                rightType.getEvaluationType() == Type.TypeEnum.errorType ||
                leftType.getEvaluationType() == Type.TypeEnum.chanType ||
                rightType.getEvaluationType() == Type.TypeEnum.chanType ||
                leftType.getEvaluationType() == Type.TypeEnum.voidType ||
                rightType.getEvaluationType() == Type.TypeEnum.voidType ||
                leftType.getEvaluationType() == Type.TypeEnum.invalidType ||
                rightType.getEvaluationType() == Type.TypeEnum.invalidType) {
            logger.log(new ErrorLog(ctx, "Type error: cannot assign " + rightType + " to " + leftType));
            return ERROR_TYPE;
        }

        if (leftType.equals(rightType)) {
            return VOID_TYPE;
        }
        else if ((leftType.getEvaluationType() == Type.TypeEnum.clockType) && (rightType.getEvaluationType() == Type.TypeEnum.intType)
                && (rightType.getArrayDimensions() == 0)) {
            return VOID_TYPE;
        }
        else {
            logger.log(new ErrorLog(ctx, "Type error: cannot assign " + rightType + " to " + leftType));
            return ERROR_TYPE;
        }
    }

    @Override
    public Type visitIdExpr(UCELParser.IdExprContext ctx) {
        try {
            var variable = currentScope.get(ctx.reference);
            return variable.getType();
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error, invalid reference"));
            return ERROR_TYPE;
        }
    }

    @Override
    public Type visitParen(UCELParser.ParenContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Type visitStructAccess(UCELParser.StructAccessContext ctx) {
        Type structType = visit(ctx.expression());
        Type[] parameterTypes = structType.getParameters();
        String[] parameterNames = structType.getParameterNames();

        String identifier = ctx.ID().getText();

        if(structType.getEvaluationType() != Type.TypeEnum.structType ||
            parameterTypes == null || parameterNames == null) {
            logger.log(new ErrorLog(ctx, "Invalid struct"));
            return ERROR_TYPE;
        }

        for (int i = 0; i < parameterNames.length; i++) {
            if(parameterNames[i].equals(identifier)) {
                ctx.reference = new DeclarationReference(-1, i);
                return parameterTypes[i];
            }
        }

        logger.log(new ErrorLog(ctx, "Struct type " + structType + " does not contain field '" + identifier + "'"));
        return ERROR_TYPE;
    }

    @Override
    public Type visitAddSub(UCELParser.AddSubContext ctx) {
        Type leftType = visit(ctx.expression(0));
        Type rightType = visit(ctx.expression(1));

        return intDoubleBinaryOp(ctx, leftType, rightType);
    }

    @Override
    public Type visitArrayIndex(UCELParser.ArrayIndexContext ctx) {
        Type arrayType = visit(ctx.expression(0));
        Type arrayIndex = visit(ctx.expression(1));

        if (!isArray(arrayType)) {
            logger.log(new ErrorLog(ctx.expression(0), "Expected an array type but found " + arrayType));
            return ERROR_TYPE;
        }
        if (!(arrayIndex.getEvaluationType() == Type.TypeEnum.intType)) {
            logger.log(new ErrorLog(ctx, arrayIndex + " cannot be used as array index, only an integer can"));
            return ERROR_TYPE;
        }

        return arrayType;
    }

    @Override
    public Type visitMarkExpr(UCELParser.MarkExprContext ctx) {
        Type type = visit(ctx.expression());

        if(type.equals(CLOCK_TYPE)) return CLOCK_TYPE;
        else {
            logger.log(new ErrorLog(ctx.expression(), "Expected " + CLOCK_TYPE + " but found " + type));
            return ERROR_TYPE;
        }
    }

    @Override
    public Type visitMultDiv(UCELParser.MultDivContext ctx) {
        Type leftType = visit(ctx.expression(0));
        Type rightType = visit(ctx.expression(1));

        return intDoubleBinaryOp(ctx, leftType, rightType);
    }

    private boolean isArray(Type t) {
        return t.getArrayDimensions() > 0;
    }

    //TODO comment
    private Type intDoubleBinaryOp(ParserRuleContext ctx, Type leftType, Type rightType) {
        if(isArray(leftType) || isArray(rightType)) {
            logger.log(new ErrorLog(ctx, "Array type not supported for operator"));
            return ERROR_TYPE;
        }

        Type.TypeEnum leftEnum = leftType.getEvaluationType();
        Type.TypeEnum rightEnum = rightType.getEvaluationType();

        // Error || Error -> Error
        if(leftEnum == Type.TypeEnum.errorType || rightEnum == Type.TypeEnum.errorType)
            return ERROR_TYPE;

        // Same types: int || int -> int
        else if(leftEnum == Type.TypeEnum.intType && rightEnum == Type.TypeEnum.intType)
            return INT_TYPE;

        // Same types: double || double -> double
        else if(leftEnum == Type.TypeEnum.doubleType && rightEnum == Type.TypeEnum.doubleType)
            return DOUBLE_TYPE;

        // Mixed types: double || int -> double
        else if(leftEnum == Type.TypeEnum.doubleType && rightEnum == Type.TypeEnum.intType)
            return DOUBLE_TYPE;

        // Mixed types: int || double -> double
        else if(leftEnum == Type.TypeEnum.intType && rightEnum == Type.TypeEnum.doubleType)
            return DOUBLE_TYPE;

        else {
            logger.log(new ErrorLog(ctx, "The types " + leftType.toString() + " and " +
                                        leftType.toString() + " are not supported for operator"));
            return ERROR_TYPE;
        }
    }


    @Override
    public Type visitLiteral(UCELParser.LiteralContext ctx) {
        Type type;
        if (ctx.NAT() != null) {
            type = INT_TYPE;
        } else if (ctx.DOUBLE() != null) {
            type = DOUBLE_TYPE;
        } else if (ctx.bool() != null) {
            type = BOOL_TYPE;
        } else {
            logger.log(new ErrorLog(ctx, "Unsupported literal"));
            type = ERROR_TYPE;
        }

        return type;
    }

    @Override
    public Type visitUnaryExpr(UCELParser.UnaryExprContext ctx) {
        Type exprType = visit(ctx.expression());
        Type.TypeEnum typeEnum = exprType.getEvaluationType();

        Type evaluationType;

        boolean isPlus = ctx.unary().PLUS() != null;
        boolean isMinus = ctx.unary().MINUS() != null;
        boolean isNeg = ctx.unary().NEG() != null;
        boolean isNot = ctx.unary().NOT() != null;

        if ((isPlus || isMinus) && (typeEnum == Type.TypeEnum.intType || typeEnum == Type.TypeEnum.doubleType)) {
            evaluationType = exprType;
        } else if ((isNeg || isNot) && typeEnum == Type.TypeEnum.boolType) {
            evaluationType = exprType;
        } else {
            logger.log(new ErrorLog(ctx, typeEnum + " is unsupported for this unary operator"));
            evaluationType = ERROR_TYPE;
        }

        return evaluationType;
    }


    //region Increment/Decrement
    @Override
    public Type visitIncrementPost(UCELParser.IncrementPostContext ctx) {
        return visitIncrementDecrement(ctx, visit(ctx.expression()));
    }
    @Override
    public Type visitIncrementPre(UCELParser.IncrementPreContext ctx) {
        return visitIncrementDecrement(ctx, visit(ctx.expression()));
    }
    @Override
    public Type visitDecrementPost(UCELParser.DecrementPostContext ctx) {
        return visitIncrementDecrement(ctx, visit(ctx.expression()));
    }
    @Override
    public Type visitDecrementPre(UCELParser.DecrementPreContext ctx) {
        return visitIncrementDecrement(ctx, visit(ctx.expression()));
    }
    private Type visitIncrementDecrement (UCELParser.ExpressionContext ctx, Type typeOfVariable) {
        // Array
        if(isArray(typeOfVariable)) {
            logger.log(new ErrorLog(ctx, "Array type not supported for increment and decrement"));
            return ERROR_TYPE;
        }

        // Base types
        switch (typeOfVariable.getEvaluationType()) {
            case intType:
            case doubleType:
                return typeOfVariable;
            default:
                logger.log(new ErrorLog(ctx, typeOfVariable.getEvaluationType() + " is unsupported for increment and decrement"));
                return ERROR_TYPE;
        }
    }
    //endregion

    //region FuncCall
    @Override
    public Type visitFuncCall(UCELParser.FuncCallContext ctx) {
        Type argsType = visit(ctx.arguments());
        DeclarationInfo funcDecl;
        Type funcType;

        // Get type of function declaration
        var refArgsCount = ctx.arguments().ID().size();
        if(refArgsCount > 0) {
            funcType = ctx.originDefinition.getType();
        }
        else {
            try {
                funcType = currentScope.get(ctx.reference).getType();
            } catch (Exception e) {
                logger.log(new ErrorLog(ctx, "Could not find definition for function"));
                return ERROR_TYPE;
            }
        }

        // Compare input parameter types
        Type[] declParams = funcType.getParameters();
        Type[] argsParams = argsType.getParameters();

        if(declParams.length != argsParams.length + 1) {
            logger.log(new ErrorLog(ctx.arguments(), String.format("Function expected %s arguments, but got %s", declParams.length-1, argsParams.length)));
            return ERROR_TYPE;
        }

        boolean argsMismatch = false;
        for (int i = 0; i < argsParams.length; i++) {
            if(!declParams[i + 1].equals(argsParams[i])) {
                argsMismatch = true;
            }
        }

        if(argsMismatch) {
            logger.log(new ErrorLog(ctx, "Type " + argsParams + " given to a function "+ funcType));
            return ERROR_TYPE;
        }

        return declParams[0];
    }
    //endregion

    //region ArgumentsVisitor
    public Type visitArguments(UCELParser.ArgumentsContext ctx) {
        // Map each argument to its type
        Type[] argTypes = ctx.expression().stream().map(expr -> visit(expr)).toArray(Type[]::new);

        // If any type is error, then base-type is error-type.
        // Else if no errors, then base-type is invalidType
        if(Arrays.stream(argTypes).anyMatch(t -> t.getEvaluationType() == Type.TypeEnum.errorType)) {
            return new Type(Type.TypeEnum.errorType, argTypes);
        }
        else {
            return new Type(Type.TypeEnum.invalidType, argTypes);
        }
    }
    private UCELParser.ExpressionContext[] getArgumentsContexts(UCELParser.ArgumentsContext ctx) {
        return ctx.children.stream().toArray(UCELParser.ExpressionContext[]::new);
    }
    //endregion

    @Override
    public Type visitMinMax(UCELParser.MinMaxContext ctx) {
        Type leftNode = visit(ctx.expression(0));
        Type rightNode = visit(ctx.expression(1));

        return intDoubleBinaryOp(ctx, leftNode, rightNode);
    }

    //region Relational/Equality expressions
    @Override
    public Type visitRelExpr(UCELParser.RelExprContext ctx) {
        Type leftNode = visit(ctx.expression(0));
        Type rightNode = visit(ctx.expression(1));

        Type.TypeEnum leftEnum = leftNode.getEvaluationType();
        Type.TypeEnum rightEnum = rightNode.getEvaluationType();

        List<Type.TypeEnum> comparableTypes = new ArrayList<>() {{ add(Type.TypeEnum.intType); add(Type.TypeEnum.doubleType);}};
        List<Type.TypeEnum> comparableClockTypes = new ArrayList<>() {{ add(Type.TypeEnum.intType); add(Type.TypeEnum.clockType);}};
        if (leftEnum == Type.TypeEnum.errorType || rightEnum == Type.TypeEnum.errorType) {
            // Error propagated, no logging
            return ERROR_TYPE;
        }

        if (isArray(leftNode) || isArray(rightNode)) {
            logger.log(new ErrorLog(ctx, "Array types are unsupported for relation"));
            return ERROR_TYPE;
        }

        var isIntDoubleComparable = comparableTypes.contains(leftEnum) && comparableTypes.contains(rightEnum);
        var isIntClockComparable = comparableClockTypes.contains(leftEnum) && comparableClockTypes.contains(rightEnum);

        if (!isIntDoubleComparable && !isIntClockComparable && (leftEnum != rightEnum)) {
            logger.log(new ErrorLog(ctx, leftEnum + " and " + rightEnum + " are unsupported for relation"));
            return ERROR_TYPE;
        }

        return BOOL_TYPE;
    }

    @Override
    public Type visitEqExpr(UCELParser.EqExprContext ctx) {
        Type leftNode = visit(ctx.expression(0));
        Type rightNode = visit(ctx.expression(1));

        Type.TypeEnum leftEnum = leftNode.getEvaluationType();
        Type.TypeEnum rightEnum = rightNode.getEvaluationType();

        List<Type.TypeEnum> comparableTypes = new ArrayList<>() {{ add(Type.TypeEnum.intType); add(Type.TypeEnum.boolType); add(Type.TypeEnum.doubleType);}};

        if (leftEnum == Type.TypeEnum.errorType || rightEnum == Type.TypeEnum.errorType) {
            // Error propagated, no logging
            return ERROR_TYPE;
        } else if (!(comparableTypes.contains(leftEnum) && leftEnum == rightEnum)
                && !(leftEnum == Type.TypeEnum.intType && rightEnum == Type.TypeEnum.doubleType)
                && !(leftEnum == Type.TypeEnum.doubleType && rightEnum == Type.TypeEnum.intType)
                && !(leftEnum == Type.TypeEnum.intType && rightEnum == Type.TypeEnum.clockType)
                && !(leftEnum == Type.TypeEnum.clockType && rightEnum == Type.TypeEnum.intType)) {
            logger.log(new ErrorLog(ctx, leftEnum + " and " + rightEnum + " are unsupported for equivalence"));
            return ERROR_TYPE;
        } else if (isArray(leftNode) || isArray(rightNode)) {
            logger.log(new ErrorLog(ctx, "Array types are unsupported for equivalence"));
            return ERROR_TYPE;
        }

        return BOOL_TYPE;
    }
    //endregion

    //region Bit expressions
    @Override
    public Type visitBitshift(UCELParser.BitshiftContext ctx) {
        Type leftNode = visit(ctx.expression(0));
        Type rightNode = visit(ctx.expression(1));

        return bitExpressionDetermineType(ctx, leftNode, rightNode);
    }
    @Override
    public Type visitBitAnd(UCELParser.BitAndContext ctx) {
        Type leftNode = visit(ctx.expression(0));
        Type rightNode = visit(ctx.expression(1));

        return bitExpressionDetermineType(ctx, leftNode, rightNode);
    }

    @Override
    public Type visitBitXor(UCELParser.BitXorContext ctx) {
        Type leftNode = visit(ctx.expression(0));
        Type rightNode = visit(ctx.expression(1));

        return bitExpressionDetermineType(ctx, leftNode, rightNode);
    }

    @Override
    public Type visitBitOr(UCELParser.BitOrContext ctx) {
        Type leftNode = visit(ctx.expression(0));
        Type rightNode = visit(ctx.expression(1));

        return bitExpressionDetermineType(ctx, leftNode, rightNode);
    }

    private Type bitExpressionDetermineType(ParserRuleContext ctx, Type left, Type right) {
        Type.TypeEnum leftEnum = left.getEvaluationType();
        Type.TypeEnum rightEnum = right.getEvaluationType();

        if (leftEnum == Type.TypeEnum.errorType || rightEnum == Type.TypeEnum.errorType) {
            // Error propagated, no logging
            return ERROR_TYPE;
        } else if (!(leftEnum == Type.TypeEnum.intType && rightEnum == Type.TypeEnum.intType)) {
            logger.log(new ErrorLog(ctx, leftEnum + " and " + rightEnum + " are unsupported for bit operator"));
            return ERROR_TYPE;
        } else if (isArray(left) || isArray(right)) {
            logger.log(new ErrorLog(ctx, "Array types are unsupported for bit operator"));
            return ERROR_TYPE;
        }

        return INT_TYPE;
    }
    //endregion

    //region Logical expressions
    @Override
    public Type visitLogAnd(UCELParser.LogAndContext ctx) {
        Type leftNode = visit(ctx.expression(0));
        Type rightNode = visit(ctx.expression(1));

        return logicalExpressionDetermineType(ctx, leftNode, rightNode);
    }

    @Override
    public Type visitLogOr(UCELParser.LogOrContext ctx) {
        Type leftNode = visit(ctx.expression(0));
        Type rightNode = visit(ctx.expression(1));

        return logicalExpressionDetermineType(ctx, leftNode, rightNode);
    }

    private Type logicalExpressionDetermineType(ParserRuleContext ctx, Type left, Type right) {
        Type.TypeEnum leftEnum = left.getEvaluationType();
        Type.TypeEnum rightEnum = right.getEvaluationType();

        if (leftEnum == Type.TypeEnum.errorType || rightEnum == Type.TypeEnum.errorType) {
            //No logging, passing through
            return ERROR_TYPE;
        } else if (leftEnum != Type.TypeEnum.boolType || rightEnum != Type.TypeEnum.boolType) {
            logger.log(new ErrorLog(ctx, leftEnum + " and " + rightEnum + " are unsupported for logical operator"));
            return ERROR_TYPE;
        } else if (isArray(left) || isArray(right)) {
            logger.log(new ErrorLog(ctx, "Array types are unsupported for logical operator"));
            return ERROR_TYPE;
        }

        return BOOL_TYPE;
    }

    //endregion

    //region Conditional Expr
    @Override
    public Type visitConditional(UCELParser.ConditionalContext ctx) {
        Type condType = visit(ctx.expression(0));
        Type leftValType = visit(ctx.expression(1));
        Type rightValType = visit(ctx.expression(2));

        // Condition must be boolean
        if(condType.getEvaluationType() != Type.TypeEnum.boolType) {
            logger.log(new ErrorLog(ctx, condType.getEvaluationType() + "is not supported as predicate in conditional operator"));
            return ERROR_TYPE;
        }


        // Return types match
        if(leftValType.equals(rightValType))
            return leftValType;

        // Type Coercion
        return intDoubleBinaryOp(ctx, leftValType, rightValType);
    }

    //endregion

    //endregion

    //region Project Graph

    @Override
    public Type visitGraph(UCELParser.GraphContext ctx) {
        boolean hadError = false;
        for(var loc: ctx.location()) {
            if(visit(loc).equals(ERROR_TYPE))
                hadError = true;
        }

        for(var edge: ctx.edge()) {
            if(visit(edge).equals(ERROR_TYPE))
                hadError = true;
        }

        if(hadError)
            return ERROR_TYPE;

        return VOID_TYPE;
    }

    //region Location
    @Override
    public Type visitLocation(UCELParser.LocationContext ctx) {
        if(visit(ctx.invariant()).equals(ERROR_TYPE))
            return ERROR_TYPE;

        if(visit(ctx.exponential()).equals(ERROR_TYPE))
            return ERROR_TYPE;

        return VOID_TYPE;
    }

    @Override
    public Type visitExponential(UCELParser.ExponentialContext ctx) {
        boolean hadError = false;
        for(var expr: ctx.expression()) {
            var exprType = visit(expr);

            if(!exprType.equals(INT_TYPE)) {
                if(!exprType.equals(ERROR_TYPE)) { // Prevent duplicate errors
                    logger.log(new ErrorLog(expr, "Exponential expr must be of type integer"));
                }
                hadError = true;
            }
        }
        if(hadError)
            return ERROR_TYPE;

        return VOID_TYPE;
    }

    @Override
    public Type visitInvariant(UCELParser.InvariantContext ctx) {
        var expr = ctx.expression();
        if(expr == null)
            return BOOL_TYPE;

        if(visit(expr).equals(BOOL_TYPE))
            return BOOL_TYPE;

        logger.log(new ErrorLog(ctx, "Invariant must be of type bool"));
        return ERROR_TYPE;
    }
    //endregion

    //region Edge
    @Override
    public Type visitEdge(UCELParser.EdgeContext ctx) {
        enterScope(ctx.scope);

        var select = visit(ctx.select());
        var guard = visit(ctx.guard());
        var sync = visit(ctx.sync());
        var update = visit(ctx.update());

        var hadError = false;
        if(select != null && select.equals(ERROR_TYPE))
            hadError = true;
        else if(guard.equals(ERROR_TYPE))
            hadError = true;
        else if(sync.equals(ERROR_TYPE))
            hadError = true;
        else if(update.equals(ERROR_TYPE))
            hadError = true;
        else if(!guard.equals(BOOL_TYPE)) {
            logger.log(new ErrorLog(ctx, "Guard must be of type bool"));
            hadError = true;
        }

        exitScope();
        return hadError ? ERROR_TYPE : VOID_TYPE;
    }

    @Override
    public Type visitSelect(UCELParser.SelectContext ctx) {

        boolean errorFound = false;

        int declCount = ctx.type().size();
        for(int i=0; i<declCount; i++) {
            var type = visit(ctx.type().get(i));
            var ref = ctx.references.get(i);

            if(type.equals(ERROR_TYPE))
                errorFound = true;

            try {
                currentScope.get(ref).setType(type);
            } catch (Exception e) {
                throw new RuntimeException("Compiler error: " + e.getMessage());
            }
        }

        if(errorFound) return ERROR_TYPE;
        else return VOID_TYPE;
    }

    @Override
    public Type visitGuard(UCELParser.GuardContext ctx) {
        var expr = ctx.expression();
        if(expr == null)
            return BOOL_TYPE;

        var exprType = visit(expr);
        if(exprType.equals(BOOL_TYPE))
            return BOOL_TYPE;

        if(!exprType.equals(ERROR_TYPE))
            logger.log(new ErrorLog(ctx, "Guard must be of type bool"));

        return ERROR_TYPE;
    }

    @Override
    public Type visitSync(UCELParser.SyncContext ctx) {
        var expr = ctx.expression();
        if(expr == null)
            return VOID_TYPE;

        if(visit(expr).equals(CHAN_TYPE))
            return CHAN_TYPE;

        logger.log(new ErrorLog(ctx, "Sync must be of type channel, or empty"));
        return ERROR_TYPE;
    }

    @Override
    public Type visitUpdate(UCELParser.UpdateContext ctx) {

        boolean hadError = false;
        for(var expr: ctx.expression()) {
            if(visit(expr).equals(ERROR_TYPE))
                hadError = true;
        }
        if(hadError)
            return ERROR_TYPE;

        return VOID_TYPE;
    }
    //endregion


    //endregion

    private void enterScope(Scope scope) {
        currentScope = scope;
    }

    private void exitScope() {
        this.currentScope = this.currentScope.getParent();
    }

    public Scope getCurrentScope() {
        return currentScope;
    }

}
