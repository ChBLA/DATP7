package IntegrationTests;

import org.UcelParser.CodeGeneration.CodeGenVisitor;
import org.UcelParser.Compiler;
import org.UcelParser.Util.*;
import org.UcelParser.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

public class IntegrationTests {

    @Test
    void validUCELProgramCompilerTest() {
        var logger = new TestLogger();
        var compiler = new Compiler(logger);

        var input = """
                typedef int[0,10] smallInt;

                smallInt a = 1;
                int[0,100] b = 5;

                int f(int number) {
                    number += 1;
                    return number;
                }

                f(b);

                for (a = 0; a < 5; a++) {
                    if (a == 3) {
                        // do something
                    }
                }
                system ;""";

        var expectedOutput = ("""
                typedef int[0,10] aaaaaa_smallInt;
                aaaaaa_smallInt aaaaaa_a = 1;
                int[0,100] aaaaaa_b = 5;
                int aaaaaa_f(int aaaaab_number)
                {
                aaaaab_number += 1;
                return aaaaab_number;
                }

                aaaaaa_f(aaaaaa_b);

                for (aaaaaa_a = 0;aaaaaa_a < 5;aaaaaa_a++) {
                if (aaaaaa_a == 3) {
                }
                }

                system ;""").replaceAll("\n", String.format("%n"));

        var generatedOutput = compiler.compile(input);

        assertEquals(expectedOutput, generatedOutput);
        assertFalse(logger.hasErrors());
    }


}
