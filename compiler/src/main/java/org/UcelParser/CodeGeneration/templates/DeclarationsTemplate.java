package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class DeclarationsTemplate extends Template {
    public DeclarationsTemplate(List<Template> declarations) {
        template = new ST("<declarations; separator=[newline]>");
        template.add("declarations", declarations);
        template.add("newline", System.lineSeparator());
    }
}
