import ReferenceHandler.ReferenceVisitor;
import UCELParser_Generated.UCELParser;
import Util.DeclarationInfo;
import Util.DeclarationReference;
import Util.Scope;
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

        ArrayList<DeclarationInfo> lvl1Variables = new ArrayList<>();
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));
        lvl1Variables.add(new DeclarationInfo(correctFunctionName));
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));

        ArrayList<DeclarationInfo> lvl0Variables = new ArrayList<>();

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

        ArrayList<DeclarationInfo> lvl1Variables = new ArrayList<>();
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));
        lvl1Variables.add(new DeclarationInfo(correctFunctionName));
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));

        ArrayList<DeclarationInfo> lvl0Variables = new ArrayList<>();

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

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(correctVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
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

        ArrayList<DeclarationInfo> lvl1Variables = new ArrayList<>();
        lvl1Variables.add(new DeclarationInfo(incorrectVariableName));
        lvl1Variables.add(new DeclarationInfo(incorrectVariableName));
        lvl1Variables.add(new DeclarationInfo(correctVariableName));
        lvl1Variables.add(new DeclarationInfo(incorrectVariableName));
        ArrayList<DeclarationInfo> lvl0Variables = new ArrayList<>();

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

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
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

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.VariableIDContext node = mock(UCELParser.VariableIDContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        visitor.visitVariableID(node);

        assertEquals(new DeclarationInfo(correctVariableName), variables.get(2));
    }

    @Test
    void variableIDSetVariableReference() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
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

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(uniqueVariableName));
        variables.add(new DeclarationInfo(doubleicateVariableName));
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
    void scopeForBlockIsAdded() {

        Scope parentScope = new Scope(null, false);
        ReferenceVisitor referenceVisitor = new ReferenceVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);

        referenceVisitor.visitBlock(block);

        assertTrue(block.scope instanceof Scope);
        assertNotEquals(block.scope, parentScope);
    }

    @Test
    void declarationsForBlockAreVisited() {

        Scope parentScope = new Scope(null, false);
        ReferenceVisitor referenceVisitor = new ReferenceVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        UCELParser.LocalDeclarationContext localDecl = mock(UCELParser.LocalDeclarationContext.class);
        ArrayList<UCELParser.LocalDeclarationContext> localDecls = new ArrayList<>();
        localDecls.add(localDecl);

        when(localDecl.accept(referenceVisitor)).thenReturn(true);
        when(block.localDeclaration()).thenReturn(localDecls);

        referenceVisitor.visitBlock(block);

        verify(localDecl, times(1)).accept(referenceVisitor);
    }

    @Test
    void statementsForBlockAreVisited() {

        Scope parentScope = new Scope(null, false);
        ReferenceVisitor referenceVisitor = new ReferenceVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        UCELParser.StatementContext statementContext = mock(UCELParser.StatementContext.class);
        ArrayList<UCELParser.StatementContext> statementContexts = new ArrayList<>();
        statementContexts.add(statementContext);

        when(statementContext.accept(referenceVisitor)).thenReturn(true);
        when(block.statement()).thenReturn(statementContexts);

        referenceVisitor.visitBlock(block);

        verify(statementContext, times(1)).accept(referenceVisitor);
    }

}
