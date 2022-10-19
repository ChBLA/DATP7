package org.UcelPlugin;

import com.uppaal.model.core2.Document;
import com.uppaal.plugin.Plugin;
import com.uppaal.plugin.PluginWorkspace;
import com.uppaal.plugin.Registry;
import org.UcelPlugin.Models.SharedInterface.Project;

import java.io.IOException;

public class UcelPlugin implements Plugin {
    private PluginWorkspace[] workspaces = new PluginWorkspace[1];
    private UppaalManager uppaalManager;

    public UcelPlugin(Registry registry) {
        workspaces[0] = new UcelEditorWorkspace();
        this.uppaalManager = new UppaalManager(registry);

//        try {
////            Document document = ModelDemo.loadModel("demo/train-gate.xml");
////            DocumentParser documentParser = new DocumentParser();
////            Project project = documentParser.parseDocument(document);
//        }
//        catch (IOException e) {
//            System.err.println("Model not found");
//        }
    }

    /**
     * Returns workspaces to be loaded into separate tabs.
     */
    public PluginWorkspace[] getWorkspaces() {
        return workspaces;
    }
}
