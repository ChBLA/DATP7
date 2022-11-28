package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class InterfaceTemplate extends Template {

    public InterfaceTemplate(List<Template> interfaces) {
        template = new ST("<interfaces; separator=[newline]>");
        template.add("interfaces", interfaces);
        template.add("newline", System.lineSeparator());
    }

}
