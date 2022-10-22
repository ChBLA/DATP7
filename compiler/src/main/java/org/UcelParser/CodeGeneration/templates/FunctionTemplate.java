package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class FunctionTemplate extends Template {
    public FunctionTemplate(Template type, String ID, Template parameters, Template body) {
        template = new ST("<type> <ID>(<parameters>)<newline><body>");
        template.add("type", type);
        template.add("ID", ID);
        template.add("parameters", parameters);
        template.add("body", body);
        template.add("newline", System.lineSeparator());
    }
}
