package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class LiteralTemplate extends Template {

    public LiteralTemplate(String literal) {
        template = new ST("<literal>");
        template.add("literal", literal);
    }


}
