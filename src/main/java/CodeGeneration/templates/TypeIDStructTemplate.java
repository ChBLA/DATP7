package CodeGeneration.templates;

import java.util.List;

public class TypeIDStructTemplate implements Template {
    String result;

    public TypeIDStructTemplate(List<Template> fieldDecls) {
        result = "struct {\n";

        for (var fieldDecl : fieldDecls) {
            result += fieldDecl.getOutput() + "\n";
        }

        result += "}";
    }

    @Override
    public String getOutput() {
        return result;
    }
}
