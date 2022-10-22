package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

public class ManualTemplate extends Template{

    public ManualTemplate(String s) {
        template = new ST("<s>");
        template.add("s", s);
    }
}
