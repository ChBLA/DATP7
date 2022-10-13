package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class InitialiserTemplate extends Template {
    public InitialiserTemplate(List<Template> initialiserTemplates) {
        template = new ST("{<initialiserTemplates; separator=\", \">}");
        template.add("initialiserTemplates", initialiserTemplates);
    }
}
