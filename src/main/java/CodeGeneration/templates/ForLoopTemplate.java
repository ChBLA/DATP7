package CodeGeneration.templates;

import CodeGeneration.templates.Template;

public class ForLoopTemplate implements Template {
    private final String resultingString;

    public ForLoopTemplate(Template assign, Template expr1, Template expr2, Template stmnt) {
        resultingString = String.format("for (%s;%s;%s) %s", assign.getOutput(), expr1.getOutput(), expr2.getOutput(), stmnt.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
