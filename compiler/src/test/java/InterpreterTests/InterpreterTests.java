package InterpreterTests;

import org.UcelParser.Interpreter.InterpreterVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Scope;
import org.UcelParser.Util.Type;
import org.UcelParser.Util.Value.IntegerValue;
import org.UcelParser.Util.Value.InterpreterValue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class InterpreterTests {

    //region addSub

    @ParameterizedTest
    @MethodSource("systemFaultyTypes")
    void addIntegers(InterpreterValue v1, InterpreterValue v2, InterpreterValue expected) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.AddSubContext node = mock(UCELParser.AddSubContext.class);
        UCELParser.ExpressionContext expL = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext expR = mock(UCELParser.ExpressionContext.class);

        var actual = visitor.visitAddSub(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> prefixes() {
        return Stream.of(
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(137))
        );
    }

    //endregion


}
