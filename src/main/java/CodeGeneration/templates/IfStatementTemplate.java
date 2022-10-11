public class IfStatementTemplate implements Template{
    private final String resultingString;
    public IfStatementTemplate(Template expr, Template stmnt1, Template stmnt2) {
        resultingString = String.format("if (%s) %s else %s", expr.getOutput(), stmnt1.getOutput(), stmnt2.getOutput());
    }

    public IfStatementTemplate(Template expr, Template stmnt1) {
        resultingString = String.format("if (%s) %s", expr.getOutput(), stmnt1.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
