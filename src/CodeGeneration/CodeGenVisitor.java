import java.util.ArrayList;

public class CodeGenVisitor extends UCELBaseVisitor<Template> {


    //region Expressions
    @Override
    public Template visitAddSub(UCELParser.AddSubContext ctx) {
        var left = visit(ctx.expression(0)).getOutput();
        var right = visit(ctx.expression(1)).getOutput();

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    @Override
    public Template visitBitshift(UCELParser.BitshiftContext ctx) {
        var left = visit(ctx.expression(0)).getOutput();
        var right = visit(ctx.expression(1)).getOutput();

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    @Override
    public Template visitBitAnd(UCELParser.BitAndContext ctx) {
        var left = visit(ctx.expression(0)).getOutput();
        var right = visit(ctx.expression(1)).getOutput();

        return new BinaryExprTemplate(left, right, ctx.BITAND().getText());
    }

    @Override
    public Template visitBitXor(UCELParser.BitXorContext ctx) {
        var left = visit(ctx.expression(0)).getOutput();
        var right = visit(ctx.expression(1)).getOutput();

        return new BinaryExprTemplate(left, right, ctx.BITXOR().getText());
    }

    @Override
    public Template visitBitOr(UCELParser.BitOrContext ctx) {
        var left = visit(ctx.expression(0)).getOutput();
        var right = visit(ctx.expression(1)).getOutput();

        return new BinaryExprTemplate(left, right, ctx.BITOR().getText());
    }

    @Override
    public Template visitEqExpr(UCELParser.EqExprContext ctx) {
        var left = visit(ctx.expression(0)).getOutput();
        var right = visit(ctx.expression(1)).getOutput();

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    //endregion
}
