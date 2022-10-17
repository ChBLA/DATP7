package org.Ucel;

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
}
