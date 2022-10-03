import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;

import javax.naming.Reference;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ReferenceHandlerTests {

    //region FuncCall
    @Test
    void FuncCallExpressionSuccesfulLookup() {
        String correctFunctionName = "cfn";
        String incorrectFunctionName = "icfn";

        ArrayList<Variable> lvl1Variables = new ArrayList<>();
        lvl1Variables.add(new Variable(incorrectFunctionName));
        lvl1Variables.add(new Variable(incorrectFunctionName));
        lvl1Variables.add(new Variable(correctFunctionName));
        lvl1Variables.add(new Variable(incorrectFunctionName));

        ArrayList<Variable> lvl0Variables = new ArrayList<>();

        Scope lvl1Scope = new Scope(null, false, lvl1Variables);
        Scope lvl0Scope = new Scope(lvl1Scope, false, lvl0Variables);
        ReferenceVisitor visitor = new ReferenceVisitor(lvl0Scope);

        UCELParser.FuncCallContext funcCallContext = mock(UCELParser.FuncCallContext.class);
        UCELParser.ArgumentsContext argumentsContext = mock(UCELParser.ArgumentsContext.class);

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctFunctionName);
        when(funcCallContext.ID()).thenReturn(idNode);
        when(funcCallContext.arguments()).thenReturn(argumentsContext);

        visitor.visitFuncCall(funcCallContext);

        assertEquals(funcCallContext.reference, new TableReference(1,2));
    }

    @Test
    void FuncCallExpressionVisitArguments() {
        String correctFunctionName = "cfn";
        String incorrectFunctionName = "icfn";

        ArrayList<Variable> lvl1Variables = new ArrayList<>();
        lvl1Variables.add(new Variable(incorrectFunctionName));
        lvl1Variables.add(new Variable(incorrectFunctionName));
        lvl1Variables.add(new Variable(correctFunctionName));
        lvl1Variables.add(new Variable(incorrectFunctionName));

        ArrayList<Variable> lvl0Variables = new ArrayList<>();

        Scope lvl1Scope = new Scope(null, false, lvl1Variables);
        Scope lvl0Scope = new Scope(lvl1Scope, false, lvl0Variables);
        ReferenceVisitor visitor = new ReferenceVisitor(lvl0Scope);

        UCELParser.FuncCallContext funcCallContext = mock(UCELParser.FuncCallContext.class);
        UCELParser.ArgumentsContext argumentsContext = mock(UCELParser.ArgumentsContext.class);

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctFunctionName);
        when(funcCallContext.ID()).thenReturn(idNode);
        when(funcCallContext.arguments()).thenReturn(argumentsContext);

        visitor.visitFuncCall(funcCallContext);

        verify(argumentsContext, times(1)).accept(visitor);
    }

    //endregion

    //region IDExpr: look in immediate scope, look through multiple scope, fail to find
    @Test
    void IDExprSuccessfulLookupInImmediateScope() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<Variable> variables =new ArrayList<>();
        variables.add(new Variable(incorrectVariableName));
        variables.add(new Variable(incorrectVariableName));
        variables.add(new Variable(correctVariableName));
        variables.add(new Variable(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.IdExprContext idExprContext = mock(UCELParser.IdExprContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(idExprContext.ID()).thenReturn(idNode);

        visitor.visitIdExpr(idExprContext);

        assertEquals(idExprContext.reference, new TableReference(0,2));
    }

    @Test
    void IDExprSuccessfulLookupInDistantScope() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<Variable> lvl1Variables = new ArrayList<>();
        lvl1Variables.add(new Variable(incorrectVariableName));
        lvl1Variables.add(new Variable(incorrectVariableName));
        lvl1Variables.add(new Variable(correctVariableName));
        lvl1Variables.add(new Variable(incorrectVariableName));
        ArrayList<Variable> lvl0Variables = new ArrayList<>();

        Scope lvl1Scope = new Scope(null, false, lvl1Variables);
        Scope lvl0Scope = new Scope(lvl1Scope, false, lvl0Variables);
        ReferenceVisitor visitor = new ReferenceVisitor(lvl0Scope);

        UCELParser.IdExprContext idExprContext = mock(UCELParser.IdExprContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(idExprContext.ID()).thenReturn(idNode);

        visitor.visitIdExpr(idExprContext);

        assertEquals(idExprContext.reference, new TableReference(1,2));
    }

    @Test
    void IDExprUnsuccessfulLookupThrows() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<Variable> variables =new ArrayList<>();
        variables.add(new Variable(incorrectVariableName));
        variables.add(new Variable(incorrectVariableName));
        variables.add(new Variable(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.IdExprContext idExprContext = mock(UCELParser.IdExprContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(idExprContext.ID()).thenReturn(idNode);

        boolean actualReturnValue = visitor.visitIdExpr(idExprContext);

        assertFalse(actualReturnValue);
    }
    //endregion


}
