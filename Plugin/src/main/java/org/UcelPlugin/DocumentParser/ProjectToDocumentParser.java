package org.UcelPlugin.DocumentParser;

import com.uppaal.model.core2.*;
import org.Ucel.IGraph;
import org.Ucel.IProject;
import org.Ucel.ITemplate;

import java.util.List;

public class ProjectToDocumentParser {

    public ProjectToDocumentParser() {
        this(new Document(new PrototypeDocument()));
    }

    public ProjectToDocumentParser(Document document) {
        this.document = document;
    }

    private Document document;

    public Document getDocument() {
        return document;
    }

    public void parseProject(IProject project) {
        // Declarations
        document.setProperty("declaration", project.getDeclaration());

        // Templates
        removeTemplates();
        addTemplates(project.getTemplates());

        // System Declaration
        document.setProperty("system", project.getSystemDeclarations());
    }

    public void removeTemplates() {
        Node template = document.getTemplates();
        while(template != null) {
            template.remove();
            template = template.next;
        }
    }

    private void addTemplates(List<ITemplate> inputTemplates) {
        for(var tmp: inputTemplates)
            addTemplate(tmp);
    }

    private void addTemplate(ITemplate inputTemplate) {
        var outTemp = document.createTemplate();
        document.insert(outTemp, null);

        outTemp.setProperty("name", inputTemplate.getName());
        outTemp.setProperty("parameter", inputTemplate.getParameters());
        addGraph(outTemp, inputTemplate.getGraph());
        outTemp.setProperty("declaration", inputTemplate.getDeclarations());
    }

    private void addGraph(Template template, IGraph graph) {
        var graphParser = new GraphParserUcelToUpp(template, graph);
        graphParser.addGraph();
    }
}
