package CodeGeneration.templates;

public class VerificationTemplate implements Template{
    private final String resultingString;

    public VerificationTemplate(String op, String id, Template type, Template expr) {
        resultingString = String.format("%s (%s:%s) %s", op, id, type.getOutput(), expr.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
