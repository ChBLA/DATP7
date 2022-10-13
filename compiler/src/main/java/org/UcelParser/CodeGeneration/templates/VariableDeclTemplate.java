package org.UcelParser.CodeGeneration.templates;

//import org.junit.platform.commons.util.StringUtils;

import org.stringtemplate.v4.ST;

import java.util.List;

public class VariableDeclTemplate extends Template {


    public VariableDeclTemplate(Template type, List<Template> variableIds) {
//        if (!type.toString().equals("")) {
//            result = String.format("%s ", type);
//        }
//
//        for (var variableId : variableIds) {
//            result += String.format("%s, ", variableId);
//        }
//
//        // Remove last comma and space with semicolon
//        assert result != null;
//        result = result.replaceFirst(", $", ";");
        template = new ST("<type><variableIds; separator=\", \">;");
        template.add("type", (!type.toString().equals("")) ? type + " " : "");
        template.add("variableIds", variableIds);
    }

    public VariableDeclTemplate(List<Template> variableIds) {
        this(new ManualTemplate(""), variableIds);
    }

}
