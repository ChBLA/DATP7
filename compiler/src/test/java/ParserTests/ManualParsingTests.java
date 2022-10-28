package ParserTests;

import org.Ucel.ILocation;
import org.UcelParser.ManualParser.ManualParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ManualParsingTests {

    //region Project


    //region Project declarations


    //endregion

    //region Project template

    //region Graph


    //region Locations

    //region Invariant

    //endregion

    //region Exponential
    @Test
    void makeExponentialNodeFromValidExpressions() {
        var input = "2:1";
        var parent = mock(ParserRuleContext.class);

        var parser = new ManualParser();
        var actual = parser.parseExponential(parent, input);

        assertEquals(parent, actual.parent);
    }

    @Test
    void makeExponentialNodeFromInvalidExpressionsReturnsNull() {
        var expr = "for (i = 0;;)";
        var parent = mock(ParserRuleContext.class);

        var parser = new ManualParser();
        var actual = parser.parseExponential(parent, expr);

        assertNull(actual);
    }
    //endregion
    //endregion

    //region Edges

    //region Select

    //endregion

    //region Guard

    //endregion

    //region Sync

    //endregion

    //region Update

    //endregion
    //endregion
    //endregion
    //endregion

    //region Project system

    //endregion
    //endregion
}
