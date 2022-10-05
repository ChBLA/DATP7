import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ReferenceHandlerTests {

    //region FuncCall
    @Test
    void funcCallExpressionSuccesfulLookup() {
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

        assertEquals(funcCallContext.reference, new DeclarationReference(1,2));
    }

    @Test
    void funcCallExpressionVisitArguments() {
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
    void idExprSuccessfulLookupInImmediateScope() {
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

        assertEquals(idExprContext.reference, new DeclarationReference(0,2));
    }

    @Test
    void idExprSuccessfulLookupInDistantScope() {
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

        assertEquals(idExprContext.reference, new DeclarationReference(1,2));
    }

    @Test
    void idExprUnsuccessfulLookupThrows() {
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

    //region variableDecl

    @ParameterizedTest
    @ValueSource(ints = {0,1,2})
    void variableDeclVisitAllVariableIDs(int i) {
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false));
        UCELParser.VariableDeclContext node = mock(UCELParser.VariableDeclContext.class);
        ArrayList<UCELParser.VariableIDContext> varIDs = new ArrayList<>();
        varIDs.add(mock(UCELParser.VariableIDContext.class));
        varIDs.add(mock(UCELParser.VariableIDContext.class));
        varIDs.add(mock(UCELParser.VariableIDContext.class));

        when(node.variableID()).thenReturn(varIDs);

        visitor.visitVariableDecl(node);

        verify(varIDs.get(i), times(1)).accept(visitor);
    }

    //endregion

    //region

    @Test
    void variableIDAddVariableToScope() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<Variable> variables =new ArrayList<>();
        variables.add(new Variable(incorrectVariableName));
        variables.add(new Variable(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.VariableIDContext node = mock(UCELParser.VariableIDContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        visitor.visitVariableID(node);

        assertEquals(new Variable(correctVariableName), variables.get(2));
    }

    @Test
    void variableIDSetVariableReference() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<Variable> variables =new ArrayList<>();
        variables.add(new Variable(incorrectVariableName));
        variables.add(new Variable(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.VariableIDContext node = mock(UCELParser.VariableIDContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        visitor.visitVariableID(node);

        assertEquals(node.reference, new DeclarationReference(0, 2));
    }

    @Test
    void variableIDReturnFalseOnDuplicate() {
        String uniqueVariableName = "uvn";
        String doubleicateVariableName = "dvn";

        ArrayList<Variable> variables =new ArrayList<>();
        variables.add(new Variable(uniqueVariableName));
        variables.add(new Variable(doubleicateVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.VariableIDContext node = mock(UCELParser.VariableIDContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(doubleicateVariableName);
        when(node.ID()).thenReturn(idNode);

        boolean result = visitor.visitVariableID(node);

        assertFalse(result);
    }

    //endregion

    @Test
    void addScopeForBlock() {

        Scope parentScope = new Scope(null, false);
        ReferenceVisitor referenceVisitor = new ReferenceVisitor(parentScope);



    }

}
