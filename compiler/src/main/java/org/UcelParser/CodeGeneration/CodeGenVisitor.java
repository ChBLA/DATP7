package org.UcelParser.CodeGeneration;

import org.UcelParser.CodeGeneration.templates.*;
import org.UcelParser.UCELParser_Generated.UCELBaseVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.DeclarationInfo;
import org.UcelParser.Util.DeclarationReference;
import org.UcelParser.Util.Logging.ILogger;
import org.UcelParser.Util.Logging.Logger;
import org.UcelParser.Util.Scope;
import org.UcelParser.CodeGeneration.templates.ManualTemplate;
import org.UcelParser.CodeGeneration.templates.Template;
import org.UcelParser.Util.Type;

import javax.swing.text.DefaultCaret;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeGenVisitor extends UCELBaseVisitor<Template> {
    private Scope currentScope;
    private final ILogger logger;

    public CodeGenVisitor() {
        this.logger = new Logger();
    }

    public CodeGenVisitor(Scope currentScope) {
        this.currentScope = currentScope;
        this.logger = new Logger();
    }

    public CodeGenVisitor(ILogger logger) {
        this.logger = logger;
    }

    // Update

    @Override
    public Template visitUpdate(UCELParser.UpdateContext ctx) {
        if (ctx.expression().isEmpty()) {
            return new ManualTemplate("");
        }

        List<Template> expressions = ctx.expression().stream()
                .map(this::visit).collect(Collectors.toList());

        return new UpdateTemplate(expressions);
    }


    // Sync

    @Override
    public Template visitSync(UCELParser.SyncContext ctx) {
        if (ctx.expression() == null) {
            return new ManualTemplate("");
        }

        var expr = visit(ctx.expression());
        String label;

        if (ctx.QUESTIONMARK() != null) {
            label = ctx.QUESTIONMARK().getText();
        }
        else if (ctx.NEG() != null) {
            label = ctx.NEG().getText();
        }
        else {
            // !!! This should not happen !!!
            throw new RuntimeException("internal error: label for sync does not exist");
        }

        return new SyncTemplate(expr, label);
    }


    //endregion

    // Select

    @Override
    public Template visitSelect(UCELParser.SelectContext ctx) {
        List<Template> types = ctx.type().stream().map(this::visit).collect(Collectors.toList());
        List<String> ids = new ArrayList<>();

        assert ctx.ID().size() == ctx.references.size();
        assert ctx.ID().size() == types.size();
        for (int i = 0; i < ctx.ID().size(); i++) {
            try {
                ids.add(currentScope.get(ctx.references.get(i)).getIdentifier());
            } catch (Exception e) {
                throw new RuntimeException("internal error: could not find declaration for " + ctx.ID(i).getText() + " in scope");
            }
        }

        return new SelectTemplate(ids, types);
    }

    //endregion

    // Edge
    @Override
    public Template visitEdge(UCELParser.EdgeContext ctx) {
        enterScope(ctx.scope);

        Template select = visit(ctx.select());
        Template guard = visit(ctx.guard());
        Template sync = visit(ctx.sync());
        Template update = visit(ctx.update());

        exitScope();
        return new EdgeTemplate(select, guard, sync, update, ctx);
    }
    //endregion

    // Exponential
    @Override
    public Template visitExponential(UCELParser.ExponentialContext ctx) {
        int exprCount = ctx.expression().size();
        return switch (exprCount) {
            case 1 -> new ExponentialTemplate(visit(ctx.expression(0)));
            case 2 -> new ExponentialTemplate(visit(ctx.expression(0)), visit(ctx.expression(1)));
            default -> new ManualTemplate("");
        };
    }
    //endregion

    //Invariant
    @Override
    public Template visitInvariant(UCELParser.InvariantContext ctx) {
        return ctx.expression() != null
                ? new InvariantTemplate(visit(ctx.expression()))
                : new ManualTemplate("");
    }
    //endregion

    // Location
    @Override
    public Template visitLocation(UCELParser.LocationContext ctx) {
        Template invariant = visit(ctx.invariant());
        Template exponential = visit(ctx.exponential());
        String ID = null; //TODO: get ID from somewhere
        return new LocationTemplate(invariant, exponential, ctx, ID);
    }

    //endregion

    // Graph

    @Override
    public Template visitGraph(UCELParser.GraphContext ctx) {
        List<Template> nodes = ctx.location().stream()
                .map(this::visit)
                .collect(Collectors.toList());

        List<Template> edges = ctx.edge().stream()
                .map(this::visit)
                .collect(Collectors.toList());

        return new GraphTemplate(nodes, edges);
    }
    //endregion

    //region Project Structure
    //region Project

    @Override
    public Template visitProject(UCELParser.ProjectContext ctx) {
        enterScope(ctx.scope);
        var pDeclTemplate = visit(ctx.pdeclaration());
        var pSystemTemplate = visit(ctx.psystem());

        ArrayList<PTemplateTemplate> pTemplateTemplates = new ArrayList<>();
        for (var pTemp : ctx.ptemplate()) {
            pTemplateTemplates.add((PTemplateTemplate) visit(pTemp));
        }

        exitScope();
        return new ProjectTemplate(pTemplateTemplates, pDeclTemplate, pSystemTemplate);
    }

    //endregion

    //region Project declarations

    @Override
    public Template visitPdeclaration(UCELParser.PdeclarationContext ctx) {
        return new PDeclarationTemplate(visit(ctx.declarations()));
    }


    //endregion

    //region Project system

    @Override
    public Template visitPsystem(UCELParser.PsystemContext ctx) {
        var declTemplate = visit(ctx.declarations());
        var sysTemplate = visit(ctx.system());
        var buildTemplate = ctx.build() != null ? visit(ctx.build()) : null;

        return new PSystemTemplate(declTemplate, buildTemplate, sysTemplate);
    }
    //endregion

    //region Project template

    @Override
    public Template visitPtemplate(UCELParser.PtemplateContext ctx) {
        String name;
        try {
            name = currentScope.get(ctx.reference).generateName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        enterScope(ctx.scope);
        var params = visit(ctx.parameters());
        var decls = visit(ctx.declarations());
        var graph = (GraphTemplate) visit(ctx.graph());

        exitScope();
        return new PTemplateTemplate(name, params, graph, decls);
    }

    //endregion

    //endregion

    //region Start

    @Override
    public Template visitStart(UCELParser.StartContext ctx) {
        enterScope(ctx.scope);
        var declTemplate = visit(ctx.declarations());
        var stmnts = new ArrayList<Template>();

        for (var stmnt : ctx.statement()) {
            stmnts.add(visit(stmnt));
        }

        var sysTemplate = visit(ctx.system());
        exitScope();
        return new StartTemplate(declTemplate, stmnts, sysTemplate);
    }


    //endregion

    //region System

    @Override
    public Template visitSystem(UCELParser.SystemContext ctx) {
        var exprs = new ArrayList<Template>();

        for (var expr : ctx.expression()) {
            exprs.add(visit(expr));
        }

        return new SystemTemplate(exprs);
    }

    //endregion

    //region FuncCall

    @Override
    public Template visitFuncCall(UCELParser.FuncCallContext ctx) {
        String callName;
        try {
            callName = currentScope.get(ctx.reference).generateName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var argTemplate = visit(ctx.arguments());

        return new FuncCallTemplate(callName, argTemplate);
    }


    //endregion

    //region Guard
    @Override
    public Template visitGuard(UCELParser.GuardContext ctx) {
        return ctx.expression() == null ? new ManualTemplate("") : visit(ctx.expression());
    }
    //endregion

    //region Function

    @Override
    public Template visitFunction(UCELParser.FunctionContext ctx) {
        enterScope(ctx.scope);

        List<Template> functionTemplates = new ArrayList<>();

        var type = visit(ctx.type());

        var refParams = ctx.parameters().parameter()
                .stream()
                .filter(p -> p.REF() != null)
                .collect(Collectors.toList());

        if (ctx.occurrences != null && !ctx.occurrences.isEmpty()) {
            for (int i = 0; i < ctx.occurrences.size(); i++) {
                var funcCallRefParams = ctx.occurrences.get(i).getRefParams();

                for (int k = 0; k < funcCallRefParams.length; k++) {
                    var ref = refParams.get(k).reference;
                    currentScope.replaceDeclarationInfoForRef(ref, funcCallRefParams[k]);
                }

                var parameters = visit(ctx.parameters());
                String nameOfCall = null;
                try {
                    nameOfCall = currentScope.getParent().get(ctx.occurrences.get(i).getFuncCallContext().reference).generateName();
                } catch (Exception e) {
                    throw new RuntimeException("error: could not find occurrence");
                }
                var body = visit(ctx.block());

                functionTemplates.add(new FunctionTemplate(type, nameOfCall, parameters, body));
            }
        } else {
            String ID = null;
            try {
                ID = currentScope.getParent().get(ctx.reference).getIdentifier();
            } catch (Exception e) {
                throw new RuntimeException("error: could not find function name");
            }
            var params = visit(ctx.parameters());
            var body = visit(ctx.block());
            functionTemplates.add(new FunctionTemplate(type, ID, params, body));
        }

        exitScope();
        return new FunctionsTemplate(functionTemplates);
    }

    //endregion

    //region Instantiation

    @Override
    public Template visitInstantiation(UCELParser.InstantiationContext ctx) {
        var ID1 = "";
        try {
            ID1 = currentScope.get(ctx.instantiatedReference).generateName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        enterScope(ctx.scope);
        var ID2 = "";
        try {
            ID2 = currentScope.get(ctx.constructorReference).generateName();
        } catch (Exception e) {
            exitScope();
            throw new RuntimeException(e);
        }

        boolean useParenthesis = ctx.LEFTPAR().size() > 1;
        var paramTemplate = ctx.parameters() != null ? visit(ctx.parameters()) : new ManualTemplate("");
        var argTemplate = ctx.arguments() != null ? visit(ctx.arguments()) : new ManualTemplate("");
        exitScope();

        return new InstantiationTemplate(ID1, ID2, paramTemplate, argTemplate, useParenthesis);
    }

    //endregion

    //region Declarations

    @Override
    public Template visitDeclarations(UCELParser.DeclarationsContext ctx) {
        List<Template> declarations = new ArrayList<>();

        if(ctx.children != null) {
            for (var declarationContext : ctx.children) {
                declarations.add(visit(declarationContext));
            }
        }

        return new DeclarationsTemplate(declarations);
    }


    //endregion

    //region arrayDecl
    @Override
    public Template visitArrayDeclID(UCELParser.ArrayDeclIDContext ctx) {
        List<Template> arrayDecls = new ArrayList<>();

        for (UCELParser.ArrayDeclContext arrayDecl : ctx.arrayDecl()) {
            arrayDecls.add(visit(arrayDecl));
        }

        return new ArrayDeclIDTemplate(ctx.ID().getText(), arrayDecls);
    }

    //endregion

    //region FieldDecl

    @Override
    public Template visitFieldDecl(UCELParser.FieldDeclContext ctx) {
        Template type = visit(ctx.type());
        List<Template> arrayDeclIDs = new ArrayList<>();

        for (var arrayDeclID : ctx.arrayDeclID()) {
            arrayDeclIDs.add(visit(arrayDeclID));
        }

        return new FieldDeclTemplate(type, arrayDeclIDs);
    }

    //endregion

    //region literal



    @Override
    public Template visitLiteralExpr(UCELParser.LiteralExprContext ctx) {
        return visit(ctx.literal());
    }


    //endregion

    //region boolean

    // TODO: This is probably not needed
    @Override
    public Template visitBool(UCELParser.BoolContext ctx) {
        return new ManualTemplate(ctx.getText());
    }

    //endregion

    //region Unary

    @Override
    public Template visitUnary(UCELParser.UnaryContext ctx) {
        return new ManualTemplate(ctx.getText() + (!ctx.getText().equals("not") ? "" : " "));
    }

    //endregion

    //region Arguments

    @Override
    public Template visitArguments(UCELParser.ArgumentsContext ctx) {
        List<Template> exprTemplates = new ArrayList<>();
        for (var expr : ctx.expression()) {
            exprTemplates.add(visit(expr));
        }

        return new ArgumentsTemplate(exprTemplates);
    }

    //endregion

    //region TypeDecl

    @Override
    public Template visitTypeDecl(UCELParser.TypeDeclContext ctx) {
        Template type = visit(ctx.type());
        List<Template> arrayDeclIDs = new ArrayList<>();

        assert ctx.references.size() == ctx.arrayDeclID().size();

        for (int i = 0; i < ctx.arrayDeclID().size(); i++) {
            Template arrayDeclID = visit(ctx.arrayDeclID(i));

            try {
                String identifier = currentScope.get(ctx.references.get(i)).generateName();
                arrayDeclID = arrayDeclID.replaceValue("ID", identifier);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            arrayDeclIDs.add(arrayDeclID);
        }

//        for (UCELParser.ArrayDeclIDContext arrayDeclIDContext : ctx.arrayDeclID()) {
//            arrayDeclIDs.add(visit(arrayDeclIDContext));
//        }

        return new TypeDeclTemplate(type, arrayDeclIDs);
    }

    //endregion

    //region prefix
    @Override
    public Template visitPrefix(UCELParser.PrefixContext ctx) {
        return new ManualTemplate(ctx.getText());
    }

    //endregion

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
            result = new ManualTemplate(currentScope.get(ctx.reference).generateName());
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

    //region Parameters

    @Override
    public Template visitParameter(UCELParser.ParameterContext ctx) {
        var typeTemplate = ctx.type() != null ? visit(ctx.type()) : new ManualTemplate("");
        var ampString = ctx.BITAND() != null ? ctx.BITAND().getText() : "";
        DeclarationInfo info = null;
        try {
            if (ctx.reference != null)
                info = currentScope.get(ctx.reference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var idString = info != null ? info.generateName() : "";

        var arrayTemplates = new ArrayList<Template>();

        for (var arrayDecl : ctx.arrayDecl()) {
            arrayTemplates.add(visit(arrayDecl));
        }

        return ctx.REF() != null ? new ManualTemplate("") : new ParameterTemplate(typeTemplate, ampString, idString, arrayTemplates);
    }

    @Override
    public Template visitParameters(UCELParser.ParametersContext ctx) {
        List<Template> parameterTemplates = new ArrayList<>();

        for (var param : ctx.parameter()) {
            var paramTemplate = visit(param);
            if (!(paramTemplate.toString().equals("")))
                parameterTemplates.add(paramTemplate);
        }

        return new ParametersTemplate(parameterTemplates);
    }

    //endregion

    //region Channel expressions

    @Override
    public Template visitChanExpr(UCELParser.ChanExprContext ctx) {
        if (ctx.chanExpr() == null) {
            try {
                return new ManualTemplate(currentScope.get(ctx.reference).generateName());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        var chanExpr = visit(ctx.chanExpr());
        var expr = visit(ctx.expression());

        return new ChanExprTemplate(chanExpr, expr);
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

    //region Local Declaration

    @Override
    public Template visitLocalDeclaration(UCELParser.LocalDeclarationContext ctx) {
        return ctx.typeDecl() != null ? visit(ctx.typeDecl()) : (ctx.variableDecl() != null ? visit(ctx.variableDecl()) : new ManualTemplate(""));
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
            result = new VariableIDTemplate(declarationInfo.generateName(), arrayDecals, initialiserResult);
        }
        else {
            result = new VariableIDTemplate(declarationInfo.generateName(), arrayDecals);
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
            return new ManualTemplate(currentScope.get(ctx.reference).generateName());
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
    public Template visitMarkExpr(UCELParser.MarkExprContext ctx) {
        var expr = visit(ctx.expression());

        return new MarkExpressionTemplate(expr);
    }

    @Override
    public Template visitVerificationExpr(UCELParser.VerificationExprContext ctx) {
        return visit(ctx.verification());
    }

    @Override
    public Template visitVerification(UCELParser.VerificationContext ctx) {
        String id = "";
        try {
            id = currentScope.get(ctx.reference).generateName();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        enterScope(ctx.scope);
        var type = visit(ctx.type());
        var expr = visit(ctx.expression());

        exitScope();
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

        return new IterationTemplate(new ManualTemplate(declarationInfo != null ? declarationInfo.generateName() : ""), typeResult, stmntResult);
    }


    //endregion

    //region Return-statement

    @Override
    public Template visitReturnStatement(UCELParser.ReturnStatementContext ctx) {
        var expr = ctx.expression() != null ? visit(ctx.expression()) : null;

        return expr != null ? new ReturnStatementTemplate(expr) : new ReturnStatementTemplate();
    }

    //endregion

    //region Block

    @Override
    public Template visitBlock(UCELParser.BlockContext ctx) {
        enterScope(ctx.scope);
        List<Template> localDecls = new ArrayList<>();
        List<Template> statements = new ArrayList<>();

        for (var decl : ctx.localDeclaration()) {
            localDecls.add(visit(decl));
        }

        for (var stmnt : ctx.statement()) {
            statements.add(visit(stmnt));
        }

        exitScope();
        return new BlockTemplate(localDecls, statements);
    }


    //endregion

    //region Statement

    @Override
    public Template visitStatement(UCELParser.StatementContext ctx) {
        Template result = new ManualTemplate(";");
        if (ctx.block() != null)
            return visit(ctx.block());
        else if (ctx.expression() != null) {
            result = new ManualTemplate(visit(ctx.expression()) + ";");
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
        } else if (ctx.returnStatement() != null) {
            result = visit(ctx.returnStatement());
        }

        return new ManualTemplate(result + "\n");
    }

    //endregion

    //endregion

    @Override
    public Template visitAssignExpr(UCELParser.AssignExprContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));
        var op = new ManualTemplate(ctx.assign().getText());

        return new AssignmentTemplate(left, op, right);
    }

    @Override
    public Template visitAssignment(UCELParser.AssignmentContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));
        var op = new ManualTemplate(ctx.assign().getText());

        return new AssignmentTemplate(left, op, right);
    }

    //region Helper functions
    private void enterScope(Scope scope) {
        currentScope = scope;
    }

    private void exitScope() {
        this.currentScope = this.currentScope.getParent();
    }
    //endregion
}
