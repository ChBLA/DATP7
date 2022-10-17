import org.Ucel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProjectTests {
    @Test
    public void ctor() {
        IProject project = new Project();

        assertNotNull(project.getDeclaration());
        assertNotNull(project.getTemplates());
        assertNotNull(project.getSystemDeclarations());
    }


    @ParameterizedTest
    @ValueSource(strings = {"kljsfdk", "powprpmlxvc"})
    public void SetGetDeclaration(String value) {
        Project project = new Project();
        project.setDeclaration(value);
        assertEquals(value, project.getDeclaration());
    }

    @ParameterizedTest
    @ValueSource(strings = {"kljsfdk", "powprpmlxvc"})
    public void PutGetTemplate(String value) {
        Project project = new Project();

        Template template = new Template();
        template.setName(value);
        project.putTemplate(template);

        assertEquals(template, project.getTemplate(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"kljsfdk", "powprpmlxvc"})
    public void SetGetSystemDeclarations(String value) {
        Project project = new Project();
        project.setSystemDeclarations(value);
        assertEquals(value, project.getSystemDeclarations());
    }

}
