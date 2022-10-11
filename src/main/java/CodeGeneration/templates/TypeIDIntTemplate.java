public class TypeIDIntTemplate implements Template {
    private String resultingString;

    public TypeIDIntTemplate(Template expr1, Template expr2) {
        resultingString = String.format("int[%s,%s]", expr1.getOutput(), expr2.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
