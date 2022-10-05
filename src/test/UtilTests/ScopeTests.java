import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScopeTests {

    //region Scope.find()
    @Test
    void findSuccesfulSameScope() {
        String identifier = "identifier";

        DeclarationInfo testVar = new DeclarationInfo(identifier);
        int index = 0;
        ArrayList<DeclarationInfo> mockedVariables = new ArrayList<>();
        mockedVariables.add(testVar);

        Scope testScope = new Scope(null, false, mockedVariables);

        assertDoesNotThrow(() -> testScope.find(identifier, true));
    }
    //endregion

    //region Scope.get()
    @Test
    void getFromImmediateScope() {
        DeclarationInfo testVar = new DeclarationInfo("");
        int index = 0;
        ArrayList<DeclarationInfo> mockedVariables = new ArrayList<>();
        mockedVariables.add(testVar);

        Scope testScope = new Scope(null, false, mockedVariables);
        DeclarationReference tableRef = new DeclarationReference(0, index);
        try {
            assertDoesNotThrow(() -> testScope.get(tableRef));
            assertEquals(testVar, testScope.get(tableRef));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getFromDistantParentScope() {
        DeclarationInfo testVar = new DeclarationInfo("");
        int index = 0;
        ArrayList<DeclarationInfo> mockedVariables = mock(ArrayList.class);
        when(mockedVariables.get(index)).thenReturn(testVar);

        Scope testScope = new Scope(new Scope(new Scope(null, false, mockedVariables),false),false);
        DeclarationReference tableRef = new DeclarationReference(2, index);
        try {
            assertDoesNotThrow(() -> testScope.get(tableRef));
            assertEquals(testVar, testScope.get(tableRef));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getNoEntryAnyAccessibleScopesThrows() {
        Scope testScope = new Scope(null, false);
        DeclarationReference tableRef = new DeclarationReference(0, 0);

        assertThrows(Exception.class, () -> testScope.get(tableRef));
    }

    @Test
    void getNoParentScopeThrows() {
        Scope testScope = new Scope(null, false);
        DeclarationReference tableRef = new DeclarationReference(2, 0);

        assertThrows(Exception.class, () -> testScope.get(tableRef));
    }
    //endregion
}
