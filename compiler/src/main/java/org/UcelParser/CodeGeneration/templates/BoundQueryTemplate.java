package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class BoundQueryTemplate extends Template {
    public BoundQueryTemplate(String operator, List<Template> exprs) {
        template = new ST("<op> : <exprs; separator=\",\">");
        template.add("op", operator);
        template.add("exprs", exprs);
    }

    public BoundQueryTemplate(String operator, Template setExpr, List<Template> exprs) {
        template = new ST("<op> { <setExpr> } : <exprs; separator=\",\">");
        template.add("op", operator);
        template.add("setExpr", setExpr);
        template.add("exprs", exprs);
    }
}
