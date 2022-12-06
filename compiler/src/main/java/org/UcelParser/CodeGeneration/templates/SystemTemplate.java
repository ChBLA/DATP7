package org.UcelParser.CodeGeneration.templates;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

public class SystemTemplate extends Template{
    public SystemTemplate(List<Template> cons, List<String> names) {
        template = new ST("// Declarations for the necessary processes<newline><decls; separator=[newline]><newline><newline>system <names; separator=\", \">;");
        template.add("names", names);
        assert cons.size() == names.size();

        List<String> decls = new ArrayList<>();
        for (int i = 0; i < cons.size(); i++) {
            if (!(cons.get(i) instanceof ManualTemplate) || !cons.get(i).toString().isEmpty())
                decls.add(String.format("%s = %s;", names.get(i), cons.get(i)));
        }
        template.add("decls", decls);
        template.add("newline", System.lineSeparator());
    }
}
