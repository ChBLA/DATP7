package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class ArrayDeclIDTemplate extends Template {
    public ArrayDeclIDTemplate(String ID, List<Template> arrayDecls) {
        ST template = new ST("<ID><arrayDecls; separator=\"\">");
        template.add("ID", ID);
        template.add("arrayDecls", arrayDecls);

        this.template = template;
    }
}
