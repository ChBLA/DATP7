package org.UcelParser.CodeGeneration.templates;

import java.util.List;

public class BlockTemplate implements Template {
    private final String resultingString;

    public BlockTemplate(List<Template> localDecls, List<Template> statements) {
        var builder = new StringBuilder();
        builder.append("{\n");
        for (var decl : localDecls) {
            builder.append(String.format("%s\n", decl.getOutput()));
        }
        for (var stmnt : statements) {
            builder.append(String.format("%s", stmnt.getOutput()));
        }
        builder.append("}");
        resultingString = builder.toString();
    }

    @Override
    public String getOutput() {
        return resultingString;
    }
}
