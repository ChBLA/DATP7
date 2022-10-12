package CodeGeneration.templates;

import CodeGeneration.templates.Template;

import java.util.List;

public class BlockTemplate extends Template {

    public BlockTemplate(List<Template> localDecls, List<Template> statements) {
        var builder = new StringBuilder();
        builder.append("{\n");
        for (var decl : localDecls) {
            builder.append(String.format("%s\n", decl));
        }
        for (var stmnt : statements) {
            builder.append(String.format("%s", stmnt));
        }
        builder.append("}");
        result = builder.toString();
    }
}
