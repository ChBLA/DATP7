import java.awt.image.renderable.RenderableImage;
import java.util.ArrayList;

public class CodeGenVisitor extends UCELBaseVisitor<Template> {
    private Scope currentScope;

    public CodeGenVisitor() {

    }

    public CodeGenVisitor(Scope currentScope) {
        this.currentScope = currentScope;
    }





    //region Expressions


    @Override
    public Template visitIdExpr(UCELParser.IdExprContext ctx) {
        try {
            return new ManualTemplate(currentScope.get(ctx.reference).getIdentifier());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Template visitParen(UCELParser.ParenContext ctx) {
        var expr = visit(ctx.expression());

        return new ParenthesisTemplate(expr);
    }

    @Override
    public Template visitArrayIndex(UCELParser.ArrayIndexContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new ArrayIndexTemplate(left, right);
    }

    @Override
    public Template visitUnaryExpr(UCELParser.UnaryExprContext ctx) {
        var expr = visit(ctx.expression());
        var op = visit(ctx.unary());

        return new UnaryExprTemplate(op, expr);
    }

    @Override
    public Template visitIncrementPost(UCELParser.IncrementPostContext ctx) {
        var expr = visit(ctx.expression());

        return new UnaryExprTemplate(expr, new ManualTemplate(ctx.INCREMENT().getText()));
    }

    @Override
    public Template visitIncrementPre(UCELParser.IncrementPreContext ctx) {
        var expr = visit(ctx.expression());

        return new UnaryExprTemplate(new ManualTemplate(ctx.INCREMENT().getText()), expr);
    }

    @Override
    public Template visitDecrementPost(UCELParser.DecrementPostContext ctx) {
        var expr = visit(ctx.expression());

        return new UnaryExprTemplate(expr, new ManualTemplate(ctx.DECREMENT().getText()));
    }

    @Override
    public Template visitDecrementPre(UCELParser.DecrementPreContext ctx) {
        var expr = visit(ctx.expression());

        return new UnaryExprTemplate(new ManualTemplate(ctx.DECREMENT().getText()), expr);
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

    @Override
    public Template visitConditional(UCELParser.ConditionalContext ctx) {
        var condition = visit(ctx.expression(0));
        var posRes = visit(ctx.expression(1));
        var negRes = visit(ctx.expression(2));

        return new ConditionalExpressionTemplate(condition, posRes, negRes);
    }

    //endregion
}
