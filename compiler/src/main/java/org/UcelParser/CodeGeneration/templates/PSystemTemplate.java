package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class PSystemTemplate extends Template {
    public final Template declarations;
    public final Template build;
    public final Template system;

    public PSystemTemplate(Template declarations, Template build, Template system) {
        template = new ST("<decl><newline><build><newline><system>");
        template.add("decl", declarations);
        template.add("build", build);
        template.add("system", system);
        template.add("newline", System.lineSeparator());

        //todo: CLBA, clean up.
        this.declarations = declarations;
        this.build = build;
        this.system = system;
    }
}
