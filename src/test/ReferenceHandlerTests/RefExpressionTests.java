import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;

import javax.naming.Reference;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class RefExpressionTests {

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

        UCELParser.RefExprWrapperContext wrapper = new UCELParser.RefExprWrapperContext(new UCELParser.ExpressionContext());
        UCELParser.RefExpressionContext refExpression = mock(UCELParser.RefExpressionContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(refExpression.ID()).thenReturn(idNode);
        ArrayList<ParseTree> children = new ArrayList<>();
        children.add(refExpression);
        wrapper.children = children;

        visitor.visitRefExprWrapper(wrapper);

        assertEquals(refExpression.reference, new TableReference(0,2));
    }

    @Test
    void IDExprSuccessfulLookupInDistantScope() {
        fail();
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

        UCELParser.RefExprWrapperContext wrapper = new UCELParser.RefExprWrapperContext(new UCELParser.ExpressionContext());
        UCELParser.RefExpressionContext refExpression = mock(UCELParser.RefExpressionContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(refExpression.ID()).thenReturn(idNode);
        ArrayList<ParseTree> children = new ArrayList<>();
        children.add(refExpression);
        wrapper.children = children;

        boolean actualReturnValue = visitor.visitRefExprWrapper(wrapper);

        assertFalse(actualReturnValue);
    }
    //endregion

    //region LiteralExpr: No influence by scopes
    @Test
    void NullScopeValidLiteralExpr() {
        //SEEMS POINTLESS, DELETE?
        fail();
    }

    @Test
    void NotNullScopeValidLiteralExpr() {
        //SEEMS POINTLESS, DELETE?
        fail();
    }
    //endregion


}
