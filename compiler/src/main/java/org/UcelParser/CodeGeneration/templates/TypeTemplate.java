package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class TypeTemplate extends Template {


    public TypeTemplate(Template prefix, Template typeID) {
        template = new ST("<prefix><typeID>");
        template.add("prefix", (!prefix.toString().isEmpty()) ? prefix + " " : "");
        template.add("typeID", typeID);
    }

    public TypeTemplate(Template typeID) {
        this(new ManualTemplate(""), typeID);
    }


}
