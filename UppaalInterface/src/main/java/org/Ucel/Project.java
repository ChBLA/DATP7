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

    private ArrayList<IVerificationQuery> verificationQueries = new ArrayList<>();
    public List<IVerificationQuery> getVerificationQueries() {
        return verificationQueries;
    }
    public void addVerificationQueries(IVerificationQuery expr) {
        verificationQueries.add(expr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(declaration, project.declaration)
                &&  Objects.equals(templates, project.templates)
                && Objects.equals(systemDeclarations, project.systemDeclarations)
                && Objects.equals(verificationQueries, project.verificationQueries)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaration, templates, systemDeclarations, verificationQueries);
    }
}
