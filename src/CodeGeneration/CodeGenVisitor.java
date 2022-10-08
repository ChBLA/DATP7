import java.awt.image.renderable.RenderableImage;
import java.util.ArrayList;
import java.util.List;

public class CodeGenVisitor extends UCELBaseVisitor<Template> {
    private Scope currentScope;

    public CodeGenVisitor() {

    }

    public CodeGenVisitor(Scope currentScope) {
        this.currentScope = currentScope;
    }

    @Override
    public Template visitVariableDecl(UCELParser.VariableDeclContext ctx) {
        Template result;

        List<Template> variableIDTemplates = new ArrayList<>();

        for (var variableID : ctx.variableID()) {
            variableIDTemplates.add(visit(variableID));
        }

        if (ctx.type() != null) {
            Template typeTemplate = visit(ctx.type());
            result = new VariableDeclTemplate(typeTemplate, variableIDTemplates);
        } else {
            result = new VariableDeclTemplate(variableIDTemplates);
        }

        return result;
    }

    @Override
    public Template visitVariableID(UCELParser.VariableIDContext ctx) {
        Template result;
        DeclarationInfo declarationInfo;
        List<Template> arrayDecals = new ArrayList<>();
        try {
            declarationInfo = currentScope.get(ctx.reference);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        for (int i = 0; i < ctx.arrayDecl().size(); i++) {
            arrayDecals.add(visit(ctx.arrayDecl(i)));
        }

        if (ctx.initialiser() != null) {
            Template initialiserResult = visit(ctx.initialiser());
            result = new VariableIDTemplate(declarationInfo.getIdentifier(), arrayDecals, initialiserResult);
        }
        else {
            result = new VariableIDTemplate(declarationInfo.getIdentifier(), arrayDecals);
        }

        return result;
    }

    @Override
    public Template visitArrayDecl(UCELParser.ArrayDeclContext ctx) {

        Template result;

        if (ctx.expression() != null) {
            Template exprTemplate = visit(ctx.expression());
            result = new ArrayDeclTemplate(exprTemplate);
        }
        else if (ctx.type() != null){
            Template typeTemplate = visit(ctx.type());
            result = new ArrayDeclTemplate(typeTemplate);
        } else {
            result = new ArrayDeclTemplate();
        }

        return result;
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

    //region Control structures
    //region If-statement

    @Override
    public Template visitIfstatement(UCELParser.IfstatementContext ctx) {
        var expr = visit(ctx.expression());
        var stmnt1 = visit(ctx.statement(0));
        var stmnt2 = ctx.statement(1) != null ? visit(ctx.statement(1)) : null;

        return stmnt2 != null ? new IfStatementTemplate(expr, stmnt1, stmnt2) : new IfStatementTemplate(expr, stmnt1);
    }
    //endregion

    //region While-loop

    @Override
    public Template visitWhileLoop(UCELParser.WhileLoopContext ctx) {
        var expr = visit(ctx.expression());
        var stmnt = visit(ctx.statement());

        return new WhileLoopTemplate(expr, stmnt);
    }

    //endregion

    //region Do-while-loop

    @Override
    public Template visitDowhile(UCELParser.DowhileContext ctx) {
        var expr = visit(ctx.expression());
        var stmnt = visit(ctx.statement());

        return new DoWhileLoopTemplate(expr, stmnt);
    }

    //endregion

    //endregion

    @Override
    public Template visitAssignExpr(UCELParser.AssignExprContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new AssignmentTemplate(left, right);
    }
}
