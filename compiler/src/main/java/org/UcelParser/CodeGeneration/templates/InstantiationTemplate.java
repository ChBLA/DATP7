package org.UcelParser.CodeGeneration.templates;
import org.stringtemplate.v4.ST;
public class InstantiationTemplate extends Template {
    //ID ( LEFTPAR parameters? RIGHTPAR )? '=' ID LEFTPAR arguments? RIGHTPAR END;
    public InstantiationTemplate(String ID1, String ID2, Template params, Template arguments, boolean useParenthesis) {
        template = new ST("<id1><leftpar><params><rightpar> = <id2>(<args>);");
        template.add("id1", ID1);
        template.add("id2", ID2);
        template.add("params", params);
        template.add("args", arguments);
        template.add("leftpar", useParenthesis ? "(" : "");
        template.add("rightpar", useParenthesis ? ")" : "");
    }

}
