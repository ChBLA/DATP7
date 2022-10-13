package org.UcelPlugin;

import com.uppaal.plugin.Plugin;
import com.uppaal.plugin.PluginWorkspace;
import com.uppaal.plugin.Registry;
import com.uppaal.plugin.Repository;

public class PluginDemo implements Plugin
{
    private Registry registry = null;
    private PluginWorkspace[] workspaces = new PluginWorkspace[1];

    public PluginDemo(Registry registry)
    {
        System.out.println("PluginDemo created");
        workspaces[0] = new WorkspaceDemo();
        this.registry = registry;

        // this.registry.getRepositoryNames();
        for(Object reg : this.registry.getRepositoryNames()) {
            System.out.println(reg);
        }

        System.out.println("Editor content:");
        Repository editorRepository = this.registry.getRepository("EditorDocument");
        //editorRepository.
        //editorRepository.
    }

    /** Returns workspaces to be loaded into separate tabs. */
    public PluginWorkspace[] getWorkspaces() { return workspaces; }
}
