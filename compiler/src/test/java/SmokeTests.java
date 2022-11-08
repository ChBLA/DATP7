import org.Ucel.*;
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
    @MethodSource("compilerInputPSystem")
    public void compilerDoesNotThrowErrorForPSystem(String input) {
        IProject project = generateDummyProjectFromPSystem(input);
        assertDoesNotThrow(() -> new org.UcelParser.Compiler().compileProject(project));
    }

    private static Stream<Arguments> compilerInputPSystem() {
        return Stream.of(
                Arguments.arguments("int a;\nbool b = 12 + 7 > 0;\nsystem DummyTemplate();"),
                Arguments.arguments("int a;\n{int i = x * y % 12;\nsystem DummyTemplate();"),
                Arguments.arguments("int a;\nbool b = true && !false;\nsystem DummyTemplate();"),
                Arguments.arguments("int a;\nint a = 0;\nint i = 0; system DummyTemplate();"),
                Arguments.arguments("int a;\nbool b = true;\n b = not b;\nsystem DummyTemplate();"),
                Arguments.arguments("int a;\nbool b = true;\nb = not b;\nsystem DummyTemplate();")
        );
    }

    private IProject generateDummyProjectFromPSystem(String pSystem) {
        Project project = new Project();
        project.setDeclaration("// Declarations");

        org.Ucel.Template template = new org.Ucel.Template();
        template.setName("DummyTemplate");
        template.setDeclarations("// Template Declaration");
        Graph graph = new Graph();
        Location initNode = new Location();
        initNode.setInitial(true);
        Location otherNode = new Location();
        otherNode.setPosX(30);
        otherNode.setPosY(40);
        graph.addLocation(initNode);
        graph.addLocation(otherNode);
        Edge edge = new Edge();
        edge.setLocationStart(initNode);
        edge.setLocationEnd(otherNode);
        graph.addEdge(edge);
        template.setGraph(graph);
        project.putTemplate(template);

        project.setSystemDeclarations(pSystem);

        return project;
    }

    //endregion

}
