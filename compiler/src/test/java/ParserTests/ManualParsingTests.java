package ParserTests;

import org.UcelParser.ManualParser.ManualParser;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    //endregion
    //endregion

    //region Edges

    //region Select

    //endregion

    //region Guard

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

    //endregion
    //endregion
    //endregion
    //endregion

    //region Project system

    //endregion
    //endregion
}
