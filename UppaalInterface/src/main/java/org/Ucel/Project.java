package org.Ucel;

import java.util.ArrayList;
import java.util.List;

public class Project implements IProject {

    public Project () {
        this("", "");
    }

    public Project(String declaration, String systemDeclarations) {
        setDeclaration(declaration);
        templates = new ArrayList<ITemplate>();
        setSystemDeclarations(systemDeclarations);
    }

    private String declaration;

    @Override
    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String value) {
        declaration = value;
    }

    private ArrayList<ITemplate> templates;

    @Override
    public List<ITemplate> getTemplates() {
        return templates;
    }

    private String systemDeclarations;

    @Override
    public String getSystemDeclarations() {
        return systemDeclarations;
    }

    public void setSystemDeclarations(String value) {
        systemDeclarations = value;
    }
}
