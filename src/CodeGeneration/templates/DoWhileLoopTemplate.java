public class DoWhileLoopTemplate implements Template {
    private final String resultingString;

    public DoWhileLoopTemplate(Template expr, Template stmnt) {
        resultingString = String.format("do %s while (%s);", stmnt.getOutput(), expr.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
