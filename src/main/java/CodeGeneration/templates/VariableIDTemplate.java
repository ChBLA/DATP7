import java.util.List;

public class VariableIDTemplate implements Template {
    String resultingString;

    public VariableIDTemplate(String ID, List<Template> arrayDecls, Template Init) {
        resultingString = String.format("%s", ID);

        for (var decl : arrayDecls) {
            resultingString += decl.getOutput();
        }

        if (!Init.getOutput().equals(""))
            resultingString += String.format(" = %s", Init.getOutput());
    }

    public VariableIDTemplate(String ID, List<Template> arrayDecls) {
        this(ID, arrayDecls, new ManualTemplate(""));
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
