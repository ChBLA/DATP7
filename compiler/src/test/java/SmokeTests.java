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
    public void compilerDoesNotThrowError(String input) {
        assertDoesNotThrow(() -> new org.UcelParser.Compiler().compile(input));
    }

    private static Stream<Arguments> compilerInput() {
        return Stream.of(
                Arguments.arguments("int a;\n{bool b = 12 + 7 > 0;}\nsystem;"),
                Arguments.arguments("int a;\n{int i = x * y % 12;}\nsystem;"),
                Arguments.arguments("int a;\n{bool b = true && !false;}\nsystem;"),
                Arguments.arguments("int a;\n{\nint a = 0;\nint i = 0;\nfor (i = 0; i < 10; i++) {\na += i;\n}\n}\nsystem;"),
                Arguments.arguments("int a;\n{\nbool b = true;\nwhile (b) {\n b = not b;\n}\n}\nsystem;"),
                Arguments.arguments("int a;\n{\nbool b = true;\ndo {\nb = not b;\n} while (b);\n}\nsystem;")
        );
    }

    //endregion

}
