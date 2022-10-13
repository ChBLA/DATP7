package org.UcelParser.CodeGeneration.templates;

//import org.junit.platform.commons.util.StringUtils;

import org.stringtemplate.v4.ST;

import java.util.List;

public class VariableDeclTemplate extends Template {


    public VariableDeclTemplate(Template type, List<Template> variableIds) {
        template = new ST("<type><variableIds; separator=\", \">;");
        template.add("type", (!type.toString().equals("")) ? type + " " : "");
        template.add("variableIds", variableIds);
    }

    public VariableDeclTemplate(List<Template> variableIds) {
        this(new ManualTemplate(""), variableIds);
    }

}
