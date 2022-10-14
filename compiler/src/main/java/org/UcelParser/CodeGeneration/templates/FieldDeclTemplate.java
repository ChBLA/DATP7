package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class FieldDeclTemplate extends Template {
    public FieldDeclTemplate(Template type, List<Template> arrayDeclIDs) {
        template = new ST("<type> <arrayDeclIDs; separator=\", \">;");
        template.add("type", type);
        template.add("arrayDeclIDs", arrayDeclIDs);
    }
}

