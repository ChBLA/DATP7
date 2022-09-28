import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class TypeCheckerVisitor extends UCELBaseVisitor<Type> {

    @Override
    public Type visitAddSub(UCELParser.AddSubContext ctx) {
        Type leftType = visit(ctx.children.get(0));
        Type rightType = visit(ctx.children.get(1));

        return intDoubleBinaryOp(leftType, rightType);
    }

    private boolean isArray(Type t) {
        return t.getArrayDimensions() > 0;
    }

    private Type intDoubleBinaryOp(Type leftType, Type rightType) {
        if(isArray(leftType) || isArray(rightType)) {
            //TODO logger
            return new Type(Type.TypeEnum.errorType);
        }

        Type.TypeEnum leftEnum = leftType.getEvaluationType();
        Type.TypeEnum rightEnum = rightType.getEvaluationType();

        if(leftEnum == Type.TypeEnum.errorType ||
                rightEnum == Type.TypeEnum.errorType) {
            return new Type(Type.TypeEnum.errorType);
        } else if((leftEnum == Type.TypeEnum.intType ||
                leftEnum == Type.TypeEnum.doubleType) &&
                rightEnum == Type.TypeEnum.intType) {
            return new Type(leftEnum);
        } else if(rightEnum == Type.TypeEnum.doubleType &&
                (leftEnum == Type.TypeEnum.intType ||
                        leftEnum == Type.TypeEnum.doubleType)) {
            return new Type(rightEnum);
        } else {
            //TODO logger
            return new Type(Type.TypeEnum.errorType);
        }
    }

    //region Increment/Decrement
    @Override
    public Type visitIncrementPost(UCELParser.IncrementPostContext ctx) {
        return visitIncrementDecrement(ctx);
    }
    @Override
    public Type visitIncrementPre(UCELParser.IncrementPreContext ctx) {
        return visitIncrementDecrement(ctx);
    }
    @Override
    public Type visitDecrementPost(UCELParser.DecrementPostContext ctx) {
        return visitIncrementDecrement(ctx);
    }
    @Override
    public Type visitDecrementPre(UCELParser.DecrementPreContext ctx) {
        return visitIncrementDecrement(ctx);
    }
    private Type visitIncrementDecrement (UCELParser.ExpressionContext ctx) {
        Type typeOfVariable = visit(ctx.children.get(0));

        // Array
        if(isArray(typeOfVariable))
            return new Type(Type.TypeEnum.errorType);

        // Base types
        switch (typeOfVariable.getEvaluationType()) {
            case intType:
            case doubleType:
                return typeOfVariable;
            default:
                return new Type(Type.TypeEnum.errorType);
        }
    }
    //endregion

    //region Relational/Equality expressions
    @Override
    public Type visitRelExpr(UCELParser.RelExprContext ctx) {
        Type leftNode = visit(ctx.children.get(0));
        Type rightNode = visit(ctx.children.get(1));

        Type.TypeEnum leftEnum = leftNode.getEvaluationType();
        Type.TypeEnum rightEnum = rightNode.getEvaluationType();

        List<Type.TypeEnum> comparableTypes = new ArrayList<>() {{ add(Type.TypeEnum.intType); add(Type.TypeEnum.boolType); add(Type.TypeEnum.doubleType);}};
        if (!(comparableTypes.contains(leftEnum) && comparableTypes.contains(rightEnum)) && leftEnum != rightEnum) {
            //TODO: Log: No coercion between left and right, and right and left are not same type
            return new Type(Type.TypeEnum.errorType);
        } else if (isArray(leftNode) || isArray(rightNode)) {
            //TODO: Log: Either right or left is an array
            return new Type(Type.TypeEnum.errorType);
        }

        return new Type(Type.TypeEnum.boolType);
    }

    //endregion

    //region Logical expressions
    @Override
    public Type visitLogAnd(UCELParser.LogAndContext ctx) {
        Type leftNode = visit(ctx.children.get(0));
        Type rightNode = visit(ctx.children.get(1));

        return logicalExpressionDetermineType(leftNode, rightNode);
    }

    @Override
    public Type visitLogOr(UCELParser.LogOrContext ctx) {
        Type leftNode = visit(ctx.children.get(0));
        Type rightNode = visit(ctx.children.get(1));

        return logicalExpressionDetermineType(leftNode, rightNode);
    }

    private Type logicalExpressionDetermineType(Type left, Type right) {
        Type.TypeEnum leftEnum = left.getEvaluationType();
        Type.TypeEnum rightEnum = right.getEvaluationType();

        if (leftEnum == Type.TypeEnum.errorType || rightEnum == Type.TypeEnum.errorType)
            return new Type(Type.TypeEnum.errorType);
        else if (leftEnum != Type.TypeEnum.boolType || rightEnum != Type.TypeEnum.boolType) {
            // Log: left or right not bool-type
            return new Type(Type.TypeEnum.errorType);
        } else if (isArray(left) || isArray(right)) {
            // Log: either left or right is an array
            return new Type(Type.TypeEnum.errorType);
        }

        return new Type(Type.TypeEnum.boolType);
    }

    //endregion

}
