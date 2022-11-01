package org.UcelPlugin.DocumentParser;

import com.uppaal.model.core2.*;
import org.UcelPlugin.Models.SharedInterface.Graph;

import java.util.Hashtable;

public class UppaalToUcelGraphParser {

    public UppaalToUcelGraphParser() {

    }

    public Graph parseGraph(AbstractTemplate uppaalTemplate) {
        Graph newGraph = new Graph();

        visitGraph(uppaalTemplate.first, newGraph);

        return newGraph;
    }

    private void visitGraph(Node node, Graph newGraph) {
        if (node == null)
            ;
        else if (node instanceof Location)
            visitGraph((Location) node, newGraph);
        else if (node instanceof Edge)
            visitGraph((Edge) node, newGraph);
        else
            System.err.println("Unhandled node type in visitGraph");
    }

    private void visitGraph(Location node, Graph newGraph) {
        var newLocation = new org.UcelPlugin.Models.SharedInterface.Location();

        newLocation.setPosX(node.getX());
        newLocation.setPosY(node.getY());
        newLocation.setName(node.getName());

        // Optional Properties
        if (node.getProperty(UppaalPropertyNames.Location.invariant) != null)
            newLocation.setInvariant(node.getPropertyValue(UppaalPropertyNames.Location.invariant));

        if (node.getProperty(UppaalPropertyNames.Location.rateOfExponential) != null)
            newLocation.setRateOfExponential(node.getPropertyValue(UppaalPropertyNames.Location.rateOfExponential));

        if (node.getProperty(UppaalPropertyNames.Location.init) != null)
            newLocation.setInitial(node.getPropertyValue(UppaalPropertyNames.Location.init));

        if (node.getProperty(UppaalPropertyNames.Location.urgent) != null)
            newLocation.setUrgent(node.getPropertyValue(UppaalPropertyNames.Location.urgent));

        if (node.getProperty(UppaalPropertyNames.Location.committed) != null)
            newLocation.setCommitted(node.getPropertyValue(UppaalPropertyNames.Location.committed));

        if (node.getProperty(UppaalPropertyNames.Location.comments) != null)
            newLocation.setComments(node.getPropertyValue(UppaalPropertyNames.Location.comments));

        if (node.getProperty(UppaalPropertyNames.Location.testCodeOnEnter) != null)
            newLocation.setTestCodeOnEnter(node.getPropertyValue(UppaalPropertyNames.Location.testCodeOnEnter));

        if (node.getProperty(UppaalPropertyNames.Location.testCodeOnExit) != null)
            newLocation.setTestCodeOnExit(node.getPropertyValue(UppaalPropertyNames.Location.testCodeOnExit));

        locationAssoc.put(node, newLocation);
        newGraph.addLocation(newLocation);

        visitGraph(node.next, newGraph);
    }

    private void visitGraph(Edge node, Graph newGraph) {
        var newEdge = new org.UcelPlugin.Models.SharedInterface.Edge();

        newEdge.setLocationStart(locationAssoc.get(node.getSource()));
        newEdge.setLocationEnd(locationAssoc.get(node.getTarget()));

        if (node.getProperty(UppaalPropertyNames.Edge.select) != null)
            newEdge.setSelect(node.getPropertyValue(UppaalPropertyNames.Edge.select));
        if (node.getProperty(UppaalPropertyNames.Edge.guard) != null)
            newEdge.setGuard(node.getPropertyValue(UppaalPropertyNames.Edge.guard));
        if (node.getProperty(UppaalPropertyNames.Edge.sync) != null)
            newEdge.setSync(node.getPropertyValue(UppaalPropertyNames.Edge.sync));
        if (node.getProperty(UppaalPropertyNames.Edge.update) != null)
            newEdge.setUpdate(node.getPropertyValue(UppaalPropertyNames.Edge.update));
        if (node.getProperty(UppaalPropertyNames.Edge.comment) != null)
            newEdge.setComment(node.getPropertyValue(UppaalPropertyNames.Edge.comment));
        if (node.getProperty(UppaalPropertyNames.Edge.testCode) != null)
            newEdge.setTestCode(node.getPropertyValue(UppaalPropertyNames.Edge.testCode));

        edgeAssoc.put(node, newEdge);
        newGraph.addEdge(newEdge);
        visitGraph(node.next, newGraph);
    }

    private Hashtable<com.uppaal.model.core2.Location, org.UcelPlugin.Models.SharedInterface.Location> locationAssoc = new Hashtable<>();
    private Hashtable<com.uppaal.model.core2.Edge, org.UcelPlugin.Models.SharedInterface.Edge> edgeAssoc = new Hashtable<>();

}
