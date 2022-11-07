package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class SyncTemplate extends Template {
    public SyncTemplate(Template expr, String label) {
        template = new ST("<expr><label>");
        template.add("expr", expr);
        template.add("label", label);
    }
}
