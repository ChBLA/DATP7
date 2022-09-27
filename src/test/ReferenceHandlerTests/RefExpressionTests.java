import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class RefExpressionTests {

    //region IDExpr: look in immediate scope, look through multiple scope, fail to find
    @Test
    void IDExprSuccessfulLookupInImmediateScope() {
        fail();
    }

    @Test
    void IDExprSuccessfulLookupInDistantScope() {
        fail();
    }

    @Test
    void IDExprUnsuccessfulLookupThrows() {
        fail();
    }
    //endregion

    //region LiteralExpr: No influence by scopes
    @Test
    void NullScopeValidLiteralExpr() {
        fail();
    }

    @Test
    void NotNullScopeValidLiteralExpr() {
        fail();
    }
    //endregion


}
