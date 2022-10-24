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

        var input = "typedef int[0,10] smallInt;\n" +
                "\n" +
                "smallInt a = 1;\n" +
                "int[0,100] b = 5;\n" +
                "\n" +
                "int f(int ref number) {\n" +
                "    number += 1;\n" +
                "    return number;\n" +
                "}\n" +
                "\n" +
                "f(ref b);\n" +
                "\n" +
                "for (a = 0; a < 5; a++) {\n" +
                "    if (a == 3) {\n" +
                "        // do something\n" +
                "    }\n" +
                "}\n" +
                "system ;";

        var expectedOutput = ("typedef int[0,10] aaaaaa_smallInt;\n" +
                "aaaaaa_smallInt aaaaaa_a = 1;\n" +
                "int[0,100] aaaaaa_b = 5;\n" +
                "int aaaaaa_f_b()\n" +
                "{\n" +
                "aaaaaa_b += 1;\n" +
                "return aaaaaa_b;\n" +
                "}\n" +
                "\n" +
                "aaaaaa_f_b();\n" +
                "\n" +
                "for (aaaaaa_a = 0;aaaaaa_a < 5;aaaaaa_a++) {\n" +
                "if (aaaaaa_a == 3) {\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "system ;").replaceAll("\n", String.format("%n"));

        var generatedOutput = compiler.compile(input);

        assertEquals(expectedOutput, generatedOutput);
        assertFalse(logger.hasErrors());
    }


}
