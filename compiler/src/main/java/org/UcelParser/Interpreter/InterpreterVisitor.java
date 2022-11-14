package org.UcelParser.Interpreter;

import org.UcelParser.Util.DeclarationInfo;
import org.UcelParser.Util.Logging.ErrorLog;
import org.UcelParser.Util.Logging.ILogger;
import org.UcelParser.Util.Value.*;
import org.UcelParser.UCELParser_Generated.UCELBaseVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Scope;

import java.util.ArrayList;

public class InterpreterVisitor extends UCELBaseVisitor<InterpreterValue> {

    //region Header

    private Scope currentScope;
    private ILogger logger;

    public InterpreterVisitor(Scope scope) {
        currentScope = scope;
    }

    public InterpreterVisitor(ILogger logger) {
        this.logger = logger;
    }

    //endregion

    public ArrayList<InterpreterValue> interpret(UCELParser.ProjectContext ctx) {
        ParameterValue values = (ParameterValue) visitProject(ctx);
        return values.getParameters();
    }

    @Override
    public InterpreterValue visitAddSub(UCELParser.AddSubContext ctx) {
        InterpreterValue left = visit(ctx.expression().get(0));
        InterpreterValue right = visit(ctx.expression().get(1));

        if(!isIntegerValue(left) || !isIntegerValue(right)) return null;
        IntegerValue intLeft = (IntegerValue) left;
        IntegerValue intRight = (IntegerValue) right;
        int op = ctx.op.getText().equals("+") ? 1 : -1;
        return new IntegerValue(intLeft.getInt() + op * intRight.getInt());
    }

    @Override
    public InterpreterValue visitMultDiv(UCELParser.MultDivContext ctx) {
        InterpreterValue left = visit(ctx.expression().get(0));
        InterpreterValue right = visit(ctx.expression().get(1));

        if(!isIntegerValue(left) || !isIntegerValue(right)) return null;
        IntegerValue intLeft = (IntegerValue) left;
        IntegerValue intRight = (IntegerValue) right;
        String op = ctx.op.getText();
        if(op.equals("*"))
                return new IntegerValue(intLeft.getInt() * intRight.getInt());
        if(op.equals("/"))
                return new IntegerValue(intLeft.getInt() / intRight.getInt());
        if(op.equals("%"))
            return new IntegerValue(intLeft.getInt() % intRight.getInt());
        return null;
    }

    private boolean isIntegerValue(InterpreterValue v) {
        return v != null && v instanceof IntegerValue;
    }

    @Override
    public InterpreterValue visitIdExpr(UCELParser.IdExprContext ctx) {
        try{
            DeclarationInfo declInfo = currentScope.get(ctx.reference);
            return declInfo.getValue() == null ? new StringValue(declInfo.generateName()) : declInfo.getValue();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public InterpreterValue visitArrayIndex(UCELParser.ArrayIndexContext ctx) {
        InterpreterValue left = visit(ctx.expression().get(0));
        InterpreterValue right = visit(ctx.expression().get(1));

        if(!isStringValue(left) || !isIntegerValue(right)) return null;
        IntegerValue intRight = (IntegerValue) right;
        return intRight.getInt() < 0 ? null : new StringValue(left.generateName() + "_" + right.generateName());
    }

    @Override
    public InterpreterValue visitStructAccess(UCELParser.StructAccessContext ctx) {
        InterpreterValue left = visit(ctx.expression());
        String id = ctx.ID().getText();

        if(!isStringValue(left) || id == null) return null;
        return new StringValue(left.generateName() + "." + id);
    }


    private boolean isStringValue(InterpreterValue v) {
        return v != null && v instanceof StringValue && v.generateName() != null;
    }

    //region Scope

    private void enterScope(Scope scope) {
        currentScope = scope;
    }

    private void exitScope() {
        this.currentScope = this.currentScope.getParent();
    }

    //endregion

    @Override
    public InterpreterValue visitEqExpr(UCELParser.EqExprContext ctx) {
        var v0 = visit(ctx.expression(0));
        var v1 = visit(ctx.expression(1));

        boolean isEqual = v0.equals(v1);

        return new BooleanValue(isEqual);
    }

    @Override
    public InterpreterValue visitParen(UCELParser.ParenContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public InterpreterValue visitLiteral(UCELParser.LiteralContext ctx) {
        // NAT | bool | DOUBLE | DEADLOCK;
        if(ctx.NAT() != null) {
            var val = Integer.parseInt(ctx.NAT().getText());
            return new IntegerValue(val);
        }
        else if(ctx.bool() != null) {
            return visit(ctx.bool());
        }
        else if(ctx.DOUBLE() != null) {
            logger.log(new ErrorLog(ctx, "Doubles are not supported in interpretation"));
            return null;
        }
        else if(ctx.DEADLOCK() != null) {
            logger.log(new ErrorLog(ctx, "Deadlock is not supported in interpretation"));
            return null;
        }

        logger.log(new ErrorLog(ctx, "Unknown literal in interpretation"));
        return null;
    }

    @Override
    public InterpreterValue visitBool(UCELParser.BoolContext ctx) {
        if(ctx.TRUE() != null)
            return new BooleanValue(true);

        else if(ctx.FALSE() != null)
            return new BooleanValue(false);
        else {
            logger.log(new ErrorLog(ctx, "Bool is somehow neither true nor false"));
            return null;
        }
    }

}
