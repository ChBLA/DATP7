package org.Ucel;

import java.util.Objects;

public class Template implements ITemplate {

    private String name = "";

    @Override
    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    private String parameters = "";

    @Override
    public String getParameters() {
        return parameters;
    }

    public void setParameters(String value) {
        parameters = value;
    }

    private IGraph graph = new Graph();

    @Override
    public IGraph getGraph() {
        return graph;
    }

    public void setGraph(IGraph value) {
        graph = value;
    }

    private String declarations = "";

    @Override
    public String getDeclarations() {
        return declarations;
    }

    public void setDeclarations(String value) {
        declarations = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Template template = (Template) o;
        return Objects.equals(name, template.name) && Objects.equals(parameters, template.parameters) && Objects.equals(graph, template.graph) && Objects.equals(declarations, template.declarations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters, graph, declarations);
    }
}
