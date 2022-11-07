package org.UcelParser.CodeGeneration.templates;

import org.UcelParser.UCELParser_Generated.UCELParser;
import org.stringtemplate.v4.ST;

public class EdgeTemplate extends Template {
    //select guard sync update;
    public Template select;
    public Template guard;
    public Template sync;
    public Template update;

    public UCELParser.EdgeContext edge;

    public EdgeTemplate(Template select, Template guard, Template sync, Template update, UCELParser.EdgeContext edge) {
        template = new ST("");
        this.select = select;
        this.guard = guard;
        this.sync = sync;
        this.update = update;
        this.edge = edge;
    }
}
