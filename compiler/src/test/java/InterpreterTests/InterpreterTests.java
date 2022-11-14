package InterpreterTests;

import org.UcelParser.Interpreter.InterpreterVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Scope;
import org.UcelParser.Util.Value.IntegerValue;
import org.UcelParser.Util.Value.InterpreterValue;
import org.UcelParser.Util.Value.BooleanValue;
import org.UcelParser.Util.Value.StringValue;
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
    @MethodSource("equalityTestValues")
    void eqExprTests(InterpreterValue v0, InterpreterValue v1, boolean expected) {
        // Arrange
        var visitor = testVisitor();

        var val0 = mockForVisitorResult(UCELParser.ExpressionContext.class, v0, visitor);
        var val1 = mockForVisitorResult(UCELParser.ExpressionContext.class, v1, visitor);

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
    private static Stream<Arguments> equalityTestValues() {
        List<Arguments> arguments = new ArrayList<>();

        //region Ints
        for(int i = -3; i<3; i++) {
            arguments.add(Arguments.of(value(i), value(i), true));
        }

        arguments.add(Arguments.of(value(0),value(1), false));
        arguments.add(Arguments.of(value(1),value(0), false));
        arguments.add(Arguments.of(value(-1),value(1), false));
        arguments.add(Arguments.of(value(1),value(-1), false));
        //endregion

        //region Strings
        arguments.add(Arguments.of(value(""),value(""), true));
        arguments.add(Arguments.of(value("abc"),value("abc"), true));
        arguments.add(Arguments.of(value("def"),value("def"), true));

        arguments.add(Arguments.of(value(" "),value(""), false));
        arguments.add(Arguments.of(value("def "),value("def"), false));
        arguments.add(Arguments.of(value("abc"),value("def"), false));
        arguments.add(Arguments.of(value("def"),value("abc"), false));
        //endregion

        //region Bools
        arguments.add(Arguments.of(value(true),value(true), true));
        arguments.add(Arguments.of(value(false),value(false), true));
        arguments.add(Arguments.of(value(true),value(false), false));
        arguments.add(Arguments.of(value(false),value(true), false));
        //endregion

        //region Mixed types
        arguments.add(Arguments.of(value(0),value(""), false));
        arguments.add(Arguments.of(value(0),value("0"), false));
        arguments.add(Arguments.of(value(0),value(false), false));
        arguments.add(Arguments.of(value(1),value(true), false));
        //endregion

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

    //region Value
    private static IntegerValue value(int val) {
        return new IntegerValue(val);
    }

    private static StringValue value(String val) {
        return new StringValue(val);
    }
    private static BooleanValue value(boolean val) {
        return new BooleanValue(val);
    }
    //endregion
    //endregion
}
