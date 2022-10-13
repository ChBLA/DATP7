package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class TypeIDStructTemplate extends Template {

    public TypeIDStructTemplate(List<Template> fieldDecls) {
//        result = "struct {\n";
//
//        for (var fieldDecl : fieldDecls) {
//            result += fieldDecl + "\n";
//        }
//
//        result += "}";

        template = new ST("struct {\n<fieldDecls; separator=\"\n\">\n}");
        template.add("fieldDecls", fieldDecls);
    }

}
