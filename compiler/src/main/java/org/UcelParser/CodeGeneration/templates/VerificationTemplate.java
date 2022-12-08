package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class VerificationTemplate extends Template{

    public VerificationTemplate(String op, String id, Template type, Template expr) {
        template = new ST("<op> (<id>:<type>) <expr>");
        template.add("op", op);
        template.add("id", id);
        template.add("type", type);
        template.add("expr", expr);
    }

    public VerificationTemplate(String operator, List<Template> exprs) {
        template = new ST("<exprs; separator=[op]>");
        template.add("op", operator);
        template.add("exprs", exprs);
    }
}
