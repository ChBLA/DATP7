package ParserTests;

import org.Ucel.ILocation;
import org.UcelParser.ManualParser.ManualParser;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.UcelParser.ManualParser.ManualParser;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class ManualParsingTests {

    //region Project


    //region Project declarations
    @Test
    void pDeclSetsParent() {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);
        var actual = parser.parseProjectDeclaration(parent, "int i = 0;");

        assertEquals(parent, actual.parent);
    }

    //endregion

    //region Project template

    //region Graph


    //region Locations

    //region Invariant
    @Test
    void makeInvariantNodeFromValidExpression() {
        var expr = "2 > 1";
        var parent = mock(ParserRuleContext.class);

        var parser = new ManualParser();
        var actual = parser.parseInvariant(parent, expr);

        assertEquals(parent, actual.parent);
    }

    @Test
    void makeInvariantNodeFromInvalidExpressionReturnsNull() {
        var expr = "for (i = 0;;)";
        var parent = mock(ParserRuleContext.class);

        var parser = new ManualParser();
        var actual = parser.parseInvariant(parent, expr);

        assertNull(actual);
    }
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

    @Test
    public void parseGuardNotNullTest() {
        String parseString = "a < 5";

        var parentMock = mock(UCELParser.EdgeContext.class);

        var parser = new ManualParser();

        var actual = parser.parseGuard(parentMock, parseString);

        assertNotNull(actual);
        assertEquals(parentMock, actual.parent);
    }

    @Test
    public void parseGuardNullTest() {
        String parseString = "a!";
        var parser = new ManualParser();

        var actual = parser.parseGuard(null, parseString);

        assertNull(actual);
    }

    //endregion

    //region Sync
    @Test
    public void parseSyncNotNullTest() {
        String parseString = "1!";

        var parentMock = mock(UCELParser.EdgeContext.class);

        var parser = new ManualParser();

        var actual = parser.parseSync(parentMock, parseString);

        assertNotNull(actual);
        assertEquals(parentMock, actual.parent);
    }

    @Test
    public void parseSyncNullTest() {
        String parseString = "!";
        var parser = new ManualParser();

        var actual = parser.parseSync(null, parseString);

        assertNull(actual);
    }
    //endregion

    //region Update
    @Test
    void updateSetsParent() {
        ManualParser manualParser = new ManualParser();
        ParserRuleContext parent = mock(ParserRuleContext.class);

        ParserRuleContext actual = manualParser.parseUpdate(parent, "");

        assertEquals(parent, actual.getParent());
    }

    @Test
    void updateReturnsRightExpressions() {
        ManualParser manualParser = new ManualParser();
        ParserRuleContext parent = mock(ParserRuleContext.class);

        ParserRuleContext actual = manualParser.parseUpdate(parent, "a+b, !x");

        assertTrue(actual.getChild(0) instanceof UCELParser.AddSubContext);
        assertTrue(actual.getChild(2) instanceof UCELParser.UnaryExprContext);
    }

    @Test
    void updateReturnsCorrectlyWithNoChildren() {
        ManualParser manualParser = new ManualParser();
        ParserRuleContext parent = mock(ParserRuleContext.class);

        ParserRuleContext actual = manualParser.parseUpdate(parent, "");

        assertTrue(actual.getChildCount() == 0);
    }

    @Test
    void updateReturnsNullOnError() {
        ManualParser manualParser = new ManualParser();
        ParserRuleContext parent = mock(ParserRuleContext.class);

        ParserRuleContext actual = manualParser.parseUpdate(parent, ";;,;");

        assertTrue(actual == null);
    }

    //endregion
    //endregion
    //endregion
    //endregion

    //region Project system
    @Test
    void pSysSetsParent() {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);
        var actual = parser.parseProjectSystem(parent, "system s;");

        assertEquals(parent, actual.parent);
    }
    //endregion
    //endregion
}
