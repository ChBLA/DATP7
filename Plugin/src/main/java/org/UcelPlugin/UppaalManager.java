package org.UcelPlugin;

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.EngineStub;
import com.uppaal.engine.Problem;
import com.uppaal.model.core2.*;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.plugin.Registry;
import com.uppaal.plugin.Repository;
import org.Ucel.IProject;
import org.UcelPlugin.DocumentParser.UcelToUppaalDocumentParser;
import org.UcelPlugin.DocumentParser.UppaalToUcelDocumentParser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import static com.uppaal.plugin.Repository$ChangeType.*;

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

        addListenerForDocUpdates(editorDocumentRepository);
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

    //region Set Editor Project
    public void setProject(IProject project) {
        Document document = getCurrentDocument();
        UcelToUppaalDocumentParser projParser = new UcelToUppaalDocumentParser(document);
        projParser.parseProject(project);
        editorDocumentRepository.fire(REPLACED);
    }
    //endregion

    //region Set Engine Project
    public void setModelCheckerProject(IProject project) {
        UcelToUppaalDocumentParser projParser = new UcelToUppaalDocumentParser();
        projParser.parseProject(project);

        Engine engine;
        try {
            engine = connectToEngine();
        }
        catch (Exception err) {
            throw new RuntimeException();
        }
        var document = projParser.getDocument();

        UppaalSystem system;
        try {
            system = compile(engine, document);
        }
        catch (Exception err) {
            throw new RuntimeException();
        }

        this.registry.getRepository("SystemModel").set(system);
    }

    private static Engine connectToEngine() throws EngineException, IOException
    {
        String os = System.getProperty("os.name");
        String here = System.getProperty("user.dir");
        String path = null;
        if ("Linux".equals(os)) {
            path = here+"/bin-Linux/server";
        } else if ("Mac OS X".equals(os)) {
            path = here+"/bin-Darwin/server";
        } else if (os.contains("Windows")) {
            path = here+"\\bin-Windows\\server.exe";
        } else {
            throw new RuntimeException("Unknown operating system: " + os);
        }
        Engine engine = new Engine();
        engine.setServerPath(path);
        engine.setServerHost("localhost");
        engine.setConnectionMode(EngineStub.BOTH);
        engine.connect();
        return engine;
    }

    private static UppaalSystem compile(Engine engine, Document doc)
            throws EngineException
    {
        // compile the model into system:
        ArrayList<Problem> problems = new ArrayList<>();
        UppaalSystem sys = engine.getSystem(doc, problems);
        if (!problems.isEmpty()) {
            boolean fatal = false;
            System.out.println("There are problems with the document:");
            for (Problem p : problems) {
                System.out.println(p.toString());
                if (!"warning".equals(p.getType())) { // ignore warnings
                    fatal = true;
                }
            }
            if (fatal) {
                throw new RuntimeException("Errors in generated UPPAAL code");
            }
        }
        return sys;
    }
    //endregion

    //region Events
    private ArrayList<Consumer> onOnDocChange = new ArrayList<>();
    public void addOnDocChange(Consumer onChanged) {
        onOnDocChange.add(onChanged);
    }

    public enum documentChangeTypes {
        UPDATED,
        REPLACED,
        RESTRUCTURED
    }
    protected void emitOnDocChange(documentChangeTypes type) {
        for(var action: onOnDocChange) {
            action.accept(null);
        }
    }

    private void addListenerForDocUpdates(Repository docRep) {
        docRep.addListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String evtTypeName = evt.getPropertyName();

                if(evtTypeName == UPDATED.name())
                    emitOnDocChange(documentChangeTypes.UPDATED);

                else if(evtTypeName == REPLACED.name())
                    emitOnDocChange(documentChangeTypes.REPLACED);

                else if(evtTypeName == RESTRUCTURED.name())
                    emitOnDocChange(documentChangeTypes.RESTRUCTURED);

                else
                    System.err.println("Unhandled event type: " + evt.getPropertyName());
            }
        });
    }

    //endregion
}
