package org.UcelParser.ReferenceHandler;

import org.UcelParser.UCELParser_Generated.*;
import org.UcelParser.Util.*;
import org.UcelParser.Util.Logging.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;


public class ReferenceVisitor extends UCELBaseVisitor<Boolean> {
    private Scope currentScope;
    private ILogger logger;

    public ReferenceVisitor(Scope scope) {
        this.currentScope = scope;
        this.logger = new Logger();
    }

    public ReferenceVisitor(ILogger logger) {
        this.currentScope = null;
        this.logger = logger;
    }

    //region Util

    @Override
    protected Boolean defaultResult() { return true;}

    @Override
    protected Boolean aggregateResult(Boolean aggregate, Boolean nextResult) {
        return (nextResult == null || nextResult) && (aggregate == null || aggregate);
    }

    //endregion

    //region Component Extension

    //region Component
    @Override
    public Boolean visitComponent(UCELParser.ComponentContext ctx) {
        String compName = ctx.ID().getText();
        try {
            if(!currentScope.isUnique(compName, false)) {
                logger.log(new ErrorLog(ctx, "Component name '" + compName + "' is already declared"));
                return false;
            }
            ctx.reference = currentScope.add(new DeclarationInfo(ctx.ID().getText(), ctx));
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
        }

        enterScope();
        ctx.scope = currentScope;
        boolean success = (ctx.parameters() == null || visit(ctx.parameters())) && visit(ctx.interfaces()) && visit(ctx.compBody());

        exitScope();
        return success;
    }
    //endregion

    //region Build block

    @Override
    public Boolean visitBuildBlock(UCELParser.BuildBlockContext ctx) {
        enterScope();
        ctx.scope = currentScope;

        boolean success = true;

        for (var stmnt : ctx.buildStmnt()) {
            success = (success && visit(stmnt));
        }

        exitScope();
        return success;
    }


    //endregion

    //region Build declaration

    @Override
    public Boolean visitBuildDecl(UCELParser.BuildDeclContext ctx) {
        String typeIdentifier = ctx.ID(0).getText();
        String identifier = ctx.ID(1).getText();

        try {
            ctx.typeReference = currentScope.find(typeIdentifier, false);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Type " + typeIdentifier + " is not defined"));
            return false;
        }

        if(!currentScope.isUnique(identifier, true)) {
            logger.log(new ErrorLog(ctx, "The variable name '" + identifier + "' already defined in scope"));
            return false;
        }

        boolean valid = true;

        for (UCELParser.ArrayDeclContext arrayDecl : ctx.arrayDecl()) {
            valid = valid && visit(arrayDecl);
        }

        ctx.reference = currentScope.add(new DeclarationInfo(identifier, ctx));

        return valid;
    }


    //endregion

    //region Build iteration

    @Override
    public Boolean visitBuildIteration(UCELParser.BuildIterationContext ctx) {
        String identifier = ctx.ID().getText();

        if (!currentScope.isUnique(identifier, true)) {
            logger.log(new ErrorLog(ctx, "The variable name '" + identifier + "' already defined in scope"));
            return false;
        }

        ctx.reference = currentScope.add(new DeclarationInfo(identifier, ctx));

        return visit(ctx.expression(0)) && visit(ctx.expression(1)) && visit(ctx.buildStmnt());
    }

    //endregion

    //region Interface declaration

    @Override
    public Boolean visitInterfaceDecl(UCELParser.InterfaceDeclContext ctx) {
        String identifier = ctx.ID().getText();

        if (!currentScope.isUnique(identifier, false)) {
            logger.log(new ErrorLog(ctx, "The interface name '" + identifier + "' is already defined in scope"));
            return false;
        }

        ctx.reference = currentScope.add(new DeclarationInfo(identifier, ctx));
        return true;
    }

    //endregion

    //region

    @Override
    public Boolean visitCompCon(UCELParser.CompConContext ctx) {
        String identifier = ctx.ID().getText();
        DeclarationReference ref;

        try {
            ref = currentScope.find(identifier, false);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Type " + identifier + " not defined in scope"));
            return false;
        }

        ctx.constructorReference = ref;

        return visit(ctx.compVar()) && visit(ctx.arguments());
    }

    //endregion

    //region Component variable
    @Override
    public Boolean visitCompVar(UCELParser.CompVarContext ctx) {
        String identifier = ctx.ID().getText();
        DeclarationReference reference;

        try {
            reference = currentScope.find(identifier, true);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "The variable " + identifier + " is not defined in scope"));
            return false;
        }

        ctx.variableReference = reference;

        for (var expr : ctx.expression()) {
            if (!visit(expr))
                return false;
        }

        return true;
    }

    //endregion

    //region Link statement
    private DeclarationReference extractInterfaceNumberFromComponentAsReference(String id, Scope scope, UCELParser.CompVarContext node) {
        UCELParser.ComponentContext componentNode;
        try {
            componentNode = (UCELParser.ComponentContext) scope.get(node.variableReference).getNode();
        } catch (Exception e) {
            logger.log(new ErrorLog(node, "Compiler error"));
            return null;
        }

        UCELParser.ParametersContext parameters = componentNode.interfaces().parameters();
        if (parameters == null)
            return null;
        for (int i = 0; i < parameters.parameter().size(); i++) {
            UCELParser.ParameterContext param = parameters.parameter(i);
            if (param.ID().getText().equals(id)) {
                return new DeclarationReference(-1, i);
            }
        }

        return null;
    }
    @Override
    public Boolean visitLinkStatement(UCELParser.LinkStatementContext ctx) {
        boolean success = visit(ctx.compVar(0));
        var leftNode = extractInterfaceNumberFromComponentAsReference(ctx.ID(0).getText(), currentScope, ctx.compVar(0));
        success = success && leftNode != null;
        ctx.leftInterface = leftNode;

        success = success && visit(ctx.compVar(1));
        var rightNode = extractInterfaceNumberFromComponentAsReference(ctx.ID(1).getText(), currentScope, ctx.compVar(1));
        success = success && rightNode != null;
        ctx.rightInterface = rightNode;

        return success;
    }

    //endregion

    //endregion

    //region ProjectStructure

    @Override
    public Boolean visitProject(UCELParser.ProjectContext ctx) {
        enterScope();
        ctx.scope = currentScope;
        boolean b = visit(ctx.pdeclaration());
        for(UCELParser.PtemplateContext t : ctx.ptemplate())
            b = visit(t) && b;
        b = visit(ctx.psystem()) && b;
        exitScope();
        return b;
    }

    @Override
    public Boolean visitPtemplate(UCELParser.PtemplateContext ctx) {
        String templateName = ctx.ID().getText();
        try {
            if(!currentScope.isUnique(templateName, false)) {
                logger.log(new ErrorLog(ctx, "Template name '" + templateName + "' is already declared"));
                return false;
            }
            DeclarationReference declRef = currentScope.add(new DeclarationInfo(ctx.ID().getText(), ctx));
            ctx.reference = declRef;

        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
        }

        enterScope();
        ctx.scope = currentScope;

        if(!visit(ctx.parameters()) || !visit(ctx.declarations()) || !visit(ctx.graph())) {
            //No logging, passing through
            exitScope();
            return false;
        }

        exitScope();
        return true;
    }

    @Override
    public Boolean visitPsystem(UCELParser.PsystemContext ctx) {
        boolean declSuccess = visit(ctx.declarations());

        var build = ctx.build();
        var system = ctx.system();

        if(build != null) {
            return declSuccess && visit(build);
        }
        else if (system != null) {
            return declSuccess && visit(system);
        }
        else {
            logger.log(new ErrorLog(ctx,"Compiler error: Expected build or system in RefVisitor: PSystem"));
            return false;
        }
    }

    @Override
    public Boolean visitExponential(UCELParser.ExponentialContext ctx) {
        int count = ctx.getChildCount();
        return (count <= 0 || visit(ctx.expression().get(0))) && (count <= 1 || visit(ctx.expression().get(1)));
    }

    @Override
    public Boolean visitEdge(UCELParser.EdgeContext ctx) {
        enterScope();
        ctx.scope = currentScope;
        boolean b = visit(ctx.select()) && visit(ctx.guard()) && visit(ctx.sync()) && visit(ctx.update());
        exitScope();
        return b;
    }

    @Override
    public Boolean visitSelect(UCELParser.SelectContext ctx) {
        List<TerminalNode> ids = ctx.ID();
        List<UCELParser.TypeContext> types = ctx.type();

        if(ids.size() != types.size()) {
            logger.log(new ErrorLog(ctx, "Compiler Error: non equal amounts of identifiers and types"));
            return false;
        }

        boolean b = true;
        ArrayList<DeclarationReference> declRefs = new ArrayList<>();
        for(int i = 0; i < ids.size(); i++) {
            b = visit(types.get(i)) && b;
            String id = ids.get(i).getText();

            try {
                if(!currentScope.isUnique(id, true)) {
                    logger.log(new ErrorLog(ctx, "Variable '" + id + "' is not unique"));
                    b = false;
                }

                declRefs.add(currentScope.add(new DeclarationInfo(id)));
            } catch (Exception e) {
                logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
                return false;
            }

        }
        ctx.references = declRefs;
        return b;
    }

    @Override
    public Boolean visitStart(UCELParser.StartContext ctx) {
        boolean success = true;
        enterScope();
        ctx.scope = currentScope;
        if (visit(ctx.declarations())) {
            for (var stmnt : ctx.statement()) {
                if (!visit(stmnt)) {
                    success = false;
                    break;
                }
            }
            if (success)
                success = visit(ctx.system());
        } else {
            success = false;
        }

        exitScope();
        return success;
    }

    @Override
    public Boolean visitSystem(UCELParser.SystemContext ctx) {
        boolean success = true;

        for (var expr : ctx.expression()) {
            if (!visit(expr)) {
                success = false;
                break;
            }
        }

        return success;
    }

    //endregion


    @Override
    public Boolean visitInterfaces(UCELParser.InterfacesContext ctx) {
        if (ctx.parameters() != null) {
            return visit(ctx.parameters());
        } else {
            return true;
        }
    }

    public Boolean visitCompBody(UCELParser.CompBodyContext ctx) {
        enterScope();
        currentScope = ctx.scope;
        Boolean declRes = null;
        if (ctx.declarations() != null) declRes = visit(ctx.declarations());
        Boolean buildRes = null;
        if (ctx.build() != null) buildRes = visit(ctx.build());
        exitScope();
        return (declRes != null && buildRes != null && declRes && buildRes)
                || (declRes == null && buildRes != null && buildRes)
                || (declRes != null && declRes && buildRes == null);
    }

    public Boolean visitBuild(UCELParser.BuildContext ctx) {
        List<Boolean> declRes = null;
        if (ctx.buildDecl() != null) declRes = ctx.buildDecl().stream().map(this::visit).collect(Collectors.toList());
        List<Boolean> stmntRes = null;
        if (ctx.buildStmnt() != null) stmntRes = ctx.buildStmnt().stream().map(this::visit).collect(Collectors.toList());
        return (declRes == null && stmntRes != null && stmntRes.stream().allMatch(b -> b))
                || (declRes != null && declRes.stream().allMatch(b -> b) && stmntRes != null && stmntRes.stream().allMatch(b -> b));
    }



    //endregion



    @Override
    public Boolean visitFunction(UCELParser.FunctionContext ctx) {
        String funcName = ctx.ID().getText();
        try {
            if(!currentScope.isUnique(funcName, false)) {
                logger.log(new ErrorLog(ctx, "Function name '" + funcName + "' is already declared"));
                return false;
            }
            DeclarationReference declRef = currentScope.add(new DeclarationInfo(ctx.ID().getText(), ctx));
            ctx.reference = declRef;

        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
        }

        if(!visit(ctx.type())) {
            //No logging, passing through
            exitScope();
            return false;
        }

        enterScope();
        ctx.scope = currentScope;

        if(!visit(ctx.parameters()) || !visit(ctx.block())) {
            //No logging, passing through
            exitScope();
            return false;
        }

        ctx.occurrences = new ArrayList<>();
        exitScope();
        return true;
    }

    @Override
    public Boolean visitParameters(UCELParser.ParametersContext ctx) {
        boolean success = true;

        for (var param : ctx.parameter())
            success = visit(param) && success;

        return success;
    }

    @Override
    public Boolean visitParameter(UCELParser.ParameterContext ctx) {
        String parameterName = ctx.ID().getText();

        //No logging, passing through
        if(!visit(ctx.type())) return false;

        for(UCELParser.ArrayDeclContext arrayDecl : ctx.arrayDecl()) {
            if(!visit(arrayDecl)) return false;
            //No logging, passing through
        }

        try {
            if(!currentScope.isUnique(parameterName, true)) {
                logger.log(new ErrorLog(ctx, "Parameter name '" + parameterName + "' is not unique in scope"));
                return false;
            }

            ctx.reference = currentScope.add(new DeclarationInfo(parameterName));
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
            return false;
        }

        return true;
    }

    @Override
    public Boolean visitInstantiation(UCELParser.InstantiationContext ctx) {
        String instantiationIdentifier = ctx.ID().get(0).getText();
        String constructorIdentifier = ctx.ID().get(1).getText();

        try {
            if(!currentScope.isUnique(instantiationIdentifier, true)) {
                logger.log(new ErrorLog(ctx, "Instantiation name " + instantiationIdentifier + " is not unique" ));
                return false;
            }
            ctx.instantiatedReference = currentScope.add(new DeclarationInfo(instantiationIdentifier));
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
            return false;
        }

        enterScope();
        ctx.scope = currentScope;

        try {
            ctx.constructorReference = currentScope.find(constructorIdentifier, false);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
            exitScope();
            return false;
        }

        var parameters = ctx.parameters();
        var arguments = ctx.arguments();
        if(
            (parameters != null && !visit(parameters)) ||
            (arguments  != null && !visit(arguments ))
        ) {
            exitScope();
            return false;
        }

        exitScope();
        return true;
    }

    @Override
    public Boolean visitTypeIDID(UCELParser.TypeIDIDContext ctx) {
        String typeID = ctx.ID().getText();

        try {
            ctx.reference = currentScope.find(typeID, false);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
            return false;
        }

        return true;
    }

    @Override
    public Boolean visitTypeDecl(UCELParser.TypeDeclContext ctx) {

        //No logging, passing through
        if(!visit(ctx.type())) return false;

        ArrayList<DeclarationReference> references = new ArrayList<>();

        for(UCELParser.ArrayDeclIDContext arrayDeclID : ctx.arrayDeclID()) {
            String identifier = arrayDeclID.ID().getText();

            for (UCELParser.ArrayDeclContext arrayDecl : arrayDeclID.arrayDecl()) {
                if (!visit(arrayDecl)) return false;
                //No logging, passing through
            }

            try {
                if(!currentScope.isUnique(identifier, false)) {
                    logger.log(new ErrorLog(ctx, "Parameter name '" + identifier + "' is not unique in scope"));
                    return false;
                }

                references.add(currentScope.add(new DeclarationInfo(identifier)));
            } catch (Exception e) {
                logger.log(new ErrorLog(ctx, "Compiler Error: " + e.getMessage()));
                return false;
            }
        }

        ctx.references = references;

        return true;
    }

    @Override
    public Boolean visitIdExpr(UCELParser.IdExprContext ctx) {
        String identifier = ctx.ID().getText();

        DeclarationReference tableReference = null;

        try {
            tableReference = currentScope.find(identifier, true);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx,"Variable '" + identifier + "' has not been declared in scope"));
            return false;
        }

        ctx.reference = tableReference;
        return true;
    }

    @Override
    public Boolean visitFuncCall(UCELParser.FuncCallContext ctx) {
        String identifier = ctx.ID().getText();

        DeclarationReference tableReference = null;
        DeclarationInfo funcInfo = null;

        try {
            tableReference = currentScope.find(identifier, true);
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx,"Function '" + identifier + "' has not been declared in scope"));
            return false;
        }

        ctx.reference = tableReference;
        visit(ctx.arguments());
        return true;
    }

    @Override
    public Boolean visitArguments(UCELParser.ArgumentsContext ctx) {
        var res = true;
        for (var expr : ctx.expression())
            res = visit(expr) && res;

        return res;
    }

    @Override
    public Boolean visitVariableDecl(UCELParser.VariableDeclContext ctx) {
        boolean b = visit(ctx.type());

        for(UCELParser.VariableIDContext idCtx : ctx.variableID()) {
            Boolean valid = visit(idCtx);
            b = b && (valid != null) && valid;
        }

        return b;
    }

    @Override
    public Boolean visitVariableID(UCELParser.VariableIDContext ctx) {
        String identifier = ctx.ID().getText();

        //TODO: maybe delegate to Scope.add
        if(!currentScope.isUnique(identifier, true)) {
            logger.log(new ErrorLog(ctx, "The variable name '" + identifier + "' already defined in scope"));
            return false;
        }

        boolean valid = true;

        for (UCELParser.ArrayDeclContext arrayDecl : ctx.arrayDecl()) {
            valid = valid && visit(arrayDecl);
        }

        ctx.reference = currentScope.add(new DeclarationInfo(identifier, ctx));
        if(ctx.initialiser() != null)
            valid = valid && visit(ctx.initialiser());

        return valid;
    }

    @Override
    public Boolean visitIteration(UCELParser.IterationContext ctx) {
        String identifier = ctx.ID().getText();
        visit(ctx.type());

        try {
            if(!currentScope.isUnique(identifier, true)) {
                logger.log(new ErrorLog(ctx, "Variable '" + identifier + "' already exists in scope"));
                return false;
            }
            DeclarationReference declRef = currentScope.add(new DeclarationInfo(identifier, ctx));
            ctx.reference = declRef;
        } catch (Exception e) {
            logger.log(new ErrorLog(ctx, "Compiler Error" + e.getMessage()));
            return false;
        }

        return visit(ctx.statement());
    }

    @Override
    public Boolean visitBlock(UCELParser.BlockContext ctx) {

        boolean success = true;
        enterScope();
        ctx.scope = currentScope;

        for(UCELParser.LocalDeclarationContext ldc : ctx.localDeclaration())
            success &= visit(ldc);

        for(UCELParser.StatementContext sc : ctx.statement())
            success &= visit(sc);

        exitScope();

        return success;
    }

    @Override
    public Boolean visitIncrementPost(UCELParser.IncrementPostContext ctx) {
        return handleIncrementDecrement(ctx);
    }

    @Override
    public Boolean visitIncrementPre(UCELParser.IncrementPreContext ctx) {
        return handleIncrementDecrement(ctx);
    }

    @Override
    public Boolean visitDecrementPost(UCELParser.DecrementPostContext ctx) {
        return handleIncrementDecrement(ctx);
    }

    @Override
    public Boolean visitDecrementPre(UCELParser.DecrementPreContext ctx) {
        return handleIncrementDecrement(ctx);
    }

    private Boolean handleIncrementDecrement(UCELParser.ExpressionContext ctx) {
        UCELParser.ExpressionContext expr = ctx.getRuleContext(UCELParser.ExpressionContext.class, 0);
        if(expr instanceof UCELParser.IdExprContext ||
                expr instanceof UCELParser.StructAccessContext ||
                expr instanceof UCELParser.ArrayIndexContext) {
            return visit(expr);
        } else {
            logger.log(new ErrorLog(ctx, "Operator only valid for a reference expressions, " +
                    "such as a variable or a struct field"));
            return false;
        }
    }

    @Override
    public Boolean visitAssignExpr(UCELParser.AssignExprContext ctx) {
        UCELParser.ExpressionContext expr = ctx.expression(0);
        if(expr instanceof UCELParser.IdExprContext ||
                expr instanceof UCELParser.StructAccessContext ||
                expr instanceof UCELParser.ArrayIndexContext) {
            return visit(expr) && visit(ctx.expression(1));
        } else {
            logger.log(new ErrorLog(ctx, "Left side of an assignment requires a reference expressions, " +
                    "such as a variable or a struct field"));
            return false;
        }
    }

    private void enterScope() {
        enterScope(false);
    }

    private void enterScope(boolean isComponent) {
        this.currentScope = new Scope(this.currentScope, isComponent);
    }

    private void exitScope() {
        this.currentScope = this.currentScope.getParent();
    }

    public Scope getCurrentScope() {
        return currentScope;
    }
}
