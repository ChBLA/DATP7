package CodeGeneration.templates;

import java.util.List;

public class ArrayDeclIDTemplate implements Template {
    String result;

    public ArrayDeclIDTemplate(String ID, List<Template> arrayDecls) {
        result = ID;

        for (Template arrayDecl : arrayDecls) {
            result += arrayDecl.getOutput();
        }
    }

    @Override
    public String getOutput() {
        return result;
    }
}
