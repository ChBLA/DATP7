package org.UcelPlugin;

import com.uppaal.plugin.Plugin;
import com.uppaal.plugin.PluginWorkspace;
import com.uppaal.plugin.Registry;
import org.Ucel.IProject;
import org.UcelParser.Compiler;
import org.UcelParser.Util.Exception.ErrorsFoundException;

import java.util.ArrayList;
import java.util.function.Consumer;

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
            emitOnCompile(false);
            var currentProject = uppaalManager.getProject();
            previousIProject = currentProject;
            hasUndoableChanges = true;
            updatedSinceCompile = false;

            var compiled = compileProjectWithUiNotifications(currentProject);
            if(compiled != null)
                uppaalManager.setProject(compiled);
            emitOnCompile(true);
        });

        ui.getCompileToEngineButton().addOnClickAsync(e -> {
            emitOnCompile(false);
            var currentProject = uppaalManager.getProject();

            var compiled = compileProjectWithUiNotifications(currentProject);
            if(compiled != null)
                uppaalManager.setModelCheckerProject(compiled);
            emitOnCompile(true);
        });

        ui.getUndoButton().addOnClick(e -> {
            emitOnUndo();
            if(previousIProject != null) {
                uppaalManager.setProject(previousIProject);
                ui.getStatusArea().setUndoSuccess();
            }
        });

        ui.getTryBuildButton().addOnClickAsync(e -> {
            emitOnCompile(false);
            compileProjectWithUiNotifications(uppaalManager.getProject());
            emitOnCompile(true);
        });

        registerListeners();
    }

    //region Events
    //region Emitters
    //region onCompile
    private ArrayList<Consumer<Boolean>> onCompileActions = new ArrayList<>();
    public void addOnCompile(Consumer<Boolean> onCompile) {
        onCompileActions.add(onCompile);
    }
    protected void emitOnCompile(boolean isDone) {
        for(var action: onCompileActions) {
            action.accept(isDone);
        }
    }
    //endregion
    //region onUndo
    private ArrayList<Consumer> onUndoActions = new ArrayList<>();
    public void addOnUndo(Consumer onUndo) {
        onUndoActions.add(onUndo);
    }
    protected void emitOnUndo() {
        for(var action: onUndoActions) {
            action.accept(null);
        }
    }
    //endregion
    //region onUserChange
    private ArrayList<Consumer> onUserChange = new ArrayList<>();
    public void addOnUserChange(Consumer onUndo) {
        onUndoActions.add(onUndo);
    }
    protected void emitOnUserChange() {
        for(var action: onUserChange) {
            action.accept(null);
        }
    }
    //endregion


    //endregion
    //region Listeners

    private boolean updatedSinceCompile = true;
    private boolean hasUndoableChanges = false;
    private boolean isCompiling = false;
    private void registerListeners() {
        addOnCompile((isDone) -> {
            this.isCompiling = !isDone;

            updateButtonStates();
        });

        addOnUndo((x) -> {
            this.hasUndoableChanges = false;

            updateButtonStates();
        });

        uppaalManager.addOnDocChange((updateType) -> {
            if(!isCompiling)
                updatedSinceCompile = true;

            updateButtonStates();
        });

        // Live updating error list
        uppaalManager.addOnDocChange((updateType) -> {
            Compiler compiler = new Compiler();
            uppaalManager.clearProblems();
            try {
                compiler.compileProject(uppaalManager.getProject());
            } catch (ErrorsFoundException e) {
                for(var log: e.getLogs()) {
                    uppaalManager.addProblem("location", log.getFancyMessage());
                }
                uppaalManager.updateProblemDisplay();
            }
            catch (Throwable err) {
                uppaalManager.addProblem("location", err.getMessage());
                uppaalManager.updateProblemDisplay();
            }
        });
    }

    private void updateButtonStates() {
        ui.getCompileToEditorButton().setEnabled(updatedSinceCompile && !isCompiling);
        ui.getCompileToEngineButton().setEnabled(updatedSinceCompile && !isCompiling);
        ui.getUndoButton().setEnabled(hasUndoableChanges && !isCompiling);
        ui.getTryBuildButton().setEnabled(!isCompiling);
    }
    //endregion
    //endregion

    private IProject compileProjectWithUiNotifications(IProject project) {
        try {
            ui.getStatusArea().setCompiling();
            Compiler compiler = new Compiler();
            var compiledProject = compiler.compileProject(project);
            var logs = compiler.getLogs();
            if(logs.size() == 0) {
                ui.getStatusArea().setSuccess();
            }
            else {
                ui.getStatusArea().setErrors(logs);
            }
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
    }
}
