import org.Ucel.*;
import org.Ucel.Exceptions.KeyNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTests {
    @Test
    public void ctor() {
        IGraph graph = new Graph();

        assertNotNull(graph.getLocations());
        assertNotNull(graph.getEdges());
    }

    @Test
    public void AddGetLocation() {
        Graph graph = new Graph();
        String id = "sfdkls";
        ILocation loc = makeLocation(id);

        graph.putLocation(loc);

        assertEquals(loc, graph.getLocation(id));
    }

    @Test
    public void AddGetEdgeSuccess() {
        Graph graph = new Graph();
        String startId = "slkjsgdf";
        String endId = "lkfhgkl";
        String edgeId = "dsfgdf";

        ILocation start = makeLocation(startId);
        ILocation end = makeLocation(endId);

        graph.putLocation(start);
        graph.putLocation(end);

        Edge edge = new Edge();
        edge.setId(edgeId);
        edge.setLocationIdStart(startId);
        edge.setLocationIdEnd(endId);

        graph.putEdge(edge);

        assertEquals(edge, graph.getEdge(edgeId));
    }

    @Test
    public void AddEdgeFail() {
        Graph graph = new Graph();
        String startId = "slkjsgdf";
        String endId = "lkfhgkl";
        String edgeId = "dsfgdf";

        Edge edge = new Edge();
        edge.setId(edgeId);
        edge.setLocationIdStart(startId);
        edge.setLocationIdEnd(endId);

        assertThrows(KeyNotFoundException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                graph.putEdge(edge);
            }
        });
    }

    private ILocation makeLocation(String id) {
        Location loc = new Location();
        loc.setId(id);

        return loc;
    }
}

