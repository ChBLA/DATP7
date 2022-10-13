package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class IterationTemplate extends Template {

    public IterationTemplate(Template id, Template typeResult, Template stmntResult) {
        template = new ST("for (<id>:<typeResult>) <stmntResult>");
        template.add("id", id);
        template.add("typeResult", typeResult);
        template.add("stmntResult", stmntResult);
    }

}
