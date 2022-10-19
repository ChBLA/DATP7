package org.UcelPlugin;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Document;
import org.UcelPlugin.Models.SharedInterface.Graph;
import org.UcelPlugin.Models.SharedInterface.Project;
import org.UcelPlugin.Models.SharedInterface.Template;

import java.util.ArrayList;

public class DocumentParser {
    public Project parseDocument(Document document) {
        Project project = new Project();

        // Declarations
        project.setDeclaration(document.getPropertyValue("declaration"));

        // Templates
        for(Template tmp: parseTemplates(document)) {
            project.putTemplate(tmp);
        }

        // System Declaration
        project.setSystemDeclarations(document.getPropertyValue("system"));

        return project;
    }

    public ArrayList<Template> parseTemplates(Document document) {
        ArrayList <Template> parsedTemplates = new ArrayList<>();

        // AbstractTemplate is a list. It has a `first` template, and can then be iterated on `next`
        AbstractTemplate modelTemplate = (AbstractTemplate) document.first;
        while (modelTemplate != null) {
            parsedTemplates.add(parseTemplate(modelTemplate));
            modelTemplate = (AbstractTemplate) modelTemplate.next;
        }

        return parsedTemplates;
    }

    public Template parseTemplate(AbstractTemplate uppaalTemplate) {
        Template interfaceTemplate = new Template();
        interfaceTemplate.setName(uppaalTemplate.getPropertyValue("name"));
        interfaceTemplate.setParameters(uppaalTemplate.getPropertyValue("parameter"));
        //interfaceTemplate.setGraph();
        interfaceTemplate.setDeclarations(uppaalTemplate.getPropertyValue("declaration"));

        return interfaceTemplate;
    }

    public Graph parseGraph() {
        Graph newGraph = new Graph();


        return newGraph;
    }

}
