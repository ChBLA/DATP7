import org.antlr.v4.runtime.tree.ParseTree;

public class TypeCheckerVisitor extends UCELBaseVisitor<Type> {

    @Override
    public Type visitAddSub(UCELParser.AddSubContext ctx) {
        Type.TypeEnum leftType = visit(ctx.children.get(0)).getEvaluationType();
        Type.TypeEnum rightType = visit(ctx.children.get(1)).getEvaluationType();

        return intDoubleBinaryOp(leftType, rightType);
    }

    private Type intDoubleBinaryOp(Type.TypeEnum leftType, Type.TypeEnum rightType) {
        if(leftType == Type.TypeEnum.errorType ||
                rightType == Type.TypeEnum.errorType) {
            return new Type(Type.TypeEnum.errorType);
        } else if((leftType == Type.TypeEnum.intType ||
                leftType == Type.TypeEnum.doubleType) &&
                rightType == Type.TypeEnum.intType) {
            return new Type(leftType);
        } else if(rightType == Type.TypeEnum.doubleType &&
                (leftType == Type.TypeEnum.intType ||
                        leftType == Type.TypeEnum.doubleType)) {
            return new Type(rightType);
        } else {
            //TODO logger
            return new Type(Type.TypeEnum.errorType);
        }
    }



}
