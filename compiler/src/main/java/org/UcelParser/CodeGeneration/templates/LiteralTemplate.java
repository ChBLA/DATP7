package org.UcelParser.CodeGeneration.templates;

public class LiteralTemplate implements Template {

    private final String resultingString;
    public LiteralTemplate(String literal) {
        resultingString = String.format("%s", literal);
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
