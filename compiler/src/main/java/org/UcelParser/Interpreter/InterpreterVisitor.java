package org.UcelParser.Interpreter;

import org.UcelParser.Util.DeclarationInfo;
import org.UcelParser.Util.Value.*;
import org.UcelParser.UCELParser_Generated.UCELBaseVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Logging.Logger;
import org.UcelParser.Util.Scope;

import java.util.ArrayList;

public class InterpreterVisitor extends UCELBaseVisitor<InterpreterValue> {

    //region Header

    private Scope currentScope;
    private Logger logger;

    public InterpreterVisitor(Scope scope) {
        currentScope = scope;
    }

    public InterpreterVisitor(Logger logger) {
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

        if(!areIntegerValues(left, right)) return null;
        IntegerValue intLeft = (IntegerValue) left;
        IntegerValue intRight = (IntegerValue) right;
        int op = ctx.op.getText().equals("+") ? 1 : -1;
        return new IntegerValue(intLeft.getInt() + op * intRight.getInt());
    }

    @Override
    public InterpreterValue visitMultDiv(UCELParser.MultDivContext ctx) {
        InterpreterValue left = visit(ctx.expression().get(0));
        InterpreterValue right = visit(ctx.expression().get(1));

        if(!areIntegerValues(left, right)) return null;
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

    private boolean areIntegerValues(InterpreterValue l, InterpreterValue r) {
        return l != null && r != null && l instanceof IntegerValue && r instanceof IntegerValue;
    }

    public InterpreterValue visitIdExpr(UCELParser.IdExprContext ctx) {
        try{
            DeclarationInfo declInfo = currentScope.get(ctx.reference);
            return declInfo.getValue() == null ? new StringValue(declInfo.generateName()) : declInfo.getValue();
        } catch (Exception e) {
            return null;
        }
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
}
