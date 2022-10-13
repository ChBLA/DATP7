import org.UcelParser.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

public class SmokeTests {

    //region no errors thrown

    @ParameterizedTest(name = "{index} => Running compiler on {0} expecting no errors")
    @MethodSource("compilerInput")
    public void mainDoesNotThrowError(String input) {
        assertDoesNotThrow(() -> new Main(input));
    }

    private static Stream<Arguments> compilerInput() {
        return Stream.of(
                Arguments.arguments("{bool b = 12 + 7 > 0;}"),
                Arguments.arguments("{int i = x * y % 12;}"),
                Arguments.arguments("{bool b = true && !false;}"),
                Arguments.arguments("{\nint a = 0;\nint i = 0;\nfor (i = 0; i < 10; i++) {\na += i;\n}\n}"),
                Arguments.arguments("{\nbool b = true;\nwhile (b) {\n b = not b;\n}\n}"),
                Arguments.arguments("{\nbool b = true;\ndo {\nb = not b;\n} while (b);\n}")
        );
    }

    //endregion

}
