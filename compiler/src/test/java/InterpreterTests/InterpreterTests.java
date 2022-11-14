package InterpreterTests;

import org.UcelParser.Interpreter.InterpreterVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Scope;
import org.UcelParser.Util.Value.IntegerValue;
import org.UcelParser.Util.Value.InterpreterValue;
import org.UcelParser.Util.Value.BooleanValue;
import org.antlr.v4.runtime.RuleContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InterpreterTests {

    //region addSub


    @ParameterizedTest
    @MethodSource("addValues")
    void addIntegers(InterpreterValue v1, InterpreterValue v2, InterpreterValue expected) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.AddSubContext node = mock(UCELParser.AddSubContext.class);
        UCELParser.ExpressionContext expL = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext expR = mock(UCELParser.ExpressionContext.class);

        var actual = visitor.visitAddSub(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> addValues() {
        return Stream.of(
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(137))
        );
    }
    //endregion

    //region eqExpr
    @ParameterizedTest
    @MethodSource("equalStrings")
    void eqExprInt(int v0, int v1, boolean expected) {
        // Arrange
        var visitor = testVisitor();

        var val0 = mockForVisitorResult(UCELParser.ExpressionContext.class, new IntegerValue(v0), visitor);
        var val1 = mockForVisitorResult(UCELParser.ExpressionContext.class, new IntegerValue(v1), visitor);

        var node = mock(UCELParser.EqExprContext.class);
        when(node.expression(0)).thenReturn(val0);
        when(node.expression(1)).thenReturn(val1);

        // Act
        var result = visitor.visitEqExpr(node);

        // Assert
        assertInstanceOf(BooleanValue.class, result);
        var actual = ((BooleanValue)result).getBool();

        assertEquals(expected, actual);
    }
    private static Stream<Arguments> equalStrings() {
        List<Arguments> arguments = new ArrayList<>();

        for(int i = -3; i<3; i++) {
            arguments.add(Arguments.of(i,i, true));
        }

        arguments.add(Arguments.of(0,1, false));
        arguments.add(Arguments.of(1,0, false));
        arguments.add(Arguments.of(-1,1, false));
        arguments.add(Arguments.of(1,-1, false));

        return arguments.stream();
    }
    //endregion

    //region Helper methods

    private<T extends RuleContext> T mockForVisitorResult(final Class<T> nodeType, final InterpreterValue visitResult, InterpreterVisitor visitor) {
        final T mock = mock(nodeType);
        when(mock.accept(visitor)).thenReturn(visitResult);
        return mock;
    }

    private InterpreterVisitor testVisitor() {
        var scope = mock(Scope.class);
        return new InterpreterVisitor(scope);
    }
    //endregion
}
