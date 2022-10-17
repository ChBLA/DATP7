package org.UcelParser.TypeChecker;

import org.UcelParser.CodeGeneration.templates.ManualTemplate;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.UcelParser.UCELParser_Generated.*;

import org.UcelParser.Util.Type;
import org.UcelParser.Util.*;
import org.UcelParser.Util.Logging.*;

public class TypeCheckerVisitor extends UCELBaseVisitor<Type> {
    private Scope currentScope;
    private Logger logger;

    public TypeCheckerVisitor() {
            this.currentScope = null;
            this.logger = new Logger();
        }

    public TypeCheckerVisitor(Scope scope) {
            this.currentScope = scope;
            this.logger = new Logger();
        }

    public TypeCheckerVisitor(Logger logger) {
            this.currentScope = null;
            this.logger = logger;
        }

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
    private static final Type SCALAR_TYPE = new Type(Type.TypeEnum.scalarType);
    private static final Type ARRAY_TYPE = new Type(Type.TypeEnum.voidType, 1);

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
            return VOID_TYPE;
        } else {
            logger.log(new ErrorLog(ctx, "Type error: cannot assign " + rightType + " to " + leftType));
            return ERROR_TYPE;
        }
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


    public DeclarationInfo currentFunction = null;

    @Override
    public Type visitFunction(UCELParser.FunctionContext ctx) {
        try {
            var funcRef = currentScope.find(ctx.ID().getText(), false);
            var funcInfo = currentScope.get(funcRef);
            currentFunction = funcInfo;
            visit(ctx.parameters());
            visit(ctx.block());
            return funcInfo.getType();
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "ID is not in scope, and function type information unavailable"));
            return ERROR_TYPE;
        }
    }

    @Override
    public Type visitReturnstatement(UCELParser.ReturnstatementContext ctx) {
        var expression = ctx.expression();
        if (expression != null) {
            var expressionType = visit(expression);
            if (currentFunction != null && expressionType == currentFunction.getType()) {
                return expressionType;
            } else {
                logger.log(new ErrorLog(ctx, "Expression in return is of the wrong type"));
                return ERROR_TYPE;
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

        if (ctx.statement(1) != null) {
            if (visit(ctx.statement(1)).equals(ERROR_TYPE))
                return ERROR_TYPE;
            else if (visit(ctx.statement(1)).equals(VOID_TYPE))
                return VOID_TYPE;
        }
        return visit(ctx.statement(0));
    }

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

        for(ParseTree pt : ctx.children)
            if(visit(pt).equals(errorType))
                errorFound = true;

        if(errorFound) return errorType;
        else return VOID_TYPE;
    }

    @Override
    public Type visitIdExpr(UCELParser.IdExprContext ctx) {
        try {
            var variable = currentScope.get(ctx.reference);
            return variable.getType();
        } catch (Exception e) {
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
            return ERROR_TYPE;
        }
        if (!(arrayIndex.getEvaluationType() == Type.TypeEnum.intType))
            return ERROR_TYPE;

        return arrayType;
    }

    @Override
    public Type visitMarkExpr(UCELParser.MarkExprContext ctx) {
        Type type = visit(ctx.expression());

        if(type.equals(CLOCK_TYPE)) return CLOCK_TYPE;
        else return ERROR_TYPE;
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
        try {
            funcDecl = currentScope.get(ctx.reference);
            funcType = funcDecl.getType();
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Definition not found for "));
            return ERROR_TYPE;
        }

        // Compare input parameter types
        Type[] declParams = funcType.getParameters();
        String[] declNames = funcType.getParameterNames();
        Type[] argsParams = argsType.getParameters();

        if(declParams.length != argsParams.length) {
            logger.log(new ErrorLog(ctx.arguments(), String.format("Function expected {0} arguments, but got {1}", declParams.length, argsParams.length)));
            return ERROR_TYPE;
        }

        boolean argsMismatch = false;
        for (int i=0; i<declParams.length; i++) {
            if(!declParams[i].equals(argsParams[i])) {
                //Todo: Fix fancy logging (I think it's actually just the tests failing because of too tight mocking)
                //logger.log(new ErrorLog(getArgumentsContexts(ctx.arguments())[i], String.format("Parameter {0} expected argument of type {1}, but got {2}", declNames[i], declParams[i], argsParams[i])));
                argsMismatch = true;
            }
        }
        if(argsMismatch)
            return ERROR_TYPE;

        return funcType;
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
        if (leftEnum == Type.TypeEnum.errorType || rightEnum == Type.TypeEnum.errorType) {
            // Error propagated, no logging
            return ERROR_TYPE;
        } else if (!(comparableTypes.contains(leftEnum) && comparableTypes.contains(rightEnum)) && leftEnum != rightEnum) {
            logger.log(new ErrorLog(ctx, leftEnum + " and " + rightEnum + " are unsupported for relation"));
            return ERROR_TYPE;
        } else if (isArray(leftNode) || isArray(rightNode)) {
            logger.log(new ErrorLog(ctx, "Array types are unsupported for relation"));
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
                && !(leftEnum == Type.TypeEnum.doubleType && rightEnum == Type.TypeEnum.intType)) {
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

        if (leftEnum == Type.TypeEnum.errorType || rightEnum == Type.TypeEnum.errorType)
            return ERROR_TYPE;
        else if (leftEnum != Type.TypeEnum.boolType || rightEnum != Type.TypeEnum.boolType) {
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
