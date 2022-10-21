import org.Ucel.Edge;
import org.Ucel.IEdge;
import org.Ucel.ILocation;
import org.Ucel.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EdgeTests {
    @Test
    public void ctor() {
        IEdge edge = new Edge();

        assertNotNull(edge.getSelect());
        assertNotNull(edge.getGuard());
        assertNotNull(edge.getSync());
        assertNotNull(edge.getUpdate());
        assertNotNull(edge.getComment());
        assertNotNull(edge.getTestCode());
    }

    @Test
    public void ctorParameterized() {
        ILocation locationStart = new Location();
        ILocation locationEnd = new Location();
        String select = "jhsgf";
        String guard = "jkhsgn";
        String sync = "<segczfb";
        String update = "jkgd";
        String comment = "kdgh";
        String testCode = "<aser";

        IEdge edge = new Edge(locationStart, locationEnd, select, guard, sync, update, comment, testCode);

        assertEquals(locationStart, edge.getLocationStart());
        assertEquals(locationEnd, edge.getLocationEnd());
        assertEquals(select, edge.getSelect());
        assertEquals(guard, edge.getGuard());
        assertEquals(sync, edge.getSync());
        assertEquals(update, edge.getUpdate());
        assertEquals(comment, edge.getComment());
        assertEquals(testCode, edge.getTestCode());
    }

    @Test
    public void LocationStart() {
        Edge edge = new Edge();

        ILocation value = new Location();

        edge.setLocationStart(value);
        assertEquals(value, edge.getLocationStart());
    }

    @Test
    public void LocationEnd() {
        Edge edge = new Edge();

        ILocation value = new Location();

        edge.setLocationEnd(value);
        assertEquals(value, edge.getLocationEnd());
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
