import org.junit.platform.commons.util.StringUtils;

import java.util.List;

public class VariableDeclTemplate implements Template {
    private String resultingString = "";

    public VariableDeclTemplate(Template type, List<Template> variableIds) {
        if (!type.getOutput().equals("")) {
            resultingString = String.format("%s ", type.getOutput());
        }

        for (var variableId : variableIds) {
            resultingString += String.format("%s, ", variableId.getOutput());
        }

        // Remove last comma and space with semicolon
        assert resultingString != null;
        resultingString = resultingString.replaceFirst(", $", ";");
    }

    public VariableDeclTemplate(List<Template> variableIds) {
        this(new ManualTemplate(""), variableIds);
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
