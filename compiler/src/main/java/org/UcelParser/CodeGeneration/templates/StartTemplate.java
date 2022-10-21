package org.UcelParser.CodeGeneration.templates;
import org.stringtemplate.v4.ST;

import java.util.List;

public class StartTemplate extends Template {
    public StartTemplate(Template decl, List<Template> stmnts, Template sys) {
        template = new ST("<decl><stmnts; separator=\"\n\"><newline><sys>");

        template.add("decl", decl);
        template.add("stmnts", stmnts);
        template.add("newline", stmnts.size() > 0 ? "\n" : "");
        template.add("sys", sys);
    }

}
