package org.UcelPlugin.DocumentParser;

import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Template;
import org.Ucel.IEdge;
import org.Ucel.IGraph;
import org.Ucel.ILocation;

import java.util.Hashtable;
import java.util.List;

public class UcelToUppaalGraphParser {

    public UcelToUppaalGraphParser(Template template, IGraph inputGraph) {
        this.template = template;
        this.inputGraph = inputGraph;
    }

    private Template template;
    public Template getTemplate() {
        return template;
    }
    private IGraph inputGraph;
    public IGraph getInputGraph() {
        return inputGraph;
    }

    public void addGraph() {
        addLocations(inputGraph.getLocations());
        addEdges(inputGraph.getEdges());
    }

    private void addLocations(List<ILocation> ucelLocations) {
        for(var loc: ucelLocations)
            addLocation(loc);
    }

    private void addEdges(List<IEdge> ucelEdges) {
        for(var edge: ucelEdges)
            addEdge(edge);
    }

    private void addLocation(ILocation ucelLocation) {
        var loc = template.createLocation();
        template.insert(loc, null);

        loc.setProperty("x", ucelLocation.getPosX());
        loc.setProperty("y", ucelLocation.getPosY());
        loc.setProperty("name", ucelLocation.getName());

        loc.setProperty("rateOfExponential", ucelLocation.getRateOfExponential());
        loc.setProperty("init", ucelLocation.getInitial());
        loc.setProperty("urgent", ucelLocation.getUrgent());
        loc.setProperty("committed", ucelLocation.getCommitted());
        loc.setProperty("comments", ucelLocation.getComments());
        loc.setProperty("testCodeOnEnter", ucelLocation.getTestCodeOnEnter());
        loc.setProperty("testCodeOnExit", ucelLocation.getTestCodeOnExit());

        locationAssoc.put(ucelLocation, loc);
    }

    private void addEdge(IEdge ucelEdge) {
        var edge = template.createEdge();
        template.insert(edge, null);

        edge.setSource(locationAssoc.get(ucelEdge.getLocationStart()));
        edge.setTarget(locationAssoc.get(ucelEdge.getLocationEnd()));

        edge.setProperty("select", ucelEdge.getSelect());
        edge.setProperty("guard", ucelEdge.getGuard());
        edge.setProperty("sync", ucelEdge.getSync());
        edge.setProperty("update", ucelEdge.getUpdate());
        edge.setProperty("comment", ucelEdge.getComment());
        edge.setProperty("testCode", ucelEdge.getTestCode());
    }


    private Hashtable<ILocation, Location> locationAssoc = new Hashtable<>();
}
