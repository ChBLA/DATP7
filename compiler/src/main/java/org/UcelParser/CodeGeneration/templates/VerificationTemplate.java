package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class VerificationTemplate extends Template{

    public VerificationTemplate(String op, String id, Template type, Template expr) {
        template = new ST("<op> (<id>:<type>) <expr>");
        template.add("op", op);
        template.add("id", id);
        template.add("type", type);
        template.add("expr", expr);
    }

}
