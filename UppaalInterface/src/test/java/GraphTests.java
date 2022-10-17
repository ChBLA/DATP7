import org.Ucel.*;
import org.Ucel.Exceptions.KeyNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTests {
    @Test
    public void ctor() {
        IGraph graph = new Graph();

        assertNotNull(graph.GetLocations());
        assertNotNull(graph.GetEdges());
    }

    @Test
    public void AddGetLocation() {
        Graph graph = new Graph();
        String id = "sfdkls";
        ILocation loc = makeLocation(id);

        graph.AddLocation(loc);

        assertEquals(loc, graph.GetLocation(id));
    }

    @Test
    public void AddGetEdgeSuccess() {
        Graph graph = new Graph();
        String startId = "slkjsgdf";
        String endId = "lkfhgkl";
        String edgeId = "dsfgdf";

        ILocation start = makeLocation(startId);
        ILocation end = makeLocation(endId);

        graph.AddLocation(start);
        graph.AddLocation(end);

        Edge edge = new Edge();
        edge.SetId(edgeId);
        edge.SetLocationIdStart(startId);
        edge.SetLocationIdEnd(endId);

        graph.AddEdge(edge);

        assertEquals(edge, graph.GetEdge(edgeId));
    }


    @Test
    public void AddEdgeFail() {
        Graph graph = new Graph();
        String startId = "slkjsgdf";
        String endId = "lkfhgkl";
        String edgeId = "dsfgdf";

        Edge edge = new Edge();
        edge.SetId(edgeId);
        edge.SetLocationIdStart(startId);
        edge.SetLocationIdEnd(endId);

        assertThrows(KeyNotFoundException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                graph.AddEdge(edge);
            }
        });
    }

    private ILocation makeLocation(String id) {
        Location loc = new Location();
        loc.SetId(id);

        return loc;
    }
}

