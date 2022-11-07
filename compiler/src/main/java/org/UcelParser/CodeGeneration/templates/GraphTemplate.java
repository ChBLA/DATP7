package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.List;

public class GraphTemplate extends Template {

    public final List<Template> nodes;
    public final List<Template> edges;

    public GraphTemplate(List<Template> nodes, List<Template> edges) {
        template = new ST("");
        this.nodes = nodes;
        this.edges = edges;
    }
}
