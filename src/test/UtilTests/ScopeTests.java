import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScopeTests {

    //region Scope.find()
    @Test
    void ValidTableReferenceFromParentSuccessfulFind() {
        fail();
    }

    @Test
    void InvalidTableReferenceFromParentFindThrows() {
        fail();
    }

    @Test
    void ValidGetResultSuccessfulFind() {
        fail();
    }

    @Test
    void InvalidGetResultFindThrows() {
        fail();
    }
    //endregion

    //region Scope.get()
    @Test
    void GetFromImmediateScope() {
        Variable testVar = new Variable();
        int index = 0;
        ArrayList<Variable> mockedVariables = mock(ArrayList.class);
        when(mockedVariables.get(index)).thenReturn(testVar);

        Scope testScope = new Scope(null, false, mockedVariables);
        TableReference tableRef = new TableReference(0, index);
        try {
            assertDoesNotThrow(() -> testScope.get(tableRef));
            assertEquals(testVar, testScope.get(tableRef));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void GetFromDistantParentScope() {
        Variable testVar = new Variable();
        int index = 0;
        ArrayList<Variable> mockedVariables = mock(ArrayList.class);
        when(mockedVariables.get(index)).thenReturn(testVar);

        Scope testScope = new Scope(new Scope(new Scope(null, false, mockedVariables),false),false);
        TableReference tableRef = new TableReference(2, index);
        try {
            assertDoesNotThrow(() -> testScope.get(tableRef));
            assertEquals(testVar, testScope.get(tableRef));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void GetNoEntryAnyAccessibleScopesThrows() {
        Scope testScope = new Scope(null, false);
        TableReference tableRef = new TableReference(0, 0);

        assertThrows(Exception.class, () -> testScope.get(tableRef));
    }

    @Test
    void GetNoParentScopeThrows() {
        Scope testScope = new Scope(null, false);
        TableReference tableRef = new TableReference(2, 0);

        assertThrows(Exception.class, () -> testScope.get(tableRef));
    }
    //endregion
}
