package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class TypeIDStructTemplate extends Template {
    public TypeIDStructTemplate(List<Template> fieldDecls) {
        template = new ST("struct {<newline><fieldDecls; separator=[newline]><newline>}");
        template.add("fieldDecls", fieldDecls);
        template.add("newline", System.lineSeparator());
    }
}
