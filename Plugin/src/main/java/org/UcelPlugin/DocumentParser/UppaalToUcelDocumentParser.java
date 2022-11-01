package org.UcelPlugin.DocumentParser;

import com.uppaal.model.core2.*;
import org.UcelPlugin.Models.SharedInterface.Project;
import org.UcelPlugin.Models.SharedInterface.Template;

import java.util.ArrayList;

public class UppaalToUcelDocumentParser {

    public UppaalToUcelDocumentParser(Document document) {
        this.document = document;
    }

    private Document document;


    public Project parseDocument() {
        Project project = new Project();

        // Declarations
        project.setDeclaration( (String) document.getPropertyValue("declaration"));

        // Templates
        for (Template tmp : parseTemplates(document)) {
            project.putTemplate(tmp);
        }

        // System Declaration
        project.setSystemDeclarations( (String) document.getPropertyValue("system"));

        return project;
    }

    private ArrayList<Template> parseTemplates(Document document) {
        ArrayList<Template> parsedTemplates = new ArrayList<>();

        // AbstractTemplate is a list. It has a `first` template, and can then be iterated on `next`
        AbstractTemplate modelTemplate = (AbstractTemplate) document.first;
        while (modelTemplate != null) {
            parsedTemplates.add(parseTemplate(modelTemplate));
            modelTemplate = (AbstractTemplate) modelTemplate.next;
        }

        return parsedTemplates;
    }

    private Template parseTemplate(AbstractTemplate uppaalTemplate) {
        Template interfaceTemplate = new Template();
        UppaalToUcelGraphParser graphParser = new UppaalToUcelGraphParser();

        interfaceTemplate.setName( (String) uppaalTemplate.getPropertyValue(UppaalPropertyNames.Template.name));
        interfaceTemplate.setParameters( (String) uppaalTemplate.getPropertyValue(UppaalPropertyNames.Template.parameter));
        interfaceTemplate.setGraph(graphParser.parseGraph(uppaalTemplate));
        interfaceTemplate.setDeclarations( (String) uppaalTemplate.getPropertyValue(UppaalPropertyNames.Template.declaration));

        return interfaceTemplate;
    }
}
