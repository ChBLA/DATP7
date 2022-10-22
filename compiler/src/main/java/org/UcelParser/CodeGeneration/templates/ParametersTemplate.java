package org.UcelParser.CodeGeneration.templates;
import org.stringtemplate.v4.ST;

import java.util.List;

public class ParametersTemplate extends Template {
    public ParametersTemplate(List<Template> params) {
        template = new ST("<params; separator=\", \">");
        template.add("params", params);
    }
}
