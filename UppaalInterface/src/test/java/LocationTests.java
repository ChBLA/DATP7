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

        assertNotNull(location.GetId());
        assertNotNull(location.GetPosX());
        assertNotNull(location.GetPosY());
        assertNotNull(location.GetName());
        assertNotNull(location.GetInvariant());
        assertNotNull(location.GetRateOfExponential());
        assertNotNull(location.GetInitial());
        assertNotNull(location.GetUrgent());
        assertNotNull(location.GetCommitted());

        assertNotNull(location.GetComments());
        assertNotNull(location.GetTestCodeOnEnter());
        assertNotNull(location.GetTestCodeOnExit());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void Id(String value) {
        Location location = new Location();
        location.SetId(value);
        assertEquals(value, location.GetId());
    }

    @ParameterizedTest
    @ValueSource(ints = {34, 745})
    public void PosX(int value) {
        Location location = new Location();
        location.SetPosX(value);
        assertEquals(value, location.GetPosX());
    }

    @ParameterizedTest
    @ValueSource(ints = {34, 745})
    public void PosY(int value) {
        Location location = new Location();
        location.SetPosY(value);
        assertEquals(value, location.GetPosY());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void Name(String value) {
        Location location = new Location();
        location.SetName(value);
        assertEquals(value, location.GetName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void Invariant(String value) {
        Location location = new Location();
        location.SetInvariant(value);
        assertEquals(value, location.GetInvariant());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void RateOfExponential(String value) {
        Location location = new Location();
        location.SetRateOfExponential(value);
        assertEquals(value, location.GetRateOfExponential());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void Initial(boolean value) {
        Location location = new Location();
        location.SetInitial(value);
        assertEquals(value, location.GetInitial());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void Urgent(boolean value) {
        Location location = new Location();
        location.SetUrgent(value);
        assertEquals(value, location.GetUrgent());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void Committed(boolean value) {
        Location location = new Location();
        location.SetCommitted(value);
        assertEquals(value, location.GetCommitted());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void Comments(String value) {
        Location location = new Location();
        location.SetComments(value);
        assertEquals(value, location.GetComments());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void TestCodeOnEnter(String value) {
        Location location = new Location();
        location.SetTestCodeOnEnter(value);
        assertEquals(value, location.GetTestCodeOnEnter());
    }

    @ParameterizedTest
    @ValueSource(strings = {"glkdgklfjd", "lkjdgjlkdf"})
    public void TestCodeOnExit(String value) {
        Location location = new Location();
        location.SetTestCodeOnExit(value);
        assertEquals(value, location.GetTestCodeOnExit());
    }

}
