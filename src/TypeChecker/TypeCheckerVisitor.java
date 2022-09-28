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
            type = new Type(Type.TypeEnum.errorType);
        }

        return type;
    }

    @Override
    public Type visitIncrementPost (UCELParser.IncrementPostContext ctx) {
        return new Type(visit(ctx.children.get(0)).getEvaluationType());
    }


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

    @Override
    public Type visitFuncCall(UCELParser.FuncCallContext ctx) {

        return super.visitFuncCall(ctx);
    }

    @Override
    public Type visitArguments(UCELParser.ArgumentsContext ctx) {

        return super.visitArguments(ctx);
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
