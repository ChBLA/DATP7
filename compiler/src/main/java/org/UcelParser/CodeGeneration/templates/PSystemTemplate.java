package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class PSystemTemplate extends Template {
    public PSystemTemplate(Template declarations, Template buildSystem) {
        template = new ST("<decl><newline><build>");
        template.add("decl", declarations);
        template.add("build", buildSystem);
        template.add("newline", System.lineSeparator());
        //todo: Add auto-generated system from template occurrences
    }
}
