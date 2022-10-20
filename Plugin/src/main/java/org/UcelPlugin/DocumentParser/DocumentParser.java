package org.UcelPlugin.DocumentParser;

import com.uppaal.model.core2.*;
import org.UcelPlugin.Models.SharedInterface.Graph;
import org.UcelPlugin.Models.SharedInterface.Project;
import org.UcelPlugin.Models.SharedInterface.Template;

import java.util.ArrayList;

public class DocumentParser {

    public DocumentParser(Document document) {
        this.document = document;
    }

    private Document document;


    public Project parseDocument() {
        Project project = new Project();

        // Declarations
        project.setDeclaration(document.getPropertyValue("declaration"));

        // Templates
        for (Template tmp : parseTemplates(document)) {
            project.putTemplate(tmp);
        }

        // System Declaration
        project.setSystemDeclarations(document.getPropertyValue("system"));

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
        GraphParser graphParser = new GraphParser();

        interfaceTemplate.setName(uppaalTemplate.getPropertyValue("name"));
        interfaceTemplate.setParameters(uppaalTemplate.getPropertyValue("parameter"));
        interfaceTemplate.setGraph(graphParser.parseGraph(uppaalTemplate));
        interfaceTemplate.setDeclarations(uppaalTemplate.getPropertyValue("declaration"));

        return interfaceTemplate;
    }
}
