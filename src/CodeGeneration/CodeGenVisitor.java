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

    //region initialiser

    @Override
    public Template visitInitialiser(UCELParser.InitialiserContext ctx) {
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        }

        List<Template> initialisers = new ArrayList<>();

        for (var initialiser : ctx.initialiser()) {
            initialisers.add(visit(initialiser));
        }

        return new InitialiserTemplate(initialisers);
    }


    //endregion

    //region TypeID

    @Override
    public Template visitTypeIDID(UCELParser.TypeIDIDContext ctx) {
        ManualTemplate result;
        try {
            result = new ManualTemplate(currentScope.get(ctx.reference).getIdentifier());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public Template visitTypeIDType(UCELParser.TypeIDTypeContext ctx) {
        return new ManualTemplate(ctx.op.getText());
    }

    @Override
    public Template visitTypeIDInt(UCELParser.TypeIDIntContext ctx) {
        Template expr1 = (ctx.expression(0) != null) ?
                visit(ctx.expression(0)) :
                new ManualTemplate("");
        Template expr2 = (ctx.expression(1) != null) ?
                visit(ctx.expression(1)) :
                new ManualTemplate("");


        return new TypeIDIntTemplate(expr1, expr2);
    }

    @Override
    public Template visitTypeIDScalar(UCELParser.TypeIDScalarContext ctx) {
        return new TypeIDScalarTemplate(visit(ctx.expression()));
    }

    @Override
    public Template visitTypeIDStruct(UCELParser.TypeIDStructContext ctx) {
        ArrayList<Template> decls = new ArrayList<>();

        for (var fieldDecl : ctx.fieldDecl()) {
            decls.add(visit(fieldDecl));
        }

        return new TypeIDStructTemplate(decls);
    }

    //endregion


    //region Type

    @Override
    public Template visitType(UCELParser.TypeContext ctx) {
        Template result;

        var typeIDTemp = visit(ctx.typeId());

        if (ctx.prefix() != null) {
            var prefixTemp = visit(ctx.prefix());
            result = new TypeTemplate(prefixTemp, typeIDTemp);
        }
        else {
            result = new TypeTemplate(typeIDTemp);
        }

       return result;
    }

    //endregion

    //region variableDecl
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

    //endregion

    //region variableID
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
    //endregion

    //region arraclDecl
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
    //endregion

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

    @Override
    public Template visitVerificationExpr(UCELParser.VerificationExprContext ctx) {
        return visit(ctx.verification());
    }

    @Override
    public Template visitVerification(UCELParser.VerificationContext ctx) {
        String id = "";
        try {
            id = currentScope.get(ctx.reference).getIdentifier();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        var type = visit(ctx.type());
        var expr = visit(ctx.expression());

        return new VerificationTemplate(ctx.op.getText(), id, type, expr);
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

    //region For-loop

    @Override
    public Template visitForLoop(UCELParser.ForLoopContext ctx) {
        var assign = ctx.assignment() != null ? visit(ctx.assignment()) : new ManualTemplate("");
        var expr1 = ctx.expression(0) != null ? visit(ctx.expression(0)) : new ManualTemplate("");
        var expr2 = ctx.expression(1) != null ? visit(ctx.expression(1)) : new ManualTemplate("");
        var stmnt = visit(ctx.statement());

        return new ForLoopTemplate(assign, expr1, expr2, stmnt);
    }


    //endregion

    //region Iteration

    @Override
    public Template visitIteration(UCELParser.IterationContext ctx) {
        DeclarationInfo declarationInfo;
        try {
            declarationInfo = currentScope.get(ctx.reference);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        var typeResult = ctx.type() != null ? visit(ctx.type()) : new ManualTemplate("");
        var stmntResult = visit(ctx.statement());

        return new IterationTemplate(new ManualTemplate(declarationInfo != null ? declarationInfo.getIdentifier() : ""), typeResult, stmntResult);
    }


    //endregion

    //region Return-statement

    @Override
    public Template visitReturnstatement(UCELParser.ReturnstatementContext ctx) {
        var expr = visit(ctx.expression());

        return new ReturnStatementTemplate(expr);
    }

    //endregion

    //region Block

    @Override
    public Template visitBlock(UCELParser.BlockContext ctx) {
        List<Template> localDecls = new ArrayList<>();
        List<Template> statements = new ArrayList<>();

        for (var decl : ctx.localDeclaration()) {
            localDecls.add(visit(decl));
        }

        for (var stmnt : ctx.statement()) {
            statements.add(visit(stmnt));
        }

        return new BlockTemplate(localDecls, statements);
    }


    //endregion

    //region Statement

    @Override
    public Template visitStatement(UCELParser.StatementContext ctx) {
        Template result = new ManualTemplate(";");
        if (ctx.block() != null)
            return visit(ctx.block());
        else if (ctx.assignment() != null) {
            result = new ManualTemplate(visit(ctx.assignment()).getOutput() + ";");
        } else if (ctx.expression() != null) {
            result = new ManualTemplate(visit(ctx.expression()).getOutput() + ";");
        } else if (ctx.forLoop() != null) {
            result = visit(ctx.forLoop());
        } else if (ctx.iteration() != null) {
            result = visit(ctx.iteration());
        } else if (ctx.whileLoop() != null) {
            result = visit(ctx.whileLoop());
        } else if (ctx.dowhile() != null) {
            result = visit(ctx.dowhile());
        } else if (ctx.ifstatement() != null) {
            result = visit(ctx.ifstatement());
        } else if (ctx.returnstatement() != null) {
            result = visit(ctx.returnstatement());
        }

        return new ManualTemplate(result.getOutput() + "\n");
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
