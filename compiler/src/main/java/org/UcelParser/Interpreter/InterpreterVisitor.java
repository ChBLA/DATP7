package org.UcelParser.Interpreter;

import org.UcelParser.Util.*;
import org.UcelParser.Util.Logging.ErrorLog;
import org.UcelParser.Util.Logging.ILogger;
import org.UcelParser.Util.Logging.Log;
import org.UcelParser.Util.Value.*;
import org.UcelParser.UCELParser_Generated.UCELBaseVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.Objects;

public class InterpreterVisitor extends UCELBaseVisitor<InterpreterValue> {

    //region Header

    private Scope currentScope;
    private ILogger logger;

    private int nextInterfaceId;

    public InterpreterVisitor(Scope scope) {
        currentScope = scope;
        nextInterfaceId = 0;
    }

    public InterpreterVisitor(ILogger logger) {
        this.logger = logger;
        nextInterfaceId = 0;
    }

    public InterpreterVisitor(ILogger logger, Scope scope) {
        this.logger = logger;
        this.currentScope = scope;
    }

    //endregion

    //region Project / Manual Parser
    @Override
    public InterpreterValue visitProject(UCELParser.ProjectContext ctx) {
        // project locals [Scope scope]
        //    : pdeclaration ptemplate* psystem;

        enterScope(ctx.scope);
        // Only psystem can contain build statements for interpretation
        var visitRes = visit(ctx.psystem());
        exitScope();

        return visitRes;
    }

    @Override
    public InterpreterValue visitPsystem(UCELParser.PsystemContext ctx) {
        // psystem : declarations (build | system);
        boolean hadError = false;

        var build = ctx.build();
        var sys = ctx.system();

        if(build != null) {
            if (visit(build) == null)
                hadError = true;
        }
        else if(sys != null) {
            if(visit(sys) == null)
                hadError = true;
        }
        else {
            logger.log(new ErrorLog(ctx, "Compiler error: No system or build in project"));
            hadError = true;
        }

        return hadError ? null : new VoidValue();
    }

    //endregion

    //region Scope
    private void enterScope(Scope scope) {
        currentScope = scope;
    }

    private void exitScope() {
        this.currentScope = this.currentScope.getParent();
    }
    //endregion

    //region Expressions
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

        return new PointerValue(id, left);
    }

    @Override
    public InterpreterValue visitUnaryExpr(UCELParser.UnaryExprContext ctx) {
        // PLUS | MINUS | NEG | NOT;

        var exprVal = visit(ctx.expression());
        if(exprVal == null)
            return null;

        var unary = ctx.unary();
        if(unary.PLUS() != null) {
            if(!(exprVal instanceof IntegerValue)) {
                logger.log(new ErrorLog(ctx,"Unary `+` only applicable on integers in the interpreter"));
                return null;
            }
            var intVal = ((IntegerValue) exprVal).getInt();
            return new IntegerValue(intVal);
        }

        if(unary.MINUS() != null) {
            if(!(exprVal instanceof IntegerValue)) {
                logger.log(new ErrorLog(ctx,"Unary `-` only applicable on integers in the interpreter"));
                return null;
            }
            var intVal = ((IntegerValue) exprVal).getInt();
            return new IntegerValue(-intVal);
        }

        if(unary.NEG() != null) {
            if(!(exprVal instanceof BooleanValue)) {
                logger.log(new ErrorLog(ctx,"Unary `!` only applicable on booleans in the interpreter"));
                return null;
            }
            var boolVal = ((BooleanValue) exprVal).getBool();
            return new BooleanValue(!boolVal);
        }

        if(unary.NOT() != null) {
            if(!(exprVal instanceof BooleanValue)) {
                logger.log(new ErrorLog(ctx,"Unary `not` only applicable on booleans in the interpreter"));
                return null;
            }
            var boolVal = ((BooleanValue) exprVal).getBool();
            return new BooleanValue(!boolVal);
        }

        logger.log(new ErrorLog(ctx,"Unknown unary operator in interpreter"));
        return null;
    }

    @Override
    public InterpreterValue visitRelExpr(UCELParser.RelExprContext ctx) {
        var left = visit(ctx.expression(0));
        var right = visit(ctx.expression(1));

        if(left == null || right == null)
            return null;

        if(!(left instanceof IntegerValue) || !(right instanceof IntegerValue)) {
            logger.log(new ErrorLog(ctx, "Relative comparison only supports integers in interpreter"));
            return null;
        }

        var leftVal = ((IntegerValue) left).getInt();
        var rightVal = ((IntegerValue) right).getInt();

        // op=('<' | '<=' | '>=' | '>')
        switch (ctx.op.getText()) {
            case "<" : return new BooleanValue(leftVal < rightVal);
            case "<=": return new BooleanValue(leftVal <= rightVal);
            case ">=": return new BooleanValue(leftVal >= rightVal);
            case ">" : return new BooleanValue(leftVal > rightVal);
            default:
                logger.log(new ErrorLog(ctx, "Unknown relExpr operator `" + ctx.op.getText() + "` in interpreter"));
                return null;
        }
    }

    @Override
    public InterpreterValue visitEqExpr(UCELParser.EqExprContext ctx) {
        var v0 = visit(ctx.expression(0));
        var v1 = visit(ctx.expression(1));

        boolean isEqual = v0.equals(v1);

        // op=('==' | '!=')
        switch (ctx.op.getText()) {
            case "==": return new BooleanValue(isEqual);
            case "!=": return new BooleanValue(!isEqual);
            default:
                logger.log(new ErrorLog(ctx, "Unknown equality operator `"+ctx.op.getText()+"` in interpreter"));
                return null;
        }
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


    //endregion

    //region Control Flow
    @Override
    public InterpreterValue visitBuildBlock(UCELParser.BuildBlockContext ctx) {
        enterScope(ctx.scope);
        var hadError = false;
        for(var stmt: ctx.buildStmnt()) {
            if (visit(stmt) == null)
                hadError = true;
        }

        exitScope();

        if(hadError)
            return null;

        return new VoidValue();
    }

    @Override
    public InterpreterValue visitBuildStmnt(UCELParser.BuildStmntContext ctx) {
        return visit(ctx.children.get(0));
    }

    @Override
    public InterpreterValue visitBuildIf(UCELParser.BuildIfContext ctx) {
        // | IF LEFTPAR expression RIGHTPAR buildStmnt ( ELSE buildStmnt )?  #BuildIf
        var predicate = visit(ctx.expression());
        if(predicate == null)
            return null;
        if(!isBoolValue(predicate)) {
            logger.log(new ErrorLog(ctx, "Predicate must be of type boolean"));
            return null;
        }

        var predicateVal = ((BooleanValue)predicate).getBool();

        if(predicateVal) {
            var stmtReturn = visit(ctx.buildStmnt(0));
            if(stmtReturn == null)
                return null;
        }

        else {
            var elseStmt = ctx.buildStmnt(1);
            if(elseStmt != null) {
                var stmtReturn = visit(elseStmt);
                if(stmtReturn == null)
                    return null;
            }
        }

        return new VoidValue();
    }

    @Override
    public InterpreterValue visitBuildIteration(UCELParser.BuildIterationContext ctx) {
        int rangeStart = ((IntegerValue)visit(ctx.expression(0))).getInt();
        int rangeEnd   = ((IntegerValue)visit(ctx.expression(1))).getInt();

        if(rangeStart > rangeEnd) {
            logger.log(new ErrorLog(ctx, "Lower bound must not be greater than upper bound"));
            return null;
        };

        for(int i=rangeStart; i<=rangeEnd; i++) {
            try {
                currentScope.get(ctx.reference).setValue(new IntegerValue(i));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            var stmtReturn = visit(ctx.buildStmnt());
            if(stmtReturn == null)
                return null;
        }

        return new VoidValue();
    }


    //endregion

    //region Build / Linker
    /*

    DESIGN OF BUILD:
     -  Reference handler sets a declInfo on scope for the buildDecl (comp or template variable) and points
        the left hand side of the compCon to it
     -  The interpreter sets a ListValue on the declInfo
     -  visitCompCon in the interpreter adds a CompOccurrenceValue to the ListValue on declInfo for the buildDecl
     -  visitCompCon returns a voidValue or null
     -  at the end of visitBuild the occurrences on the comp node are set before that node is
        visited by the interpreter. visiting must be done last as all linking must have taken place.
     -  it must be checked by the end of build that all indices for buildDecls are used,
        that there are no duplicates and that all interfaces have been set.

    */

    @Override
    public InterpreterValue visitDeclarations(UCELParser.DeclarationsContext ctx) {
        // (variableDecl | typeDecl | function | chanPriority | instantiation | component | interfaceDecl)*;

        boolean hadError = false;

        var varDecls = ctx.variableDecl();
        if(varDecls != null) {
            for(var vardecl: varDecls) {
                if(visit(vardecl) == null)
                    hadError = true;
            }
        }

        return hadError ? null : new VoidValue();
    }

    @Override
    public InterpreterValue visitVariableDecl(UCELParser.VariableDeclContext ctx) {
        // variableDecl  : type variableID (COMMA variableID)* END;

        boolean hadError = false;

        for(var varId: ctx.variableID()) {
            if(visit(varId) == null)
                hadError = true;
        }

        return hadError ? null : new VoidValue();
    }

    @Override
    public InterpreterValue visitInitialiser(UCELParser.InitialiserContext ctx) {
        // initialiser   : expression?
        //               | LEFTCURLYBRACE initialiser (COMMA initialiser)* RIGHTCURLYBRACE;

        var expr = ctx.expression();
        if(expr != null) {
            return visit(expr);
        }

        return null; // Struct, should not be interpreted
    }

    @Override
    public InterpreterValue visitCompVar(UCELParser.CompVarContext ctx) {
        int size = ctx.expression() != null ? ctx.expression().size() : 0;
        int[] indices = new int[size];

        for(int i = 0; i < ctx.expression().size(); i++) {
            InterpreterValue v = visit(ctx.expression().get(i));
            if(isIntegerValue(v)) indices[i] = ((IntegerValue) v).getInt();
            else return null;
        }

        String id = "";

        try {
            id = currentScope.get(ctx.variableReference).getIdentifier();
        } catch (Exception e) {return null;}


        return new CompVarValue(id, indices);
    }

    @Override
    public InterpreterValue visitCompCon(UCELParser.CompConContext ctx) {

        InterpreterValue iv = visit(ctx.compVar());
        if(iv == null || !(iv instanceof CompVarValue))
            return null;
        CompVarValue compVarValue = (CompVarValue) iv;

        int argCount = ctx.arguments() == null ? 0 : ctx.arguments().expression().size();
        InterpreterValue[] arguments = new InterpreterValue[argCount];

        for(int i = 0; i < argCount; i++) {
            InterpreterValue v = visit(ctx.arguments().expression().get(i));
            if(v != null) arguments[i] = v;
            else return null;
        }

        try {
            DeclarationInfo declInfo = currentScope.get(ctx.compVar().variableReference);

            int[] indices = compVarValue.getIndices();
            ListValue listValue = getInnerList(indices, declInfo.getValue());

            InterpreterValue occurrenceValue = null;

            if(declInfo.getNode() instanceof UCELParser.ComponentContext) {
                UCELParser.ComponentContext compNode = ((UCELParser.ComponentContext) declInfo.getNode());
                Type compType = compNode.scope.getParent().get(compNode.reference).getType();

                occurrenceValue = new CompOccurrenceValue(ctx.ID().getText(), arguments, compType);
            } else if(declInfo.getNode() instanceof UCELParser.PtemplateContext) {
                occurrenceValue = new TemplateOccurrenceValue(ctx.ID().getText(), arguments);
            } else {
                return null;
            }

            if(listValue == null) {
                declInfo.setValue(occurrenceValue);
            } else {
                if(listValue.getValue(lastIndex(indices)) != null) {
                    logger.log(new ErrorLog(ctx, "two components or processes with indices: " + indices));
                }
                listValue.setValue(lastIndex(indices), occurrenceValue);
            }

        } catch (Exception e) {
            return null;
        }

        return new VoidValue();
    }

    //region helperFunctions
    private int lastIndex(int[] indices) {
        return indices[indices.length - 1];
    }

    private ListValue getInnerList(int indices[], InterpreterValue value) {
        if(!(value instanceof ListValue)) {
            return null;
        }
        ListValue listValue = (ListValue) value;
        for(int i = 0; i < indices.length - 1; i++) {
            listValue = (ListValue) listValue.getValue(indices[i]);
        }
        return listValue;
    }

    private InterpreterValue getValueFromMultiDimArray(int indices[], InterpreterValue value) {
        if(!(value instanceof  ListValue))
            return value;
        ListValue listValue = (ListValue) value;
        for(int i = 0; i < indices.length - 1; i++) {
            listValue = (ListValue) listValue.getValue(indices[i]);
        }
        return listValue.getValue(lastIndex(indices));
    }

    //endregion

    @Override
    public InterpreterValue visitLinkStatement(UCELParser.LinkStatementContext ctx) {
        InterpreterValue left = visit(ctx.compVar().get(0));
        InterpreterValue right = visit(ctx.compVar().get(1));

        if(!isCompVarValue(left) || !isCompVarValue(right))
            return null;

        CompVarValue compVarLeft = (CompVarValue) left;
        CompVarValue compVarRight = (CompVarValue) right;

        DeclarationInfo leftInfo;
        DeclarationInfo rightInfo;

        try{
            //Get the DeclInfo of each buildDecl
            leftInfo = currentScope.get(ctx.compVar().get(0).variableReference);
            rightInfo = currentScope.get(ctx.compVar().get(1).variableReference);

            InterpreterValue leftValue = leftInfo.getValue();
            InterpreterValue rightValue = rightInfo.getValue();

            //Find the occurrenceValues in each declInfo
            int[] leftIndices = compVarLeft.getIndices();
            int[] rightIndices = compVarRight.getIndices();
            CompOccurrenceValue leftCompOcc = (CompOccurrenceValue) getValueFromMultiDimArray(leftIndices, leftValue);
            CompOccurrenceValue rightCompOcc = (CompOccurrenceValue) getValueFromMultiDimArray(rightIndices, rightValue);

            //Construct the interface values
            int leftParamNum = ctx.leftInterface.getDeclarationId();
            int rightParamNum = ctx.rightInterface.getDeclarationId();

            int id = nextInterfaceId;
            nextInterfaceId++;
            StringValue interfaceAlias = new StringValue(Integer.toString(id));
            InterfaceValue leftInterfaceValue = new InterfaceValue(leftParamNum, id, interfaceAlias);
            InterfaceValue rightInterfaceValue = new InterfaceValue(rightParamNum, id, interfaceAlias);

            //Set the interfaceValues on occurrenceValues
            leftCompOcc.getInterfaces()[leftParamNum] = leftInterfaceValue;
            rightCompOcc.getInterfaces()[rightParamNum] = rightInterfaceValue;

            //Set stringValue on interface
            var leftInterface = extractInterfaceDefFromLink(ctx, 0, ctx.leftInterface);
            var rightInterface = extractInterfaceDefFromLink(ctx, 1, ctx.rightInterface);

            assert Objects.equals(leftInterface, rightInterface);
            assert leftInterface != null;
            if (leftInterface.occurrences == null)
                leftInterface.occurrences = new ArrayList<>();
            leftInterface.occurrences.add(interfaceAlias);

        } catch (Exception e) {
            return null;
        }

        return new VoidValue();
    }

    //region link helper functions

    private UCELParser.InterfaceDeclContext extractInterfaceDefFromLink(UCELParser.LinkStatementContext node, int index, DeclarationReference ref) {
        UCELParser.InterfaceDeclContext interfaceNode;
        try {
            UCELParser.ComponentContext componentNode = (UCELParser.ComponentContext) currentScope.get(node.compVar(index).variableReference).getNode();
            UCELParser.ParameterContext interfaceTypeNode = componentNode.interfaces().parameters().parameter(ref.getDeclarationId());
            DeclarationReference interfaceRef = interfaceTypeNode.type().typeId().reference;
            interfaceNode = (UCELParser.InterfaceDeclContext) componentNode.scope.get(interfaceRef).getNode();
        } catch (Exception e) {
            return null;
        }
        return interfaceNode;
    }

    private UCELParser.ComponentContext getCompCtxNode(ParserRuleContext prc){
        return prc != null && prc instanceof UCELParser.CompConContext ? (UCELParser.ComponentContext) prc : null;
    }

    private boolean isCompVarValue(InterpreterValue value) {
        return value != null && value instanceof CompVarValue;
    }

    //endregion

    @Override
    public InterpreterValue visitBuildDecl(UCELParser.BuildDeclContext ctx) {
        CompVarValue compVarValue = (CompVarValue) visit(ctx.compVar());

        try {
            InterpreterValue value = recBuildMultiDimLists(compVarValue.getIndices(),
                    compVarValue.getIndices().length - 1 );
            currentScope.get(ctx.compVar().variableReference).setValue(value);
        } catch (Exception e) {
            return null;
        }

        return new VoidValue();
    }

    public InterpreterValue recBuildMultiDimLists(int[] sizes, int layer) {
        if(layer <= 0) return null;
        if(layer == 1) return new ListValue(lastIndex(sizes));

        InterpreterValue[] values = new InterpreterValue[sizes[layer]];

        for(int i = 0; i < sizes[layer]; i++) {
            values[i] = recBuildMultiDimLists(sizes, layer - 1);
        }
        return new ListValue(values);
    }

    @Override
    public InterpreterValue visitBuild(UCELParser.BuildContext ctx) {
        for(UCELParser.BuildDeclContext buildDecl : ctx.buildDecl()) {
            InterpreterValue value = visit(buildDecl);
            if(value == null || !(value instanceof VoidValue))
                return null;
        }

        for(UCELParser.BuildStmntContext buildStmnt : ctx.buildStmnt()) {
            InterpreterValue value = visit(buildStmnt);
            if(value == null || !(value instanceof VoidValue))
                return null;
        }

        for(UCELParser.BuildDeclContext buildDecl : ctx.buildDecl()) {


            try {
                InterpreterValue value = null;
                DeclarationReference variableReference = buildDecl.compVar().variableReference;
                ParserRuleContext node = currentScope.get(variableReference).getNode();

                value = currentScope.get(variableReference).getValue();

                if(!visitCompWithAllOccurrences(node, value, ""))
                    return null;
            } catch (Exception e) {
                return null;
            }
        }

        return new VoidValue();
    }

    private boolean visitCompWithAllOccurrences(ParserRuleContext node,
                                                InterpreterValue value, String indices) {
        if(value instanceof ListValue) {
            ListValue listvalue = (ListValue) value;

            for(int i = 0; i < listvalue.size(); i++) {
                boolean b = visitCompWithAllOccurrences(node,
                        listvalue.getValue(i), indices + "_" + i);
                if(!b) return false;
            }

            return true;
        } else if(value instanceof CompOccurrenceValue && node instanceof UCELParser.ComponentContext) {
            UCELParser.ComponentContext componentNode = (UCELParser.ComponentContext) node;
            if(componentNode.occurrences == null)
                componentNode.occurrences = new ArrayList<>();
            return visitCompWithOccurrence(componentNode, (CompOccurrenceValue) value, indices);
        } else if(value instanceof TemplateOccurrenceValue && node instanceof UCELParser.PtemplateContext) {
            UCELParser.PtemplateContext componentNode = (UCELParser.PtemplateContext) node;
            if(componentNode.occurrences == null)
                componentNode.occurrences = new ArrayList<>();
            return visitTempWithOccurrence(componentNode, (TemplateOccurrenceValue) value, indices);
        } else {
            return false;
        }
    }

    private boolean visitCompWithOccurrence(UCELParser.ComponentContext componentNode, CompOccurrenceValue value, String indices) {
        ComponentOccurrence componentOccurrence = new ComponentOccurrence(value.generateName() + indices,
                value.getArguments(), value.getInterfaces());

        componentNode.occurrences.add(componentOccurrence);

        if(componentNode.compBody().build() == null) {
            //No build block means nothing needs to be interpreted
            return true;
        }

        Scope oldScope = currentScope;
        enterScope(componentNode.scope);

        //Set parameters
        try {
            var parameters = componentNode.parameters().parameter();
            for(int i = 0; i < parameters.size(); i++) {
                UCELParser.ParameterContext paramCtx = parameters.get(i);
                DeclarationInfo parameterInfo = currentScope.get(paramCtx.reference);
                parameterInfo.setValue(value.getArguments()[i]);
            }

            var interFaceParameters = componentNode.interfaces().parameters().parameter();
            for(int i = 0; i < interFaceParameters.size(); i++) {
                UCELParser.ParameterContext paramCtx = interFaceParameters.get(i);
                DeclarationInfo parameterInfo = currentScope.get(paramCtx.reference);
                parameterInfo.setValue(value.getInterfaces()[i]);
            }
        } catch (Exception e) {
            return false;
        }

        enterScope(componentNode.compBody().scope);
        visit(componentNode.compBody().build());
        currentScope = oldScope;

        return true;
    }

    private boolean visitTempWithOccurrence(UCELParser.PtemplateContext templateNode, TemplateOccurrenceValue value, String indices) {
        TemplateOccurrence templateOccurrence = new TemplateOccurrence(value.generateName() + indices,
                value.getArguments());

        templateNode.occurrences.add(templateOccurrence);

        return true;
    }

    //endregion

    //region Helper Functions
    private boolean isIntegerValue(InterpreterValue v) {
        return v != null && v instanceof IntegerValue;
    }

    private boolean isStringValue(InterpreterValue v) {
        return v instanceof StringValue && v.generateName() != null;
    }

    private boolean isBoolValue(InterpreterValue v) {
        return v != null && v instanceof BooleanValue;
    }
    //endregion
}
