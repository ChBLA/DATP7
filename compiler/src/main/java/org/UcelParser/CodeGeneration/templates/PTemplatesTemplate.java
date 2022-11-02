package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class PTemplatesTemplate extends Template {
    public final List<PTemplateTemplate> pTemplateTemplates;

    public PTemplatesTemplate(List<PTemplateTemplate> pTemplates) {
        template = new ST("");
        this.pTemplateTemplates = pTemplates;
    }
}
