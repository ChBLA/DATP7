import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScopeTests {

    //region Scope.find()
    @Test
    void findSuccesfulSameScope() {

        fail();


    }

    @Test
    void invalidTableReferenceFromParentFindThrows() {
        fail();
    }

    @Test
    void validGetResultSuccessfulFind() {
        fail();
    }

    @Test
    void invalidGetResultFindThrows() {
        fail();
    }
    //endregion

    //region Scope.get()
    @Test
    void getFromImmediateScope() {
        Variable testVar = new Variable("");
        int index = 0;
        ArrayList<Variable> mockedVariables = new ArrayList<>();
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
        Variable testVar = new Variable("");
        int index = 0;
        ArrayList<Variable> mockedVariables = mock(ArrayList.class);
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
