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

        ILocation loc = new Location();

        graph.addLocation(loc);

        assertEquals(loc, graph.getLocations().get(0));
    }

    @Test
    public void AddGetEdgeSuccess() {
        Graph graph = new Graph();

        ILocation start = new Location();
        ILocation end = new Location();

        graph.addLocation(start);
        graph.addLocation(end);

        Edge edge = new Edge();
        edge.setLocationStart(start);
        edge.setLocationEnd(end);

        graph.AddEdge(edge);

        assertEquals(edge, graph.getEdges().get(0));
    }

    @Test
    public void AddEdgeFail() {
        Graph graph = new Graph();

        Edge edge = new Edge();
        edge.setLocationStart(new Location());
        edge.setLocationEnd(new Location());

        assertThrows(IllegalArgumentException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                graph.AddEdge(edge);
            }
        });
    }
}

