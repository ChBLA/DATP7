package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class InterfaceFieldsTemplate extends Template {
    public InterfaceFieldsTemplate(List<Template> types, List<Template> fields) {
        template = new ST("<field; separator=[newline]>");
        for (int i = 0; i < types.size(); i++){
            template.add("field", String.format("%s %s;", types.get(i), fields.get(i)));
        }
        template.add("newline", System.lineSeparator());
    }
}
