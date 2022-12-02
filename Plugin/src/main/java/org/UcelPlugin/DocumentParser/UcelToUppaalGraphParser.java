package org.UcelPlugin.DocumentParser;

import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Node;
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

        var propX = ucelLocation.getPosX();
        var propY = ucelLocation.getPosY();

        setValues(loc, propX, propY, new Hashtable<>() {{
            put(UppaalPropertyNames.Location.posX, ucelLocation.getPosX());
            put(UppaalPropertyNames.Location.posY, ucelLocation.getPosY());
            put(UppaalPropertyNames.Location.name, ucelLocation.getName());

            put(UppaalPropertyNames.Location.invariant, ucelLocation.getInvariant());
            put(UppaalPropertyNames.Location.rateOfExponential, ucelLocation.getRateOfExponential());
            put(UppaalPropertyNames.Location.init, ucelLocation.getInitial());
            put(UppaalPropertyNames.Location.urgent, ucelLocation.getUrgent());
            put(UppaalPropertyNames.Location.committed, ucelLocation.getCommitted());
            put(UppaalPropertyNames.Location.comments, ucelLocation.getComments());
            put(UppaalPropertyNames.Location.testCodeOnEnter, ucelLocation.getTestCodeOnEnter());
            put(UppaalPropertyNames.Location.testCodeOnExit, ucelLocation.getTestCodeOnExit());
        }});

        locationAssoc.put(ucelLocation, loc);
    }

    private void addEdge(IEdge ucelEdge) {
        var edge = template.createEdge();
        template.insert(edge, null);

        edge.setSource(locationAssoc.get(ucelEdge.getLocationStart()));
        edge.setTarget(locationAssoc.get(ucelEdge.getLocationEnd()));

        var propX = (ucelEdge.getLocationStart().getPosX()+ucelEdge.getLocationEnd().getPosX())/2;
        var propY = (ucelEdge.getLocationStart().getPosY()+ucelEdge.getLocationEnd().getPosY())/2;

        setValues(edge, propX, propY, new Hashtable<>() {{
            put(UppaalPropertyNames.Edge.select, ucelEdge.getSelect());
            put(UppaalPropertyNames.Edge.guard, ucelEdge.getGuard());
            put(UppaalPropertyNames.Edge.sync, ucelEdge.getSync());
            put(UppaalPropertyNames.Edge.update, ucelEdge.getUpdate());
            put(UppaalPropertyNames.Edge.comment, ucelEdge.getComment());
            put(UppaalPropertyNames.Edge.testCode, ucelEdge.getTestCode());
        }});
    }



    private void setValues(Node node, int baseX, int baseY, Hashtable<String, Object> values) {
        int x = baseX;
        int y = baseY + 11;

        for(var val: values.entrySet()) {
            if(val.getValue() == null || val.getValue() == "")
                continue;

            setValueWithPosition(node, val.getKey(), val.getValue(), x, y);
            y += 17;
        }
    }

    private void setValueWithPosition(Node node, String valueId, Object value, int x, int y) {
        var prop = node.setProperty(valueId, value);
        prop.setProperty("x", x);
        prop.setProperty("y", y);
    }

    private Hashtable<ILocation, Location> locationAssoc = new Hashtable<>();
}
