package CodeGeneration.templates;

import CodeGeneration.templates.Template;

import java.util.List;

public class InitialiserTemplate extends Template {
    public InitialiserTemplate(List<Template> initialiserTemplates) {
        result = "{";

        for (Template template : initialiserTemplates) {
            result += String.format("%s, ", template);
        }

        result = result.replaceFirst(", $", "");

        result += "}";
    }
}
