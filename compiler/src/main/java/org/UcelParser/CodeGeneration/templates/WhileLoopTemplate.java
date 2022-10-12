package org.UcelParser.CodeGeneration.templates;

public class WhileLoopTemplate implements Template{
    private final String resultingString;

    public WhileLoopTemplate(Template expr, Template stmnt) {
        resultingString = String.format("while (%s) %s", expr.getOutput(), stmnt.getOutput());
    }


    @Override
    public String getOutput() {
        return resultingString;
    }
}
