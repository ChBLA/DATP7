package org.UcelParser.CodeGeneration;

import org.Ucel.*;
import org.Ucel.Template;
import org.UcelParser.CodeGeneration.templates.*;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProjectCodeLinker {

    public Project generateUppaalProject(ProjectTemplate projectCode) {
        var project = new Project(projectCode.pDeclarationTemplate.toString(), projectCode.pSystemTemplate.toString());

        List<PQueryTemplate> verificationQueries = ((VerificationListTemplate) projectCode.verificationListTemplate).queries;
        for (var query : verificationQueries) {
            project.addVerificationQueries(new VerificationQuery(query.toString(), query.comment));
        }

        var templatesCode = ((PTemplatesTemplate)projectCode.pTemplatesTemplate).pTemplateTemplates;
        for (var temp : templatesCode) {
            var template = new Template();
            template.setName(temp.name);
            template.setDeclarations(temp.declarations.toString());
            template.setParameters(temp.parameters.toString());

            var graph = new Graph();

            HashMap<ILocation, Integer> locationMap = new HashMap<>();

            for (var l : temp.graph.nodes) {
                var location = (LocationTemplate) l;
                var loc = new Location();

                var locNode = location.location;

                loc.setPosX(locNode.posX);
                loc.setPosY(locNode.posY);
                loc.setName(location.ID);
                loc.setInvariant(location.invariant.toString());
                loc.setRateOfExponential(location.exponential.toString());
                loc.setInitial(locNode.isInitial);
                loc.setUrgent(locNode.isUrgent);
                loc.setCommitted(locNode.isCommitted);
                loc.setComments(locNode.comments);
                loc.setTestCodeOnEnter(locNode.testCodeEnter);
                loc.setTestCodeOnExit(locNode.testCodeExit);

                graph.addLocation(loc);
                locationMap.put(loc, locNode.id);
            }

            for (var e : temp.graph.edges) {
                var edgeCode = (EdgeTemplate) e;
                var edge = new Edge();

                var edgeNode = edgeCode.edge;

                for (var entry : locationMap.entrySet()) {
                    if (Objects.equals(entry.getValue(), edgeNode.locationStartID))
                        edge.setLocationStart(entry.getKey());
                    if (Objects.equals(entry.getValue(), edgeNode.locationEndID))
                        edge.setLocationEnd(entry.getKey());
                }

                edge.setSelect(edgeCode.select.toString());
                edge.setGuard(edgeCode.guard.toString());
                edge.setSync(edgeCode.sync.toString());
                edge.setUpdate(edgeCode.update.toString());
                edge.setComment(edgeNode.comments);
                edge.setTestCode(edgeNode.testCode);

                graph.addEdge(edge);
            }
            template.setGraph(graph);
            project.putTemplate(template);
        }

        return project;
    }


}
