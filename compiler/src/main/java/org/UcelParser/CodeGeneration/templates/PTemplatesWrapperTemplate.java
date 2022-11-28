package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class PTemplatesWrapperTemplate extends Template {
    public List<PTemplateTemplate> pTemplates;

    public PTemplatesWrapperTemplate(List<PTemplateTemplate> pTemplateTemplateList) {
        template = new ST("");
        this.pTemplates = pTemplateTemplateList;
    }
}
