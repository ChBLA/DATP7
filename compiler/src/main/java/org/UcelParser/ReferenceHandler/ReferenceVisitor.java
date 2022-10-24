package org.UcelParser.ReferenceHandler;

import org.UcelParser.UCELParser_Generated.*;
import org.UcelParser.Util.*;
import org.UcelParser.Util.Logging.*;

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

    @Override
    protected Boolean aggregateResult(Boolean aggregate, Boolean nextResult) {
        return (nextResult == null || nextResult) && (aggregate == null || aggregate);
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

        enterScope();
        ctx.scope = currentScope;

        if(!visit(ctx.type()) || !visit(ctx.parameters())) {
            //No logging, passing through
            return false;
        }

        if(!visit(ctx.block())) {
            //No logging, passing through
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

        if(!visit(ctx.parameters()) || !visit(ctx.arguments())) {
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

        var refArgs = ctx.arguments().ID();
        DeclarationInfo[] references = new DeclarationInfo[refArgs.size()];

        try {
            tableReference = currentScope.find(identifier, true);
            funcInfo = currentScope.get(tableReference);

            for (int i = 0; i < refArgs.size(); i++) {
                references[i] = currentScope.get(currentScope.find(refArgs.get(i).getText(), true));
            }

        } catch (Exception e) {
            logger.log(new ErrorLog(ctx,"Function '" + identifier + "' has not been declared in scope"));
            return false;
        }

        if (refArgs.size() > 0) {
            var func = (UCELParser.FunctionContext) funcInfo.getNode();
            var occurrence = new FuncCallOccurrence(ctx, references);
            func.occurrences.add(occurrence);
            DeclarationReference newFuncReference = null;
            try {
                var builder = new StringBuilder(funcInfo.getIdentifier());
                for (int i = 0; i < references.length; i++) {
                    builder.append(String.format("_%s", references[i].getIdentifier()));
                }

                newFuncReference = currentScope.getScope(tableReference).add(new DeclarationInfo(builder.toString(), funcInfo.getType(), funcInfo.getNode()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ctx.reference = newFuncReference;
        } else {
            ctx.reference = tableReference;
        }
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
