package org.UcelPlugin;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.PrototypeDocument;
import com.uppaal.plugin.Plugin;
import com.uppaal.plugin.PluginWorkspace;
import com.uppaal.plugin.Registry;
import org.UcelPlugin.DocumentParser.DocumentParser;
import org.UcelPlugin.Models.SharedInterface.Project;

import java.io.IOException;
import java.net.URL;

public class UcelPlugin implements Plugin {
    private PluginWorkspace[] workspaces = new PluginWorkspace[1];
    private UppaalManager uppaalManager;

    public UcelPlugin(Registry registry) {
        this.uppaalManager = new UppaalManager(registry);
        workspaces[0] = new UcelEditorWorkspace(uppaalManager);

        try {
            Document document = new PrototypeDocument().load(new URL("file", null, "demo/train-gate.xml"));
            DocumentParser documentParser = new DocumentParser(document);
            Project project = documentParser.parseDocument();
            System.out.println(project);
        }
        catch (IOException e) {
            System.err.println("Model not found");
        }
    }

    /**
     * Returns workspaces to be loaded into separate tabs.
     */
    public PluginWorkspace[] getWorkspaces() {
        return workspaces;
    }
}