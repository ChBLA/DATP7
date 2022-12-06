package org.UcelPlugin;

import com.uppaal.plugin.Plugin;
import com.uppaal.plugin.PluginWorkspace;
import com.uppaal.plugin.Registry;
import org.Ucel.IProject;
import org.UcelParser.Compiler;
import org.UcelParser.Util.Exception.ErrorsFoundException;

public class UcelPlugin implements Plugin {
    private UcelEditorUI ui = new UcelEditorUI();
    private PluginWorkspace[] workspaces = new PluginWorkspace[]{ui};

    public PluginWorkspace[] getWorkspaces() {
        return workspaces;
    }

    private UppaalManager uppaalManager;

    private IProject previousIProject = null;

    public UcelPlugin(Registry registry) {
        this.uppaalManager = new UppaalManager(registry);

        ui.getCompileToEditorButton().addOnClickAsync(e -> {
            var currentProject = uppaalManager.getProject();
            previousIProject = currentProject;

            var compiled = compileProjectWithUiNotifications(currentProject);
            if(compiled != null)
                uppaalManager.setProject(compiled);
        });

        ui.getCompileToEngineButton().addOnClickAsync(e -> {
            var currentProject = uppaalManager.getProject();

            var compiled = compileProjectWithUiNotifications(currentProject);
            if(compiled != null)
                uppaalManager.setModelCheckerProject(compiled);
        });

        ui.getUndoButton().addOnClick(e -> {
            if(previousIProject != null) {
                uppaalManager.setProject(previousIProject);
                ui.getStatusArea().setUndoSuccess();
            }
        });

        ui.getTryBuildButton().addOnClickAsync(e -> {
            compileProjectWithUiNotifications(uppaalManager.getProject());
        });
    }

    private IProject compileProject(IProject project) throws ErrorsFoundException {
        Compiler compiler = new Compiler();
        return compiler.compileProject(project);
    }

    private IProject compileProjectWithUiNotifications(IProject project) {
        try {
            var compiledProject = compileProject(project);
            ui.getStatusArea().setSuccess();
            return compiledProject;
        }
        catch (ErrorsFoundException err) {
            ui.getStatusArea().setErrors(err.getLogs());
            return null;
        }
        catch (Throwable ex) {
            ui.getStatusArea().setError(ex);
            return null;
        }
        catch (StackOverflowError err) {
            return null;
        }
    }
}
