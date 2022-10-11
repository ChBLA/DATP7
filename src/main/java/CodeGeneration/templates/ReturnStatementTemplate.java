public class ReturnStatementTemplate implements Template{
    private final String resultingString;

    public ReturnStatementTemplate(Template expr) {
        resultingString = String.format("return %s;", expr.getOutput());
    }

    public ReturnStatementTemplate() {
        resultingString = "return;";
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
