package org.UcelPlugin;

import com.uppaal.model.core2.Document;
import com.uppaal.plugin.Plugin;
import com.uppaal.plugin.PluginWorkspace;
import com.uppaal.plugin.Registry;
import org.Ucel.IProject;
import org.UcelParser.Compiler;
import org.UcelParser.Util.Exception.ErrorsFoundException;
import org.UcelPlugin.DocumentParser.UcelToUppaalDocumentParser;
import org.UcelPlugin.DocumentParser.UppaalToUcelDocumentParser;

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

        ui.getCompileButton().addOnClick(e -> {
            var currentProject = getCurrentProject();
            previousIProject = currentProject;
            compileAndSetInNewThread(currentProject);
        });

        ui.getCompileX100Button().addOnClick(e -> {
            previousIProject = getCurrentProject();
            new Thread(() -> {
                try {
                    for (int i = 0; i < 100; i++)
                        setCurrentProject(compileProject(getCurrentProject()));

                    ui.getStatusArea().setSuccess();
                }
                catch (ErrorsFoundException err) {
                    ui.getStatusArea().setErrors(err.getLogs());
                }
                catch (Exception ex) {
                    ui.getStatusArea().setError(ex);
                }
            }).start();
        });

        ui.getUndoButton().addOnClick(e -> {
            if(previousIProject != null) {
                setCurrentProject(previousIProject);
                ui.getStatusArea().setUndoSuccess();
            }
        });

        ui.getTryBuildButton().addOnClick(e -> {
            compileProjectWithUiNotifications(getCurrentProject());
        });
    }

    private IProject getCurrentProject() {
        Document document = uppaalManager.getCurrentDocument();
        UppaalToUcelDocumentParser documentParser = new UppaalToUcelDocumentParser(document);
        return documentParser.parseDocument();
    }

    private IProject compileProject(IProject project) throws ErrorsFoundException {
        Compiler compiler = new Compiler();
        return compiler.compileProject(project);
    }

    private void compileAndSetInNewThread(IProject project) {
        ui.getStatusArea().setCompiling();
        new Thread(() -> {
            var compiled = compileProjectWithUiNotifications(project);
            if(compiled != null)
                setCurrentProject(compiled);
        }).start();
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
        catch (Exception ex) {
            ui.getStatusArea().setError(ex);
            return null;
        }
    }

    private void setCurrentProject(IProject project) {
        Document document = uppaalManager.getCurrentDocument();
        UcelToUppaalDocumentParser projParser = new UcelToUppaalDocumentParser(document);
        projParser.parseProject(project);
    }
}
