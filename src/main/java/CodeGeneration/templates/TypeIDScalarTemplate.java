package CodeGeneration.templates;

public class TypeIDScalarTemplate implements Template {
    private final String result;

    public TypeIDScalarTemplate(Template expr) {
        result = String.format("scalar[%s]", expr.getOutput());
    }

    @Override
    public String getOutput() {
        return result;
    }
}
