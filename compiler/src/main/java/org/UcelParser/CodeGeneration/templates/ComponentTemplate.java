package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;

public class ComponentTemplate extends Template {

    public ComponentTemplate(String name, ArrayList<Template> parameters, ArrayList<Template> interfaces, ArrayList<Template> subComponents, Template compBodyTemplate) {
        if (parameters.isEmpty() && interfaces.isEmpty() && subComponents.isEmpty() && compBodyTemplate.toString().isEmpty()) {
            template = new ST("");
        } else {
            template = new ST("// Generation for component: <name><newline><param; separator=[newline]><bnewline><interfaces; separator=[newline]><subcomps; separator=[newline]><newline><body><newline>");
            template.add("name", name);
            template.add("param", parameters);
            template.add("interfaces", interfaces);
            template.add("subcomps", subComponents);
            template.add("body", compBodyTemplate);
            template.add("newline", System.lineSeparator());
            template.add("bnewline", compBodyTemplate != null && !compBodyTemplate.toString().isEmpty() ? System.lineSeparator() : "");
        }
    }

}
