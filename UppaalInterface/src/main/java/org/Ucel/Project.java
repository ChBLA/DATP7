package org.Ucel;

import java.util.*;

public class Project implements IProject {

    public Project() {
        this("", "");
    }

    public Project(String declaration, String systemDeclarations) {
        setDeclaration(declaration);
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

    private Dictionary<String, ITemplate> templates = new Hashtable<>();

    @Override
    public List<ITemplate> getTemplates() {
        return Collections.list(templates.elements());
    }

    public void putTemplate(Template template) {
        templates.put(template.getName(), template);
    }

    public ITemplate getTemplate(String name) {
        return templates.get(name);
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
