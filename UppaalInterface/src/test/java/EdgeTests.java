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

        assertNotNull(edge.GetId());
        assertNotNull(edge.GetLocationIdStart());
        assertNotNull(edge.GetLocationIdEnd());
        assertNotNull(edge.GetSelect());
        assertNotNull(edge.GetGuard());
        assertNotNull(edge.GetSync());
        assertNotNull(edge.GetUpdate());
        assertNotNull(edge.GetComment());
        assertNotNull(edge.GetTestCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Id(String value) {
        Edge edge = new Edge();

        edge.SetId(value);
        assertEquals(value, edge.GetId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void LocationIdStart(String value) {
        Edge edge = new Edge();

        edge.SetLocationIdStart(value);
        assertEquals(value, edge.GetLocationIdStart());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void LocationIdEnd(String value) {
        Edge edge = new Edge();

        edge.SetLocationIdEnd(value);
        assertEquals(value, edge.GetLocationIdEnd());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Guard(String value) {
        Edge edge = new Edge();

        edge.SetGuard(value);
        assertEquals(value, edge.GetGuard());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Sync(String value) {
        Edge edge = new Edge();

        edge.SetSync(value);
        assertEquals(value, edge.GetSync());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Update(String value) {
        Edge edge = new Edge();

        edge.SetUpdate(value);
        assertEquals(value, edge.GetUpdate());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void Comment(String value) {
        Edge edge = new Edge();

        edge.SetComment(value);
        assertEquals(value, edge.GetComment());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sdfgsfdghhsf", "hsdfghfdg"})
    public void TestCode(String value) {
        Edge edge = new Edge();

        edge.SetTestCode(value);
        assertEquals(value, edge.GetTestCode());
    }

}
