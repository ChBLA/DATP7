package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class PSystemTemplate extends Template {
    public final Template declarations;
    public final Template system;
    public PSystemTemplate(Template declarations, Template buildSystem) {
        template = new ST("<decl><newline><build>");
        this.declarations = declarations;
        this.system = buildSystem;
        //todo: Add auto-generated system from template occurrences
    }

    public void finalise() {
        template.add("decl", this.declarations);
        template.add("build", this.system);
        template.add("newline", System.lineSeparator());
    }
}
