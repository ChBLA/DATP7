package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class ProjectTemplate extends Template {
    public Template pDeclarationTemplate;
    public Template pTemplatesTemplate;
    public Template pSystemTemplate;
    public Template verificationListTemplate;

    public ProjectTemplate(List<PTemplateTemplate> pTemplates, Template pDeclarationTemplate, Template pSystemTemplate, Template verificationListTemplate) {
        template = new ST("");
        this.pSystemTemplate = pSystemTemplate;
        this.pDeclarationTemplate = pDeclarationTemplate;
        this.pTemplatesTemplate = new PTemplatesTemplate(pTemplates);
        this.verificationListTemplate = verificationListTemplate;
    }

}
