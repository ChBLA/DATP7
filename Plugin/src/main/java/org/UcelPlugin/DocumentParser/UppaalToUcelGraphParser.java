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
        if (node.getProperty("invariant") != null)
            newLocation.setInvariant(node.getPropertyValue("invariant"));

        if (node.getProperty("rateOfExponential") != null)
            newLocation.setRateOfExponential(node.getPropertyValue("rateOfExponential"));

        if (node.getProperty("init") != null)
            newLocation.setInitial(node.getPropertyValue("init"));

        if (node.getProperty("urgent") != null)
            newLocation.setUrgent(node.getPropertyValue("urgent"));

        if (node.getProperty("committed") != null)
            newLocation.setCommitted(node.getPropertyValue("committed"));

        if (node.getProperty("comments") != null)
            newLocation.setComments(node.getPropertyValue("comments"));

        if (node.getProperty("testCodeOnEnter") != null)
            newLocation.setTestCodeOnEnter(node.getPropertyValue("testCodeOnEnter"));

        if (node.getProperty("testCodeOnExit") != null)
            newLocation.setTestCodeOnExit(node.getPropertyValue("testCodeOnExit"));

        locationAssoc.put(node, newLocation);
        newGraph.addLocation(newLocation);

        visitGraph(node.next, newGraph);
    }

    private void visitGraph(Edge node, Graph newGraph) {
        var newEdge = new org.UcelPlugin.Models.SharedInterface.Edge();

        newEdge.setLocationStart(locationAssoc.get(node.getSource()));
        newEdge.setLocationEnd(locationAssoc.get(node.getTarget()));

        if (node.getProperty("select") != null)
            newEdge.setSelect(node.getPropertyValue("select"));
        if (node.getProperty("guard") != null)
            newEdge.setGuard(node.getPropertyValue("guard"));
        if (node.getProperty("sync") != null)
            newEdge.setSync(node.getPropertyValue("sync"));
        if (node.getProperty("update") != null)
            newEdge.setUpdate(node.getPropertyValue("update"));
        if (node.getProperty("comment") != null)
            newEdge.setComment(node.getPropertyValue("comment"));
        if (node.getProperty("testCode") != null)
            newEdge.setTestCode(node.getPropertyValue("testCode"));

        edgeAssoc.put(node, newEdge);
        newGraph.addEdge(newEdge);
        visitGraph(node.next, newGraph);
    }

    private Hashtable<com.uppaal.model.core2.Location, org.UcelPlugin.Models.SharedInterface.Location> locationAssoc = new Hashtable<>();
    private Hashtable<com.uppaal.model.core2.Edge, org.UcelPlugin.Models.SharedInterface.Edge> edgeAssoc = new Hashtable<>();

}
