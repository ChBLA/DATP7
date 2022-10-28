package ParserTests;

import org.Ucel.*;
import org.UcelParser.ManualParser.ManualParser;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    @Test
    void graphSuccessfullyBuild() {
        ManualParser parser = mock(ManualParser.class);
        ParserRuleContext parent = mock(ParserRuleContext.class);
        IGraph graph = mock(IGraph.class);
        ILocation location1 = mock(ILocation.class);
        ILocation location2 = mock(ILocation.class);

        IEdge edge1 = mock(IEdge.class);
        IEdge edge2 = mock(IEdge.class);

        List<IEdge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        List<ILocation> locations = new ArrayList<>();
        locations.add(location1);
        locations.add(location2);
        when(graph.getLocations()).thenReturn(locations);
        when(graph.getEdges()).thenReturn(edges);

        UCELParser.LocationContext locationCtx1 = mock(UCELParser.LocationContext.class);
        UCELParser.LocationContext locationCtx2 = mock(UCELParser.LocationContext.class);

        UCELParser.EdgeContext edgeCtx1 = mock(UCELParser.EdgeContext.class);
        UCELParser.EdgeContext edgeCtx2 = mock(UCELParser.EdgeContext.class);

        when(parser.parserGraph(parent, graph)).thenCallRealMethod();
        when(parser.parseLocation(any(), eq(location1))).thenReturn(locationCtx1);
        when(parser.parseLocation(any(), eq(location2))).thenReturn(locationCtx2);
        when(parser.parseEdge(any(), eq(edge1))).thenReturn(edgeCtx1);
        when(parser.parseEdge(any(), eq(edge2))).thenReturn(edgeCtx2);

        UCELParser.GraphContext actual = parser.parserGraph(parent, graph);

        assertEquals(locationCtx1, actual.getChild(0));
        assertEquals(locationCtx2, actual.getChild(1));
        assertEquals(edgeCtx1, actual.getChild(2));
        assertEquals(edgeCtx2, actual.getChild(3));
    }

    @Test
    void graphSuccessfullyBuildNoEdges() {
        ManualParser parser = mock(ManualParser.class);
        ParserRuleContext parent = mock(ParserRuleContext.class);
        IGraph graph = mock(IGraph.class);
        ILocation location1 = mock(ILocation.class);
        ILocation location2 = mock(ILocation.class);

        List<IEdge> edges = new ArrayList<>();
        List<ILocation> locations = new ArrayList<>();
        locations.add(location1);
        locations.add(location2);
        when(graph.getLocations()).thenReturn(locations);
        when(graph.getEdges()).thenReturn(edges);

        UCELParser.LocationContext locationCtx1 = mock(UCELParser.LocationContext.class);
        UCELParser.LocationContext locationCtx2 = mock(UCELParser.LocationContext.class);

        when(parser.parserGraph(parent, graph)).thenCallRealMethod();
        when(parser.parseLocation(any(), eq(location1))).thenReturn(locationCtx1);
        when(parser.parseLocation(any(), eq(location2))).thenReturn(locationCtx2);

        UCELParser.GraphContext actual = parser.parserGraph(parent, graph);

        assertEquals(locationCtx1, actual.getChild(0));
        assertEquals(locationCtx2, actual.getChild(1));
        assertEquals(2, actual.getChildCount());
    }

    @Test
    void graphReturnNullOnNullEdge() {
        ManualParser parser = mock(ManualParser.class);
        ParserRuleContext parent = mock(ParserRuleContext.class);
        IGraph graph = mock(IGraph.class);
        ILocation location1 = mock(ILocation.class);
        ILocation location2 = mock(ILocation.class);

        IEdge edge1 = mock(IEdge.class);
        IEdge edge2 = mock(IEdge.class);

        List<IEdge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        List<ILocation> locations = new ArrayList<>();
        locations.add(location1);
        locations.add(location2);
        when(graph.getLocations()).thenReturn(locations);
        when(graph.getEdges()).thenReturn(edges);

        UCELParser.LocationContext locationCtx1 = mock(UCELParser.LocationContext.class);
        UCELParser.LocationContext locationCtx2 = mock(UCELParser.LocationContext.class);

        UCELParser.EdgeContext edgeCtx1 = mock(UCELParser.EdgeContext.class);

        when(parser.parserGraph(parent, graph)).thenCallRealMethod();
        when(parser.parseLocation(any(), eq(location1))).thenReturn(locationCtx1);
        when(parser.parseLocation(any(), eq(location2))).thenReturn(locationCtx2);
        when(parser.parseEdge(any(), eq(edge1))).thenReturn(edgeCtx1);
        when(parser.parseEdge(any(), eq(edge2))).thenReturn(null);

        UCELParser.GraphContext actual = parser.parserGraph(parent, graph);

        assertEquals(null, actual);
    }


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
