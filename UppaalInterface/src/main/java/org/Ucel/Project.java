package org.Ucel;

import java.util.ArrayList;
import java.util.List;

public class Project implements IProject {

    public Project () {
        this("", "");
    }

    public Project(String declaration, String systemDeclarations) {
        SetDeclaration(declaration);
        templates = new ArrayList<ITemplate>();
        SetSystemDeclarations(systemDeclarations);
    }

    private String declaration;

    @Override
    public String GetDeclaration() {
        return declaration;
    }

    public void SetDeclaration(String value) {
        declaration = value;
    }

    private ArrayList<ITemplate> templates;

    @Override
    public List<ITemplate> GetTemplates() {
        return templates;
    }

    private String systemDeclarations;

    @Override
    public String GetSystemDeclarations() {
        return systemDeclarations;
    }

    public void SetSystemDeclarations(String value) {
        systemDeclarations = value;
    }
}
