package org.UcelParser.CodeGeneration;

import com.sun.source.tree.ArrayAccessTree;
import org.UcelParser.CodeGeneration.templates.*;
import org.UcelParser.UCELParser_Generated.UCELBaseVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.*;
import org.UcelParser.Util.Exception.CouldNotFindException;
import org.UcelParser.Util.Logging.*;
import org.UcelParser.CodeGeneration.templates.ManualTemplate;
import org.UcelParser.CodeGeneration.templates.Template;
import org.UcelParser.Util.Value.IntegerValue;
import org.UcelParser.Util.Value.InterfaceValue;
import org.UcelParser.Util.Value.ListValue;
import org.stringtemplate.v4.ST;

import javax.swing.text.DefaultCaret;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CodeGenVisitor extends UCELBaseVisitor<Template> {
    private Scope currentScope;
    private final ILogger logger;
    public String componentPrefix = "";
    public int depthFromComponentScope = 0;
    private int counter = 0;
    private List<InterfaceTemplate> interfaces = new ArrayList<>();
    private boolean inVerificationMode = false;
    private List<Integer> arrayIndices;
    private Occurrence globalOccurrence;
    private Boolean hasRecursion = false;

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

    //region Component Extension
    //region Component


    @Override
    public Template visitComponent(UCELParser.ComponentContext ctx) {
        return new ManualTemplate("");
    }

    public Template visitComponentOccurrence(UCELParser.ComponentContext ctx, ComponentOccurrence occurrence) {
        enterScope(ctx.scope);
        String prevPrefix = componentPrefix;
        this.componentPrefix = occurrence.getPrefix() + (hasRecursion ? "_" + this.counter++ : "");
        this.depthFromComponentScope = 0;

        ArrayList<Template> parameters = new ArrayList<>();
        ArrayList<Template> interfaces = new ArrayList<>();
        Template compBodyTemplate;
        if (ctx.parameters() != null) {
            for (int i = 0; i < ctx.parameters().parameter().size(); i++) {
                UCELParser.ParameterContext paramNode = ctx.parameters().parameter().get(i);
                Type paramNodeType;
                try {
                    paramNodeType = currentScope.get(paramNode.reference).getType();
                } catch (CouldNotFindException e) {
                    logger.log(new MissingReferenceErrorLog(paramNode, paramNode.ID().getText()));
                    exitScope();
                    return new ManualTemplate("");
                }
                if (paramNode.REF() != null) {
                    //TODO: Implement when component with references
                } else if (!paramNodeType.getEvaluationType().equals(Type.TypeEnum.interfaceType) && !paramNodeType.getEvaluationType().equals(Type.TypeEnum.chanType)) {
                    Template paramTemplate = visit(paramNode);
                    String actualParameter = occurrence.getParameters()[i].generateName("");
                    ManualTemplate paramDeclaration = new ManualTemplate(String.format("%s = %s;", paramTemplate, actualParameter));
                    parameters.add(paramDeclaration);
                }
            }
        }

        compBodyTemplate = visit(ctx.compBody());

        ArrayList<Template> subComps = new ArrayList<>();
        for (var child : occurrence.getChildren()) {
            if (child instanceof ComponentOccurrence) {
                var subComp = visitComponentOccurrence(((ComponentOccurrence) child).getNode(), (ComponentOccurrence) child);
                if (!subComp.toString().isEmpty())
                    subComps.add(subComp);
            }
        }


        exitScope();
        var result = new ComponentTemplate(this.componentPrefix, parameters, interfaces, subComps, compBodyTemplate);
        this.componentPrefix = prevPrefix;

        return result;
    }
    //endregion

    //region Component body

    @Override
    public Template visitCompBody(UCELParser.CompBodyContext ctx) {
        enterScope(ctx.scope);
        var result = ctx.declarations() != null ? visit(ctx.declarations()) : new ManualTemplate("");
        exitScope();
        return result;
    }


    //endregion

    //region Interfaces

    @Override
    public Template visitInterfaceDecl(UCELParser.InterfaceDeclContext ctx) {
        String id;
        try {
            id = currentScope.get(ctx.reference).generateName(this.componentPrefix);
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return new ManualTemplate("");
        }

        ArrayList<Template> interfaceTemplates = new ArrayList<>();
        if (ctx.occurrences != null) {
            for (var occ : ctx.occurrences) {
                String occId = occ.generateName();
                this.componentPrefix = String.format("%s_%s", id, occId);
                this.depthFromComponentScope = -1;
                occ.setValue(id + "_");

                var occRes = visit(ctx.interfaceVarDecl());
                interfaceTemplates.add(occRes);
            }
        }
        this.componentPrefix = "";

        this.interfaces.add(new InterfaceTemplate(interfaceTemplates));
        return new ManualTemplate("");
    }

    @Override
    public Template visitInterfaceVarDecl(UCELParser.InterfaceVarDeclContext ctx) {
        String fieldPrefix = this.componentPrefix + "_";

        ArrayList<Template> types = new ArrayList<>();
        ArrayList<Template> arrayIDDecls = new ArrayList<>();

        for (int i = 0; i < ctx.arrayDeclID().size(); i++) {
            types.add(visit(ctx.type(i)));
            List<Template> arrayDecls = new ArrayList<>();

            if (ctx.arrayDeclID(i).arrayDecl() != null) {
                for (var arrayDecl : ctx.arrayDeclID(i).arrayDecl()) {
                    arrayDecls.add(visit(arrayDecl));
                }
            }
            arrayIDDecls.add(new ArrayDeclIDTemplate(fieldPrefix + ctx.arrayDeclID(i).ID().getText(), arrayDecls));
        }

        return new InterfaceFieldsTemplate(types, arrayIDDecls);
    }

    //endregion

    //endregion

    //region Verification and queries

    @Override
    public Template visitBoundQuery(UCELParser.BoundQueryContext ctx) {
        List<Template> exprs = new ArrayList<>();

        for (var expr : ctx.expression()) {
            exprs.add(visit(expr));
        }

        return new BoundQueryTemplate(ctx.op.getText(), exprs);
    }

    @Override
    public Template visitBoundSetQuery(UCELParser.BoundSetQueryContext ctx) {
        List<Template> exprs = new ArrayList<>();

        for (UCELParser.ExpressionContext expr : ctx.expression()) {
            exprs.add(visit(expr));
        }
        Template specialExpr = exprs.remove(0);
        return new BoundQueryTemplate(ctx.op.getText(), specialExpr, exprs);
    }

    @Override
    public Template visitImplicationQuery(UCELParser.ImplicationQueryContext ctx) {
        return new ImplicationQueryTemplate(visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public Template visitPQuery(UCELParser.PQueryContext ctx) {
        enterScope(ctx.scope);
        var result = ctx.symbQuery() != null ? visit(ctx.symbQuery()) : new ManualTemplate("");
        exitScope();
        return new PQueryTemplate(result, String.format("%s%n%n%s", ctx.getText(), ctx.comment));
    }

    @Override
    public Template visitQuantifierQuery(UCELParser.QuantifierQueryContext ctx) {
        return new QuantifierQueryTemplate(ctx.op.getText(), visit(ctx.expression()));
    }

    @Override
    public Template visitVerificationList(UCELParser.VerificationListContext ctx) {
        this.inVerificationMode = true;

        List<PQueryTemplate> queries = new ArrayList<>();

        for (var query : ctx.pQuery()) {
            queries.add((PQueryTemplate) visit(query));
        }

        return new VerificationListTemplate(queries);
    }

    //endregion

    //region Update

    @Override
    public Template visitUpdate(UCELParser.UpdateContext ctx) {
        if (ctx.expression().isEmpty()) {
            return new ManualTemplate("");
        }

        List<Template> expressions = ctx.expression().stream()
                .map(this::visit).collect(Collectors.toList());

        return new UpdateTemplate(expressions);
    }
    //endregion

    //region Sync

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

    //region Select

    @Override
    public Template visitSelect(UCELParser.SelectContext ctx) {
        List<Template> types = ctx.type().stream().map(this::visit).collect(Collectors.toList());
        List<String> ids = new ArrayList<>();

        assert ctx.ID().size() == ctx.references.size();
        assert ctx.ID().size() == types.size();
        for (int i = 0; i < ctx.ID().size(); i++) {
            try {
                ids.add(currentScope.get(ctx.references.get(i)).getIdentifier());
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(ctx, ctx.ID(i).getText()));
                return new ManualTemplate("");
            }
        }

        return new SelectTemplate(ids, types);
    }

    //endregion

    //region Edge
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

    //region Exponential
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

    //region Invariant
    @Override
    public Template visitInvariant(UCELParser.InvariantContext ctx) {
        return ctx.expression() != null
                ? new InvariantTemplate(visit(ctx.expression()))
                : new ManualTemplate("");
    }
    //endregion

    //region Location
    @Override
    public Template visitLocation(UCELParser.LocationContext ctx) {
        Template invariant = visit(ctx.invariant());
        Template exponential = visit(ctx.exponential());
        String ID = ctx.ID().getText();
        return new LocationTemplate(invariant, exponential, ctx, ID);
    }

    //endregion

    //region Graph

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
        this.globalOccurrence = ctx.occurence;
        this.hasRecursion = ctx.hasRecursion;

        enterScope(ctx.scope);
        PDeclarationTemplate pDeclTemplate = (PDeclarationTemplate) visit(ctx.pdeclaration());
        PSystemTemplate pSystemTemplate = (PSystemTemplate) visit(ctx.psystem());
        exitScope();
        for (var child : globalOccurrence.getChildren()) {
            if (child instanceof ComponentOccurrence)
                pSystemTemplate.comps.add(visitComponentOccurrence(((ComponentOccurrence) child).getNode(), (ComponentOccurrence) child));
        }

        enterScope(ctx.scope);
        ArrayList<PTemplateTemplate> pTemplateTemplates = new ArrayList<>();
        for (var pTemp : ctx.ptemplate()) {
            PTemplateTemplate template = (PTemplateTemplate) visit(pTemp);
            pTemplateTemplates.add(template);
            if (template.namesForSysDeclarations.size() > 0) {
                pSystemTemplate.system.template.add("decls", System.lineSeparator() + "// Declaration of all processes of type "
                        + template.name + System.lineSeparator() + template.sysDeclarations);
                pSystemTemplate.system.template.add("names", template.namesForSysDeclarations);
            }
        }
        pSystemTemplate.finalise();

        pDeclTemplate.template.add("decls", this.interfaces);

        var verificationsTemplate = visit(ctx.verificationList());

        exitScope();
        return new ProjectTemplate(pTemplateTemplates, pDeclTemplate, pSystemTemplate, verificationsTemplate);
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
        var buildSystemTemplate = ctx.build() != null ? new SystemTemplate(new ArrayList<>(), new ArrayList<>()) : visit(ctx.system());

        return new PSystemTemplate(declTemplate, buildSystemTemplate);
    }
    //endregion

    //region Project template

    @Override
    public Template visitPtemplate(UCELParser.PtemplateContext ctx) {
        String name;
        try {
            name = currentScope.get(ctx.reference).generateName();
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return new ManualTemplate("");
        }

        enterScope(ctx.scope);
        var params = visit(ctx.parameters());
        var decls = visit(ctx.declarations());
        var graph = (GraphTemplate) visit(ctx.graph());
        exitScope();

        // Make the instantiations for all template occurrences
        ArrayList<String> namesForInstans = new ArrayList<>();
        ArrayList<Template> declarations = new ArrayList<>();
        if (ctx.occurrences != null && ctx.occurrences.size() > 0) {
            for (var occ : ctx.occurrences) {
                String occName = occ.getPrefix() + "_" + this.counter++;
                occ.setPrefix(occName);
                ST constructorCall = new ST("<cons>(<exprs; separator=\", \">)");
                constructorCall.add("cons", name);
                for (var param : occ.getParameters()) {
                    if (param instanceof InterfaceValue) {
                        InterfaceValue interfaceParam = (InterfaceValue) param;
                        for (var interfaceField : interfaceParam.getInterfaceNode().interfaceVarDecl().arrayDeclID()) {
                            constructorCall.add("exprs", param.generateName() + "_" + interfaceField.ID().getText());
                        }
                    } else if (param instanceof ListValue) {
                        ListValue listValue = (ListValue) param;
                        for (int i = 0; i < listValue.size(); i++) {
                            if (listValue.getValue(i) instanceof InterfaceValue) {
                                InterfaceValue interfaceParam = (InterfaceValue) listValue.getValue(i);
                                for (var interfaceField : interfaceParam.getInterfaceNode().interfaceVarDecl().arrayDeclID()) {
                                    constructorCall.add("exprs", interfaceParam.generateName() + "_" + interfaceField.ID().getText() + "_" + i);
                                }
                            } else {
                                constructorCall.add("exprs", param.generateName());
                            }
                        }
                    } else {
                        constructorCall.add("exprs", param.generateName());
                    }
                }
                Template occDecl = new ManualTemplate(String.format("%s = %s;", occName, constructorCall.render()));

                namesForInstans.add(occName);
                declarations.add(occDecl);
            }
        }
        Template sysDecls = new DeclarationsTemplate(declarations);
        return new PTemplateTemplate(name, params, graph, decls, sysDecls, namesForInstans);
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
        var constructions = new ArrayList<Template>();
        var names = new ArrayList<String>();

        for (int i = 0; i < ctx.expression().size(); i++) {
            if (ctx.expression(i) instanceof UCELParser.FuncCallContext) {
                try {
                    constructions.add(visit(ctx.expression(i)));
                    String templateName = currentScope.get(ctx.expression(i).reference).generateName();
                    names.add(templateName + "_" + i);
                } catch (CouldNotFindException e) {
                    logger.log(new MissingReferenceErrorLog(ctx.expression(i), ((UCELParser.FuncCallContext) ctx.expression(i)).ID().getText()));
                }
            } else {
                names.add(visit(ctx.expression(i)).toString());
                constructions.add(new ManualTemplate(""));
            }
        }

        return new SystemTemplate(constructions, names);
    }

    //endregion

    //region FuncCall

    @Override
    public Template visitFuncCall(UCELParser.FuncCallContext ctx) {
        String callName;
        try {
            callName = currentScope.get(ctx.reference).generateName(getComponentPrefix(ctx.reference.getRelativeScope()));
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return new ManualTemplate("");
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
                    DeclarationReference callRef = ctx.occurrences.get(i).getFuncCallContext().reference;
                    nameOfCall = currentScope.getParent().get(callRef).generateName(getComponentPrefix(callRef.getRelativeScope()));
                } catch (CouldNotFindException e) {
                    logger.log(new MissingReferenceErrorLog(ctx.occurrences.get(i).getFuncCallContext(), ctx.occurrences.get(i).getFuncCallContext().ID().getText()));
                    return new ManualTemplate("");
                }
                var body = visit(ctx.block());

                functionTemplates.add(new FunctionTemplate(type, nameOfCall, parameters, body));
            }
        } else {
            String ID = null;
            try {
                ID = currentScope.getParent().get(ctx.reference).generateName(getComponentPrefix(ctx.reference.getRelativeScope()));
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
                return new ManualTemplate("");
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
            ID1 = currentScope.get(ctx.instantiatedReference).generateName(getComponentPrefix(ctx.instantiatedReference.getRelativeScope()));
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID(0).getText()));
            return new ManualTemplate("");
        }

        enterScope(ctx.scope);
        var ID2 = "";
        try {
            ID2 = currentScope.get(ctx.constructorReference).generateName(getComponentPrefix(ctx.constructorReference.getRelativeScope()));
        } catch (CouldNotFindException e) {
            exitScope();
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID(1).getText()));
            return new ManualTemplate("");
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
                String identifier = currentScope.get(ctx.references.get(i)).generateName(getComponentPrefix(ctx.references.get(i).getRelativeScope()));
                arrayDeclID = arrayDeclID.replaceValue("ID", identifier);
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(ctx.arrayDeclID(i), ctx.arrayDeclID(i).ID().getText()));
                return new ManualTemplate("");
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
            result = new ManualTemplate(currentScope.get(ctx.reference).generateName(getComponentPrefix(ctx.reference.getRelativeScope())));
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return new ManualTemplate("");
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
        DeclarationInfo paramInfo = null;
        try {
            paramInfo = currentScope.get(ctx.reference);
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return new ManualTemplate("");
        }

        if (paramInfo.getType().getEvaluationType().equals(Type.TypeEnum.interfaceType)) {
            DeclarationInfo paramTypeInfo;
            try {
                paramTypeInfo = currentScope.get(ctx.type().typeId().reference);
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(ctx.type().typeId(), ctx.type().getText()));
                return new ManualTemplate("");
            }

            if (paramTypeInfo.getNode() instanceof UCELParser.InterfaceDeclContext) {

                StringBuilder arrayString = new StringBuilder("");

                for (var arrayDecl : ctx.arrayDecl()) {
                    arrayString.append(visit(arrayDecl));
                }

                UCELParser.InterfaceDeclContext interfaceNode = (UCELParser.InterfaceDeclContext) paramTypeInfo.getNode();
                ArrayList<Template> paramDecls = new ArrayList<>();

                for (int i = 0; i < interfaceNode.interfaceVarDecl().arrayDeclID().size(); i++) {
                    Scope oldScope = currentScope;
                    currentScope = currentScope.getParent();
                    String type = visit(interfaceNode.interfaceVarDecl().type(i).typeId()).toString();
                    currentScope = oldScope;
                    String name = paramInfo.generateName(getComponentPrefix(ctx.reference.getRelativeScope())) + "_" + visit(interfaceNode.interfaceVarDecl().arrayDeclID(i));
                    paramDecls.add(new ManualTemplate(String.format("%s &%s%s", type, name, arrayString)));
                }
                return new ParametersTemplate(paramDecls);
            }
        } else {
            var typeTemplate = ctx.type() != null ? visit(ctx.type()) : new ManualTemplate("");
            var ampString = ctx.BITAND() != null ? ctx.BITAND().getText() : "";


            var idString = paramInfo.generateName(getComponentPrefix(ctx.reference.getRelativeScope()));

            var arrayTemplates = new ArrayList<Template>();

            for (var arrayDecl : ctx.arrayDecl()) {
                arrayTemplates.add(visit(arrayDecl));
            }

            return ctx.REF() != null ? new ManualTemplate("") : new ParameterTemplate(typeTemplate, ampString, idString, arrayTemplates);
        }

        return new ManualTemplate("");
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
                return new ManualTemplate(currentScope.get(ctx.reference).generateName(getComponentPrefix(ctx.reference.getRelativeScope())));
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
                return new ManualTemplate("");
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
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return new ManualTemplate("");
        }

        for (int i = 0; i < ctx.arrayDecl().size(); i++) {
            arrayDecals.add(visit(ctx.arrayDecl(i)));
        }

        if (ctx.initialiser() != null) {
            Template initialiserResult = visit(ctx.initialiser());
            result = new VariableIDTemplate(declarationInfo.generateName(getComponentPrefix(ctx.reference.getRelativeScope())), arrayDecals, initialiserResult);
        }
        else {
            result = new VariableIDTemplate(declarationInfo.generateName(getComponentPrefix(ctx.reference.getRelativeScope())), arrayDecals);
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
    public Template visitStructAccess(UCELParser.StructAccessContext ctx) {

        if (this.inVerificationMode) {
            var localIndices = this.arrayIndices;
            this.arrayIndices = new ArrayList<>();
            var expr = ctx.expression();
            if (!(expr instanceof UCELParser.IdExprContext || expr instanceof UCELParser.StructAccessContext || expr instanceof UCELParser.ArrayIndexContext)) {
                if (expr instanceof UCELParser.FuncCallContext)
                    return new ManualTemplate(String.format("%s.%s", ctx.expression().getText(), ctx.ID().getText()));
                //todo: log
                return new ManualTemplate("");
            }
            var leftSide = visit(ctx.expression());
            this.arrayIndices = localIndices;
            if (leftSide instanceof OccurrenceAccessTemplate) {
                var leftSideOccurrence = ((OccurrenceAccessTemplate) leftSide).getOccurrence();
                if (leftSideOccurrence instanceof ComponentOccurrence) {
                    var resultingOccurrence = leftSideOccurrence.findChildOccurrence(ctx.ID().getText(), this.arrayIndices);
                    if (resultingOccurrence == null) {
                        UCELParser.ComponentContext componentNode = ((ComponentOccurrence) leftSideOccurrence).getNode();
                        var componentBodyScope = componentNode.compBody().scope;
                        try {
                            var reference = componentBodyScope.find(ctx.ID().getText(), true);
                            var info = componentBodyScope.get(reference);
                            return new ManualTemplate(info.generateName(leftSideOccurrence.getPrefix()));
                        } catch (CouldNotFindException e) {
                            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
                            return new ManualTemplate("");
                        }
                    }
                    return new OccurrenceAccessTemplate(resultingOccurrence);
                } else if (leftSideOccurrence instanceof TemplateOccurrence) {
                    var leftName = leftSideOccurrence.getPrefix();
                    String name;
                    try {
                        var templateScope = ((TemplateOccurrence) leftSideOccurrence).getNode().scope;
                        var ref = templateScope.find(ctx.ID().getText(), true);
                        var info = templateScope.get(ref);
                        return new ManualTemplate(String.format("%s.%s", leftName, info.generateName()));
                    } catch (CouldNotFindException e) {
                        return new ManualTemplate(String.format("%s.%s", leftName, ctx.ID().getText()));
                    }
                }
            }

            //todo:log error?
            return new ManualTemplate(String.format("%s.%s", leftSide, ctx.ID().getText()));
        }

        var expr = ctx.expression();
        int arrayCounter;
        for (arrayCounter = 0; expr instanceof UCELParser.ArrayIndexContext; arrayCounter++)
            expr = ((UCELParser.ArrayIndexContext) expr).expression(0);

        if (expr instanceof UCELParser.IdExprContext) {
            DeclarationInfo exprInfo;
            try {
                exprInfo = currentScope.get(expr.reference);
            } catch (CouldNotFindException e) {
                logger.log(new MissingReferenceErrorLog(expr, ((UCELParser.IdExprContext) expr).ID().getText()));
                return new ManualTemplate("");
            }

            if (exprInfo.getType().getEvaluationType().equals(Type.TypeEnum.interfaceType)) {
                Template leftSide = visit(ctx.expression());
                if (leftSide instanceof ArrayIndexTemplate) {
                    ArrayIndexTemplate leftSideCasted = (ArrayIndexTemplate) leftSide;
                    leftSideCasted.template.add("unexpectedField", "_" + ctx.ID().getText());
                    return leftSideCasted;
                }
                return new ManualTemplate(String.format("%s_%s", leftSide, ctx.ID().getText()));
            }
        }

        // Struct.id and [Struct].id

        // interface T {chan c, int q}, T t, T s[]
        // t.c -> t_c   s[i].c -> s_c[i]

        //interface T1 {chan c, int q[10]}, T1 t1, T1 s1[]
        // t1.q[i] -> t1_q[i], s1[j].q[i] -> s1_q[j][i]


        return new ManualTemplate(String.format("%s.%s", visit(ctx.expression()), ctx.ID().getText()));
    }

    @Override
    public Template visitIdExpr(UCELParser.IdExprContext ctx) {
        if (inVerificationMode) {
            var occ = globalOccurrence.findChildOccurrence(ctx.ID().getText(), this.arrayIndices);
            this.arrayIndices = new ArrayList<>();
            if (occ != null) {
                return new OccurrenceAccessTemplate(occ);
            } else {
                try {
                    var reference = currentScope.find(ctx.ID().getText(), true);
                    var info = currentScope.get(reference);
                    if (info.getValue() instanceof IntegerValue)
                        return new IntegerValueTemplate(((IntegerValue) info.getValue()).getInt());
                    return new ManualTemplate(info.generateName(""));
                } catch (CouldNotFindException e) {
                    return new ManualTemplate(ctx.ID().getText());
                }

            }
        }

        try {
            return new ManualTemplate(currentScope.get(ctx.reference).generateName(getComponentPrefix(ctx.reference.getRelativeScope())));
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return new ManualTemplate("");
        }
    }

    @Override
    public Template visitParen(UCELParser.ParenContext ctx) {
        var expr = visit(ctx.expression());

        return new ParenthesisTemplate(expr);
    }

    @Override
    public Template visitArrayIndex(UCELParser.ArrayIndexContext ctx) {

        if (this.inVerificationMode) {
            int index;
            if(ctx.expression(1) instanceof UCELParser.LiteralExprContext && ((UCELParser.LiteralExprContext) ctx.expression(1)).literal().NAT() != null) {
                index = Integer.parseInt(ctx.expression(1).getText());
            } else if (ctx.expression(1) instanceof UCELParser.IdExprContext) {
                var rightSide = visit(ctx.expression(1));
                if (rightSide instanceof IntegerValueTemplate)
                    index = ((IntegerValueTemplate) rightSide).value;
                else
                    return new ManualTemplate("");
            } else {
                //todo: log error
                return new ManualTemplate("");
            }

            this.arrayIndices.add(0, index);
            var leftSide = visit(ctx.expression(0));

            if (leftSide instanceof OccurrenceAccessTemplate) {
                return leftSide;
            }
        }

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
        if (inVerificationMode && ctx.NAT() != null)
            return new IntegerValueTemplate(Integer.parseInt(ctx.getText()));
        return new LiteralTemplate(ctx.getText());
    }

    @Override
    public Template visitPower(UCELParser.PowerContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        return new PowerExprTemplate(left, right);
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
        if (ctx.type().typeId() instanceof UCELParser.TypeIDIntContext) {
            UCELParser.TypeIDIntContext typeNode = (UCELParser.TypeIDIntContext) ctx.type().typeId();
            var left = visit(typeNode.expression(0));
            var right = visit(typeNode.expression(1));

            if (left instanceof IntegerValueTemplate && right instanceof IntegerValueTemplate) {
                var leftInt = ((IntegerValueTemplate) left).value;
                var rightInt = ((IntegerValueTemplate) right).value;

                DeclarationInfo info = new DeclarationInfo(ctx.ID().getText());
                try {
                    if (ctx.reference == null)
                        ctx.reference = currentScope.add(info);
                    else
                        info = currentScope.get(ctx.reference);
                } catch (CouldNotFindException e) {
                    logger.log(new MissingReferenceErrorLog(ctx, "variable " + ctx.ID().getText()));
                    return new ManualTemplate("");
                }

                List<Template> exprs = new ArrayList<>();
                for (int i = leftInt; i <= rightInt; i++) {
                    info.setValue(new IntegerValue(i));
                    exprs.add(visit(ctx.expression()));
                }

                String operator = ctx.FORALL() != null ? "&&" : (ctx.EXISTS() != null ? "||" : "+");

                return new VerificationTemplate(operator, exprs);
            }
        }

        //todo: in verification id is never added. Consider where to add it. Global scope may cause conflicts
        try {
            String id = currentScope.get(ctx.reference).generateName(getComponentPrefix(ctx.reference.getRelativeScope()));

            var type = visit(ctx.type());
            var expr = visit(ctx.expression());

            return new VerificationTemplate(ctx.op.getText(), id, type, expr);
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return new ManualTemplate("");
        } catch (Exception e) {
            logger.log(new Warning(ctx, "Could not generate verification query, inserting as it was before compilation. Error: " + e.getMessage()));
            return new ManualTemplate(ctx.getText());
        }
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
        } catch (CouldNotFindException e) {
            logger.log(new MissingReferenceErrorLog(ctx, ctx.ID().getText()));
            return new ManualTemplate("");
        }

        var typeResult = ctx.type() != null ? visit(ctx.type()) : new ManualTemplate("");
        var stmntResult = visit(ctx.statement());

        return new IterationTemplate(new ManualTemplate(declarationInfo != null
                ? declarationInfo.generateName(getComponentPrefix(ctx.reference.getRelativeScope()))
                : ""), typeResult, stmntResult);
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
    private String getComponentPrefix(int depth) {
        return depth <= this.depthFromComponentScope ? componentPrefix : "";
    }

    private void enterScope(Scope scope) {
        if (!this.componentPrefix.equals(""))
            this.depthFromComponentScope++;
        currentScope = scope;
    }

    private void exitScope() {
        this.currentScope = this.currentScope.getParent();
    }
    //endregion
}
