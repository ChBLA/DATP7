import java.util.ArrayList;

public class CodeGenVisitor extends UCELBaseVisitor<Template> {


    //region Expressions


    @Override
    public Template visitArrayIndex(UCELParser.ArrayIndexContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new ArrayIndexTemplate(left, right);
    }

    public Template visitUnaryExpr(UCELParser.UnaryExprContext ctx) {
        var expr = visit(ctx.expression());
        var op = visit(ctx.unary());

        return new UnaryExprTemplate(expr, op);
    }
    
    @Override
    public Template visitLiteral(UCELParser.LiteralContext ctx) {
        return new LiteralTemplate(ctx.getText());
    }

    @Override
    public Template visitAddSub(UCELParser.AddSubContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    @Override
    public Template visitMultDiv(UCELParser.MultDivContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    @Override
    public Template visitBitshift(UCELParser.BitshiftContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    @Override
    public Template visitBitAnd(UCELParser.BitAndContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.BITAND().getText());
    }

    @Override
    public Template visitBitXor(UCELParser.BitXorContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.BITXOR().getText());
    }

    @Override
    public Template visitBitOr(UCELParser.BitOrContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.BITOR().getText());
    }

    @Override
    public Template visitEqExpr(UCELParser.EqExprContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    @Override
    public Template visitMinMax(UCELParser.MinMaxContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    @Override
    public Template visitRelExpr(UCELParser.RelExprContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    @Override
    public Template visitLogAnd(UCELParser.LogAndContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    @Override
    public Template visitLogOr(UCELParser.LogOrContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new BinaryExprTemplate(left, right, ctx.op.getText());
    }

    //endregion
}
