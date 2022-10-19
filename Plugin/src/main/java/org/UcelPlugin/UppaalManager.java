package org.UcelPlugin;

import com.uppaal.plugin.Registry;
import com.uppaal.plugin.Repository;

public class UppaalManager {
    public Registry registry;
    public Repository concreteTraceRepository;
    public Repository editorDocumentRepository;
    public Repository editorProblemsRepository;
    public Repository symbolicTraceRepository;
    public Repository systemModelRepository;
    public UppaalManager(Registry registry) {
        this.registry = registry;

        this.concreteTraceRepository = this.registry.getRepository("ConcreteTrace");
        this.editorDocumentRepository = this.registry.getRepository("EditorDocument");
        this.editorProblemsRepository = this.registry.getRepository("EditorProblems");
        this.symbolicTraceRepository = this.registry.getRepository("SymbolicTrace");
        this.systemModelRepository = this.registry.getRepository("SystemModel");

    }

}
