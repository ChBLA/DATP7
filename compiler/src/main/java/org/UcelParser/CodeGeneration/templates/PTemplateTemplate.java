package org.UcelParser.CodeGeneration.templates;

import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

public class PTemplateTemplate extends Template {
    public final String name;
    public final Template parameters;
    public final GraphTemplate graph;
    public final Template declarations;
    public final Template sysDeclarations;
    public final List<String> namesForSysDeclarations;

    public PTemplateTemplate(String name, Template parameters, GraphTemplate graph, Template declarations, Template sysDecl, List<String> names) {
        template = new ST("");
        this.name = name;
        this.parameters = parameters;
        this.graph = graph;
        this.declarations = declarations;
        this.sysDeclarations = sysDecl;
        this.namesForSysDeclarations = names;
    }

    // For testing only!
    public PTemplateTemplate() {
        template = new ST("");
        this.name = "";
        this.parameters = new ManualTemplate("");
        this.graph = new GraphTemplate(new ArrayList<>(), new ArrayList<>());
        this.declarations = new ManualTemplate("");
        this.sysDeclarations = new ManualTemplate("");
        this.namesForSysDeclarations = new ArrayList<>();
    }

}
