package org.UcelParser.CodeGeneration.templates;

public class LiteralTemplate extends Template {

    public LiteralTemplate(String literal) {
        result = String.format("%s", literal);
    }


}
