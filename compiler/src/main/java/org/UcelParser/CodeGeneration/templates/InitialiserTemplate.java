package org.UcelParser.CodeGeneration.templates;

import java.util.List;

public class InitialiserTemplate implements Template {
    String result;

    public InitialiserTemplate(List<Template> initialiserTemplates) {
        result = "{";

        for (Template template : initialiserTemplates) {
            result += String.format("%s, ", template.getOutput());
        }

        result = result.replaceFirst(", $", "");

        result += "}";
    }

    @Override
    public String getOutput() {
        return result;
    }
}
