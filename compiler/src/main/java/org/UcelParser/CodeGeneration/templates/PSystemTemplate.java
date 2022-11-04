package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class PSystemTemplate extends Template {
    public final Template declarations;
    public final Template build;
    public final Template system;

    public PSystemTemplate(Template declarations, Template build, Template system) {
        template = new ST("");
        this.declarations = declarations;
        this.build = build;
        this.system = system;
    }
}
