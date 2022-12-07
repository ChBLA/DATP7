package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class PQueryTemplate extends Template {
    public final String comment;
    public PQueryTemplate(Template query, String comment) {
        template = new ST("<query>");
        template.add("query", query);
        this.comment = comment;
    }
}
