package org.UcelPlugin;

import com.uppaal.model.core2.Document;
import com.uppaal.plugin.Registry;
import com.uppaal.plugin.Repository;
import org.Ucel.IProject;
import org.UcelPlugin.DocumentParser.UcelToUppaalDocumentParser;
import org.UcelPlugin.DocumentParser.UppaalToUcelDocumentParser;

public class UppaalManager {
    protected Registry registry;
    protected Repository concreteTraceRepository;
    protected Repository editorDocumentRepository;
    protected Repository editorProblemsRepository;
    protected Repository symbolicTraceRepository;
    protected Repository systemModelRepository;
    public UppaalManager(Registry registry) {
        this.registry = registry;

        this.concreteTraceRepository = this.registry.getRepository("ConcreteTrace");
        this.editorDocumentRepository = this.registry.getRepository("EditorDocument");
        this.editorProblemsRepository = this.registry.getRepository("EditorProblems");
        this.symbolicTraceRepository = this.registry.getRepository("SymbolicTrace");
        this.systemModelRepository = this.registry.getRepository("SystemModel");

    }

    protected Document getCurrentDocument() {
        Repository reg = registry.getRepository("EditorDocument");
        Document doc = (Document)reg.get();
        return doc;
    }

    public IProject getProject() {
        Document document = getCurrentDocument();
        UppaalToUcelDocumentParser documentParser = new UppaalToUcelDocumentParser(document);
        return documentParser.parseDocument();
    }

    public void setProject(IProject project) {
        Document document = getCurrentDocument();
        UcelToUppaalDocumentParser projParser = new UcelToUppaalDocumentParser(document);
        projParser.parseProject(project);
    }

    public void getEngine() {

    }


}
