package org.UcelParser.CodeGeneration.templates;
import org.stringtemplate.v4.ST;

public class FuncCallTemplate extends Template {
    public FuncCallTemplate(String name, Template args) {
        template = new ST("<func_name>(<arguments>)");
        template.add("func_name", name);
        template.add("arguments", args);
    }

}
