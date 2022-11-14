package InterpreterTests;

import org.UcelParser.Interpreter.InterpreterVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Scope;
import org.UcelParser.Util.Value.IntegerValue;
import org.UcelParser.Util.Value.InterpreterValue;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InterpreterTests {

    //region addSub


    @ParameterizedTest
    @MethodSource("addValues")
    void addIntegers(InterpreterValue v1, InterpreterValue v2, InterpreterValue expected, String op) {
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

    private static Stream<Arguments> addValues() {
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

    //region eqExpr

    //endregion


}
