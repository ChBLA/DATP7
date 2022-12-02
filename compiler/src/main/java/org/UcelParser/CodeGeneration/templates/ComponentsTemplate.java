package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;

public class ComponentsTemplate extends Template {
    public ComponentsTemplate(ArrayList<ComponentTemplate> components) {
        template = new ST("<comp; separator=[newline]>");
        template.add("comp", components);
        template.add("newline", System.lineSeparator());
    }
}
