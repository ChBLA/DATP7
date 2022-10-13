package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class TypeDeclTemplate extends Template {
    public TypeDeclTemplate(Template type, List<Template> arrayDeclIDs) {
        ST template = new ST("typedef <type> <arrayDeclIDs; separator=\", \">;");

        template.add("type", type);
        template.add("arrayDeclIDs", arrayDeclIDs);

//        result = String.format("typedef %s ", type);
//
//        for (var arrayDeclIDsItem : arrayDeclIDs) {
//            result += arrayDeclIDsItem + ", ";
//        }
//
//        result = result.replaceFirst(", $", ";");
        this.template = template;
    }
}
