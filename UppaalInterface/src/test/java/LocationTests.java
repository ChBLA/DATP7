import org.Ucel.ILocation;
import org.Ucel.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LocationTests {
    @Test
    public void ctor() {
        ILocation location = new Location();

        assertNotNull(location.getId());
        assertNotNull(location.getPosX());
        assertNotNull(location.getPosY());
        assertNotNull(location.getName());
        assertNotNull(location.getInvariant());
        assertNotNull(location.getRateOfExponential());
        assertNotNull(location.getInitial());
        assertNotNull(location.getUrgent());
        assertNotNull(location.getCommitted());

        assertNotNull(location.getComments());
        assertNotNull(location.getTestCodeOnEnter());
        assertNotNull(location.getTestCodeOnExit());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void Id(String value) {
        Location location = new Location();
        location.setId(value);
        assertEquals(value, location.getId());
    }

    @ParameterizedTest
    @ValueSource(ints = {34, 745})
    public void PosX(int value) {
        Location location = new Location();
        location.setPosX(value);
        assertEquals(value, location.getPosX());
    }

    @ParameterizedTest
    @ValueSource(ints = {34, 745})
    public void PosY(int value) {
        Location location = new Location();
        location.setPosY(value);
        assertEquals(value, location.getPosY());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void Name(String value) {
        Location location = new Location();
        location.setName(value);
        assertEquals(value, location.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void Invariant(String value) {
        Location location = new Location();
        location.setInvariant(value);
        assertEquals(value, location.getInvariant());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void RateOfExponential(String value) {
        Location location = new Location();
        location.setRateOfExponential(value);
        assertEquals(value, location.getRateOfExponential());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void Initial(boolean value) {
        Location location = new Location();
        location.setInitial(value);
        assertEquals(value, location.getInitial());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void Urgent(boolean value) {
        Location location = new Location();
        location.setUrgent(value);
        assertEquals(value, location.getUrgent());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void Committed(boolean value) {
        Location location = new Location();
        location.setCommitted(value);
        assertEquals(value, location.getCommitted());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void Comments(String value) {
        Location location = new Location();
        location.setComments(value);
        assertEquals(value, location.getComments());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void TestCodeOnEnter(String value) {
        Location location = new Location();
        location.setTestCodeOnEnter(value);
        assertEquals(value, location.getTestCodeOnEnter());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void TestCodeOnExit(String value) {
        Location location = new Location();
        location.setTestCodeOnExit(value);
        assertEquals(value, location.getTestCodeOnExit());
    }

}
