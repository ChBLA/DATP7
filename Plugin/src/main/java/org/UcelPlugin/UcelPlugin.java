package org.UcelPlugin;

import com.uppaal.model.core2.Document;
import com.uppaal.plugin.Plugin;
import com.uppaal.plugin.PluginWorkspace;
import com.uppaal.plugin.Registry;
import org.Ucel.IProject;
import org.UcelParser.Compiler;
import org.UcelPlugin.DocumentParser.UcelToUppaalDocumentParser;
import org.UcelPlugin.DocumentParser.UppaalToUcelDocumentParser;
import org.UcelPlugin.Models.SharedInterface.Project;

public class UcelPlugin implements Plugin {
    private UcelEditorUI ui = new UcelEditorUI();
    private PluginWorkspace[] workspaces = new PluginWorkspace[]{ui};

    public PluginWorkspace[] getWorkspaces() {
        return workspaces;
    }

    private UppaalManager uppaalManager;

    public UcelPlugin(Registry registry) {
        this.uppaalManager = new UppaalManager(registry);

        ui.addCompileAction(e ->
                setCurrentProject(CompileCurrentProject())
        );

        ui.addCompileActionX100(e -> {
            for (int i = 0; i < 100; i++)
                setCurrentProject(CompileCurrentProject());
        });
    }

    private IProject CompileCurrentProject() {
        Document document = uppaalManager.getCurrentDocument();
        UppaalToUcelDocumentParser documentParser = new UppaalToUcelDocumentParser(document);
        Project project = documentParser.parseDocument();

        Compiler compiler = new Compiler();
        return compiler.compileProject(project);
    }

    private void setCurrentProject(IProject project) {
        Document document = uppaalManager.getCurrentDocument();
        UcelToUppaalDocumentParser projParser = new UcelToUppaalDocumentParser(document);
        projParser.parseProject(project);
    }
}
