package ParserTests;

import org.Ucel.*;
import org.Ucel.IEdge;
import org.Ucel.ILocation;
import org.UcelParser.ManualParser.ErrorListener;
import org.UcelParser.ManualParser.ManualParser;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Logging.Logger;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class ManualParsingTests {

    //region Project
    @Test
    public void parseProjectTest() {
        var parser = mock(ManualParser.class);
        when(parser.parseProject(any())).thenCallRealMethod();

        var templateMock = mock(UCELParser.PtemplateContext.class);

        var projectMock = mock(IProject.class);

        when(projectMock.getDeclaration()).thenReturn("int i = 0;");
        when(projectMock.getSystemDeclarations()).thenReturn("int i = 0; system g;");
        when(projectMock.getTemplates()).thenReturn(List.of(mock(ITemplate.class)));

        var result = parser.parseProject(projectMock);

        assertNotNull(result);
        assertEquals(4, result.getChildCount());

    }
    //endregion

    //region Project declarations
    @Test
    void pDeclSetsParent() {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);
        var actual = parser.parseProjectDeclaration(parent, "int i = 0;");

        assertEquals(parent, actual.parent);
    }

    @Test
    void pDeclValidInput() {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);

        String input =
                "chan take, release;		// Take and release torch\n" +
                "int[0,1] L;		// The side the torch is on\n" +
                "clock time;		// Global time";

        var actual = parser.parseProjectDeclaration(parent, input);

        assertNotNull(actual);
    }

    @Test
    void pDeclParseViking() {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);

        String input =
                "/* * Four vikings are about to cross a damaged bridge in the middle of the\n" +
                        " * night. The bridge can only carry two of the vikings at the time and to\n" +
                        " * find the way over the bridge the vikings need to bring a torch.  The\n" +
                        " * vikings need 5, 10, 20 and 25 minutes (one-way) respectively to cross\n" +
                        " * the bridge.\n" +
                        "\n" +
                        " * Does a schedule exist which gets all four vikings over the bridge\n" +
                        " * within 60 minutes?\n" +
                        " */\n" +
                        "\n" +
                        "chan take, release;\t\t// Take and release torch\n" +
                        "int[0,1] L;\t\t// The side the torch is on\n" +
                        "clock time;\t\t// Global time";

        var actual = parser.parseProjectDeclaration(parent, input);

        assertNotNull(actual);
    }

    void pDeclParseTrain() {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);

        String input =
                "const int N = 6;         // # trains\n" +
                "typedef int[0,N-1] id_t;\n" +
                "\n" +
                "chan        appr[N], stop[N], leave[N];\n" +
                "urgent chan go[N];";

        var actual = parser.parseProjectDeclaration(parent, input);

        assertNotNull(actual);
    }

    //endregion

    //region Project template

    // ! This assumes that generateParser.parameters() and
    // ! generateParser.declarations() are working correctly
    @Test
    void projectTemplateCorrect() {
        var parser = mock(ManualParser.class);
        when(parser.parseTemplate(any(), any())).thenCallRealMethod();

        var parent = mock(UCELParser.ProjectContext.class);
        var templateMock = mock(ITemplate.class);
        var graphMock = mock(IGraph.class);
        var graphCTXMock = mock(UCELParser.GraphContext.class);

        when(templateMock.getGraph()).thenReturn(graphMock);
        when(templateMock.getName()).thenReturn("test");
        when(templateMock.getParameters()).thenReturn("int[0,5] a");
        when(templateMock.getDeclarations()).thenReturn("int[0,10] a = 0;");
        when(parser.parseGraph(any(), eq(graphMock))).thenReturn(graphCTXMock);

        var actual = parser.parseTemplate(parent, templateMock);

        assertEquals(parent, actual.parent);
        assertEquals("test", actual.ID().getText());
        assertEquals(4, actual.children.size());
        assertTrue(actual.children.get(1) instanceof UCELParser.ParametersContext);
        assertTrue(actual.children.get(2) instanceof UCELParser.GraphContext);
        assertTrue(actual.children.get(3) instanceof UCELParser.DeclarationsContext);
    }

    @Test
    void projectTemplateParamsNull() {
        var parser = mock(ManualParser.class);
        when(parser.parseTemplate(any(), any())).thenCallRealMethod();

        var parent = mock(UCELParser.ProjectContext.class);
        var templateMock = mock(ITemplate.class);
        var graphMock = mock(IGraph.class);
        var graphCTXMock = mock(UCELParser.GraphContext.class);

        when(templateMock.getGraph()).thenReturn(graphMock);
        when(templateMock.getName()).thenReturn("test");
        when(templateMock.getParameters()).thenReturn("(int[0,5)");
        when(templateMock.getDeclarations()).thenReturn("int[0,10] a = 0;");
        when(parser.parseGraph(any(), eq(graphMock))).thenReturn(graphCTXMock);

        var actual = parser.parseTemplate(parent, templateMock);

        assertNull(actual);
    }

    @Test
    void projectTemplateIDNull() {
        var parser = mock(ManualParser.class);
        when(parser.parseTemplate(any(), any())).thenCallRealMethod();

        var parent = mock(UCELParser.ProjectContext.class);
        var templateMock = mock(ITemplate.class);
        var graphMock = mock(IGraph.class);
        var graphCTXMock = mock(UCELParser.GraphContext.class);

        when(templateMock.getGraph()).thenReturn(graphMock);
        when(templateMock.getName()).thenReturn(null);
        when(templateMock.getParameters()).thenReturn("int[0,5] a");
        when(templateMock.getDeclarations()).thenReturn("int[0,10] a = 0;");
        when(parser.parseGraph(any(), eq(graphMock))).thenReturn(graphCTXMock);

        var actual = parser.parseTemplate(parent, templateMock);

        assertNull(actual);
    }

    @Test
    void projectTemplateIDEmpty() {
        var parser = mock(ManualParser.class);
        when(parser.parseTemplate(any(), any())).thenCallRealMethod();

        var parent = mock(UCELParser.ProjectContext.class);
        var templateMock = mock(ITemplate.class);
        var graphMock = mock(IGraph.class);
        var graphCTXMock = mock(UCELParser.GraphContext.class);

        when(templateMock.getGraph()).thenReturn(graphMock);
        when(templateMock.getName()).thenReturn("");
        when(templateMock.getParameters()).thenReturn("int[0,5] a");
        when(templateMock.getDeclarations()).thenReturn("int[0,10] a = 0;");
        when(parser.parseGraph(any(), eq(graphMock))).thenReturn(graphCTXMock);

        var actual = parser.parseTemplate(parent, templateMock);

        assertNull(actual);
    }

    @Test
    void projectTemplateDeclarationsNull() {
        var parser = mock(ManualParser.class);
        when(parser.parseTemplate(any(), any())).thenCallRealMethod();

        var parent = mock(UCELParser.ProjectContext.class);
        var templateMock = mock(ITemplate.class);
        var graphMock = mock(IGraph.class);
        var graphCTXMock = mock(UCELParser.GraphContext.class);

        when(templateMock.getGraph()).thenReturn(graphMock);
        when(templateMock.getName()).thenReturn("");
        when(templateMock.getParameters()).thenReturn("int[0,5] a");
        when(templateMock.getDeclarations()).thenReturn("int[0,10] a 0;");
        when(parser.parseGraph(any(), eq(graphMock))).thenReturn(graphCTXMock);

        var actual = parser.parseTemplate(parent, templateMock);

        assertNull(actual);
    }

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

        locationCtx1.id = 1;
        locationCtx2.id = 2;

        var locMap = new HashMap<ILocation, Integer>();
        locMap.put(location1, locationCtx1.id);
        locMap.put(location2, locationCtx2.id);

        when(parser.parseGraph(parent, graph)).thenCallRealMethod();
        when(parser.parseLocation(any(), eq(location1))).thenReturn(locationCtx1);
        when(parser.parseLocation(any(), eq(location2))).thenReturn(locationCtx2);
        when(parser.parseEdge(any(), eq(edge1), eq(locMap))).thenReturn(edgeCtx1);
        when(parser.parseEdge(any(), eq(edge2), eq(locMap))).thenReturn(edgeCtx2);

        UCELParser.GraphContext actual = parser.parseGraph(parent, graph);

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

        when(parser.parseGraph(parent, graph)).thenCallRealMethod();
        when(parser.parseLocation(any(), eq(location1))).thenReturn(locationCtx1);
        when(parser.parseLocation(any(), eq(location2))).thenReturn(locationCtx2);

        UCELParser.GraphContext actual = parser.parseGraph(parent, graph);

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

        locationCtx1.id = 1;
        locationCtx2.id = 2;

        var locMap = new HashMap<ILocation, Integer>();
        locMap.put(location1, locationCtx1.id);
        locMap.put(location2, locationCtx2.id);

        UCELParser.EdgeContext edgeCtx1 = mock(UCELParser.EdgeContext.class);

        when(parser.parseGraph(parent, graph)).thenCallRealMethod();
        when(parser.parseLocation(any(), eq(location1))).thenReturn(locationCtx1);
        when(parser.parseLocation(any(), eq(location2))).thenReturn(locationCtx2);
        when(parser.parseEdge(any(), eq(edge1), eq(locMap))).thenReturn(edgeCtx1);
        when(parser.parseEdge(any(), eq(edge2), eq(locMap))).thenReturn(null);

        UCELParser.GraphContext actual = parser.parseGraph(parent, graph);

        assertNull(actual);
    }


    //region Locations
    @Test
    void makeLocationFromValidILocation() {
        var iLoc = mock(ILocation.class);
        var parser = mock(ManualParser.class);
        var parent = mock(ParserRuleContext.class);

        when(iLoc.getInitial()).thenReturn(true);
        when(iLoc.getUrgent()).thenReturn(false);
        when(iLoc.getCommitted()).thenReturn(false);
        when(iLoc.getPosX()).thenReturn(1);
        when(iLoc.getPosY()).thenReturn(2);
        when(iLoc.getComments()).thenReturn("comments");
        when(iLoc.getTestCodeOnEnter()).thenReturn("enter");
        when(iLoc.getTestCodeOnExit()).thenReturn("exit");

        var invariant = "2 > 1";
        var exponential = "2:1";
        var invMock = mock(UCELParser.InvariantContext.class);
        var expMock = mock(UCELParser.ExponentialContext.class);

        when(iLoc.getName()).thenReturn("Test");
        when(iLoc.getInvariant()).thenReturn(invariant);
        when(iLoc.getRateOfExponential()).thenReturn(exponential);

        when(parser.parseLocation(parent, iLoc)).thenCallRealMethod();
        when(parser.parseExponential(any(), eq(exponential))).thenReturn(expMock);
        when(parser.parseInvariant(any(), eq(invariant))).thenReturn(invMock);

        var actual = parser.parseLocation(parent, iLoc);

        assertTrue(actual instanceof UCELParser.LocationContext);
        var actualCasted = (UCELParser.LocationContext)actual;
        assertEquals(invMock, actualCasted.invariant());
        assertEquals(expMock, actualCasted.exponential());
        assertEquals("Test", actualCasted.ID().getText());
        assertTrue(actualCasted.isInitial);
        assertFalse(actualCasted.isUrgent);
        assertFalse(actualCasted.isCommitted);
        assertEquals(1, actualCasted.posX);
        assertEquals(2, actualCasted.posY);
        assertEquals("comments", actualCasted.comments);
        assertEquals("enter", actualCasted.testCodeEnter);
        assertEquals("exit", actualCasted.testCodeExit);
    }

    @Test
    void makeLocationFromInvalidID() {
        var iLoc = mock(ILocation.class);
        var parser = mock(ManualParser.class);
        var parent = mock(ParserRuleContext.class);

        when(iLoc.getInitial()).thenReturn(true);
        when(iLoc.getUrgent()).thenReturn(false);
        when(iLoc.getCommitted()).thenReturn(false);
        when(iLoc.getPosX()).thenReturn(1);
        when(iLoc.getPosY()).thenReturn(2);
        when(iLoc.getComments()).thenReturn("comments");
        when(iLoc.getTestCodeOnEnter()).thenReturn("enter");
        when(iLoc.getTestCodeOnExit()).thenReturn("exit");

        var invariant = "2 > 1";
        var exponential = "2:1";
        var invMock = mock(UCELParser.InvariantContext.class);
        var expMock = mock(UCELParser.ExponentialContext.class);

        when(iLoc.getName()).thenReturn("");
        when(iLoc.getInvariant()).thenReturn(invariant);
        when(iLoc.getRateOfExponential()).thenReturn(exponential);

        when(parser.parseLocation(parent, iLoc)).thenCallRealMethod();
        when(parser.parseExponential(any(), eq(exponential))).thenReturn(expMock);
        when(parser.parseInvariant(any(), eq(invariant))).thenReturn(invMock);

        var actual = parser.parseLocation(parent, iLoc);

        assertTrue(actual instanceof UCELParser.LocationContext);
        var actualCasted = (UCELParser.LocationContext)actual;
        assertEquals(invMock, actualCasted.invariant());
        assertEquals(expMock, actualCasted.exponential());
        assertEquals("", actualCasted.ID().getText());
        assertTrue(actualCasted.isInitial);
        assertFalse(actualCasted.isUrgent);
        assertFalse(actualCasted.isCommitted);
        assertEquals(1, actualCasted.posX);
        assertEquals(2, actualCasted.posY);
        assertEquals("comments", actualCasted.comments);
        assertEquals("enter", actualCasted.testCodeEnter);
        assertEquals("exit", actualCasted.testCodeExit);
    }
    @Test
    void makeLocationFromInvalidInvariantFails() {
        var iLoc = mock(ILocation.class);
        var parser = mock(ManualParser.class);
        var parent = mock(ParserRuleContext.class);

        when(iLoc.getInitial()).thenReturn(true);
        when(iLoc.getUrgent()).thenReturn(false);
        when(iLoc.getCommitted()).thenReturn(false);
        when(iLoc.getPosX()).thenReturn(1);
        when(iLoc.getPosY()).thenReturn(2);
        when(iLoc.getComments()).thenReturn("comments");
        when(iLoc.getTestCodeOnEnter()).thenReturn("enter");
        when(iLoc.getTestCodeOnExit()).thenReturn("exit");

        var invariant = "2 > 1";
        var exponential = "2:1";
        var invMock = mock(UCELParser.InvariantContext.class);
        var expMock = mock(UCELParser.ExponentialContext.class);

        when(iLoc.getName()).thenReturn("Test");
        when(iLoc.getInvariant()).thenReturn(null);
        when(iLoc.getRateOfExponential()).thenReturn(exponential);

        when(parser.parseLocation(parent, iLoc)).thenCallRealMethod();
        when(parser.parseExponential(any(), eq(exponential))).thenReturn(expMock);
        when(parser.parseInvariant(any(), eq(invariant))).thenReturn(invMock);

        var actual = parser.parseLocation(parent, iLoc);
        assertNull(actual);
    }
    @Test
    void makeLocationFromInvalidExponentialFails() {
        var iLoc = mock(ILocation.class);
        var parser = mock(ManualParser.class);
        var parent = mock(ParserRuleContext.class);

        when(iLoc.getInitial()).thenReturn(true);
        when(iLoc.getUrgent()).thenReturn(false);
        when(iLoc.getCommitted()).thenReturn(false);
        when(iLoc.getPosX()).thenReturn(1);
        when(iLoc.getPosY()).thenReturn(2);
        when(iLoc.getComments()).thenReturn("comments");
        when(iLoc.getTestCodeOnEnter()).thenReturn("enter");
        when(iLoc.getTestCodeOnExit()).thenReturn("exit");

        var invariant = "2 > 1";
        var exponential = "2:1";
        var invMock = mock(UCELParser.InvariantContext.class);
        var expMock = mock(UCELParser.ExponentialContext.class);

        when(iLoc.getName()).thenReturn("Test");
        when(iLoc.getInvariant()).thenReturn(invariant);
        when(iLoc.getRateOfExponential()).thenReturn(null);

        when(parser.parseLocation(parent, iLoc)).thenCallRealMethod();
        when(parser.parseExponential(any(), eq(exponential))).thenReturn(expMock);
        when(parser.parseInvariant(any(), eq(invariant))).thenReturn(invMock);

        var actual = parser.parseLocation(parent, iLoc);
        assertNull(actual);
    }

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

        var locMap = new HashMap<ILocation, Integer>();
        locMap.put(locationStart, 1);
        locMap.put(locationEnd, 2);

        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge, locMap)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(selectMock);
        when(parser.parseGuard(any(), any())).thenReturn(guardMock);
        when(parser.parseSync(any(), any())).thenReturn(syncMock);
        when(parser.parseUpdate(any(), any())).thenReturn(updateMock);

        var actual = parser.parseEdge(parent, edge, locMap);

        assertEquals(parent, actual.parent);
        assertNotNull(actual);
        assertEquals(4, actual.getChildCount());
        assertEquals(1, actual.locationStartID);
        assertEquals(2, actual.locationEndID);
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

        var locMap = new HashMap<ILocation, Integer>();
        locMap.put(locationStart, 1);
        locMap.put(locationEnd, 2);

        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge, locMap)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(null);
        when(parser.parseGuard(any(), any())).thenReturn(guardMock);
        when(parser.parseSync(any(), any())).thenReturn(syncMock);
        when(parser.parseUpdate(any(), any())).thenReturn(updateMock);

        var actual = parser.parseEdge(parent, edge, locMap);

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

        var locMap = new HashMap<ILocation, Integer>();
        locMap.put(locationStart, 1);
        locMap.put(locationEnd, 2);

        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge, locMap)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(selectMock);
        when(parser.parseGuard(any(), any())).thenReturn(null);
        when(parser.parseSync(any(), any())).thenReturn(syncMock);
        when(parser.parseUpdate(any(), any())).thenReturn(updateMock);

        var actual = parser.parseEdge(parent, edge, locMap);

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

        var locMap = new HashMap<ILocation, Integer>();
        locMap.put(locationStart, 1);
        locMap.put(locationEnd, 2);

        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge, locMap)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(selectMock);
        when(parser.parseGuard(any(), any())).thenReturn(guardMock);
        when(parser.parseSync(any(), any())).thenReturn(null);
        when(parser.parseUpdate(any(), any())).thenReturn(updateMock);

        var actual = parser.parseEdge(parent, edge, locMap);

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

        var locMap = new HashMap<ILocation, Integer>();
        locMap.put(locationStart, 1);
        locMap.put(locationEnd, 2);

        when(edge.getComment()).thenReturn("comment");
        when(edge.getTestCode()).thenReturn("testCode");

        var parser = mock(ManualParser.class);
        when(parser.parseEdge(parent, edge, locMap)).thenCallRealMethod();

        when(parser.parseSelect(any(), any())).thenReturn(selectMock);
        when(parser.parseGuard(any(), any())).thenReturn(guardMock);
        when(parser.parseSync(any(), any())).thenReturn(syncMock);
        when(parser.parseUpdate(any(), any())).thenReturn(null);

        var actual = parser.parseEdge(parent, edge, locMap);

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
        var actual = parser.parseProjectSystem(parent, "int one = 1; system s;");

        assertEquals(parent, actual.parent);
    }

    @ParameterizedTest
    @ValueSource(strings = {"int one = 0; system s;", "typedef int[0,N-1] id_t; system s;"})
    void pSysReturnsCorrectlyOnValidInput(String input) {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);
        var actual = parser.parseProjectSystem(parent, input);

        assertNotNull(actual);
    }

    @Test
    void pSysParseViking() {
        var parser = new ManualParser();
        var parent = mock(UCELParser.ProjectContext.class);
        String input =
                "const int fastest = 5;\n" +
                "const int fast = 10;\n" +
                "const int slow = 20;\n" +
                "const int slowest = 25;\n" +
                "\n" +
                "Viking1 = Soldier(fastest);\n" +
                "Viking2 = Soldier(fast);\n" +
                "Viking3 = Soldier(slow);\n" +
                "Viking4 = Soldier(slowest);\n" +
                "\n" +
                "system Viking1, Viking2, Viking3, Viking4, Torch;";

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
