package org.UcelParser.TypeChecker;

import com.sun.jdi.FloatType;
import org.UcelParser.CodeGeneration.templates.ManualTemplate;
import org.UcelParser.CodeGeneration.templates.Template;
import org.UcelParser.Util.Exception.CouldNotFindException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.nio.channels.FileLock;
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
    private static final Type COMPONENT_TYPE = new Type(Type.TypeEnum.componentType);
    private static final Type TEMPLATE_TYPE = new Type(Type.TypeEnum.templateType);
    private static final Type SCALAR_TYPE = new Type(Type.TypeEnum.scalarType);
    private static final Type ARRAY_TYPE = new Type(Type.TypeEnum.voidType, 1);
    public DeclarationInfo currentFunction = null;
    //endregion

    // region compCon

    @Override
    public Type visitCompCon(UCELParser.CompConContext ctx) {
        Type argumentsTypes = visit(ctx.arguments());

        if (argumentsTypes.getEvaluationType().equals(ERROR_TYPE.getEvaluationType()))
            return ERROR_TYPE;

        DeclarationInfo constructorInfo;
        DeclarationInfo variableCompInfo;

        try {
            constructorInfo = currentScope.get(ctx.constructorReference);
        } catch (CouldNotFindException e) {
            logger.log(new TypeNotDeclaredErrorLog(ctx, ctx.ID().getText()));
            return ERROR_TYPE;
        }

        try {
            variableCompInfo = currentScope.get(ctx.compVar().variableReference);
        } catch (CouldNotFindException e) {
            logger.log(new VariableNotDeclaredErrorLog(ctx.compVar(), ctx.compVar().ID().getText()));
            return ERROR_TYPE;
        }

        if (!constructorInfo.getNode().equals(variableCompInfo.getNode())) {
            String conID = constructorInfo.getNode() instanceof UCELParser.ComponentContext
                    ? ((UCELParser.ComponentContext) constructorInfo.getNode()).ID().getText()
                    : ((UCELParser.PtemplateContext) constructorInfo.getNode()).ID().getText();
            String varConID = variableCompInfo.getNode() instanceof UCELParser.ComponentContext
                    ? ((UCELParser.ComponentContext) variableCompInfo.getNode()).ID().getText()
                    : ((UCELParser.PtemplateContext) variableCompInfo.getNode()).ID().getText();
            logger.log(new WrongAssignmentTypeErrorLog(ctx, conID, varConID));
            return ERROR_TYPE;
        }

        Type constructorType = constructorInfo.getType();
        if (!(constructorType.getEvaluationType().equals(Type.TypeEnum.componentType)
                || constructorType.getEvaluationType().equals(Type.TypeEnum.templateType))) {
            logger.log(new WrongTypeErrorLog(ctx,
                    new ArrayList<Type>() {{ add(COMPONENT_TYPE); add(TEMPLATE_TYPE); }},
                    constructorType, "for component construction"
            ));
            return ERROR_TYPE;
        }

        for (int i = 0; i < argumentsTypes.getParameters().length; i++) {
            if (!constructorType.getParameters()[i+1].getEvaluationType().equals(argumentsTypes.getParameters()[i].getEvaluationType())) {
                logger.log(new WrongTypeErrorLog(ctx, constructorType.getParameters()[i], argumentsTypes.getParameters()[i], "for component construction arguments"));
                return ERROR_TYPE;
            }
        }

        Type compVarType;
        try {
            compVarType = currentScope.get(ctx.compVar().variableReference).getType();
        } catch (Exception e) {
            logger.log(new VariableNotDeclaredErrorLog(ctx.compVar(), ctx.compVar().ID().getText()));
            return ERROR_TYPE;
        }

        if (compVarType == null) {
            logger.log(new MissingTypeErrorLog(ctx.compVar(), ctx.compVar().ID().getText()));
            return ERROR_TYPE;
        }

        if (!(compVarType.getEvaluationType().equals(Type.TypeEnum.processType) && constructorType.getEvaluationType().equals(Type.TypeEnum.templateType))
            && !(compVarType.getEvaluationType().equals(Type.TypeEnum.componentType) && constructorType.getEvaluationType().equals(Type.TypeEnum.componentType))) {
            logger.log(new WrongAssignmentTypeErrorLog(ctx, constructorType, compVarType));
            return ERROR_TYPE;
        }

        return VOID_TYPE;
    }


    // endregion

    // region compVar

//    @Override
//    public Type visitCompVar(UCELParser.CompVarContext ctx) {
//        boolean success = true;
//        DeclarationInfo variableRefDecl = null;
//
//        if (ctx.expression() != null) {
//            for (var expr : ctx.expression()) {
//                Type exprType = visit(expr);
//                if (exprType.getEvaluationType() != Type.TypeEnum.intType) {
//                    logger.log(new ErrorLog(expr, "type error: expected type int, got type " + exprType));
//                    success = false;
//                }
//            }
//        }
//
//        try {
//            variableRefDecl = currentScope.get(ctx.variableReference);
//        } catch (Exception e) {
//            logger.log(new ErrorLog(ctx, "internal error: variable reference could not be found in scope"));
//            return ERROR_TYPE;
//        }
//
//        if (variableRefDecl.getType().getArrayDimensions() < ctx.expression().size()){
//            logger.log(new ErrorLog(ctx, "type error: expected "
//                        + variableRefDecl.getType().getArrayDimensions()
//                        + " array dimensions, got " + ctx.expression().size()));
//            success = false;
//        }
//
//        if (variableRefDecl.getType().getEvaluationType() != Type.TypeEnum.componentType
//                || variableRefDecl.getType().getEvaluationType() != Type.TypeEnum.templateType){
//            logger.log(new ErrorLog(ctx, "type error: expected type component or template, got type "
//                    + variableRefDecl.getType()));
//            success = false;
//        }
//
//        Type result = new Type(Type.TypeEnum.componentType, variableRefDecl.getType().getArrayDimensions() - ctx.expression().size());
//        return success ? result : ERROR_TYPE;
//    }

    // endregion


    // region linkStatement
    private Type extractInterfaceTypeFromComponent(int number, Scope scope, UCELParser.CompVarContext node) throws CouldNotFindException {
        UCELParser.ComponentContext componentNode;
        DeclarationInfo compInfo;
        try {
            componentNode = (UCELParser.ComponentContext) scope.get(node.variableReference).getNode();
            compInfo = componentNode.scope.getParent().get(componentNode.reference);
        } catch (Exception e) {
            throw new CouldNotFindException(e.getMessage());
        }

        Type compType = compInfo.getType();
        int counter = 0;
        while (compType.getParameters()[counter].getEvaluationType() != Type.TypeEnum.seperatorType)
            counter++;

        return compType.getParameters()[counter + number + 1];
    }
    @Override
    public Type visitLinkStatement(UCELParser.LinkStatementContext ctx) {
        Type compVar1;
        Type compVar2;

        try {
            compVar1 = currentScope.get(ctx.compVar(0).variableReference).getType();
        } catch (Exception e) {
            logger.log(new VariableNotDeclaredErrorLog(ctx.compVar(0), ctx.compVar(0).ID().getText()));
            return ERROR_TYPE;
        }

        try {
            compVar2 = currentScope.get(ctx.compVar(1).variableReference).getType();
        } catch (Exception e) {
            logger.log(new VariableNotDeclaredErrorLog(ctx.compVar(1), ctx.compVar(1).ID().getText()));
            return ERROR_TYPE;
        }

        if (compVar1 == null) {
            logger.log(new MissingTypeErrorLog(ctx.compVar(0), ctx.compVar(0).ID().getText()));
            return ERROR_TYPE;
        }

        if (compVar2 == null) {
            logger.log(new MissingTypeErrorLog(ctx.compVar(1), ctx.compVar(1).ID().getText()));
            return ERROR_TYPE;
        }

        Type leftInterfaceType;
        try {
            leftInterfaceType = extractInterfaceTypeFromComponent(ctx.leftInterface.getDeclarationId(), currentScope, ctx.compVar(0));
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx.compVar(0), "interface binding"));
            return ERROR_TYPE;
        }

        Type rightInterfaceType;
        try {
            rightInterfaceType = extractInterfaceTypeFromComponent(ctx.rightInterface.getDeclarationId(), currentScope, ctx.compVar(1));
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx.compVar(1), "interface binding"));
            return ERROR_TYPE;
        }

        if (compVar1.getEvaluationType().equals(Type.TypeEnum.errorType)
                || compVar2.getEvaluationType().equals(Type.TypeEnum.errorType))
            return ERROR_TYPE;

        if (leftInterfaceType == null || rightInterfaceType == null ||
                !leftInterfaceType.getEvaluationType().equals(rightInterfaceType.getEvaluationType())) {
            logger.log(new IncompatibleTypeErrorLog(ctx, leftInterfaceType, rightInterfaceType, "linking"));
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
            logger.log(new MissingReferenceErrorLog(ctx, "interface " + ctx.ID().getText()));
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
            int dimensions = ctx.arrayDeclID(i).arrayDecl().size();

            names.add(ctx.arrayDeclID(i).ID().getText());
            types.add(varType.deepCopy(dimensions));

            if (varType.equals(ERROR_TYPE)) {
                logger.log(new WrongTypeErrorLog(ctx.arrayDeclID(i), ctx.arrayDeclID(i).ID().getText(), varType));
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
            logger.log(new WrongTypeErrorLog(ctx.pdeclaration(), VOID_TYPE, pdeclarationType, "declarations in declaration file"));
            result = ERROR_TYPE;
        }

        for (var template : ctx.ptemplate()) {
            var templateType = visit(template);
            if (templateType.equals(ERROR_TYPE)) {
                result = ERROR_TYPE;
            } else if (!templateType.equals(VOID_TYPE)) {
                logger.log(new WrongTypeErrorLog(template, VOID_TYPE, templateType, "template"));
                result = ERROR_TYPE;
            }
        }

        var psystemType = visit(ctx.psystem());
        if (psystemType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (!psystemType.equals(VOID_TYPE)) {
            logger.log(new WrongTypeErrorLog(ctx.psystem(), VOID_TYPE, psystemType, "system declarations file"));
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
            logger.log(new WrongTypeErrorLog(ctx.declarations(), VOID_TYPE, declarationsType, "declarations"));
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
            logger.log(new WrongTypeErrorLog(ctx.parameters(), VOID_TYPE, parametersType, "parameters in template"));
            result = ERROR_TYPE;
        }

        var declarationsType = visit(ctx.declarations());
        if (declarationsType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (!declarationsType.equals(VOID_TYPE)) {
            logger.log(new WrongTypeErrorLog(ctx.declarations(), VOID_TYPE, declarationsType, "declarations in template"));
            result = ERROR_TYPE;
        }

        var graphType = visit(ctx.graph());
        if (graphType.equals(ERROR_TYPE)) {
            result = ERROR_TYPE;
        } else if (!graphType.equals(VOID_TYPE)) {
            logger.log(new WrongTypeErrorLog(ctx.graph(), VOID_TYPE, graphType, "graph in template"));
            result = ERROR_TYPE;
        }

        exitScope();

        Type[] paramTypes = parametersType.getParameters();
        String[] paramNames = parametersType.getParameterNames();
        Type[] templateTypes = new Type[paramTypes != null ? paramTypes.length + 1 : 1];
        String[] templateNames = new String[paramNames != null ? paramNames.length + 1 : 1];
        templateTypes[0] = new Type(Type.TypeEnum.processType);
        templateNames[0] = "";
        if (paramTypes != null)
            System.arraycopy(paramTypes, 0, templateTypes, 1, paramTypes.length);
        if (paramNames != null)
            System.arraycopy(paramNames, 0, templateNames, 1, paramNames.length);
        try {
            currentScope.get(ctx.reference).setType(new Type(Type.TypeEnum.templateType, templateNames, templateTypes));
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, "template " + ctx.ID().getText()));
            return ERROR_TYPE;
        }

        return result;
    }

    @Override
    public Type visitPsystem(UCELParser.PsystemContext ctx) {
        var result = VOID_TYPE;

        var declarationsType = visit(ctx.declarations());
        if (!declarationsType.equals(VOID_TYPE)) {
            if (!declarationsType.equals(ERROR_TYPE))
                logger.log(new WrongTypeErrorLog(ctx.declarations(), VOID_TYPE, declarationsType, "declarations"));
            return ERROR_TYPE;
        }


        if (ctx.build() != null) {
            var buildType = visit(ctx.build());
            if (!buildType.equals(VOID_TYPE)) {
                if (!buildType.equals(ERROR_TYPE))
                    logger.log(new WrongTypeErrorLog(ctx.build(), VOID_TYPE, buildType, "build"));
                return ERROR_TYPE;
            }
        } else if (ctx.system() != null) {
            var systemType = visit(ctx.system());
            if (!systemType.equals(VOID_TYPE)) {
                if (!systemType.equals(ERROR_TYPE))
                    logger.log(new WrongTypeErrorLog(ctx.system(), VOID_TYPE, systemType, "system"));
                return ERROR_TYPE;
            }
        } else {
            logger.log(new CompilerErrorLog(ctx, "PSystem: Expected either build or system in type checker"));
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
        exitScope();

        if (parametersType.equals(ERROR_TYPE) || interfacesType.equals(ERROR_TYPE))
            return ERROR_TYPE;

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
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, "component " + ctx.ID().getText()));
            return ERROR_TYPE;
        }

        enterScope(ctx.scope);
        Type compBodyType = visit(ctx.compBody());
        exitScope();

        if (!compBodyType.equals(VOID_TYPE)) {
            if (!compBodyType.equals(ERROR_TYPE))
                logger.log(new WrongTypeErrorLog(ctx.compBody(), VOID_TYPE, compBodyType, "component body"));
            return ERROR_TYPE;
        }

        return componentType;
    }

    @Override
    public Type visitCompBody(UCELParser.CompBodyContext ctx) {
        enterScope(ctx.scope);

        var declsType = ctx.declarations() != null ? visit(ctx.declarations()) : VOID_TYPE;
        var buildType = ctx.build() != null && !declsType.equals(ERROR_TYPE) ? visit(ctx.build()) : VOID_TYPE;

        exitScope();

        if (declsType.equals(ERROR_TYPE) || buildType.equals(ERROR_TYPE)) {
            return ERROR_TYPE;
        } else if (!declsType.equals(VOID_TYPE)) {
            logger.log(new WrongTypeErrorLog(ctx.declarations(), VOID_TYPE, declsType, "declarations in component body"));
            return ERROR_TYPE;
        } else if (!buildType.equals((VOID_TYPE))) {
            logger.log(new WrongTypeErrorLog(ctx.build(), VOID_TYPE, buildType, "build in component body"));
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

        var decls = ctx.buildDecl();
        if(decls != null) {
            for (var decl : decls) {
                var declType = visit(decl);
                if (!declType.equals(VOID_TYPE)) {
                    if (!declType.equals(ERROR_TYPE))
                        logger.log(new WrongTypeErrorLog(decl, VOID_TYPE, declType, "declaration in build"));
                    return ERROR_TYPE;
                }
            }
        }

        for(var stmt: ctx.buildStmnt()) {
            var stmtType = visit(stmt);
            if (!stmtType.equals(VOID_TYPE)) {
                if(!stmtType.equals(ERROR_TYPE))
                    logger.log(new WrongTypeErrorLog(stmt, VOID_TYPE, stmtType, "statement in build"));
                return ERROR_TYPE;
            }
        }

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
            logger.log(new WrongTypeErrorLog(ctx.expression(), BOOL_TYPE, exprType, "expression in if-statement"));
            return ERROR_TYPE;
        }

        for (var stmnt : ctx.buildStmnt()) {
            var stmntType = visit(stmnt);
            if (stmntType.equals(ERROR_TYPE))
                return ERROR_TYPE;
            else if (!stmntType.equals(VOID_TYPE)) {
                logger.log(new WrongTypeErrorLog(ctx.buildStmnt(0), VOID_TYPE, stmntType, "statement in if-statement"));
                return ERROR_TYPE;
            }
        }

        return VOID_TYPE;
    }

    @Override
    public Type visitBuildDecl(UCELParser.BuildDeclContext ctx) {
        Type type;
        try {
            type = currentScope.get(ctx.typeReference).getType();
        } catch (CouldNotFindException e) {
            logger.log(new ReferenceErrorLog(ctx, "component or template " + ctx.ID().getText()));
            return ERROR_TYPE;
        }

        if (type == null) {
            logger.log(new MissingTypeErrorLog(ctx, ctx.ID().getText()));
            return ERROR_TYPE;
        }

        if (!(type.getEvaluationType().equals(Type.TypeEnum.componentType) || type.getEvaluationType().equals(Type.TypeEnum.templateType))) {
            logger.log(new WrongTypeErrorLog(ctx, new ArrayList<Type>() {{ add(COMPONENT_TYPE); add(TEMPLATE_TYPE); }}, type, "instantiation in build block"));
            return ERROR_TYPE;
        }

        boolean isInitialised = ctx.compVar() == null;
        int arrayDims = !isInitialised && ctx.compVar().expression() != null
                ? ctx.compVar().expression().size()
                : 0;
        if (arrayDims > 0) {
            for (var arrayDecl : ctx.compVar().expression()) {
                Type arrayDeclType = visit(arrayDecl);
                if (arrayDeclType.getEvaluationType() != Type.TypeEnum.intType) {
                    logger.log(new WrongTypeErrorLog(arrayDecl, INT_TYPE, arrayDeclType, "declaration of array"));
                    return ERROR_TYPE;
                }
            }
        }

        if(isInitialised && (ctx.compCon().compVar().expression() != null
                             && ctx.compCon().compVar().expression().size() != 0)) {
            logger.log(new ErrorLog(ctx, "Array declarations cannot be initialised in a build declaration"));
        }

        Type assigneeType = type.deepCopy(arrayDims);
        if (type.getEvaluationType().equals(Type.TypeEnum.templateType))
            assigneeType.setEvaluationType(Type.TypeEnum.processType);

        UCELParser.CompVarContext compVar = isInitialised ? ctx.compCon().compVar() : ctx.compVar();
        try {
            currentScope.get(compVar.variableReference).setType(assigneeType);
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(compVar, compVar.ID().getText()));
            return ERROR_TYPE;
        }

        return isInitialised ? visit(ctx.compCon()) : VOID_TYPE;
    }

    //endregion

    @Override
    public Type visitBuildIteration(UCELParser.BuildIterationContext ctx) {
        DeclarationInfo iteratorInfo;
        try {
            iteratorInfo = currentScope.get(ctx.reference);
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return ERROR_TYPE;
        }
        iteratorInfo.setType(INT_TYPE);
        var lowerBound = visit(ctx.expression(0));
        var upperBound = visit(ctx.expression(1));
        var stmt = visit(ctx.buildStmnt());

        boolean hadError = false;

        if(!lowerBound.equals(INT_TYPE)) {
            if(!lowerBound.equals(ERROR_TYPE))
                logger.log(new WrongTypeErrorLog(ctx.expression(0), INT_TYPE, lowerBound, "lower bound"));
            hadError = true;
        }

        if(!upperBound.equals(INT_TYPE)) {
            if(!upperBound.equals(ERROR_TYPE))
                logger.log(new WrongTypeErrorLog(ctx.expression(1), INT_TYPE, upperBound, "upper bound"));
            hadError = true;
        }

        if(!stmt.equals(VOID_TYPE)) {
            if(!stmt.equals(ERROR_TYPE))
                logger.log(new WrongTypeErrorLog(ctx.buildStmnt(), VOID_TYPE, stmt, "statement"));
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
            logger.log(new WrongTypeErrorLog(ctx, VOID_TYPE, childType, "statement in build"));
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
            logger.log(new WrongTypeErrorLog(ctx.declarations(), VOID_TYPE, declType, "declarations"));
            return ERROR_TYPE;
        }

        boolean correct = true;

        for (var stmnt : ctx.statement()) {
            var stmntType = visit(stmnt);
            if (!stmntType.equals(VOID_TYPE)) {
                correct = false;
                if (!stmntType.equals(ERROR_TYPE))
                    logger.log(new WrongTypeErrorLog(stmnt, VOID_TYPE, stmntType, "statement"));
            }
        }

        var sysType = visit(ctx.system());
        if (!sysType.equals(VOID_TYPE)) {
            if (!sysType.equals(ERROR_TYPE))
                logger.log(new WrongTypeErrorLog(ctx.system(), VOID_TYPE, sysType, "system"));;
        }
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
                if (!exprRes.equals(ERROR_TYPE))
                    logger.log(new WrongTypeErrorLog(expr, new ArrayList<Type>() {{add(PROCESS_TYPE); add(TEMPLATE_TYPE);}}, exprRes, "expression in system"));
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
            logger.log(new CompilerErrorLog(ctx, e.getMessage()));
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
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, "variable"));
            exitScope();
            return ERROR_TYPE;
        }

        try {
            constructorInfo = currentScope.get(ctx.constructorReference);
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, "constructor"));
            exitScope();
            return ERROR_TYPE;
        }


        Type[] argumentTypes = visit(ctx.arguments()).getParameters();
        Type constructorType = constructorInfo.getType();

        if(constructorType.getEvaluationType() != Type.TypeEnum.templateType) {
            logger.log(new WrongTypeErrorLog(ctx, TEMPLATE_TYPE, constructorType, "process instantiation"));
            exitScope();
            return ERROR_TYPE;
        }

        Type[] constructorParameters = constructorType.getParameters();
        for (int i = 1; i < constructorParameters.length; i++) {
            if(!constructorParameters[i].equals(argumentTypes[i-1])) {
                logger.log(new WrongTypeErrorLog(ctx, constructorParameters[i], argumentTypes[i-1], "argument in template constructor"));
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
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(parameter, "parameter " + parameter.ID().getText()));
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
        Type parameterType = type.deepCopy(ctx.arrayDecl().size());

        try {
            getCurrentScope().get(ctx.reference).setType(parameterType);
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
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
                logger.log(new Warning(sc, "Unreachable code"));
                return commonType;
            }
            if(!(hasFoundError)) {
                if(statementType.equals(ERROR_TYPE)) {
                    hasFoundError = true;
                } else if(!statementType.equals(VOID_TYPE) && sc.expression() == null) {
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
                logger.log(new CompilerErrorLog(ctx, "Invalid type for function"));
                return ERROR_TYPE;
            } else if(!expressionType.equals(funcType.getParameters()[0])) {
                logger.log(new WrongTypeErrorLog(ctx.expression(), funcType.getParameters()[0], expressionType, "return statement"));
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
            logger.log(new WrongTypeErrorLog(ctx.expression(), BOOL_TYPE, condType, "loop condition"));
            return ERROR_TYPE;
        }

        return visit(ctx.statement());
    }

    @Override
    public Type visitDowhile(UCELParser.DowhileContext ctx) {
        Type condType = visit(ctx.expression());

        if (!condType.equals(BOOL_TYPE)) {
            logger.log(new WrongTypeErrorLog(ctx.expression(), BOOL_TYPE, condType, "loop condition"));
            return ERROR_TYPE;
        }

        return visit(ctx.statement());
    }

    @Override
    public Type visitForLoop(UCELParser.ForLoopContext ctx) {
        //todo: fix type checking in this one
        if ((ctx.assignment() != null)) {
            if (visit(ctx.assignment()).equals(ERROR_TYPE)) {
                logger.log(new ErrorLog(ctx.assignment(), "Assignment is not valid"));
                return ERROR_TYPE;
            }
        }

        Type condType = visit(ctx.expression(0));
        if (!condType.equals(BOOL_TYPE)) {
            logger.log(new WrongTypeErrorLog(ctx.expression(0), BOOL_TYPE, condType, "loop condition"));
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
            logger.log(new WrongTypeErrorLog(ctx.type(), new ArrayList<Type>() {{add(INT_TYPE);add(SCALAR_TYPE);}}, type, "iteration type"));
            return ERROR_TYPE;
        }

        try {
            currentScope.get(ctx.reference).setType(INT_TYPE);
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, "variable " + ctx.ID().getText()));
            return ERROR_TYPE;
        }

        return visit(ctx.statement());
    }

    @Override
    public Type visitIfstatement(UCELParser.IfstatementContext ctx) {
        Type condType = visit(ctx.expression());

        if (!condType.equals(BOOL_TYPE)) {
            logger.log(new WrongTypeErrorLog(ctx.expression(), BOOL_TYPE, condType, "condition"));
            return ERROR_TYPE;
        }

        for (var stmnt : ctx.statement()) {
            var stmntType = visit(stmnt);
            if (stmntType.equals(ERROR_TYPE))
                return ERROR_TYPE;
            else if (!stmntType.equals(VOID_TYPE)) {
                logger.log(new WrongTypeErrorLog(stmnt, VOID_TYPE, stmntType, "statement"));
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
            case "in" -> type.deepCopy(Type.TypePrefixEnum.in);
            case "out" -> type.deepCopy(Type.TypePrefixEnum.out);
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
            case "void" -> VOID_TYPE;
            default -> ERROR_TYPE;
        };
    }

    @Override
    public Type visitTypeIDID(UCELParser.TypeIDIDContext ctx) {
        Type retType;
        try {
            retType = currentScope.get(ctx.reference).getType();
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return ERROR_TYPE;
        }

        if (retType == null) {
            logger.log(new MissingTypeErrorLog(ctx, ctx.ID().getText()));
            return ERROR_TYPE;
        }

        return retType;
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
                        logger.log(new WrongTypeErrorLog(ctx.expression().get(1), INT_TYPE, right, "int typedef"));
                        return ERROR_TYPE;
                    }
                }
            } else {
                logger.log(new WrongTypeErrorLog(ctx.expression().get(0), INT_TYPE, left, "int typedef"));
                return ERROR_TYPE;
            }
        }
    }

    @Override
    public Type visitTypeIDScalar(UCELParser.TypeIDScalarContext ctx) {
        Type exprType = visit(ctx.expression());
        if(exprType.equals(INT_TYPE))
            return SCALAR_TYPE;
        logger.log(new WrongTypeErrorLog(ctx.expression(), INT_TYPE, exprType, "scalar type definition"));
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

            for (var arrayDecl : ctx.arrayDeclID(i).arrayDecl()){
                if (visit(arrayDecl).equals(ERROR_TYPE))
                    return ERROR_TYPE;
            }
            Type declType = type.deepCopy(ctx.arrayDeclID(i).arrayDecl().size());

            if (declType == ERROR_TYPE) {
                //todo: should this be here?
                logger.log(new ErrorLog(ctx.arrayDeclID(i), "type error: declaration has type error"));
            }

            try {
                getCurrentScope().get(ctx.references.get(i)).setType(declType);
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(ctx.arrayDeclID(i), "variable " + ctx.arrayDeclID(i).ID().getText()));
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
                if(varIDType.getEvaluationType().equals(Type.TypeEnum.voidType)) {
                    try {
                        DeclarationInfo declInfo = currentScope.get(varID.reference);
                        Type assigneeType = declaredType.deepCopy(varIDType.getArrayDimensions());
                        declInfo.setType(assigneeType);
                    } catch (CouldNotFindException e) {
                        logger.log(new MissingReferenceErrorLog(varID, varID.ID().getText()));
                        errorFound = true;
                    }
                } else  if(declaredType.equals(DOUBLE_TYPE) &&
                    varIDType.equals(INT_TYPE)) {

                    try {
                        DeclarationInfo declInfo = currentScope.get(varID.reference);
                        declInfo.setType(DOUBLE_TYPE);
                    } catch (CouldNotFindException e) {
                        logger.log(new MissingReferenceErrorLog(varID, varID.ID().getText()));
                        errorFound = true;
                    }
                } else {
                    logger.log(new WrongTypeErrorLog(varID, declaredType, varIDType, "declared variable"));
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
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return errorType;
        }
    }

    private Type structToArray(ParserRuleContext ctx, Type type, int arrayDim) {
        if(arrayDim == 0) return type;
        if(type.equals(VOID_TYPE)) return VOID_TYPE.deepCopy(arrayDim);
        if(type.getEvaluationType() != Type.TypeEnum.structType) {
            logger.log(new WrongTypeErrorLog(ctx, STRUCT_TYPE, type, "array declaration"));
        }
        Type internalType = null;
        for(Type t : type.getParameters()) {
            Type paramType = structToArray(ctx, t, arrayDim - 1);
            if(internalType != null && !internalType.equals(paramType)) {
                //todo: I do not quite get what this refers to
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
            logger.log(new WrongAssignmentTypeErrorLog(ctx, leftType, rightType));
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
            logger.log(new WrongAssignmentTypeErrorLog(ctx, leftType, rightType));
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
            logger.log(new WrongAssignmentTypeErrorLog(ctx, leftType, rightType));
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
            logger.log(new WrongAssignmentTypeErrorLog(ctx, leftType, rightType));
            return ERROR_TYPE;
        }
    }

    @Override
    public Type visitIdExpr(UCELParser.IdExprContext ctx) {
        DeclarationInfo variable;
        try {
            variable = currentScope.get(ctx.reference);
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return ERROR_TYPE;
        }

        if (variable.getType() == null) {
            logger.log(new MissingTypeErrorLog(ctx, variable.getIdentifier()));
        }

        return variable.getType();
    }

    @Override
    public Type visitParen(UCELParser.ParenContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Type visitPower(UCELParser.PowerContext ctx) {
        Type base = visit(ctx.expression(0));
        Type exponent = visit(ctx.expression(1));

        if (base.getEvaluationType() == Type.TypeEnum.intType && exponent.getEvaluationType() == Type.TypeEnum.intType) {
            return INT_TYPE;
        } else if (base.getEvaluationType() == Type.TypeEnum.doubleType && exponent.getEvaluationType() == Type.TypeEnum.intType) {
            return DOUBLE_TYPE;
        } else {
            //todo: non-compliant error log
            logger.log(new ErrorLog(ctx, "Type error: cannot raise " + base + " to the power of " + exponent));
            return ERROR_TYPE;
        }
    }

    @Override
    public Type visitStructAccess(UCELParser.StructAccessContext ctx) {
        Type structType = visit(ctx.expression());
        Type[] parameterTypes = structType.getParameters();
        String[] parameterNames = structType.getParameterNames();

        String identifier = ctx.ID().getText();

        if((structType.getEvaluationType() != Type.TypeEnum.structType && structType.getEvaluationType() != Type.TypeEnum.interfaceType) ||
            parameterTypes == null || parameterNames == null) {
            //todo: non-compliant error log
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
            //todo: create special array type for logging?
            logger.log(new TypeErrorLog(ctx.expression(0), "Expected an array type but found " + arrayType));
            return ERROR_TYPE;
        }
        if (!(arrayIndex.getEvaluationType() == Type.TypeEnum.intType)) {
            logger.log(new WrongTypeErrorLog(ctx.expression(1), INT_TYPE, arrayIndex, "array indexing"));
            return ERROR_TYPE;
        }

        return arrayType.deepCopy(arrayType.getArrayDimensions() - 1);
    }

    @Override
    public Type visitMarkExpr(UCELParser.MarkExprContext ctx) {
        Type type = visit(ctx.expression());

        if(type.equals(CLOCK_TYPE)) return CLOCK_TYPE;
        else {
            logger.log(new WrongTypeErrorLog(ctx.expression(), CLOCK_TYPE, type, "expression"));
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
            logger.log(new TypeErrorLog(ctx, "Array type not supported for operator"));
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
            logger.log(new IncompatibleTypeErrorLog(ctx, leftType, rightType, "binary operator"));
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
            logger.log(new TypeErrorLog(ctx, "Unsupported literal"));
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
            logger.log(new TypeErrorLog(ctx, typeEnum + " is unsupported for this unary operator"));
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
            logger.log(new TypeErrorLog(ctx, "Array type not supported for increment and decrement"));
            return ERROR_TYPE;
        }

        // Base types
        switch (typeOfVariable.getEvaluationType()) {
            case intType:
            case doubleType:
                return typeOfVariable;
            default:
                logger.log(new TypeErrorLog(ctx, typeOfVariable.getEvaluationType() + " is unsupported for increment and decrement"));
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
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(ctx, "function " + ctx.ID().getText()));
                return ERROR_TYPE;
            }
        }

        // Compare input parameter types
        Type[] declParams = funcType.getParameters();
        Type[] argsParams = argsType.getParameters();

        if(declParams.length != argsParams.length + 1) {
            logger.log(new TypeErrorLog(ctx.arguments(), String.format("Function expected %s arguments, but got %s", declParams.length-1, argsParams.length)));
            return ERROR_TYPE;
        }

        boolean argsMismatch = false;
        for (int i = 0; i < argsParams.length; i++) {
            if(!declParams[i + 1].equals(argsParams[i])) {
                logger.log(new WrongTypeErrorLog(ctx, declParams[i + 1], argsParams[i], "function argument"));
                argsMismatch = true;
            }
        }

        if(argsMismatch) {
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
        // Else if no errors, then base-type is void-type
        if(Arrays.stream(argTypes).anyMatch(t -> t.getEvaluationType() == Type.TypeEnum.errorType)) {
            return ERROR_TYPE;
        }
        else {
            return new Type(Type.TypeEnum.voidType, argTypes);
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
            logger.log(new TypeErrorLog(ctx, "Array types are unsupported for relation"));
            return ERROR_TYPE;
        }

        var isIntDoubleComparable = comparableTypes.contains(leftEnum) && comparableTypes.contains(rightEnum);
        var isIntClockComparable = comparableClockTypes.contains(leftEnum) && comparableClockTypes.contains(rightEnum);

        if (!isIntDoubleComparable && !isIntClockComparable && (leftEnum != rightEnum)) {
            logger.log(new IncompatibleTypeErrorLog(ctx, leftNode, rightNode, "relational operator"));
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
            logger.log(new IncompatibleTypeErrorLog(ctx, leftNode, rightNode, "equivalence operator"));
            return ERROR_TYPE;
        } else if (isArray(leftNode) || isArray(rightNode)) {
            logger.log(new TypeErrorLog(ctx, "Array types are unsupported for equivalence"));
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
            logger.log(new IncompatibleTypeErrorLog(ctx, left, right, "bitwise operator"));
            return ERROR_TYPE;
        } else if (isArray(left) || isArray(right)) {
            logger.log(new TypeErrorLog(ctx, "Array types are unsupported for bit operator"));
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
            logger.log(new IncompatibleTypeErrorLog(ctx, left, right, "logical operator"));
            return ERROR_TYPE;
        } else if (isArray(left) || isArray(right)) {
            logger.log(new TypeErrorLog(ctx, "Array types are unsupported for logical operator"));
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
            logger.log(new WrongTypeErrorLog(ctx.expression(0), BOOL_TYPE, condType, "condition"));
            return ERROR_TYPE;
        }

        //todo: handle void cases
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
                    logger.log(new WrongTypeErrorLog(expr, INT_TYPE, exprType, "exponential expression"));
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

        Type exprType = visit(expr);
        if(exprType.equals(BOOL_TYPE))
            return BOOL_TYPE;

        logger.log(new WrongTypeErrorLog(ctx.expression(), BOOL_TYPE, exprType, "invariant expression"));
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
            logger.log(new WrongTypeErrorLog(ctx.guard(), BOOL_TYPE, guard, "guard expressions"));
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
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(ctx, ctx.ID(i).getText()));
                return ERROR_TYPE;
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
            logger.log(new WrongTypeErrorLog(ctx.expression(), BOOL_TYPE, exprType, "guard expression"));

        return ERROR_TYPE;
    }

    @Override
    public Type visitSync(UCELParser.SyncContext ctx) {
        var expr = ctx.expression();
        if(expr == null)
            return VOID_TYPE;

        Type exprType = visit(expr);
        if(exprType.equals(CHAN_TYPE))
            return CHAN_TYPE;

        logger.log(new WrongTypeErrorLog(ctx, new ArrayList<Type>() {{add(CHAN_TYPE);add(VOID_TYPE);}}, exprType, "sync"));
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
