import org.Ucel.Edge;
import org.Ucel.IEdge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EdgeTests {
    @Test
    public void ctor() {
        IEdge edge = new Edge();

        assertNotNull(edge.getId());
        assertNotNull(edge.getLocationIdStart());
        assertNotNull(edge.getLocationIdEnd());
        assertNotNull(edge.getSelect());
        assertNotNull(edge.getGuard());
        assertNotNull(edge.getSync());
        assertNotNull(edge.getUpdate());
        assertNotNull(edge.getComment());
        assertNotNull(edge.getTestCode());
    }
    @Test
    public void ctorParameterized() {
        String id = "asd";
        String locationIdStart = "gdf";
        String locationIdEnd = "adgf";
        String select = "jhsgf";
        String guard = "jkhsgn";
        String sync = "<segczfb";
        String update = "jkgd";
        String comment = "kdgh";
        String testCode = "<aser";

        IEdge edge = new Edge(id, locationIdStart, locationIdEnd, select, guard, sync, update, comment, testCode);

        assertEquals(id, edge.getId());
        assertEquals(locationIdStart, edge.getLocationIdStart());
        assertEquals(locationIdEnd, edge.getLocationIdEnd());
        assertEquals(select, edge.getSelect());
        assertEquals(guard, edge.getGuard());
        assertEquals(sync, edge.getSync());
        assertEquals(update, edge.getUpdate());
        assertEquals(comment, edge.getComment());
        assertEquals(testCode, edge.getTestCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Id(String value) {
        Edge edge = new Edge();

        edge.setId(value);
        assertEquals(value, edge.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void LocationIdStart(String value) {
        Edge edge = new Edge();

        edge.setLocationIdStart(value);
        assertEquals(value, edge.getLocationIdStart());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void LocationIdEnd(String value) {
        Edge edge = new Edge();

        edge.setLocationIdEnd(value);
        assertEquals(value, edge.getLocationIdEnd());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Guard(String value) {
        Edge edge = new Edge();

        edge.setGuard(value);
        assertEquals(value, edge.getGuard());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Sync(String value) {
        Edge edge = new Edge();

        edge.setSync(value);
        assertEquals(value, edge.getSync());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Update(String value) {
        Edge edge = new Edge();

        edge.setUpdate(value);
        assertEquals(value, edge.getUpdate());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Comment(String value) {
        Edge edge = new Edge();

        edge.setComment(value);
        assertEquals(value, edge.getComment());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void TestCode(String value) {
        Edge edge = new Edge();

        edge.setTestCode(value);
        assertEquals(value, edge.getTestCode());
    }

}
