package CodeGeneration.templates;

import java.util.List;

public class ArrayDeclIDTemplate extends Template {
    public ArrayDeclIDTemplate(String ID, List<Template> arrayDecls) {
        result = ID;

        for (Template arrayDecl : arrayDecls) {
            result += arrayDecl;
        }
    }
}
