package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class UpdateTemplate extends Template {
    public UpdateTemplate(List<Template> updates) {
        template = new ST("<updates; separator=\", \">");
        template.add("updates", updates);
    }
}
