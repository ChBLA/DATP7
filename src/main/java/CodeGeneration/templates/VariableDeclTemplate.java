package CodeGeneration.templates;

//import org.junit.platform.commons.util.StringUtils;

import java.util.List;

public class VariableDeclTemplate extends Template {


    public VariableDeclTemplate(Template type, List<Template> variableIds) {
        if (!type.toString().equals("")) {
            result = String.format("%s ", type);
        }

        for (var variableId : variableIds) {
            result += String.format("%s, ", variableId);
        }

        // Remove last comma and space with semicolon
        assert result != null;
        result = result.replaceFirst(", $", ";");
    }

    public VariableDeclTemplate(List<Template> variableIds) {
        this(new ManualTemplate(""), variableIds);
    }

}
