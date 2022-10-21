package org.UcelParser.CodeGeneration.templates;
import org.stringtemplate.v4.ST;

import java.util.List;

public class SystemTemplate extends Template{
    public SystemTemplate(List<Template> exprs) {
        template = new ST("system <exprs; separator=\", \">;");
        template.add("exprs", exprs);
    }
}
