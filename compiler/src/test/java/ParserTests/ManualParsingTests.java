package ParserTests;

import org.Ucel.*;
import org.Ucel.IEdge;
import org.Ucel.ILocation;
import org.UcelParser.ManualParser.ManualParser;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    public void parseEdgeReturnsCorrectEdgeCtx() {
        var parent = mock(ParserRuleContext.class);
        var edge = mock(IEdge.class);
        var locationStart = mock(ILocation.class);
        var locationEnd = mock(ILocation.class);

        var selectMock = mock(UCELParser.SelectContext.class);
        var guardMock = mock(UCELParser.GuardContext.class);
        var syncMock = mock(UCELParser.SyncContext.class);
        var updateMock = mock(UCELParser.UpdateContext.class);


        when(edge.getLocationStart()).thenReturn(locationStart);
        when(edge.getLocationEnd()).thenReturn(locationEnd);
        when(locationStart.getName()).thenReturn("1");
        when(locationEnd.getName()).thenReturn("2");
        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(selectMock);
        when(parser.parseGuard(any(), any())).thenReturn(guardMock);
        when(parser.parseSync(any(), any())).thenReturn(syncMock);
        when(parser.parseUpdate(any(), any())).thenReturn(updateMock);

        var actual = parser.parseEdge(parent, edge);

        assertEquals(parent, actual.parent);
        assertNotNull(actual);
        assertEquals(4, actual.getChildCount());
        assertEquals(actual.locationStartID, Integer.parseInt(locationStart.getName()));
        assertEquals(actual.locationEndID, Integer.parseInt(locationEnd.getName()));
        assertEquals(actual.comments, edge.getComment());
        assertEquals(actual.testCode, edge.getTestCode());
    }

    @Test
    public void parseEdgeReturnsNullWhenInvalidSelect() {
        var parent = mock(ParserRuleContext.class);
        var edge = mock(IEdge.class);
        var locationStart = mock(ILocation.class);
        var locationEnd = mock(ILocation.class);

        var guardMock = mock(UCELParser.GuardContext.class);
        var syncMock = mock(UCELParser.SyncContext.class);
        var updateMock = mock(UCELParser.UpdateContext.class);

        when(edge.getLocationStart()).thenReturn(locationStart);
        when(edge.getLocationEnd()).thenReturn(locationEnd);
        when(locationStart.getName()).thenReturn("1");
        when(locationEnd.getName()).thenReturn("2");
        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(null);
        when(parser.parseGuard(any(), any())).thenReturn(guardMock);
        when(parser.parseSync(any(), any())).thenReturn(syncMock);
        when(parser.parseUpdate(any(), any())).thenReturn(updateMock);

        var actual = parser.parseEdge(parent, edge);

        assertNull(actual);
    }

    @Test
    public void parseEdgeReturnsNullWhenInvalidGuard() {
        var parent = mock(ParserRuleContext.class);
        var edge = mock(IEdge.class);
        var locationStart = mock(ILocation.class);
        var locationEnd = mock(ILocation.class);

        var selectMock = mock(UCELParser.SelectContext.class);
        var syncMock = mock(UCELParser.SyncContext.class);
        var updateMock = mock(UCELParser.UpdateContext.class);

        when(edge.getLocationStart()).thenReturn(locationStart);
        when(edge.getLocationEnd()).thenReturn(locationEnd);
        when(locationStart.getName()).thenReturn("1");
        when(locationEnd.getName()).thenReturn("2");
        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(selectMock);
        when(parser.parseGuard(any(), any())).thenReturn(null);
        when(parser.parseSync(any(), any())).thenReturn(syncMock);
        when(parser.parseUpdate(any(), any())).thenReturn(updateMock);

        var actual = parser.parseEdge(parent, edge);

        assertNull(actual);
    }

    @Test
    public void parseEdgeReturnsNullWhenInvalidSync() {
        var parent = mock(ParserRuleContext.class);
        var edge = mock(IEdge.class);
        var locationStart = mock(ILocation.class);
        var locationEnd = mock(ILocation.class);

        var selectMock = mock(UCELParser.SelectContext.class);
        var guardMock = mock(UCELParser.GuardContext.class);
        var updateMock = mock(UCELParser.UpdateContext.class);

        when(edge.getLocationStart()).thenReturn(locationStart);
        when(edge.getLocationEnd()).thenReturn(locationEnd);
        when(locationStart.getName()).thenReturn("1");
        when(locationEnd.getName()).thenReturn("2");
        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(selectMock);
        when(parser.parseGuard(any(), any())).thenReturn(guardMock);
        when(parser.parseSync(any(), any())).thenReturn(null);
        when(parser.parseUpdate(any(), any())).thenReturn(updateMock);

        var actual = parser.parseEdge(parent, edge);

        assertNull(actual);
    }

    @Test
    public void parseEdgeReturnsNullWhenInvalidUpdate() {
        var parent = mock(ParserRuleContext.class);
        var edge = mock(IEdge.class);
        var locationStart = mock(ILocation.class);
        var locationEnd = mock(ILocation.class);

        var selectMock = mock(UCELParser.SelectContext.class);
        var guardMock = mock(UCELParser.GuardContext.class);
        var syncMock = mock(UCELParser.SyncContext.class);
        var updateMock = mock(UCELParser.UpdateContext.class);

        when(edge.getLocationStart()).thenReturn(locationStart);
        when(edge.getLocationEnd()).thenReturn(locationEnd);
        when(locationStart.getName()).thenReturn("1");
        when(locationEnd.getName()).thenReturn("2");
        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(selectMock);
        when(parser.parseGuard(any(), any())).thenReturn(guardMock);
        when(parser.parseSync(any(), any())).thenReturn(syncMock);
        when(parser.parseUpdate(any(), any())).thenReturn(null);

        var actual = parser.parseEdge(parent, edge);

        assertNull(actual);
    }

    //region Select
    @Test
    public void parseSelectNotNullTest() {
        String parseString = "a : int[0,10]";

        var parentMock = mock(UCELParser.EdgeContext.class);

        var parser = new ManualParser();

        var actual = parser.parseSelect(parentMock, parseString);

        assertNotNull(actual);
        assertEquals(parentMock, actual.parent);
    }

    @Test
    public void parseSelectNullTest() {
        String parseString = "a : int[0,10] this does not work";
        var parser = new ManualParser();
        var actual = parser.parseSelect(null, parseString);
        assertNull(actual);
    }

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

    //Generate test on psys returning correctly on valid input
    @ParameterizedTest
    @ValueSource(strings = {"system s;", "system s1;", "system s2;", "system s3;", "system s4;", "system s5;"})
    void pSysReturnsCorrectlyOnValidInput(String input) {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);
        var actual = parser.parseProjectSystem(parent, input);

        assertNotNull(actual);
    }

    @Test
    void pSysReturnsNullOnNoEOF() {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);
        var actual = parser.parseProjectSystem(parent, "system s");

        assertTrue(null == actual);
    }
    @Test
    void pSysReturnsNullOnNoSystem() {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);
        var actual = parser.parseProjectSystem(parent, "int i = 1;");

        assertTrue(null == actual);
    }
    //endregion
    //endregion
}
