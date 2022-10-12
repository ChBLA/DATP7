package CodeGeneration.templates;

import java.util.List;

public class TypeIDStructTemplate extends Template {

    public TypeIDStructTemplate(List<Template> fieldDecls) {
        result = "struct {\n";

        for (var fieldDecl : fieldDecls) {
            result += fieldDecl + "\n";
        }

        result += "}";
    }

}
