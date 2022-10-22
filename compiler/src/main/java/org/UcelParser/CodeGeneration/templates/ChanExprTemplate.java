package org.UcelParser.CodeGeneration.templates;
import org.stringtemplate.v4.ST;

public class ChanExprTemplate extends Template {
    public ChanExprTemplate(Template chanExpr, Template expr) {
        template = new ST("<chanExpr>[<expr>]");
        template.add("chanExpr", chanExpr);
        template.add("expr", expr);
    }
}
