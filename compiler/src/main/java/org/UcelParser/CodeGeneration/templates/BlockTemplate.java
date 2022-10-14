package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class BlockTemplate extends Template {

    public BlockTemplate(List<Template> localDecls, List<Template> statements) {
        template = new ST("{<newlineSpecial><localDecls; separator=[newline]><newline><statements; separator=\"\">}");
        template.add("localDecls", localDecls);
        template.add("statements", statements);
        // We need this, or it breaks the formatting(???)
        template.add("newlineSpecial", (localDecls.isEmpty()) ? "" : System.lineSeparator());
        template.add("newline", System.lineSeparator());
    }
}
