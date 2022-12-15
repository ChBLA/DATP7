package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;

public class PSystemTemplate extends Template {
    public final Template declarations;
    public final Template system;
    public final ArrayList<Template> comps;

    public PSystemTemplate(Template declarations, Template buildSystem) {
        template = new ST("<decl><newline><comps; separator=[newline]><newline><build>");
        this.declarations = declarations;
        this.comps = new ArrayList<>();
        this.system = buildSystem;
        //todo: Add auto-generated system from template occurrences
    }

    public void finalise() {
        template.add("decl", this.declarations);
        template.add("comps", this.comps);
        template.add("build", this.system);
        template.add("newline", System.lineSeparator());
    }
}
