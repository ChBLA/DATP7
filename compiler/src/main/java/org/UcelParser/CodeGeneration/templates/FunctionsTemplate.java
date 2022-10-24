package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class FunctionsTemplate extends Template {
    public FunctionsTemplate(List<Template> functions) {
        template = new ST("<functions; separator=[newlines]><newlines>");

        template.add("functions", functions);
        template.add("newlines", String.format("%n%n"));
    }
}
