package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class VariableIDTemplate extends Template {

    public VariableIDTemplate(String ID, List<Template> arrayDecls, Template Init) {
        template = new ST("<ID><arrayDecls; separator=\"\"><Init>");
        template.add("ID", ID);
        template.add("arrayDecls", arrayDecls);
        template.add("Init", (!Init.toString().equals("")) ? " = " + Init : "");

    }

    public VariableIDTemplate(String ID, List<Template> arrayDecls) {
        this(ID, arrayDecls, new ManualTemplate(""));
    }

}
