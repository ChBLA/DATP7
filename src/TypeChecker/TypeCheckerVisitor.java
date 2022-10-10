import com.sun.jdi.BooleanType;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public Type visitReturnstatement(UCELParser.ReturnstatementContext ctx) {
        var expression = ctx.expression();
        if (expression != null) {
            var expressionType = visit(expression);
            return expressionType;
        } else {
            return VOID_TYPE;
        }
    }




    @Override
    public Type visitWhileLoop(UCELParser.WhileLoopContext ctx) {
        Type condType = visit(ctx.expression());
        Type statementType = null;

        if (!condType.equals(BOOL_TYPE)) return ERROR_TYPE;

        statementType = visit(ctx.statement());

        return statementType;
    }
    @Override
    public Type visitBlock(UCELParser.BlockContext ctx) {

        Type commonType = null;
        Type errorType = new Type(Type.TypeEnum.errorType);
        Type voidType = new Type(Type.TypeEnum.voidType);
        enterScope(ctx.scope);

        for(UCELParser.LocalDeclarationContext ldc : ctx.localDeclaration()) {
            Type declType = visit(ldc);
            if(declType.equals(errorType))
                commonType = declType;
        }

        if(commonType != null && commonType.equals(errorType)) {
            //No logging just passing the error up
            exitScope();
            return errorType;
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
                return errorType;
            }
            if(!(hasFoundError)) {
                if(statementType.equals(errorType)) {
                    hasFoundError = true;
                } else if(!statementType.equals(voidType)) {
                    hasFoundType = true;
                    commonType = statementType;
                }
            }
        }

        exitScope();

        if(hasFoundError) return errorType;
        else if(commonType == null) return voidType;
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
        Type intType = new Type(Type.TypeEnum.intType);
        Type doubleType = new Type(Type.TypeEnum.doubleType);
        if(ctx.type() != null) declaredType = visit(ctx.type());

        boolean errorFound = false;

        for(UCELParser.VariableIDContext varID : ctx.variableID()) {
            Type varIDType = visit(varID);
            if(declaredType != null && !varIDType.equalsOrIsArrayOf(declaredType)) {
                if(declaredType.equals(doubleType) &&
                    varIDType.equals(intType)) {

                    try {
                        DeclarationInfo declInfo = currentScope.get(varID.reference);
                        declInfo.setType(doubleType);
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

        if(errorFound) return new Type(Type.TypeEnum.errorType);
        else return new Type(Type.TypeEnum.voidType);
    }

    @Override
    public Type visitVariableID(UCELParser.VariableIDContext ctx) {
        Type initialiserType = ctx.initialiser() != null ?
                visit(ctx.initialiser()) : new Type(Type.TypeEnum.voidType);
        Type errorType = new Type(Type.TypeEnum.errorType);

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
        Type voidType =  new Type(Type.TypeEnum.voidType);
        if(arrayDim == 0) return type;
        if(type.equals(voidType)) return voidType.deepCopy(arrayDim);
        if(type.getEvaluationType() != Type.TypeEnum.structType) {
            logger.log(new ErrorLog(ctx, "Array declaration does not match initializer"));
        }
        Type internalType = null;
        for(Type t : type.getParameters()) {
            Type paramType = structToArray(ctx, t, arrayDim - 1);
            if(internalType != null && !internalType.equals(paramType)) {
                logger.log(new ErrorLog(ctx, "Array initializer cannot contain both " +
                        paramType + " and " + internalType));
                return new Type(Type.TypeEnum.errorType);
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
        return super.visitAssign(ctx);
    }

    @Override
    public Type visitStatement(UCELParser.StatementContext ctx) {
        return super.visitStatement(ctx);
    }

    @Override
    public Type visitDeclarations(UCELParser.DeclarationsContext ctx) {
        boolean errorFound = false;
        Type errorType = new Type(Type.TypeEnum.errorType);

        for(ParseTree pt : ctx.children)
            if(visit(pt).equals(errorType))
                errorFound = true;

        if(errorFound) return errorType;
        else return new Type(Type.TypeEnum.voidType);
    }

    @Override
    public Type visitIdExpr(UCELParser.IdExprContext ctx) {
        try {
            var variable = currentScope.get(ctx.reference);
            return variable.getType();
        } catch (Exception e) {
            return new Type(Type.TypeEnum.errorType);
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
            //TODO logger
            return new Type(Type.TypeEnum.errorType);
        }

        for (int i = 0; i < parameterNames.length; i++) {
            if(parameterNames[i].equals(identifier)) {
                ctx.reference = new DeclarationReference(-1, i);
                return parameterTypes[i];
            }
        }
        //TODO logger not found
        return new Type(Type.TypeEnum.errorType);
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
            return new Type(Type.TypeEnum.errorType);
        }
        if (!(arrayIndex.getEvaluationType() == Type.TypeEnum.intType))
            return new Type(Type.TypeEnum.errorType);

        return arrayType;
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
            return new Type(Type.TypeEnum.errorType);
        }

        Type.TypeEnum leftEnum = leftType.getEvaluationType();
        Type.TypeEnum rightEnum = rightType.getEvaluationType();

        // Error || Error -> Error
        if(leftEnum == Type.TypeEnum.errorType || rightEnum == Type.TypeEnum.errorType)
            return new Type(Type.TypeEnum.errorType);

        // Same types: int || int -> int
        else if(leftEnum == Type.TypeEnum.intType && rightEnum == Type.TypeEnum.intType)
            return new Type(Type.TypeEnum.intType);

        // Same types: double || double -> double
        else if(leftEnum == Type.TypeEnum.doubleType && rightEnum == Type.TypeEnum.doubleType)
            return new Type(Type.TypeEnum.doubleType);

        // Mixed types: double || int -> double
        else if(leftEnum == Type.TypeEnum.doubleType && rightEnum == Type.TypeEnum.intType)
            return new Type(Type.TypeEnum.doubleType);

        // Mixed types: int || double -> double
        else if(leftEnum == Type.TypeEnum.intType && rightEnum == Type.TypeEnum.doubleType)
            return new Type(Type.TypeEnum.doubleType);

        else {
            logger.log(new ErrorLog(ctx, "The types " + leftType.toString() + " and " +
                                        leftType.toString() + " are not supported for operator"));
            return new Type(Type.TypeEnum.errorType);
        }
    }


    @Override
    public Type visitLiteral(UCELParser.LiteralContext ctx) {
        Type type;
        if (ctx.NAT() != null) {
            type = new Type(Type.TypeEnum.intType);
        } else if (ctx.DOUBLE() != null) {
            type = new Type(Type.TypeEnum.doubleType);
        } else if (ctx.boolean_() != null) {
            type = new Type(Type.TypeEnum.boolType);
        } else {
            logger.log(new ErrorLog(ctx, "Unsupported literal"));
            type = new Type(Type.TypeEnum.errorType);
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
            evaluationType = new Type(Type.TypeEnum.errorType);
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
            return new Type(Type.TypeEnum.errorType);
        }

        // Base types
        switch (typeOfVariable.getEvaluationType()) {
            case intType:
            case doubleType:
                return typeOfVariable;
            default:
                logger.log(new ErrorLog(ctx, typeOfVariable.getEvaluationType() + " is unsupported for increment and decrement"));
                return new Type(Type.TypeEnum.errorType);
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
            return new Type(Type.TypeEnum.errorType);
        }

        // Compare input parameter types
        Type[] declParams = funcType.getParameters();
        String[] declNames = funcType.getParameterNames();
        Type[] argsParams = argsType.getParameters();

        if(declParams.length != argsParams.length) {
            logger.log(new ErrorLog(ctx.arguments(), String.format("Function expected {0} arguments, but got {1}", declParams.length, argsParams.length)));
            return new Type(Type.TypeEnum.errorType);
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
            return new Type(Type.TypeEnum.errorType);

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
            return new Type(Type.TypeEnum.errorType);
        } else if (!(comparableTypes.contains(leftEnum) && comparableTypes.contains(rightEnum)) && leftEnum != rightEnum) {
            logger.log(new ErrorLog(ctx, leftEnum + " and " + rightEnum + " are unsupported for relation"));
            return new Type(Type.TypeEnum.errorType);
        } else if (isArray(leftNode) || isArray(rightNode)) {
            logger.log(new ErrorLog(ctx, "Array types are unsupported for relation"));
            return new Type(Type.TypeEnum.errorType);
        }

        return new Type(Type.TypeEnum.boolType);
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
            return new Type(Type.TypeEnum.errorType);
        } else if (!(comparableTypes.contains(leftEnum) && leftEnum == rightEnum)
                && !(leftEnum == Type.TypeEnum.intType && rightEnum == Type.TypeEnum.doubleType)
                && !(leftEnum == Type.TypeEnum.doubleType && rightEnum == Type.TypeEnum.intType)) {
            logger.log(new ErrorLog(ctx, leftEnum + " and " + rightEnum + " are unsupported for equivalence"));
            return new Type(Type.TypeEnum.errorType);
        } else if (isArray(leftNode) || isArray(rightNode)) {
            logger.log(new ErrorLog(ctx, "Array types are unsupported for equivalence"));
            return new Type(Type.TypeEnum.errorType);
        }

        return new Type(Type.TypeEnum.boolType);
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
            return new Type(Type.TypeEnum.errorType);
        } else if (!(leftEnum == Type.TypeEnum.intType && rightEnum == Type.TypeEnum.intType)) {
            logger.log(new ErrorLog(ctx, leftEnum + " and " + rightEnum + " are unsupported for bit operator"));
            return new Type(Type.TypeEnum.errorType);
        } else if (isArray(left) || isArray(right)) {
            logger.log(new ErrorLog(ctx, "Array types are unsupported for bit operator"));
            return new Type(Type.TypeEnum.errorType);
        }

        return new Type(Type.TypeEnum.intType);
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
            return new Type(Type.TypeEnum.errorType);
        else if (leftEnum != Type.TypeEnum.boolType || rightEnum != Type.TypeEnum.boolType) {
            logger.log(new ErrorLog(ctx, leftEnum + " and " + rightEnum + " are unsupported for logical operator"));
            return new Type(Type.TypeEnum.errorType);
        } else if (isArray(left) || isArray(right)) {
            logger.log(new ErrorLog(ctx, "Array types are unsupported for logical operator"));
            return new Type(Type.TypeEnum.errorType);
        }

        return new Type(Type.TypeEnum.boolType);
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
            return new Type(Type.TypeEnum.errorType);
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
