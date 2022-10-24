package org.UcelPlugin.DocumentParser;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Property;
import com.uppaal.model.core2.PrototypeDocument;
import org.Ucel.IProject;
import org.UcelPlugin.Models.SharedInterface.Project;
import org.UcelPlugin.Models.SharedInterface.Template;

public class ProjectToDocumentParser {

    public ProjectToDocumentParser() {
        this(new PrototypeDocument().getDocument());
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
//        for (Template tmp : parseTemplates(document)) {
//            project.putTemplate(tmp);
//        }

        // System Declaration
        document.setProperty("system", project.getSystemDeclarations());
    }

}
