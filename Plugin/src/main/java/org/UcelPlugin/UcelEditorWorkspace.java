package org.UcelPlugin;

import com.uppaal.model.core2.Document;
import com.uppaal.plugin.PluginWorkspace;
import org.Ucel.IProject;
import org.UcelParser.Compiler;
import org.UcelPlugin.DocumentParser.UppaalToUcelDocumentParser;
import org.UcelPlugin.DocumentParser.UcelToUppaalDocumentParser;
import org.UcelPlugin.Models.SharedInterface.Project;
;

import javax.swing.*;
import java.awt.*;

public class UcelEditorWorkspace implements PluginWorkspace {

    public UcelEditorWorkspace(UppaalManager uppaalManager) {
        this.uppaalManager = uppaalManager;
        editorUi = new UcelEditorUI(this);
    }

    private UppaalManager uppaalManager;

    private UcelEditorUI editorUi;
    private UcelEditorUI getUi() {
        return editorUi;
    }



    public void compileCurrentProject() {
        Document document = uppaalManager.getCurrentDocument();
        UppaalToUcelDocumentParser documentParser = new UppaalToUcelDocumentParser(document);
        Project project = documentParser.parseDocument();

        Compiler compiler = new Compiler();
        IProject compiledProject = compiler.compileProject(project);

        UcelToUppaalDocumentParser projParser = new UcelToUppaalDocumentParser(document);
        projParser.parseProject(compiledProject);

        System.out.println(project);
    }

    @Override
    public String getTitle() {
        return "UCEL";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getTitleToolTip() {
        return "Uppaal Component Extension Language - Editor";
    }

    @Override
    public Component getComponent() {
        return getUi().getJPanel();
    }

    @Override
    public int getDevelopmentIndex() {
        return 0;
    }

    @Override
    public boolean getCanZoom() {
        return false;
    }

    @Override
    public boolean getCanZoomToFit() {
        return false;
    }

    @Override
    public double getZoom() {
        return 1;
    }

    @Override
    public void setZoom(double v) {

    }

    @Override
    public void zoomToFit() {

    }

    @Override
    public void zoomIn() {

    }

    @Override
    public void zoomOut() {

    }

    @Override
    public void setActive(boolean b) {

    }
}
