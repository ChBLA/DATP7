package org.UcelPlugin;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.PrototypeDocument;
import com.uppaal.plugin.PluginWorkspace;
import org.Ucel.IProject;
import org.UcelParser.Compiler;
import org.UcelParser.UCELParser_Generated.UCELLexer;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelPlugin.DocumentParser.DocumentParser;
import org.UcelPlugin.DocumentParser.ProjectToDocumentParser;
import org.UcelPlugin.Models.SharedInterface.Project;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

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

    public CodeTree getCurrentProject() {
        Document document = uppaalManager.getCurrentDocument();
        DocumentParser documentParser = new DocumentParser(document);
        Project project = documentParser.parseDocument();
        System.out.println(project);

        var declaration = project.getDeclaration();
        CharStream declCharStream = CharStreams.fromString(declaration);
        UCELLexer declLexer = new UCELLexer(declCharStream);
        CommonTokenStream declTokenStream = new CommonTokenStream(declLexer);
        UCELParser declParser = new UCELParser(declTokenStream);

        var declTree = declParser.pdeclaration();

        var templateTrees = new ArrayList<UCELParser.PtemplateContext>();
        for (var template : project.getTemplates()) {
            CharStream charStream = CharStreams.fromString(template.getDeclarations());
            UCELLexer lexer = new UCELLexer(charStream);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            UCELParser templateParser = new UCELParser(tokenStream);
            var templateTree = templateParser.ptemplate();
            templateTrees.add(templateTree);
        }

        var systemDeclaration = project.getSystemDeclarations();
        CharStream systemDeclCharStream = CharStreams.fromString(systemDeclaration);
        UCELLexer systemDeclLexer = new UCELLexer(systemDeclCharStream);
        CommonTokenStream systemDeclTokenStream = new CommonTokenStream(systemDeclLexer);
        UCELParser systemDeclParser = new UCELParser(systemDeclTokenStream);

        var systemDeclTree = systemDeclParser.psystem();

        return new CodeTree(declTree, templateTrees, systemDeclTree);
    }

    public void compileCurrentProject() {
        Document document = uppaalManager.getCurrentDocument();
        DocumentParser documentParser = new DocumentParser(document);
        Project project = documentParser.parseDocument();

        Compiler compiler = new Compiler();
        IProject compiledProject = compiler.compileProject(project);

        ProjectToDocumentParser projParser = new ProjectToDocumentParser(document);
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
