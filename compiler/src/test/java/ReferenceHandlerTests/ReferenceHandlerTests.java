package ReferenceHandlerTests;

import org.UcelParser.ReferenceHandler.ReferenceVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.DeclarationInfo;
import org.UcelParser.Util.DeclarationReference;
import org.UcelParser.Util.FuncCallOccurrence;
import org.UcelParser.Util.Scope;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.*;
import java.sql.Ref;
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

    //region VariableID

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
    void variableIDVisitInitialiser() {
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

        UCELParser.InitialiserContext initialiser = mock(UCELParser.InitialiserContext.class);

        when(node.initialiser()).thenReturn(initialiser);
        when(initialiser.accept(visitor)).thenReturn(true);

        visitor.visitVariableID(node);

        verify(initialiser, times(1)).accept(visitor);
    }

    @Test
    void variableIDVisitArrayDecl() {
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

        ArrayList<UCELParser.ArrayDeclContext> arrayDecls = new ArrayList<>();

        UCELParser.ArrayDeclContext arrayDecl0 = mock(UCELParser.ArrayDeclContext.class);
        UCELParser.ArrayDeclContext arrayDecl1 = mock(UCELParser.ArrayDeclContext.class);
        UCELParser.ArrayDeclContext arrayDecl2 = mock(UCELParser.ArrayDeclContext.class);

        arrayDecls.add(arrayDecl0);
        arrayDecls.add(arrayDecl1);
        arrayDecls.add(arrayDecl2);

        when(node.arrayDecl()).thenReturn(arrayDecls);
        when(arrayDecl0.accept(visitor)).thenReturn(true);
        when(arrayDecl1.accept(visitor)).thenReturn(true);
        when(arrayDecl2.accept(visitor)).thenReturn(true);

        visitor.visitVariableID(node);

        verify(arrayDecl1, times(1)).accept(visitor);
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

    //region Block

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

    //endregion

    //region iteration

    @Test
    void iterationReferenceSet() {
        String identifier = "a";
        Scope scope = mock(Scope.class);

        ReferenceVisitor referenceVisitor = new ReferenceVisitor(scope);

        UCELParser.IterationContext node = mock(UCELParser.IterationContext.class);
        TerminalNode id = mock(TerminalNode.class);
        UCELParser.StatementContext statement = mock(UCELParser.StatementContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        when(node.type()).thenReturn(type);
        when(node.statement()).thenReturn(statement);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(identifier);

        when(type.accept(referenceVisitor)).thenReturn(true);
        when(statement.accept(referenceVisitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(identifier, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {
            fail();
        }

        referenceVisitor.visitIteration(node);

        assertEquals(declRef, node.reference);
    }

    @Test
    void iterationStatementVisited() {
        String identifier = "a";
        Scope scope = mock(Scope.class);

        ReferenceVisitor referenceVisitor = new ReferenceVisitor(scope);

        UCELParser.IterationContext node = mock(UCELParser.IterationContext.class);
        TerminalNode id = mock(TerminalNode.class);
        UCELParser.StatementContext statement = mock(UCELParser.StatementContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        when(node.type()).thenReturn(type);
        when(node.statement()).thenReturn(statement);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(identifier);

        when(type.accept(referenceVisitor)).thenReturn(true);
        when(statement.accept(referenceVisitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(identifier, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {
            fail();
        }

        referenceVisitor.visitIteration(node);

        verify(statement, times(1)).accept(referenceVisitor);
    }

    //endregion

    //region increment/decrement

    @Test
    void incrementPostAcceptIDExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPostContext node = mock(UCELParser.IncrementPostContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPost(node);

        assertTrue(actual);
    }

    @Test
    void incrementPreAcceptIDExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPreContext node = mock(UCELParser.IncrementPreContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPre(node);

        assertTrue(actual);
    }

    @Test
    void decrementPostAcceptIDExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPostContext node = mock(UCELParser.DecrementPostContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPost(node);

        assertTrue(actual);
    }

    @Test
    void decrementPreAcceptExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPreContext node = mock(UCELParser.DecrementPreContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPre(node);

        assertTrue(actual);
    }

    @Test
    void incrementPostAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPostContext node = mock(UCELParser.IncrementPostContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPost(node);

        assertTrue(actual);
    }

    @Test
    void incrementPreAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPreContext node = mock(UCELParser.IncrementPreContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPre(node);

        assertTrue(actual);
    }

    @Test
    void decrementPostAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPostContext node = mock(UCELParser.DecrementPostContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPost(node);

        assertTrue(actual);
    }

    @Test
    void decrementPreAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPreContext node = mock(UCELParser.DecrementPreContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPre(node);

        assertTrue(actual);
    }

    @Test
    void incrementPostInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPostContext node = mock(UCELParser.IncrementPostContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPost(node);

        assertFalse(actual);
    }

    @Test
    void incrementPreInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPreContext node = mock(UCELParser.IncrementPreContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPre(node);

        assertFalse(actual);
    }

    @Test
    void decrementPostInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPostContext node = mock(UCELParser.DecrementPostContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPost(node);

        assertFalse(actual);
    }

    @Test
    void decrementPreInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPreContext node = mock(UCELParser.DecrementPreContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPre(node);

        assertFalse(actual);
    }

    //endregion

    //region assignment
    @Test
    void assignmentAcceptIDExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.AssignExprContext node = mock(UCELParser.AssignExprContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        when(node.expression(0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitAssignExpr(node);

        assertTrue(actual);
    }

    @Test
    void assignmentPostAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.AssignExprContext node = mock(UCELParser.AssignExprContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        when(node.expression(0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitAssignExpr(node);

        assertTrue(actual);
    }


    @Test
    void assignmentInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.AssignExprContext node = mock(UCELParser.AssignExprContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.expression(0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitAssignExpr(node);

        assertFalse(actual);
    }

    //endregion

    //region Function

    @Test
    void functionVisitsType() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String funcIdentifier = "f";
        UCELParser.FunctionContext node = mock(UCELParser.FunctionContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(funcIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.block()).thenReturn(block);

        when(type.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(block.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(funcIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitFunction(node);

        verify(type, times(1)).accept(visitor);
    }

    @Test
    void functionVisitsParameters() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String funcIdentifier = "f";
        UCELParser.FunctionContext node = mock(UCELParser.FunctionContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(funcIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.block()).thenReturn(block);

        when(type.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(block.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(funcIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitFunction(node);

        verify(parameters, times(1)).accept(visitor);
    }

    @Test
    void functionVisitsBlock() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String funcIdentifier = "f";
        UCELParser.FunctionContext node = mock(UCELParser.FunctionContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(funcIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.block()).thenReturn(block);

        when(type.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(block.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(funcIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitFunction(node);

        verify(block, times(1)).accept(visitor);
    }

    @Test
    void functionSetsOccurrences() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String funcIdentifier = "f";
        UCELParser.FunctionContext node = mock(UCELParser.FunctionContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(funcIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.block()).thenReturn(block);

        when(type.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(block.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(funcIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitFunction(node);

        assertTrue(node.occurrences != null && node.occurrences instanceof ArrayList);
    }

    //endregion

}
