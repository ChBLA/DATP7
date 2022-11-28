package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class PDeclarationTemplate extends Template {
    public final Template declarations;

    public PDeclarationTemplate(Template declarations) {
        template = new ST("<decls; separator=[newline]>");
        template.add("decls", declarations);
        template.add("newline", System.lineSeparator());
        this.declarations = declarations;
    }
}
