package InterpreterTests;

import org.UcelParser.Interpreter.InterpreterVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.DeclarationInfo;
import org.UcelParser.Util.DeclarationReference;
import org.UcelParser.Util.Scope;
import org.UcelParser.Util.Value.IntegerValue;
import org.UcelParser.Util.Value.InterpreterValue;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import org.UcelParser.Util.Value.BooleanValue;
import org.UcelParser.Util.Value.StringValue;
import org.antlr.v4.runtime.RuleContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InterpreterTests {

    //region addSub


    @ParameterizedTest
    @MethodSource("addSubValues")
    void addSubIntegers(InterpreterValue v1, InterpreterValue v2, InterpreterValue expected, String op) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.AddSubContext node = mock(UCELParser.AddSubContext.class);
        UCELParser.ExpressionContext expL = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext expR = mock(UCELParser.ExpressionContext.class);

        var exprs = new ArrayList<UCELParser.ExpressionContext>();
        exprs.add(expL);
        exprs.add(expR);
        when(node.expression()).thenReturn(exprs);
        when(visitor.visit(expL)).thenReturn(v1);
        when(visitor.visit(expR)).thenReturn(v2);

        var operatorToken = mock(Token.class);
        when(operatorToken.getText()).thenReturn(op);
        node.op = operatorToken;

        var actual = visitor.visitAddSub(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> addSubValues() {
        return Stream.of(
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(137), "+"),
                Arguments.arguments(new IntegerValue(-3) , new IntegerValue(7), new IntegerValue(4), "+"),
                Arguments.arguments(null , new IntegerValue(134), null, "+"),
                Arguments.arguments(null , null, null, "+"),
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(-131), "-"),
                Arguments.arguments(new IntegerValue(-3) , new IntegerValue(7), new IntegerValue(-10), "-"),
                Arguments.arguments(null , new IntegerValue(134), null, "-"),
                Arguments.arguments(null , null, null, "-")
                );
    }
    //endregion

    //region MultDiv

    @ParameterizedTest
    @MethodSource("multDivValues")
    void multDivIntegers(InterpreterValue v1, InterpreterValue v2, InterpreterValue expected, String op) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.MultDivContext node = mock(UCELParser.MultDivContext.class);
        UCELParser.ExpressionContext expL = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext expR = mock(UCELParser.ExpressionContext.class);

        var exprs = new ArrayList<UCELParser.ExpressionContext>();
        exprs.add(expL);
        exprs.add(expR);
        when(node.expression()).thenReturn(exprs);
        when(visitor.visit(expL)).thenReturn(v1);
        when(visitor.visit(expR)).thenReturn(v2);

        var operatorToken = mock(Token.class);
        when(operatorToken.getText()).thenReturn(op);
        node.op = operatorToken;

        var actual = visitor.visitMultDiv(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> multDivValues() {
        return Stream.of(
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(14), new IntegerValue(42), "*"),
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(-14), new IntegerValue(-42), "*"),
                Arguments.arguments(new IntegerValue(-3) , new IntegerValue(7), new IntegerValue(-21), "*"),
                Arguments.arguments(null , new IntegerValue(134), null, "*"),
                Arguments.arguments(null , null, null, "*"),
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(0), "/"),
                Arguments.arguments(new IntegerValue(-27) , new IntegerValue(7), new IntegerValue(-3), "/"),
                Arguments.arguments(null , new IntegerValue(134), null, "/"),
                Arguments.arguments(null , null, null, "/"),
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(3), "%"),
                Arguments.arguments(new IntegerValue(-3) , new IntegerValue(7), new IntegerValue(-3), "%"),
                Arguments.arguments(null , new IntegerValue(134), null, "%"),
                Arguments.arguments(null , null, null, "%")
        );
    }
    //endregion

    //region idExpr

    @ParameterizedTest
    @MethodSource("idValues")
    void idValues(InterpreterValue v, InterpreterValue expected) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.IdExprContext node = mock(UCELParser.IdExprContext.class);
        DeclarationReference declRef = new DeclarationReference(0,0);
        DeclarationInfo declInfo = mock(DeclarationInfo.class);
        node.reference = declRef;
        when(declInfo.getValue()).thenReturn(v);
        when(declInfo.generateName()).thenReturn("aaaaaa_id");

        try{
            when(scope.get(declRef)).thenReturn(declInfo);
        } catch (Exception e) {fail();}

        var actual = visitor.visitIdExpr(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> idValues() {
        return Stream.of(
                Arguments.arguments(new IntegerValue(3), new IntegerValue(3)),
                Arguments.arguments(new IntegerValue(-19), new IntegerValue(-19)),
                Arguments.arguments(new BooleanValue(true), new BooleanValue(true)),
                Arguments.arguments(new StringValue("s"), new StringValue("s")),
                Arguments.arguments(null, new StringValue("aaaaaa_id"))

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
