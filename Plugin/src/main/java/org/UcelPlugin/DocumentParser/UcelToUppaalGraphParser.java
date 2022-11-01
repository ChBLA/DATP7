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

        loc.setProperty(UppaalPropertyNames.Location.posX, ucelLocation.getPosX());
        loc.setProperty(UppaalPropertyNames.Location.posY, ucelLocation.getPosY());
        loc.setProperty(UppaalPropertyNames.Location.name, ucelLocation.getName());

        loc.setProperty(UppaalPropertyNames.Location.rateOfExponential, ucelLocation.getRateOfExponential());
        loc.setProperty(UppaalPropertyNames.Location.init, ucelLocation.getInitial());
        loc.setProperty(UppaalPropertyNames.Location.urgent, ucelLocation.getUrgent());
        loc.setProperty(UppaalPropertyNames.Location.committed, ucelLocation.getCommitted());
        loc.setProperty(UppaalPropertyNames.Location.comments, ucelLocation.getComments());
        loc.setProperty(UppaalPropertyNames.Location.testCodeOnEnter, ucelLocation.getTestCodeOnEnter());
        loc.setProperty(UppaalPropertyNames.Location.testCodeOnExit, ucelLocation.getTestCodeOnExit());

        locationAssoc.put(ucelLocation, loc);
    }

    private void addEdge(IEdge ucelEdge) {
        var edge = template.createEdge();
        template.insert(edge, null);

        edge.setSource(locationAssoc.get(ucelEdge.getLocationStart()));
        edge.setTarget(locationAssoc.get(ucelEdge.getLocationEnd()));

        edge.setProperty(UppaalPropertyNames.Edge.select, ucelEdge.getSelect());
        edge.setProperty(UppaalPropertyNames.Edge.guard, ucelEdge.getGuard());
        edge.setProperty(UppaalPropertyNames.Edge.sync, ucelEdge.getSync());
        edge.setProperty(UppaalPropertyNames.Edge.update, ucelEdge.getUpdate());
        edge.setProperty(UppaalPropertyNames.Edge.comment, ucelEdge.getComment());
        edge.setProperty(UppaalPropertyNames.Edge.testCode, ucelEdge.getTestCode());
    }


    private Hashtable<ILocation, Location> locationAssoc = new Hashtable<>();
}
