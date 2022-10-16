package org.UcelParser.CodeGeneration.templates;
import org.stringtemplate.v4.ST;

import java.util.List;

public class ParameterTemplate extends Template {
    public ParameterTemplate(Template type, String amp, String id, List<Template> arrayDecls) {
        template = new ST("<type><amp> <id><arrayDecls; separator=\"\">");
        template.add("type", type);
        template.add("amp", amp);
        template.add("id", id);
        template.add("arrayDecls", arrayDecls);
    }
}
