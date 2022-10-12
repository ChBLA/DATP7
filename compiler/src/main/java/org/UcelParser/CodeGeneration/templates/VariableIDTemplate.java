package org.UcelParser.CodeGeneration.templates;

import java.util.List;

public class VariableIDTemplate extends Template {

    public VariableIDTemplate(String ID, List<Template> arrayDecls, Template Init) {
        result = String.format("%s", ID);

        for (var decl : arrayDecls) {
            result += decl;
        }

        if (!Init.toString().equals(""))
            result += String.format(" = %s", Init);
    }

    public VariableIDTemplate(String ID, List<Template> arrayDecls) {
        this(ID, arrayDecls, new ManualTemplate(""));
    }

}
