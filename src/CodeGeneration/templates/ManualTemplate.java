public class ManualTemplate implements Template{
    private String output;

    public ManualTemplate(String s) {
        output = s;
    }

    @Override
    public String getOutput() {
        return output;
    }
}
