package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;

public class ComponentTemplate extends Template {

    public ComponentTemplate(ArrayList<Template> parameters, ArrayList<Template> interfaces, Template compBodyTemplate) {
        template = new ST("<param; separator=[newline]><newline><interfaces; separator=[newline]><newline><body>");
        template.add("param", parameters);
        template.add("interfaces", interfaces);
        template.add("body", compBodyTemplate);
        template.add("newline", System.lineSeparator());
    }

}
