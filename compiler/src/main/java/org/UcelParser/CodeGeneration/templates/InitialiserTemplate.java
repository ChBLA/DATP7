package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class InitialiserTemplate extends Template {
    public InitialiserTemplate(List<Template> initialiserTemplates) {
//        result = "{";
//
//        for (Template template : initialiserTemplates) {
//            result += String.format("%s, ", template);
//        }
//
//        result = result.replaceFirst(", $", "");
//
//        result += "}";

        template = new ST("{<initialiserTemplates; separator=\", \">}");
        template.add("initialiserTemplates", initialiserTemplates);
    }
}
