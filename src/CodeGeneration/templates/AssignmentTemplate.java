import java.text.MessageFormat;

public class AssignmentTemplate implements Template{
    private final String resultingString;
    public AssignmentTemplate(Template left, Template right) {
        resultingString = MessageFormat.format("{0} = {1}", left.getOutput(), right.getOutput());
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
