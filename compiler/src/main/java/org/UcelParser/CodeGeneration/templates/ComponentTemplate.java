package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;

public class ComponentTemplate extends Template {

    public ComponentTemplate(String name, ArrayList<Template> parameters, ArrayList<Template> interfaces, Template compBodyTemplate) {
        template = new ST("// Generation for component: <name><newline><param; separator=[newline]><bnewline><interfaces; separator=[newline]><body><newline>");
        template.add("name", name);
        template.add("param", parameters);
        template.add("interfaces", interfaces);
        template.add("body", compBodyTemplate);
        template.add("newline", System.lineSeparator());
        template.add("bnewline", compBodyTemplate != null && !compBodyTemplate.toString().isEmpty() ? System.lineSeparator() : "");
    }

}
