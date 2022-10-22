import org.Ucel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TemplateTests {
    @Test
    public void ctor() {
        ITemplate template = new Template();

        assertNotNull(template.getName());
        assertNotNull(template.getParameters());
        assertNotNull(template.getGraph());
        assertNotNull(template.getDeclarations());
    }


    @ParameterizedTest
    @ValueSource(strings = {"kljsfdk", "powprpmlxvc"})
    public void SetGetName(String value) {
        Template template = new Template();
        template.setName(value);
        assertEquals(value, template.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"kljsfdk", "powprpmlxvc"})
    public void SetGetParameters(String value) {
        Template template = new Template();
        template.setParameters(value);
        assertEquals(value, template.getParameters());
    }

    @Test
    public void SetGetGraph() {
        Template template = new Template();

        IGraph value = new Graph();

        template.setGraph(value);
        assertEquals(value, template.getGraph());
    }

    @ParameterizedTest
    @ValueSource(strings = {"kljsfdk", "powprpmlxvc"})
    public void SetGetDeclarations(String value) {
        Template template = new Template();
        template.setDeclarations(value);
        assertEquals(value, template.getDeclarations());
    }


}
