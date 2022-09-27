import org.antlr.v4.runtime.tree.ParseTree;

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
    public Type visitIncrementPost (UCELParser.IncrementPostContext ctx) {
        return new Type(visit(ctx.children.get(0)).getEvaluationType());
    }


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
