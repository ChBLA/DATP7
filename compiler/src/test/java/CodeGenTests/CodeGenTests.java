package CodeGenTests;

import org.UcelParser.CodeGeneration.CodeGenVisitor;
import org.UcelParser.CodeGeneration.templates.*;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.*;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CodeGenTests {

    //region UCEL

    //region Component
    @Test
    void componentNoOccurrencesGeneratesNoCode() {
        var expected = "";
        var visitor = new CodeGenVisitor();

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.ComponentContext.class);
        node.scope = scopeMock;
        when(node.parameters()).thenThrow(new RuntimeException());
        when(node.interfaces()).thenThrow(new RuntimeException());
        when(node.compBody()).thenThrow(new RuntimeException());

        var actual = visitor.visitComponent(node);

        assertEquals(expected, actual.toString());
    }


    @Test
    void componentOneOccurrenceNoNestedComponentsGeneratedCorrectly() {
        var expected = String.format("// Generation for component: Component1%nint Component1_aa_p1 = z;%nint Component1_aa_q = Component1_aa_p1 + 1;%n");
        var paramName = "p1";
        var actualParamName = "z";

        var parameterTemplate = new ManualTemplate("int Component1_aa_p1");
        var interfaceTemplate = new ManualTemplate("");
        var compBodyTemplate = new ManualTemplate("int Component1_aa_q = Component1_aa_p1 + 1;");

        var visitor = new CodeGenVisitor();

        var scopeCompNode = mock(Scope.class);

        var node = mock(UCELParser.ComponentContext.class);
        var parametersNode = mock(UCELParser.ParametersContext.class);
        var parameterNode = mockForVisitorResult(UCELParser.ParameterContext.class, parameterTemplate, visitor);
        var interfaceNode = mockForVisitorResult(UCELParser.InterfacesContext.class, interfaceTemplate, visitor);
        var compBodyNode = mockForVisitorResult(UCELParser.CompBodyContext.class, compBodyTemplate, visitor);

        var constructorCallNode = mock(UCELParser.CompConContext.class);
        var parameterNodes = new ArrayList<UCELParser.ParameterContext>() {{ add(parameterNode); }};
        when(node.parameters()).thenReturn(parametersNode);
        when(parametersNode.parameter()).thenReturn(parameterNodes);
        when(node.interfaces()).thenReturn(interfaceNode);
        when(node.compBody()).thenReturn(compBodyNode);

        DeclarationInfo[] parameters = new DeclarationInfo[1];
        DeclarationInfo[] interfaces = new DeclarationInfo[1];
        var parameterInfo = mock(DeclarationInfo.class);
        var interfaceInfo = mock(DeclarationInfo.class);

        when(parameterInfo.generateName(anyString())).thenReturn(actualParamName);

        parameters[0] = parameterInfo;
        interfaces[0] = interfaceInfo;
        var occurrence = new ComponentOccurrence(node, "Component1", parameters, interfaces, null);

        var parameterRef = mock(DeclarationReference.class);
        parameterNode.reference = parameterRef;

        when(parameterInfo.getType()).thenReturn(new Type(Type.TypeEnum.intType));

        try {
            when(scopeCompNode.get(parameterRef)).thenReturn(parameterInfo);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        node.occurrences = new ArrayList<>() {{ add(occurrence); }};
        node.scope = scopeCompNode;

        var actual = visitor.visitComponent(node);

        assertEquals(expected, actual.toString());
    }


    @Test
    void componentMultipleOccurrencesNoNestedComponentsGeneratedCorrectly() {
        var expected = String.format("// Generation for component: Component1%n" +
                "int Component1_aa_p1 = z;%n" +
                "int Component1_aa_q = Component1_aa_p1 + 1;%n" +
                "%n" +
                "// Generation for component: Component2%n" +
                "int Component2_ab_p1 = x;%n" +
                "int Component2_ab_q = Component2_ab_p1 + 1;%n");
        var paramName = "p1";
        var actualParamName1 = "z";
        var actualParamName2 = "x";

        var parameterTemplate1 = new ManualTemplate("int Component1_aa_p1");
        var interfaceTemplate1 = new ManualTemplate("");
        var compBodyTemplate1 = new ManualTemplate("int Component1_aa_q = Component1_aa_p1 + 1;");

        var parameterTemplate2 = new ManualTemplate("int Component2_ab_p1");
        var interfaceTemplate2 = new ManualTemplate("");
        var compBodyTemplate2 = new ManualTemplate("int Component2_ab_q = Component2_ab_p1 + 1;");

        var visitor = new CodeGenVisitor();

        var scopeCompNode = mock(Scope.class);

        var node = mock(UCELParser.ComponentContext.class);
        var parametersNode1 = mock(UCELParser.ParametersContext.class);
        var parameterNode1 = mockForVisitorResult(UCELParser.ParameterContext.class, parameterTemplate1, visitor);
        var interfaceNode1 = mockForVisitorResult(UCELParser.InterfacesContext.class, interfaceTemplate1, visitor);
        var compBodyNode1 = mockForVisitorResult(UCELParser.CompBodyContext.class, compBodyTemplate1, visitor);

        var parametersNode2 = mock(UCELParser.ParametersContext.class);
        var parameterNode2 = mockForVisitorResult(UCELParser.ParameterContext.class, parameterTemplate2, visitor);
        var interfaceNode2 = mockForVisitorResult(UCELParser.InterfacesContext.class, interfaceTemplate2, visitor);
        var compBodyNode2 = mockForVisitorResult(UCELParser.CompBodyContext.class, compBodyTemplate2, visitor);

        var constructorCallNode = mock(UCELParser.CompConContext.class);
        var parameterNodes1 = new ArrayList<UCELParser.ParameterContext>() {{ add(parameterNode1); }};
        when(parametersNode1.parameter()).thenReturn(parameterNodes1);

        var parameterNodes2 = new ArrayList<UCELParser.ParameterContext>() {{ add(parameterNode2); }};
        when(parametersNode2.parameter()).thenReturn(parameterNodes2);

        when(node.parameters()).thenReturn(parametersNode1, parametersNode1, parametersNode1, parametersNode2);
        when(node.interfaces()).thenReturn(interfaceNode1, interfaceNode2);
        when(node.compBody()).thenReturn(compBodyNode1, compBodyNode2);

        DeclarationInfo[] parameters1 = new DeclarationInfo[1];
        DeclarationInfo[] interfaces1 = new DeclarationInfo[1];
        var parameterInfo1 = mock(DeclarationInfo.class);
        var interfaceInfo1 = mock(DeclarationInfo.class);
        when(parameterInfo1.generateName(anyString())).thenReturn(actualParamName1);
        parameters1[0] = parameterInfo1;
        interfaces1[0] = interfaceInfo1;

        DeclarationInfo[] parameters2 = new DeclarationInfo[1];
        DeclarationInfo[] interfaces2 = new DeclarationInfo[1];
        var parameterInfo2 = mock(DeclarationInfo.class);
        var interfaceInfo2 = mock(DeclarationInfo.class);
        when(parameterInfo2.generateName(anyString())).thenReturn(actualParamName2);
        parameters2[0] = parameterInfo2;
        interfaces2[0] = interfaceInfo2;

        var parameterRef1 = mock(DeclarationReference.class);
        var parameterRef2 = mock(DeclarationReference.class);
        parameterNode1.reference = parameterRef1;
        parameterNode2.reference = parameterRef2;

        when(parameterInfo1.getType()).thenReturn(new Type(Type.TypeEnum.intType));
        when(parameterInfo2.getType()).thenReturn(new Type(Type.TypeEnum.intType));

        try {
            when(scopeCompNode.get(parameterRef1)).thenReturn(parameterInfo1);
            when(scopeCompNode.get(parameterRef2)).thenReturn(parameterInfo2);
        } catch (Exception e) {
            fail(e.getMessage());
        }


        var occurrence1 = new ComponentOccurrence(node, "Component1", parameters1, interfaces1, null);
        var occurrence2 = new ComponentOccurrence(node, "Component2", parameters2, interfaces2, null);
        node.occurrences = new ArrayList<>() {{ add(occurrence1); add(occurrence2); }};
        node.scope = scopeCompNode;

        var actual = visitor.visitComponent(node);

        assertEquals(expected, actual.toString());
    }

    //endregion

    //region Component body
    @Test
    void compBodyNoDeclarationsNoBuildGeneratesNothing() {
        var expected = "";

        var node = mock(UCELParser.CompBodyContext.class);
        node.scope = mock(Scope.class);

        var visitor = new CodeGenVisitor();

        var actual = visitor.visitCompBody(node);

        assertEquals(expected, actual.toString());
    }

    @Test
    void compBodyWithDeclarationsNoBuildGeneratedCorrectly() {
        var declTemplate = generateDefaultDeclarationTemplate("a", "10");

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.CompBodyContext.class);
        var declNode = mockForVisitorResult(UCELParser.DeclarationsContext.class, declTemplate, visitor);
        var scope = mock(Scope.class);

        when(node.declarations()).thenReturn(declNode);
        node.scope = scope;

        var actual = visitor.visitCompBody(node);

        assertEquals(declTemplate.toString(), actual.toString());
    }

    @Test
    void compBodyEnsureBuildIsNeverVisited() {
        var scope = mock(Scope.class);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.CompBodyContext.class);
        var buildNode = mock(UCELParser.BuildContext.class);

        node.scope = scope;
        when(node.build()).thenReturn(buildNode);
        when(buildNode.accept(visitor)).thenThrow(new RuntimeException());

        assertDoesNotThrow(() -> visitor.visitCompBody(node));
    }
    
    //endregion

    //region Update

    @Test
    void updateOneExpr() {
        String expected = "x = 0";

        var visitor = new CodeGenVisitor();

        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class,
                new ManualTemplate(expected), visitor);

        List<UCELParser.ExpressionContext> exprs = new ArrayList<>();
        exprs.add(exprMock);

        var node = mock(UCELParser.UpdateContext.class);
        when(node.expression()).thenReturn(exprs);

        var actual = visitor.visitUpdate(node).toString();
        assertEquals(expected, actual);
    }
    @Test
    void updateMultipleExpr() {
        String expected = "x = 0, y = 1";

        var visitor = new CodeGenVisitor();

        var exprMock1 = mockForVisitorResult(UCELParser.ExpressionContext.class,
                new ManualTemplate("x = 0"), visitor);

        var exprMock2 = mockForVisitorResult(UCELParser.ExpressionContext.class,
                new ManualTemplate("y = 1"), visitor);

        List<UCELParser.ExpressionContext> exprs = new ArrayList<>();
        exprs.add(exprMock1);
        exprs.add(exprMock2);

        var node = mock(UCELParser.UpdateContext.class);
        when(node.expression()).thenReturn(exprs);

        var actual = visitor.visitUpdate(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void updateNoExpr() {
        String expected = "";

        var visitor = new CodeGenVisitor();

        List<UCELParser.ExpressionContext> exprs = new ArrayList<>();

        var node = mock(UCELParser.UpdateContext.class);
        when(node.expression()).thenReturn(exprs);

        var actual = visitor.visitUpdate(node).toString();
        assertEquals(expected, actual);
    }


    //endregion

    //region sync

    @Test
    void syncQuestionMarkCorrect() {
        var expected = "a[1]?";
        var visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class,
                new ManualTemplate("a[1]"), visitor);

        var terminalNodeMock = mock(TerminalNode.class);
        when(terminalNodeMock.getText()).thenReturn("?");

        var node = mock(UCELParser.SyncContext.class);
        when(node.expression()).thenReturn(expr);
        when(node.QUESTIONMARK()).thenReturn(terminalNodeMock);

        var actual = visitor.visitSync(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void syncNegCorrect() {
        var expected = "a[1]!";
        var visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class,
                new ManualTemplate("a[1]"), visitor);

        var terminalNodeMock = mock(TerminalNode.class);
        when(terminalNodeMock.getText()).thenReturn("!");

        var node = mock(UCELParser.SyncContext.class);
        when(node.expression()).thenReturn(expr);
        when(node.NEG()).thenReturn(terminalNodeMock);

        var actual = visitor.visitSync(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void syncNoExpr() {
        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.SyncContext.class);

        var actual = visitor.visitSync(node);
        assertEquals("", actual.toString());
    }

    //endregion

    //region Select

    @Test
    void selectCorrectOne() {
        var scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var expected = generateDefaultSelectTemplateSingle();

        var typeTemp = new ManualTemplate("int");
        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeTemp, visitor);

        var IDMock = mock(TerminalNodeImpl.class);
        when(IDMock.getText()).thenReturn("a");

        var refMock = mock(DeclarationReference.class);
        var declInfoMock = mock(DeclarationInfo.class);
        when(declInfoMock.getIdentifier()).thenReturn("b");

        try {
            when(scopeMock.get(refMock)).thenReturn(declInfoMock);
        } catch (Exception e) {
            fail("unable to mock scope");
        }

        List<UCELParser.TypeContext> typeContexts = new ArrayList<>();
        typeContexts.add(typeMock);

        List<TerminalNode> IDs = new ArrayList<>();
        IDs.add(IDMock);

        List<DeclarationReference> references = new ArrayList<>();
        references.add(refMock);

        var node = mock(UCELParser.SelectContext.class);
        when(node.type()).thenReturn(typeContexts);
        when(node.ID()).thenReturn(IDs);
        node.references = references;

        var actual = visitor.visitSelect(node).toString();
        assertEquals(expected.toString(), actual);
    }

    @Test
    void selectCorrectMultiple() {
        var scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var expected = generateDefaultSelectTemplateMultiple();

        var typeTemp = new ManualTemplate("int");
        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeTemp, visitor);

        var IDMock1 = mock(TerminalNodeImpl.class);
        when(IDMock1.getText()).thenReturn("a");

        var IDMock2 = mock(TerminalNodeImpl.class);
        when(IDMock2.getText()).thenReturn("b");

        var refMock1 = mock(DeclarationReference.class);
        var declInfoMock1 = mock(DeclarationInfo.class);
        when(declInfoMock1.getIdentifier()).thenReturn("c");

        var refMock2 = mock(DeclarationReference.class);
        var declInfoMock2 = mock(DeclarationInfo.class);
        when(declInfoMock2.getIdentifier()).thenReturn("d");

        try {
            when(scopeMock.get(refMock1)).thenReturn(declInfoMock1);
            when(scopeMock.get(refMock2)).thenReturn(declInfoMock2);
        } catch (Exception e) {
            fail("unable to mock scope");
        }


        List<UCELParser.TypeContext> typeContexts = new ArrayList<>();
        typeContexts.add(typeMock);
        typeContexts.add(typeMock);

        List<TerminalNode> IDs = new ArrayList<>();
        IDs.add(IDMock1);
        IDs.add(IDMock2);

        List<DeclarationReference> references = new ArrayList<>();
        references.add(refMock1);
        references.add(refMock2);

        var node = mock(UCELParser.SelectContext.class);
        when(node.type()).thenReturn(typeContexts);
        when(node.ID()).thenReturn(IDs);
        node.references = references;

        var actual = visitor.visitSelect(node).toString();
        assertEquals(expected.toString(), actual);
    }



    //endregion

    //region Edge
    @Test
    void edgeCorrect() {
        var visitor = new CodeGenVisitor();

        var scopeMock = mock(Scope.class);

        var select = new ManualTemplate("select");
        var guard = new ManualTemplate("guard");
        var sync = new ManualTemplate("sync");
        var update = new ManualTemplate("update");

        var selectMock = mockForVisitorResult(UCELParser.SelectContext.class, select, visitor);
        var guardMock = mockForVisitorResult(UCELParser.GuardContext.class, guard, visitor);
        var syncMock = mockForVisitorResult(UCELParser.SyncContext.class, sync, visitor);
        var updateMock = mockForVisitorResult(UCELParser.UpdateContext.class, update, visitor);

        var edgeMock = mock(UCELParser.EdgeContext.class);
        edgeMock.scope = scopeMock;
        when(edgeMock.select()).thenReturn(selectMock);
        when(edgeMock.guard()).thenReturn(guardMock);
        when(edgeMock.sync()).thenReturn(syncMock);
        when(edgeMock.update()).thenReturn(updateMock);

        var actual = visitor.visitEdge(edgeMock);
        assertInstanceOf(EdgeTemplate.class, actual);
        var actualCasted = (EdgeTemplate) actual;
        assertEquals(actualCasted.select, select);
        assertEquals(actualCasted.guard, guard);
        assertEquals(actualCasted.sync, sync);
        assertEquals(actualCasted.update, update);
        assertEquals(actualCasted.edge, edgeMock);
    }
    //endregion

    //region invariant
    @Test
    void invariantCorrect() {
        var visitor = new CodeGenVisitor();

        var exprTemp = generateDefaultInvariantTemplate();
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemp, visitor);

        var node = mock(UCELParser.InvariantContext.class);
        when(node.expression()).thenReturn(exprMock);

        var actual = visitor.visitInvariant(node);
        assertEquals(exprTemp.toString(), actual.toString());
    }
    //endregion

    //region exponential
    @Test
    void exponentialCorrect() {
        var visitor = new CodeGenVisitor();

        var expected = "1 : 2";

        var expr1Temp = new ManualTemplate("1");
        var expr1Mock = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Temp, visitor);

        var expr2Temp = new ManualTemplate("2");
        var expr2Mock = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Temp, visitor);


        List<UCELParser.ExpressionContext> exprs = new ArrayList<>();
        exprs.add(expr1Mock);
        exprs.add(expr2Mock);

        var node = mock(UCELParser.ExponentialContext.class);
        when(node.expression()).thenReturn(exprs);
        when(node.expression(0)).thenReturn(expr1Mock);
        when(node.expression(1)).thenReturn(expr2Mock);

        var actual = visitor.visitExponential(node).toString();
        assertEquals(expected, actual);
    }
    //endregion

    //region Location
    @Test
    void locationGetsCorrectNodes() {
        var visitor = new CodeGenVisitor();

        var invariantTemplate = generateDefaultInvariantTemplate();
        var exponentialTemplate = generateDefaultExponentialTemplate();

        var invariantMock = mockForVisitorResult(UCELParser.InvariantContext.class, invariantTemplate, visitor);
        var exponentialMock = mockForVisitorResult(UCELParser.ExponentialContext.class, exponentialTemplate, visitor);

        var locationID = "loc1";

        var node = mock(UCELParser.LocationContext.class);
        var idNode = mock(TerminalNode.class);

        when(idNode.getText()).thenReturn(locationID);
        when(node.ID()).thenReturn(idNode);
        when(node.invariant()).thenReturn(invariantMock);
        when(node.exponential()).thenReturn(exponentialMock);

        var actual = visitor.visitLocation(node);
        assertInstanceOf(LocationTemplate.class, actual);

        var actualCasted = (LocationTemplate) actual;
        assertEquals(exponentialTemplate.toString(), actualCasted.exponential.toString());
        assertEquals(invariantTemplate.toString(), actualCasted.invariant.toString());
        assertEquals(node, actualCasted.location);
        assertEquals(locationID, actualCasted.ID);
    }

    //endregion

    //region Graph
    @Test
    void graphGetsCorrectLocationAndEdge() {
         var visitor = new CodeGenVisitor();

         var node = mock(UCELParser.GraphContext.class);

         var edgeTemplate = new ManualTemplate("edge");
         var locationTemplate = new ManualTemplate("location");

         var edgeNodeMock = mockForVisitorResult(UCELParser.EdgeContext.class, edgeTemplate, visitor);
         var locationNodeMock = mockForVisitorResult(UCELParser.LocationContext.class, locationTemplate, visitor);

         List<UCELParser.EdgeContext> edgesList = new ArrayList<>();
         edgesList.add(edgeNodeMock);
         List<UCELParser.LocationContext> locationsList = new ArrayList<>();
         locationsList.add(locationNodeMock);

         when(node.edge()).thenReturn(edgesList);
         when(node.location()).thenReturn(locationsList);

         var actual = visitor.visitGraph(node);

         assertInstanceOf(GraphTemplate.class, actual);
         var actualCasted = (GraphTemplate) actual;
         assertEquals(1, actualCasted.edges.size());
         assertEquals(1, actualCasted.nodes.size());
         assertEquals(edgeTemplate, actualCasted.edges.get(0));
         assertEquals(locationTemplate, actualCasted.nodes.get(0));
         assertEquals("location", actualCasted.nodes.get(0).template.render());
         assertEquals("edge", actualCasted.edges.get(0).template.render());
    }
    //endregion

    //region Project structure

    //region Project
    @Test
    void projectNoPTemplatesGeneratedCorrectly() {
        var pDeclTemplate = generateDefaultPDeclTemplate();
        var pSystemTemplate = generateDefaultPSystemTemplate();

        var visitor = new CodeGenVisitor();
        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.ProjectContext.class);
        var systemNode = mockForVisitorResult(UCELParser.PsystemContext.class, pSystemTemplate, visitor);
        var declNode = mockForVisitorResult(UCELParser.PdeclarationContext.class, pDeclTemplate, visitor);
        var verificationNode = mock(UCELParser.VerificationListContext.class);

        node.scope = scopeMock;
        when(node.psystem()).thenReturn(systemNode);
        when(node.pdeclaration()).thenReturn(declNode);
        when(node.verificationList()).thenReturn(verificationNode);

        var actual = visitor.visitProject(node);
        assertInstanceOf(ProjectTemplate.class, actual);
        var actualCasted = (ProjectTemplate) actual;
        assertEquals(pDeclTemplate, actualCasted.pDeclarationTemplate);
        assertEquals(pSystemTemplate, actualCasted.pSystemTemplate);
        assertInstanceOf(PTemplatesTemplate.class, actualCasted.pTemplatesTemplate);
        var actualPTemplates = (PTemplatesTemplate) actualCasted.pTemplatesTemplate;
        assertTrue(actualPTemplates.pTemplateTemplates.isEmpty());
    }

    @Test
    void projectOnePTemplateGeneratedCorrectly() {
        var pDeclTemplate = generateDefaultPDeclTemplate();
        var pSystemTemplate = generateDefaultPSystemTemplate();
        var pTemplateTemplate = generateDefaultPTemplateTemplate();

        var visitor = new CodeGenVisitor();

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.ProjectContext.class);
        var systemNode = mockForVisitorResult(UCELParser.PsystemContext.class, pSystemTemplate, visitor);
        var declNode = mockForVisitorResult(UCELParser.PdeclarationContext.class, pDeclTemplate, visitor);
        var tempNode1 = mockForVisitorResult(UCELParser.PtemplateContext.class, pTemplateTemplate, visitor);
        var verificationNode = mock(UCELParser.VerificationListContext.class);

        var pTemplateNodes = new ArrayList<UCELParser.PtemplateContext>() {{ add(tempNode1); }};

        node.scope = scopeMock;
        when(node.psystem()).thenReturn(systemNode);
        when(node.pdeclaration()).thenReturn(declNode);
        when(node.ptemplate()).thenReturn(pTemplateNodes);
        when(node.verificationList()).thenReturn(verificationNode);

        var actual = visitor.visitProject(node);
        assertInstanceOf(ProjectTemplate.class, actual);
        var actualCasted = (ProjectTemplate) actual;
        assertEquals(pDeclTemplate, actualCasted.pDeclarationTemplate);
        assertEquals(pSystemTemplate, actualCasted.pSystemTemplate);
        assertInstanceOf(PTemplatesTemplate.class, actualCasted.pTemplatesTemplate);
        var actualPTemplates = (PTemplatesTemplate) actualCasted.pTemplatesTemplate;
        assertEquals(pTemplateNodes.size(), actualPTemplates.pTemplateTemplates.size());
        assertEquals(pTemplateTemplate, actualPTemplates.pTemplateTemplates.get(0));
    }

    @Test
    void projectManyPTemplatesGeneratedCorrectly() {
        PDeclarationTemplate pDeclTemplate = generateDefaultPDeclTemplate();
        PSystemTemplate pSystemTemplate = generateDefaultPSystemTemplate();
        var pTemplateTemplate1 = generateDefaultPTemplateTemplate();
        var pTemplateTemplate2 = generateDefaultPTemplateTemplate();
        var pTemplateTemplates = new ArrayList<PTemplateTemplate>() {{add(pTemplateTemplate1); add(pTemplateTemplate2);}};

        var visitor = new CodeGenVisitor();
        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.ProjectContext.class);
        var systemNode = mockForVisitorResult(UCELParser.PsystemContext.class, pSystemTemplate, visitor);
        var declNode = mockForVisitorResult(UCELParser.PdeclarationContext.class, pDeclTemplate, visitor);
        var tempNode1 = mockForVisitorResult(UCELParser.PtemplateContext.class, pTemplateTemplate1, visitor);
        var tempNode2 = mockForVisitorResult(UCELParser.PtemplateContext.class, pTemplateTemplate2, visitor);
        var verificationNode = mock(UCELParser.VerificationListContext.class);
        var pTemplateNodes = new ArrayList<UCELParser.PtemplateContext>() {{ add(tempNode1); add(tempNode2); }};

        node.scope = scopeMock;
        when(node.psystem()).thenReturn(systemNode);
        when(node.pdeclaration()).thenReturn(declNode);
        when(node.ptemplate()).thenReturn(pTemplateNodes);
        when(node.verificationList()).thenReturn(verificationNode);

        var actual = visitor.visitProject(node);
        assertInstanceOf(ProjectTemplate.class, actual);
        var actualCasted = (ProjectTemplate) actual;
        assertEquals(pDeclTemplate, actualCasted.pDeclarationTemplate);
        assertEquals(pSystemTemplate, actualCasted.pSystemTemplate);
        assertInstanceOf(PTemplatesTemplate.class, actualCasted.pTemplatesTemplate);
        var actualPTemplates = (PTemplatesTemplate) actualCasted.pTemplatesTemplate;
        assertEquals(pTemplateNodes.size(), actualPTemplates.pTemplateTemplates.size());
        for (int i = 0; i < pTemplateNodes.size(); i++) {
            assertEquals(pTemplateTemplates.get(i), actualPTemplates.pTemplateTemplates.get(i));
        }
    }


    //endregion

    //region Project Declaration
    @Test
    void pDeclarationGeneratedCorrectly() {
        var declTemp = generateDefaultDeclarationTemplate("a", "1");

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.PdeclarationContext.class);
        var declNode = mockForVisitorResult(UCELParser.DeclarationsContext.class, declTemp, visitor);

        when(node.declarations()).thenReturn(declNode);

        var actual = visitor.visitPdeclaration(node);
        assertInstanceOf(PDeclarationTemplate.class, actual);
        var actualCasted = (PDeclarationTemplate) actual;
        assertEquals(declTemp, actualCasted.declarations);
    }

    //endregion

    //region Project system
    @Test
    void projectSystemNoBuildGeneratedCorrectly() {
        var declTemplate = generateDefaultDeclarationTemplate("a", "1");
        var sysTemplate = generateDefaultSystemTemplate("a");

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.PsystemContext.class);
        var declNode = mockForVisitorResult(UCELParser.DeclarationsContext.class, declTemplate, visitor);
        var sysNode = mockForVisitorResult(UCELParser.SystemContext.class, sysTemplate, visitor);

        when(node.declarations()).thenReturn(declNode);
        when(node.system()).thenReturn(sysNode);

        var actual = visitor.visitPsystem(node);
        assertInstanceOf(PSystemTemplate.class, actual);
        var actualCasted = (PSystemTemplate) actual;
    }

    private Template generateDefaultBuildTemplate() {
        return new ManualTemplate("build : {\n}");
    }
    @Test
    void projectSystemWithBuildGeneratedCorrectly() {
        var declTemplate = generateDefaultDeclarationTemplate("a", "1");
        var sysTemplate = generateDefaultSystemTemplate("a");
        var buildTemplate = generateDefaultBuildTemplate();

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.PsystemContext.class);
        var declNode = mockForVisitorResult(UCELParser.DeclarationsContext.class, declTemplate, visitor);
        var sysNode = mockForVisitorResult(UCELParser.SystemContext.class, sysTemplate, visitor);
        var buildNode = mockForVisitorResult(UCELParser.BuildContext.class, buildTemplate, visitor);

        when(node.declarations()).thenReturn(declNode);
        when(node.system()).thenReturn(sysNode);
        when(node.build()).thenReturn(buildNode);

        var actual = visitor.visitPsystem(node);
        assertInstanceOf(PSystemTemplate.class, actual);
        var actualCasted = (PSystemTemplate) actual;
    }

    //endregion

    //region Project Template
    @Test
    void projectTemplateGeneratedCorrectly() {
        var declTemplate = generateDefaultDeclarationTemplate("a", "1");
        var paramTemplate = generateDefaultParametersTemplate("int", "b");
        var graphTemplate = generateDefaultGraphTemplate();

        String name = "P";
        var declInfo = mock(DeclarationInfo.class);
        var declRef = mock(DeclarationReference.class);

        var scopeMock = mock(Scope.class);
        var childScope = mock(Scope.class);

        try {
            when(scopeMock.get(declRef)).thenReturn(declInfo);
            when(declInfo.generateName()).thenReturn(name);
        } catch (Exception e) {
            fail();
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var declNode = mockForVisitorResult(UCELParser.DeclarationsContext.class, declTemplate, visitor);
        var paramNode = mockForVisitorResult(UCELParser.ParametersContext.class, paramTemplate, visitor);
        var graphNode = mockForVisitorResult(UCELParser.GraphContext.class, graphTemplate, visitor);

        var node = mock(UCELParser.PtemplateContext.class);
        node.scope = childScope;
        node.reference = declRef;

        when(node.declarations()).thenReturn(declNode);
        when(node.parameters()).thenReturn(paramNode);
        when(node.graph()).thenReturn(graphNode);

        var actual = visitor.visitPtemplate(node);

        assertInstanceOf(PTemplateTemplate.class, actual);
        var actualCasted = (PTemplateTemplate) actual;

        assertEquals(name, actualCasted.name);
        assertEquals(declTemplate, actualCasted.declarations);
        assertEquals(paramTemplate, actualCasted.parameters);
        assertEquals(graphTemplate, actualCasted.graph);
    }


    //endregion

    //endregion

    //region Start
    @Test
    void startDeclarationSystemGeneratedCorrectly() {
        var scopeMock = mock(Scope.class);

        var declTemplate = generateDefaultDeclarationTemplate("a", "1");
        var sysTemplate = generateDefaultSystemTemplate("B");

        var expected = String.format("%s%s", declTemplate, sysTemplate);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.StartContext.class);
        var declNode = mockForVisitorResult(UCELParser.DeclarationsContext.class, declTemplate, visitor);
        var sysNode = mockForVisitorResult(UCELParser.SystemContext.class, sysTemplate, visitor);

        node.scope = scopeMock;
        when(node.declarations()).thenReturn(declNode);
        when(node.system()).thenReturn(sysNode);

        var actual = visitor.visitStart(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void startOneStatementGeneratedCorrectly() {
        var scopeMock = mock(Scope.class);
        var declTemplate = generateDefaultDeclarationTemplate("a", "1");
        var stmnt1Template = generateDefaultStatementTemplate();
        var sysTemplate = generateDefaultSystemTemplate("B");

        var expected = String.format("%s%s%n%s", declTemplate, stmnt1Template,  sysTemplate);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.StartContext.class);
        var declNode = mockForVisitorResult(UCELParser.DeclarationsContext.class, declTemplate, visitor);
        var stmnt1Node = mockForVisitorResult(UCELParser.StatementContext.class, stmnt1Template, visitor);
        var sysNode = mockForVisitorResult(UCELParser.SystemContext.class, sysTemplate, visitor);

        var stmnts = new ArrayList<UCELParser.StatementContext>() {{ add(stmnt1Node); }};

        node.scope = scopeMock;
        when(node.declarations()).thenReturn(declNode);
        when(node.statement()).thenReturn(stmnts);
        when(node.system()).thenReturn(sysNode);

        var actual = visitor.visitStart(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void startMultipleStatementsGeneratedCorrectly() {
        var scopeMock = mock(Scope.class);
        var declTemplate = generateDefaultDeclarationTemplate("a", "1");
        var stmnt1Template = generateDefaultStatementTemplate();
        var stmnt2Template = generateDefaultStatementTemplate();
        var sysTemplate = generateDefaultSystemTemplate("B");

        var expected = String.format("%s%s%n%s%n%s", declTemplate, stmnt1Template, stmnt2Template, sysTemplate);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.StartContext.class);
        var declNode = mockForVisitorResult(UCELParser.DeclarationsContext.class, declTemplate, visitor);
        var stmnt1Node = mockForVisitorResult(UCELParser.StatementContext.class, stmnt1Template, visitor);
        var stmnt2Node = mockForVisitorResult(UCELParser.StatementContext.class, stmnt2Template, visitor);
        var sysNode = mockForVisitorResult(UCELParser.SystemContext.class, sysTemplate, visitor);

        var stmnts = new ArrayList<UCELParser.StatementContext>() {{ add(stmnt1Node); add(stmnt2Node); }};

        node.scope = scopeMock;
        when(node.declarations()).thenReturn(declNode);
        when(node.statement()).thenReturn(stmnts);
        when(node.system()).thenReturn(sysNode);

        var actual = visitor.visitStart(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region System
    @Test
    void systemOneIDExpressionGeneratedCorrectly() {
        String name = "B";
        var exprTemplate = generateDefaultExprTemplate(name);
        var expected = String.format("// Declarations for the necessary processes%n%n%nsystem %s;", name);
        var scopeMock = mock(Scope.class);
        var declInfo = mock(DeclarationInfo.class);
        var declRef = mock(DeclarationReference.class);

        try {
            when(scopeMock.get(declRef)).thenReturn(declInfo);
            when(declInfo.generateName()).thenReturn(name);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.SystemContext.class);
        var exprNode = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemplate, visitor);
        var exprInfo = mock(DeclarationInfo.class);
        var exprs = new ArrayList<UCELParser.ExpressionContext>() {{ add(exprNode); }};

        when(node.expression()).thenReturn(exprs);
        for (int i = 0; i < exprs.size(); i++)
            when(node.expression(i)).thenReturn(exprs.get(i));
        exprNode.originDefinition = exprInfo;
        exprNode.reference = declRef;
        when(exprInfo.generateName()).thenReturn(name);

        var actual = visitor.visitSystem(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void systemOneFuncCallExpressionGeneratedCorrectly() {
        String name = "B_0";
        var exprTemplate = generateDefaultExprTemplate("B()");
        var expected = String.format("// Declarations for the necessary processes%n%s = %s;%n%nsystem %s;", name, exprTemplate, name);

        var scopeMock = mock(Scope.class);
        var declInfoFuncCall = mock(DeclarationInfo.class);
        var declRefFuncCall = mock(DeclarationReference.class);

        try {
            when(scopeMock.get(declRefFuncCall)).thenReturn(declInfoFuncCall);
            when(declInfoFuncCall.generateName()).thenReturn("B");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.SystemContext.class);
        var exprNode = mockForVisitorResult(UCELParser.FuncCallContext.class, exprTemplate, visitor);
        var exprInfo = mock(DeclarationInfo.class);
        var exprs = new ArrayList<UCELParser.ExpressionContext>() {{ add(exprNode); }};

        when(node.expression()).thenReturn(exprs);
        for (int i = 0; i < exprs.size(); i++)
            when(node.expression(i)).thenReturn(exprs.get(i));
        exprNode.originDefinition = exprInfo;
        exprNode.reference = declRefFuncCall;
        when(exprInfo.generateName()).thenReturn("B");

        var actual = visitor.visitSystem(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void systemMultipleIdAndFuncCallExpressionsGeneratedCorrectly() {
        String name1 = "B", name2 = "C_1";
        var expr1Template = generateDefaultExprTemplate("B");
        var expr2Template = generateDefaultExprTemplate("C()");
        var expected = String.format("// Declarations for the necessary processes%n%s = %s;%n%nsystem %s, %s;", name2, expr2Template, name1, name2);

        var scopeMock = mock(Scope.class);
        var declInfoFuncCall = mock(DeclarationInfo.class);
        var declInfoID = mock(DeclarationInfo.class);
        var declRefFuncCall = mock(DeclarationReference.class);
        var declRefID = mock(DeclarationReference.class);

        try {
            when(scopeMock.get(declRefID)).thenReturn(declInfoID);
            when(scopeMock.get(declRefFuncCall)).thenReturn(declInfoFuncCall);
            when(declInfoID.generateName()).thenReturn(name1);
            when(declInfoFuncCall.generateName()).thenReturn("C");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.SystemContext.class);
        var expr1Node = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Template, visitor);
        var expr2Node = mockForVisitorResult(UCELParser.FuncCallContext.class, expr2Template, visitor);
        var expr1Info = mock(DeclarationInfo.class);
        var expr2Info = mock(DeclarationInfo.class);
        var exprs = new ArrayList<UCELParser.ExpressionContext>() {{ add(expr1Node); add(expr2Node); }};

        when(node.expression()).thenReturn(exprs);
        for (int i = 0; i < exprs.size(); i++)
            when(node.expression(i)).thenReturn(exprs.get(i));
        expr1Node.originDefinition = expr1Info;
        expr2Node.originDefinition = expr2Info;
        expr1Node.reference = declRefID;
        expr2Node.reference = declRefFuncCall;
        when(expr1Info.generateName()).thenReturn("B");
        when(expr2Info.generateName()).thenReturn("C");

        var actual = visitor.visitSystem(node).toString();

        assertEquals(expected, actual);
    }


    //endregion

    //region Guard
    @ParameterizedTest
    @ValueSource(strings = {"==", "!=", "<", ">", "<=", ">="})
    void guardExpressionGeneratedCorrectly(String expectedOperator) {
        var exprTemplate = generateDefaultExprTemplate(String.format("x %s 0", expectedOperator));
        var expected = String.format("%s", exprTemplate);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.GuardContext.class);
        var exprNode = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemplate, visitor);

        when(node.expression()).thenReturn(exprNode);

        var actual = visitor.visitGuard(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void guardExpressionNullGeneratedCorrectly() {
        var expected = "";
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.GuardContext.class);

        when(node.expression()).thenReturn(null);

        var actual = visitor.visitGuard(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Function

    @Test
    void functionNoParams() {
        Template type = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        Template ID = new ManualTemplate("functionName");
        Template block = new ManualTemplate(String.format("{%n}"));
        Template parameters = new ManualTemplate("");

        String expected = String.format("%s %s(%s)%n%s%n%n", type, ID, parameters, block);

        Scope scopeMock = mock(Scope.class);
        Scope scopeMockNode = mock(Scope.class);
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.FunctionContext.class);
        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, type, visitor);
        var blockMock = mockForVisitorResult(UCELParser.BlockContext.class, block, visitor);
        var parametersMock = mockForVisitorResult(UCELParser.ParametersContext.class, parameters, visitor);

        var referenceMock = mock(DeclarationReference.class);
        var infoMock = mock(DeclarationInfo.class);

        node.reference = referenceMock;
        node.scope = scopeMockNode;
        try {
            when(scopeMock.get(node.reference)).thenReturn(infoMock);
            when(scopeMockNode.getParent()).thenReturn(scopeMock);
        } catch (Exception e) {
            fail("cannot mock scope.get");
        }

        when(infoMock.generateName(anyString())).thenReturn(ID.toString());
        when(node.type()).thenReturn(typeMock);
        when(node.block()).thenReturn(blockMock);
        when(node.parameters()).thenReturn(parametersMock);

        var actual = visitor.visitFunction(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void functionNoRefs() {
        Template type = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        Template ID = new ManualTemplate("functionName");
        Template block = new ManualTemplate(String.format("{%n}"));
        Template parameters = new ManualTemplate("int a");

        String expected = String.format("%s %s(%s)%n%s%n%n", type, ID, parameters, block);

        Scope scopeMock = mock(Scope.class);
        Scope scopeMockNode = mock(Scope.class);
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.FunctionContext.class);
        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, type, visitor);
        var blockMock = mockForVisitorResult(UCELParser.BlockContext.class, block, visitor);
        var parametersMock = mockForVisitorResult(UCELParser.ParametersContext.class, parameters, visitor);

        var referenceMock = mock(DeclarationReference.class);
        var infoMock = mock(DeclarationInfo.class);

        node.reference = referenceMock;
        node.scope = scopeMockNode;
        try {
            when(scopeMock.get(node.reference)).thenReturn(infoMock);
            when(scopeMockNode.getParent()).thenReturn(scopeMock);
        } catch (Exception e) {
            fail("cannot mock scope.get");
        }

        when(infoMock.generateName(anyString())).thenReturn(ID.toString());
        when(node.type()).thenReturn(typeMock);
        when(node.block()).thenReturn(blockMock);
        when(node.parameters()).thenReturn(parametersMock);

        var actual = visitor.visitFunction(node).toString();
        assertEquals(expected, actual);
    }

    //Does this work...???
    @Test
    void functionTwoRefs() {
        Template type = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        Template block = new ManualTemplate(String.format("{%n}"));
        Template parameters = new ManualTemplate("");

        String expected = String.format("int functionName1()%n{%n}%n%nint functionName2()%n{%n}%n%n");

        Scope funcScope = mock(Scope.class);
        Scope scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.FunctionContext.class);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, type, visitor);
        var blockMock = mockForVisitorResult(UCELParser.BlockContext.class, block, visitor);
        when(node.type()).thenReturn(typeMock);
        when(node.block()).thenReturn(blockMock);

        var parameter = mock(UCELParser.ParameterContext.class);
        var REFNodeMock = mock(TerminalNode.class);
        parameter.reference = mock(DeclarationReference.class);
        when(parameter.REF()).thenReturn(REFNodeMock);

        var parametersMock = mockForVisitorResult(UCELParser.ParametersContext.class, parameters, visitor);
        var parametersList = new ArrayList<UCELParser.ParameterContext>();
        parametersList.add(parameter);
        parametersList.add(parameter);
        when(parametersMock.parameter()).thenReturn(parametersList);
        when(node.parameters()).thenReturn(parametersMock);

        var refParamInfoMock = mock(DeclarationInfo.class);

        DeclarationInfo[] declarationInfos = {refParamInfoMock, refParamInfoMock};

        var FuncCallCtxMock1 = mock(UCELParser.FuncCallContext.class);
        FuncCallCtxMock1.reference = mock(DeclarationReference.class);
        var FuncCallInfoMock1 = mock(DeclarationInfo.class);
        try {
            when(scopeMock.get(FuncCallCtxMock1.reference)).thenReturn(FuncCallInfoMock1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(FuncCallInfoMock1.generateName(anyString())).thenReturn("functionName1");

        var FuncCallCtxMock2 = mock(UCELParser.FuncCallContext.class);
        FuncCallCtxMock2.reference = mock(DeclarationReference.class);
        var FuncCallInfoMock2 = mock(DeclarationInfo.class);
        try {
            when(scopeMock.get(FuncCallCtxMock2.reference)).thenReturn(FuncCallInfoMock2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(FuncCallInfoMock2.generateName(anyString())).thenReturn("functionName2");

        var occurrenceMock1 = mock(FuncCallOccurrence.class);
        when(occurrenceMock1.getFuncCallContext()).thenReturn(FuncCallCtxMock1);
        when(occurrenceMock1.getRefParams()).thenReturn(declarationInfos);

        var occurrenceMock2 = mock(FuncCallOccurrence.class);
        when(occurrenceMock2.getFuncCallContext()).thenReturn(FuncCallCtxMock2);
        when(occurrenceMock2.getRefParams()).thenReturn(declarationInfos);

        var occurrencesList = new ArrayList<FuncCallOccurrence>();
        occurrencesList.add(occurrenceMock1);
        occurrencesList.add(occurrenceMock2);

        node.occurrences = occurrencesList;
        node.scope = funcScope;

        when(funcScope.getParent()).thenReturn(scopeMock);

        var actual = visitor.visitFunction(node).toString();
        assertEquals(expected, actual);
    }

    //endregion

    //region Instantiation
    @Test
    void instantiationNoParenNoParameterNoArgumentsGeneratedCorrectly() {
        String id1Name = "a";
        String id2Name = "b";
        String expected = String.format("%s = %s();", id1Name, id2Name);
        Scope scopeMock = mock(Scope.class);
        Scope scopeNodeMock = mock(Scope.class);
        DeclarationReference ref1Mock = mock(DeclarationReference.class);
        DeclarationReference ref2Mock = mock(DeclarationReference.class);

        DeclarationInfo info1Mock = mock(DeclarationInfo.class);
        DeclarationInfo info2Mock = mock(DeclarationInfo.class);

        when(info1Mock.generateName(anyString())).thenReturn(id1Name);
        when(info2Mock.generateName(anyString())).thenReturn(id2Name);

        try {
            when(scopeMock.get(ref1Mock)).thenReturn(info1Mock);
            when(scopeNodeMock.get(ref2Mock)).thenReturn(info2Mock);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.InstantiationContext.class);
        node.instantiatedReference = ref1Mock;
        node.constructorReference = ref2Mock;
        node.scope = scopeNodeMock;

        String actual = visitor.visitInstantiation(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void instantiationParenNoParameterNoArgumentsGeneratedCorrectly() {
        String id1Name = "a";
        String id2Name = "b";
        String expected = String.format("%s() = %s();", id1Name, id2Name);
        Scope scopeMock = mock(Scope.class);
        Scope scopeNodeMock = mock(Scope.class);
        DeclarationReference ref1Mock = mock(DeclarationReference.class);
        DeclarationReference ref2Mock = mock(DeclarationReference.class);

        DeclarationInfo info1Mock = mock(DeclarationInfo.class);
        DeclarationInfo info2Mock = mock(DeclarationInfo.class);

        when(info1Mock.generateName(anyString())).thenReturn(id1Name);
        when(info2Mock.generateName(anyString())).thenReturn(id2Name);

        try {
            when(scopeMock.get(ref1Mock)).thenReturn(info1Mock);
            when(scopeNodeMock.get(ref2Mock)).thenReturn(info2Mock);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.InstantiationContext.class);
        var parMocks = new ArrayList<TerminalNode>() {{add(mock(TerminalNode.class)); add(mock(TerminalNode.class));}};
        node.instantiatedReference = ref1Mock;
        node.constructorReference = ref2Mock;
        node.scope = scopeNodeMock;
        when(node.LEFTPAR()).thenReturn(parMocks);

        String actual = visitor.visitInstantiation(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void instantiationParenParameterNoArgumentsGeneratedCorrectly() {
        String id1Name = "a";
        String id2Name = "b";
        var paramTemplate = generateDefaultParametersTemplate("int", "c");
        String expected = String.format("%s(%s) = %s();", id1Name, paramTemplate, id2Name);
        Scope scopeMock = mock(Scope.class);
        Scope scopeNodeMock = mock(Scope.class);
        DeclarationReference ref1Mock = mock(DeclarationReference.class);
        DeclarationReference ref2Mock = mock(DeclarationReference.class);

        DeclarationInfo info1Mock = mock(DeclarationInfo.class);
        DeclarationInfo info2Mock = mock(DeclarationInfo.class);

        when(info1Mock.generateName(anyString())).thenReturn(id1Name);
        when(info2Mock.generateName(anyString())).thenReturn(id2Name);

        try {
            when(scopeMock.get(ref1Mock)).thenReturn(info1Mock);
            when(scopeNodeMock.get(ref2Mock)).thenReturn(info2Mock);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }
        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.InstantiationContext.class);
        var paramMock = mockForVisitorResult(UCELParser.ParametersContext.class, paramTemplate, visitor);
        var parMocks = new ArrayList<TerminalNode>() {{add(mock(TerminalNode.class)); add(mock(TerminalNode.class));}};
        node.instantiatedReference = ref1Mock;
        node.constructorReference = ref2Mock;
        node.scope = scopeNodeMock;
        when(node.LEFTPAR()).thenReturn(parMocks);
        when(node.parameters()).thenReturn(paramMock);

        String actual = visitor.visitInstantiation(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void instantiationNoParenNoParameterArgumentsGeneratedCorrectly() {
        String id1Name = "a";
        String id2Name = "b";
        var argTemplate = generateDefaultArgumentsTemplate(Type.TypeEnum.intType);
        String expected = String.format("%s = %s(%s);", id1Name, id2Name, argTemplate);
        Scope scopeMock = mock(Scope.class);
        Scope scopeNodeMock = mock(Scope.class);
        DeclarationReference ref1Mock = mock(DeclarationReference.class);
        DeclarationReference ref2Mock = mock(DeclarationReference.class);

        DeclarationInfo info1Mock = mock(DeclarationInfo.class);
        DeclarationInfo info2Mock = mock(DeclarationInfo.class);

        when(info1Mock.generateName(anyString())).thenReturn(id1Name);
        when(info2Mock.generateName(anyString())).thenReturn(id2Name);

        try {
            when(scopeMock.get(ref1Mock)).thenReturn(info1Mock);
            when(scopeNodeMock.get(ref2Mock)).thenReturn(info2Mock);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }
        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.InstantiationContext.class);
        var argMock = mockForVisitorResult(UCELParser.ArgumentsContext.class, argTemplate, visitor);
        node.instantiatedReference = ref1Mock;
        node.constructorReference = ref2Mock;
        node.scope = scopeNodeMock;
        when(node.arguments()).thenReturn(argMock);

        String actual = visitor.visitInstantiation(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void instantiationParenNoParameterArgumentsGeneratedCorrectly() {
        String id1Name = "a";
        String id2Name = "b";
        var argTemplate = generateDefaultArgumentsTemplate(Type.TypeEnum.intType);
        String expected = String.format("%s() = %s(%s);", id1Name, id2Name, argTemplate);
        Scope scopeMock = mock(Scope.class);
        Scope scopeNodeMock = mock(Scope.class);
        DeclarationReference ref1Mock = mock(DeclarationReference.class);
        DeclarationReference ref2Mock = mock(DeclarationReference.class);

        DeclarationInfo info1Mock = mock(DeclarationInfo.class);
        DeclarationInfo info2Mock = mock(DeclarationInfo.class);

        when(info1Mock.generateName(anyString())).thenReturn(id1Name);
        when(info2Mock.generateName(anyString())).thenReturn(id2Name);

        try {
            when(scopeMock.get(ref1Mock)).thenReturn(info1Mock);
            when(scopeNodeMock.get(ref2Mock)).thenReturn(info2Mock);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.InstantiationContext.class);
        var argMock = mockForVisitorResult(UCELParser.ArgumentsContext.class, argTemplate, visitor);
        var parMocks = new ArrayList<TerminalNode>() {{add(mock(TerminalNode.class)); add(mock(TerminalNode.class));}};
        node.instantiatedReference = ref1Mock;
        node.constructorReference = ref2Mock;
        node.scope = scopeNodeMock;
        when(node.LEFTPAR()).thenReturn(parMocks);
        when(node.arguments()).thenReturn(argMock);

        String actual = visitor.visitInstantiation(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void instantiationParenParameterArgumentsGeneratedCorrectly() {
        String id1Name = "a";
        String id2Name = "b";
        var paramTemplate = generateDefaultParametersTemplate("int", "c");
        var argTemplate = generateDefaultArgumentsTemplate(Type.TypeEnum.intType);
        String expected = String.format("%s(%s) = %s(%s);", id1Name, paramTemplate, id2Name, argTemplate);
        Scope scopeMock = mock(Scope.class);
        Scope scopeNodeMock = mock(Scope.class);
        DeclarationReference ref1Mock = mock(DeclarationReference.class);
        DeclarationReference ref2Mock = mock(DeclarationReference.class);

        DeclarationInfo info1Mock = mock(DeclarationInfo.class);
        DeclarationInfo info2Mock = mock(DeclarationInfo.class);

        when(info1Mock.generateName(anyString())).thenReturn(id1Name);
        when(info2Mock.generateName(anyString())).thenReturn(id2Name);

        try {
            when(scopeMock.get(ref1Mock)).thenReturn(info1Mock);
            when(scopeNodeMock.get(ref2Mock)).thenReturn(info2Mock);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }
        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.InstantiationContext.class);
        var paramMock = mockForVisitorResult(UCELParser.ParametersContext.class, paramTemplate, visitor);
        var argMock = mockForVisitorResult(UCELParser.ArgumentsContext.class, argTemplate, visitor);
        var parMocks = new ArrayList<TerminalNode>() {{add(mock(TerminalNode.class)); add(mock(TerminalNode.class));}};
        node.instantiatedReference = ref1Mock;
        node.constructorReference = ref2Mock;
        node.scope = scopeNodeMock;
        when(node.LEFTPAR()).thenReturn(parMocks);
        when(node.parameters()).thenReturn(paramMock);
        when(node.arguments()).thenReturn(argMock);

        String actual = visitor.visitInstantiation(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region parameters
    @Test
    void parametersNoParameterGeneratedCorrectly() {
        String expected = "";

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.ParametersContext.class);

        var actual = visitor.visitParameters(node).toString();
        assertEquals(expected, actual);
    }
    @Test
    void parametersOneParameterGeneratedCorrectly() {
        var paramTemplate = generateDefaultParametersTemplate("int", "a");
        var expected = paramTemplate.toString();

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.ParametersContext.class);
        var paramNodes = new ArrayList<UCELParser.ParameterContext>()
            {{add(mockForVisitorResult(UCELParser.ParameterContext.class, paramTemplate, visitor));}};

        when(node.parameter()).thenReturn(paramNodes);

        var actual = visitor.visitParameters(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void parametersMultipleParameterGeneratedCorrectly() {
        var param1Template = generateDefaultParametersTemplate("int", "a");
        var param2Template = generateDefaultParametersTemplate("bool", "b");
        var expected = String.format("%s, %s", param1Template, param2Template);

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.ParametersContext.class);
        var paramNodes = new ArrayList<UCELParser.ParameterContext>()
        {{
            add(mockForVisitorResult(UCELParser.ParameterContext.class, param1Template, visitor));
            add(mockForVisitorResult(UCELParser.ParameterContext.class, param2Template, visitor));
        }};

        when(node.parameter()).thenReturn(paramNodes);

        var actual = visitor.visitParameters(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void parameterReferenceIDGeneratedCorrectly() {
        var typeTemplate = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var id = "a";
        var expected = "";
        var variable = mock(DeclarationInfo.class);
        var ref = mock(DeclarationReference.class);

        when(variable.generateName()).thenReturn(id);
        when(variable.getType()).thenReturn(new Type(Type.TypeEnum.intType));
        var scopeMock = mock(Scope.class);

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var refNode = mock(TerminalNode.class);

        var node = mock(UCELParser.ParameterContext.class);
        var typeNode = mockForVisitorResult(UCELParser.TypeContext.class, typeTemplate, visitor);
        node.reference = ref;
        when(refNode.getText()).thenReturn("ref");
        when(node.REF()).thenReturn(refNode);
        when(node.type()).thenReturn(typeNode);

        var actual = visitor.visitParameter(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void parameterNormalIDGeneratedCorrectly() {
        var typeTemplate = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var id = "a";
        var expected = String.format("%s %s", typeTemplate, id);
        var variable = mock(DeclarationInfo.class);
        var ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(id);
        when(variable.getType()).thenReturn(new Type(Type.TypeEnum.intType));
        var scopeMock = mock(Scope.class);

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.ParameterContext.class);
        var typeNode = mockForVisitorResult(UCELParser.TypeContext.class, typeTemplate, visitor);
        node.reference = ref;
        when(node.type()).thenReturn(typeNode);

        var actual = visitor.visitParameter(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void parameterIDWithOneArrayDeclGeneratedCorrectly() {
        var typeTemplate = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var arrayDeclTemplate = generateDefaultArrayDeclTemplate();
        var id = "a";
        var expected = String.format("%s %s%s", typeTemplate, id, arrayDeclTemplate);
        var variable = mock(DeclarationInfo.class);
        var ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(id);
        when(variable.getType()).thenReturn(new Type(Type.TypeEnum.intType));
        var scopeMock = mock(Scope.class);

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.ParameterContext.class);
        var typeNode = mockForVisitorResult(UCELParser.TypeContext.class, typeTemplate, visitor);
        var arrayNode = new ArrayList<UCELParser.ArrayDeclContext>()
            {{add(mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDeclTemplate, visitor));}};
        node.reference = ref;

        when(node.arrayDecl()).thenReturn(arrayNode);
        when(node.type()).thenReturn(typeNode);

        var actual = visitor.visitParameter(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void parameterIDWithMultipleArrayDeclGeneratedCorrectly() {
        var typeTemplate = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var arrayDeclTemplate = generateDefaultArrayDeclTemplate();
        var id = "a";
        var expected = String.format("%s %s%s%s", typeTemplate, id, arrayDeclTemplate, arrayDeclTemplate);
        var variable = mock(DeclarationInfo.class);
        var ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(id);
        when(variable.getType()).thenReturn(new Type(Type.TypeEnum.intType));
        var scopeMock = mock(Scope.class);

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.ParameterContext.class);
        var typeNode = mockForVisitorResult(UCELParser.TypeContext.class, typeTemplate, visitor);
        var arrayNode = new ArrayList<UCELParser.ArrayDeclContext>()
        {{
            add(mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDeclTemplate, visitor));
            add(mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDeclTemplate, visitor));
        }};
        node.reference = ref;

        when(node.arrayDecl()).thenReturn(arrayNode);
        when(node.type()).thenReturn(typeNode);

        var actual = visitor.visitParameter(node).toString();
        assertEquals(expected, actual);
    }

    @Test
    void parameterIDAmpersandGeneratedCorrectly() {
        var typeTemplate = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var id = "a";
        var ampersand = "&";
        var expected = String.format("%s%s %s", typeTemplate, ampersand, id);
        var variable = mock(DeclarationInfo.class);
        var ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(id);
        when(variable.getType()).thenReturn(new Type(Type.TypeEnum.intType));
        var scopeMock = mock(Scope.class);

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);
        var ampNode = mock(TerminalNode.class);

        var node = mock(UCELParser.ParameterContext.class);
        var typeNode = mockForVisitorResult(UCELParser.TypeContext.class, typeTemplate, visitor);

        node.reference = ref;

        when(ampNode.getText()).thenReturn(ampersand);
        when(node.BITAND()).thenReturn(ampNode);
        when(node.type()).thenReturn(typeNode);

        var actual = visitor.visitParameter(node).toString();
        assertEquals(expected, actual);
    }

    //endregion

    //region Channel expression
    @Test
    void chanExprIDGeneratedCorrectly() {
        String expected = "a";
        Scope scopeMock = mock(Scope.class);
        DeclarationReference declarationReferenceMock = mock(DeclarationReference.class);
        DeclarationInfo declarationInfoMock = mock(DeclarationInfo.class);

        when(declarationInfoMock.generateName(anyString())).thenReturn("a");

        try {
            when(scopeMock.get(declarationReferenceMock)).thenReturn(declarationInfoMock);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.ChanExprContext.class);
        node.reference = declarationReferenceMock;

        String actual = visitor.visitChanExpr(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void chanExprIDWithBracketsGeneratedCorrectly() {
        var chanTemplate = generateDefaultChanExprTemplate("a");
        var exprTemplate = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("%s[%s]", chanTemplate, exprTemplate);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.ChanExprContext.class);
        var chanNode = mockForVisitorResult(UCELParser.ChanExprContext.class, chanTemplate, visitor);
        var exprNode = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemplate, visitor);

        when(node.chanExpr()).thenReturn(chanNode);
        when(node.expression()).thenReturn(exprNode);

        String actual = visitor.visitChanExpr(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region Declarations
    @Test
    public void declarationsGeneratedCorrectly() {
        Template variableDecl1 = generateDefaultLocalDeclaration(Type.TypeEnum.intType, "a");
        Template variableDecl2 = generateDefaultLocalDeclaration(Type.TypeEnum.boolType, "b");

        String expected = String.format("%s%n%s", variableDecl1, variableDecl2);

        var visitor = new CodeGenVisitor();

        var variableDecl1Mock = mockForVisitorResult(UCELParser.VariableDeclContext.class, variableDecl1, visitor);
        var variableDecl2Mock = mockForVisitorResult(UCELParser.VariableDeclContext.class, variableDecl2, visitor);

        var declarations = new ArrayList<ParseTree>();
        declarations.add(variableDecl1Mock);
        declarations.add(variableDecl2Mock);

        var node = mock(UCELParser.DeclarationsContext.class);
        node.children = declarations;

        var actual = visitor.visitDeclarations(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region fieldDecl
    @Test
    public void fieldDeclSingle() {
        Template type = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        Template arrayDeclID = generateDefaultExprTemplate("a");
        String expected = String.format("%s %s;", type, arrayDeclID);

        var visitor = new CodeGenVisitor();

        var arrayDeclIDContext = mockForVisitorResult(UCELParser.ArrayDeclIDContext.class, arrayDeclID, visitor);
        List<UCELParser.ArrayDeclIDContext> arrayDeclIDContexts = new ArrayList<>();
        arrayDeclIDContexts.add(arrayDeclIDContext);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, type, visitor);

        var node = mock(UCELParser.FieldDeclContext.class);

        when(node.type()).thenReturn(typeMock);
        when(node.arrayDeclID()).thenReturn(arrayDeclIDContexts);

        var actual = visitor.visitFieldDecl(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    public void fieldDeclMultiple() {
        Template arrayDeclID1 = generateDefaultExprTemplate("a");
        Template arrayDeclID2 = generateDefaultExprTemplate("b");
        Template template = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        String expected = String.format("%s %s, %s;", template, arrayDeclID1, arrayDeclID2);

        var visitor = new CodeGenVisitor();

        var arrayDeclIDContext1 = mockForVisitorResult(UCELParser.ArrayDeclIDContext.class, arrayDeclID1, visitor);
        var arrayDeclIDContext2 = mockForVisitorResult(UCELParser.ArrayDeclIDContext.class, arrayDeclID2, visitor);

        List<UCELParser.ArrayDeclIDContext> arrayDeclIDContexts = new ArrayList<>();
        arrayDeclIDContexts.add(arrayDeclIDContext1);
        arrayDeclIDContexts.add(arrayDeclIDContext2);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, template, visitor);

        var node = mock(UCELParser.FieldDeclContext.class);

        when(node.type()).thenReturn(typeMock);
        when(node.arrayDeclID()).thenReturn(arrayDeclIDContexts);

        var actual = visitor.visitFieldDecl(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region literal

    @Test
    public void literalExpr() {
        Template literal = generateDefaultLiteralTemplate(Type.TypeEnum.boolType);
        String expected = literal.toString();

        var visitor = new CodeGenVisitor();

        var literalMock = mockForVisitorResult(UCELParser.LiteralContext.class, literal, visitor);
        var ctx = mock(UCELParser.LiteralExprContext.class);

        when(ctx.literal()).thenReturn(literalMock);

        var actual = visitor.visitLiteralExpr(ctx).toString();

        assertEquals(expected, actual);
    }

    @Test
    public void literal() {
        String expected = generateDefaultLiteralTemplate(Type.TypeEnum.intType).toString();

        var visitor = new CodeGenVisitor();

        var terminalNode = mock(TerminalNode.class);

        var node = mock(UCELParser.LiteralContext.class);
        when(node.NAT()).thenReturn(terminalNode);
        when(node.getText()).thenReturn(expected);
        var actual = visitor.visitLiteral(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region arrayDeclID
    @Test
    public void arrayDeclIDNoArray() {
        String expected = "var1";

//        Template arrayDeclIDTemplate = mock(Template.class);
//        arrayDeclIDTemplate.template = new ST("<ID>");
//        arrayDeclIDTemplate.template.add("ID", expected);

        UCELParser.ArrayDeclIDContext node = mock(UCELParser.ArrayDeclIDContext.class);
        TerminalNodeImpl id = new TerminalNodeImpl(new CommonToken(UCELParser.ID, expected));
        when(node.ID()).thenReturn(id);
        CodeGenVisitor visitor = new CodeGenVisitor();
        String actual = visitor.visitArrayDeclID(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    public void arrayDeclIDWithArray() {
        String expected = "var1[5][5][5]";

        Template arrayDecl = new ManualTemplate("[5]");

        CodeGenVisitor visitor = new CodeGenVisitor();

        UCELParser.ArrayDeclIDContext node = mock(UCELParser.ArrayDeclIDContext.class);

        var arrayDeclMock = mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDecl, visitor);
        List<UCELParser.ArrayDeclContext> arrayDeclList = new ArrayList<>();
        arrayDeclList.add(arrayDeclMock);
        arrayDeclList.add(arrayDeclMock);
        arrayDeclList.add(arrayDeclMock);
        when(node.arrayDecl()).thenReturn(arrayDeclList);

        TerminalNodeImpl id = new TerminalNodeImpl(new CommonToken(UCELParser.ID, "var1"));
        when(node.ID()).thenReturn(id);

        String actual = visitor.visitArrayDeclID(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region boolean
    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void booleanGeneratedCorrectly(String expected) {
        var ctx = mock(UCELParser.BoolContext.class);
        when(ctx.getText()).thenReturn(expected);

        var visitor = new CodeGenVisitor();
        var actual = visitor.visitBool(ctx).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Unary
    @ParameterizedTest
    @ValueSource(strings = {"+", "-", "not ", "!"})
    void unaryGeneratedCorrectly(String expected) {
        var node = mock(UCELParser.UnaryContext.class);
        when(node.getText()).thenReturn(expected.replace(" ", ""));

        var visitor = new CodeGenVisitor();
        var actual = visitor.visitUnary(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Arguments (and ArgumentsImd)
    @Test
    void argumentsNoExpressionsGeneratedCorrectly() {
        var expected = "";
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.ArgumentsContext.class);
        var actual = visitor.visitArguments(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void argumentsOneExpressionsGeneratedCorrectly() {
        var exprTemplate = generateDefaultExprTemplate(Type.TypeEnum.intType);
        var expected = exprTemplate.toString();

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.ArgumentsContext.class);
        var exprNode = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemplate, visitor);
        var exprList = new ArrayList<UCELParser.ExpressionContext>() {{ add(exprNode); }};

        when(node.expression(0)).thenReturn(exprNode);
        when(node.expression()).thenReturn(exprList);

        var actual = visitor.visitArguments(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void argumentsManyExpressionsGeneratedCorrectly() {
        var expr1Template = generateDefaultExprTemplate(Type.TypeEnum.intType);
        var expr2Template = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        var expr3Template = generateDefaultExprTemplate(Type.TypeEnum.doubleType);
        var expected = String.format("%s, %s, %s", expr1Template, expr2Template, expr3Template);

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.ArgumentsContext.class);
        var expr1Node = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Template, visitor);
        var expr2Node = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Template, visitor);
        var expr3Node = mockForVisitorResult(UCELParser.ExpressionContext.class, expr3Template, visitor);
        var exprList = new ArrayList<UCELParser.ExpressionContext>() {{ add(expr1Node); add(expr2Node); add(expr3Node); }};

        when(node.expression()).thenReturn(exprList);

        var actual = visitor.visitArguments(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void argumentsIgnoresREFIDGeneratedCorrectly() {
        var expected = "";

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.ArgumentsContext.class);

        when(node.ID()).thenThrow(new RuntimeException());
        when(node.REF()).thenThrow(new RuntimeException());
        when(node.ID(0)).thenThrow(new RuntimeException());
        when(node.REF(0)).thenThrow(new RuntimeException());

        var actual = visitor.visitArguments(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region LocalDeclaration
    @Test
    void localDeclarationTypeDeclGeneratedCorrectly() {
        var typeDeclTemplate = generateDefaultTypeDeclTemplate("a", "10");
        var expected = typeDeclTemplate.toString();

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.LocalDeclarationContext.class);
        var typeDeclNode = mockForVisitorResult(UCELParser.TypeDeclContext.class, typeDeclTemplate, visitor);

        when(node.typeDecl()).thenReturn(typeDeclNode);

        var actual = visitor.visitLocalDeclaration(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void localDeclarationVariableDeclGeneratedCorrectly() {
        var varDeclTemplate = generateDefaultVariableDeclTemplate("a", "10");
        var expected = varDeclTemplate.toString();

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.LocalDeclarationContext.class);
        var typeDeclNode = mockForVisitorResult(UCELParser.VariableDeclContext.class, varDeclTemplate, visitor);

        when(node.variableDecl()).thenReturn(typeDeclNode);

        var actual = visitor.visitLocalDeclaration(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region TypeDecl

    @Test
    public void typeDeclOneIDGeneratedCorrectly() {
        String expected = "typedef int list[10];";
        Scope scopeMock = mock(Scope.class);
        DeclarationReference declarationReferenceMock = mock(DeclarationReference.class);
        DeclarationInfo declarationInfoMock = mock(DeclarationInfo.class);

        List<DeclarationReference> declarationReferences = new ArrayList<>();
        declarationReferences.add(declarationReferenceMock);

        when(declarationInfoMock.generateName(anyString())).thenReturn("list");

        try {
            when(scopeMock.get(declarationReferenceMock)).thenReturn(declarationInfoMock);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var arrayDeclIDMock = mock(Template.class);
        var arrayDeclIDContext = mockForVisitorResult(UCELParser.ArrayDeclIDContext.class, arrayDeclIDMock, visitor);

        var arrayDeclIDMockModified = mock(Template.class);

        List<UCELParser.ArrayDeclIDContext> arrayDeclIDs = new ArrayList<>();
        arrayDeclIDs.add(arrayDeclIDContext);

        Template type = new ManualTemplate("int");
        var typeContext = mockForVisitorResult(UCELParser.TypeContext.class, type, visitor);

        var node = mock(UCELParser.TypeDeclContext.class);

        when(arrayDeclIDMockModified.toString()).thenReturn("list[10]");
        when(arrayDeclIDMock.replaceValue("ID", "list")).thenReturn(arrayDeclIDMockModified);
        when(node.type()).thenReturn(typeContext);
        when(node.arrayDeclID()).thenReturn(arrayDeclIDs);
        when(node.arrayDeclID(0)).thenReturn(arrayDeclIDContext);
        node.references = declarationReferences;

        String actual = visitor.visitTypeDecl(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void typeDeclMultipleIDsCorrectly() {
        String expected = "typedef int list[10], betterInt;";
        Scope scopeMock = mock(Scope.class);
        DeclarationReference declarationReferenceMock1 = mock(DeclarationReference.class);
        DeclarationInfo declarationInfoMock1 = mock(DeclarationInfo.class);

        DeclarationReference declarationReferenceMock2 = mock(DeclarationReference.class);
        DeclarationInfo declarationInfoMock2 = mock(DeclarationInfo.class);

        List<DeclarationReference> declarationReferences = new ArrayList<>();
        declarationReferences.add(declarationReferenceMock1);
        declarationReferences.add(declarationReferenceMock2);


        when(declarationInfoMock1.generateName(anyString())).thenReturn("list");
        when(declarationInfoMock2.generateName(anyString())).thenReturn("betterInt");

        try {
            when(scopeMock.get(declarationReferenceMock1)).thenReturn(declarationInfoMock1);
            when(scopeMock.get(declarationReferenceMock2)).thenReturn(declarationInfoMock2);
        } catch (Exception e) {
            fail("error: can't mock scope");
        }

        var visitor = new CodeGenVisitor(scopeMock);

        var arrayDeclIDMock = mock(Template.class);
        var arrayDeclIDContext = mockForVisitorResult(UCELParser.ArrayDeclIDContext.class, arrayDeclIDMock, visitor);

        var arrayDeclIDMockModified1 = mock(Template.class);
        var arrayDeclIDMockModified2 = mock(Template.class);

        List<UCELParser.ArrayDeclIDContext> arrayDeclIDs = new ArrayList<>();
        arrayDeclIDs.add(arrayDeclIDContext);
        arrayDeclIDs.add(arrayDeclIDContext);

        Template type = new ManualTemplate("int");
        var typeContext = mockForVisitorResult(UCELParser.TypeContext.class, type, visitor);

        var node = mock(UCELParser.TypeDeclContext.class);

        when(arrayDeclIDMockModified1.toString()).thenReturn("list[10]");
        when(arrayDeclIDMockModified2.toString()).thenReturn("betterInt");
        when(arrayDeclIDMock.replaceValue("ID", "list")).thenReturn(arrayDeclIDMockModified1);
        when(arrayDeclIDMock.replaceValue("ID", "betterInt")).thenReturn(arrayDeclIDMockModified2);
        when(node.type()).thenReturn(typeContext);
        when(node.arrayDeclID()).thenReturn(arrayDeclIDs);
        when(node.arrayDeclID(0)).thenReturn(arrayDeclIDContext);
        when(node.arrayDeclID(1)).thenReturn(arrayDeclIDContext);

        node.references = declarationReferences;

        String actual = visitor.visitTypeDecl(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region prefix
    @ParameterizedTest
    @ValueSource(strings = {"urgent", "beta", "meta", "const"})
    void prefixGeneratedCorrectly(String expected) {
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.PrefixContext.class);
        when(node.getText()).thenReturn(expected);

        String actual = visitor.visitPrefix(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region initialiser

    @Test
    void initialiserExpressionGeneratedCorrectly() {
        Template expr = generateDefaultExprTemplate(Type.TypeEnum.intType);

        var visitor = new CodeGenVisitor();
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, expr, visitor);

        var node = mock(UCELParser.InitialiserContext.class);

        when(node.expression()).thenReturn(exprMock);

        String actual = visitor.visitInitialiser(node).toString();

        assertEquals(expr.toString(), actual);
    }

    @Test
    void initialiserNoExpr() {
        String expected = "{}";
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.InitialiserContext.class);

        String actual = visitor.visitInitialiser(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void initialiserGeneratedCorrectly() {
        Template expr = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("{%s, %s}", expr, expr);

        var visitor = new CodeGenVisitor();
        var initialiserMock = mockForVisitorResult(UCELParser.InitialiserContext.class, expr, visitor);
        List<UCELParser.InitialiserContext> initialiserContextList = new ArrayList<>();
        initialiserContextList.add(initialiserMock);
        initialiserContextList.add(initialiserMock);

        var node = mock(UCELParser.InitialiserContext.class);

        when(node.initialiser()).thenReturn(initialiserContextList);

        String actual = visitor.visitInitialiser(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region TypeID

    @Test
    void typeIDIDGeneratedCorrectly() {
        String variableID = "var1";
        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        var scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.TypeIDIDContext.class);
        node.reference = ref;

        when(variable.generateName(anyString())).thenReturn(variableID);
        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("could not mock scope");
        }

        String actual = visitor.visitTypeIDID(node).toString();

        assertEquals(variableID, actual);
    }

    @Test
    void typeIDTypeGeneratedCorrectly() {
        String expected = generateDefaultTypeTemplate(Type.TypeEnum.doubleType).toString();

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.TypeIDTypeContext.class);
        // Maybe set correct type, but should not matter
        node.op = new CommonToken(0, expected);

        String actual = visitor.visitTypeIDType(node).toString();

        assertEquals(expected, actual);
    }


    @Test
    void typeIDIntGeneratedCorrectly() {
        Template exprTemp = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("int[%s,%s]", exprTemp, exprTemp);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.TypeIDIntContext.class);
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemp, visitor);

        when(node.expression(0)).thenReturn(exprMock);
        when(node.expression(1)).thenReturn(exprMock);

        String actual = visitor.visitTypeIDInt(node).toString();

        assertEquals(expected, actual);
   }

    @Test
    void typeIDIntNoLeftExprGeneratedCorrectly() {
        Template exprTemp = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("int[,%s]", exprTemp);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.TypeIDIntContext.class);
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemp, visitor);

        when(node.expression(1)).thenReturn(exprMock);

        String actual = visitor.visitTypeIDInt(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void typeIDIntNoRightExprGeneratedCorrectly() {
        Template exprTemp = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("int[%s,]", exprTemp);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.TypeIDIntContext.class);
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemp, visitor);

        when(node.expression(0)).thenReturn(exprMock);

        String actual = visitor.visitTypeIDInt(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void typeIDScalarGeneratedCorrectly() {
        Template exprTemp = generateDefaultExprTemplate(Type.TypeEnum.intType);
        Template scalarTemp = generateDefaultTypeTemplate(Type.TypeEnum.scalarType);
        String expected = String.format("%s[%s]", scalarTemp, exprTemp);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.TypeIDScalarContext.class);
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemp, visitor);

        when(node.expression()).thenReturn(exprMock);

        String actual = visitor.visitTypeIDScalar(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void typeIDStructGeneratedCorrectly() {
        Template fieldDecl1Temp = new ManualTemplate("int a;");
        Template fieldDecl2Temp = new ManualTemplate("int b;");
        String expected = String.format("struct {%n%s%n%s%n}",
                fieldDecl1Temp,
                fieldDecl2Temp);

        var visitor = new CodeGenVisitor();

        var fieldDecl1Mock = mockForVisitorResult(UCELParser.FieldDeclContext.class, fieldDecl1Temp, visitor);
        var fieldDecl2Mock = mockForVisitorResult(UCELParser.FieldDeclContext.class, fieldDecl2Temp, visitor);
        List<UCELParser.FieldDeclContext> fieldDeclContextList = new ArrayList<>();
        fieldDeclContextList.add(fieldDecl1Mock);
        fieldDeclContextList.add(fieldDecl2Mock);

        var node = mock(UCELParser.TypeIDStructContext.class);

        when(node.fieldDecl()).thenReturn(fieldDeclContextList);

        String actual = visitor.visitTypeIDStruct(node).toString();

        assertEquals(expected, actual);
    }

    //endregion
    //region Type
    @Test
    void typeWithPrefixGeneratedCorrectly() {
        Template prefixTemplate = new ManualTemplate("urgent");
        Template typeIDTemplate = new ManualTemplate("int[0,10]");
        String expected = "urgent int[0,10]";

        var visitor = new CodeGenVisitor();

        var prefixMock = mockForVisitorResult(UCELParser.PrefixContext.class, prefixTemplate, visitor);
        var typeIDMock = mockForVisitorResult(UCELParser.TypeIdContext.class, typeIDTemplate, visitor);

        var node = mock(UCELParser.TypeContext.class);
        when(node.prefix()).thenReturn(prefixMock);
        when(node.typeId()).thenReturn(typeIDMock);

        String actual = visitor.visitType(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void typeNoPrefixGeneratedCorrectly() {
        Template typeIDTemplate = new ManualTemplate("int[0,10]");
        String expected = "int[0,10]";

        var visitor = new CodeGenVisitor();

        var typeIDMock = mockForVisitorResult(UCELParser.TypeIdContext.class, typeIDTemplate, visitor);

        var node = mock(UCELParser.TypeContext.class);
        when(node.typeId()).thenReturn(typeIDMock);

        String actual = visitor.visitType(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region variableDecl
    @Test
    void tariableDeclSingleVarWithType() {
        Template typeTemp = new ManualTemplate("int[0,10]");
        Template variableIdTemp = new ManualTemplate("goimer = 5");
        String expected = "int[0,10] goimer = 5;";

        var visitor = new CodeGenVisitor();

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeTemp, visitor);
        var variableIdMock = mockForVisitorResult(UCELParser.VariableIDContext.class, variableIdTemp, visitor);
        List<UCELParser.VariableIDContext> variableIDContextList = new ArrayList<>();
        variableIDContextList.add(variableIdMock);

        var node = mock(UCELParser.VariableDeclContext.class);
        when(node.variableID()).thenReturn(variableIDContextList);
        when(node.type()).thenReturn(typeMock);

        var actual = visitor.visitVariableDecl(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void variableDeclMultipleVarWithType() {
        Template typeTemp = new ManualTemplate("int[0,10]");
        Template variableIdTemp = new ManualTemplate("goimer = 5");
        // Not valid but should not matter for these tests
        String expected = "int[0,10] goimer = 5, goimer = 5, goimer = 5;";

        var visitor = new CodeGenVisitor();

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeTemp, visitor);
        var variableIdMock = mockForVisitorResult(UCELParser.VariableIDContext.class, variableIdTemp, visitor);
        List<UCELParser.VariableIDContext> variableIDContextList = new ArrayList<>();
        variableIDContextList.add(variableIdMock);
        variableIDContextList.add(variableIdMock);
        variableIDContextList.add(variableIdMock);

        var node = mock(UCELParser.VariableDeclContext.class);
        when(node.variableID()).thenReturn(variableIDContextList);
        when(node.type()).thenReturn(typeMock);

        var actual = visitor.visitVariableDecl(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void variableDeclSingleVarNoType() {
        Template variableIdTemp = new ManualTemplate("goimer = 5");
        String expected = "goimer = 5;";

        var visitor = new CodeGenVisitor();

        var variableIdMock = mockForVisitorResult(UCELParser.VariableIDContext.class, variableIdTemp, visitor);
        List<UCELParser.VariableIDContext> variableIDContextList = new ArrayList<>();
        variableIDContextList.add(variableIdMock);

        var node = mock(UCELParser.VariableDeclContext.class);
        when(node.variableID()).thenReturn(variableIDContextList);

        var actual = visitor.visitVariableDecl(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void variableDeclMultipleVarNoType() {
        Template variableIdTemp = new ManualTemplate("goimer = 5");
        // Not valid but should not matter for these tests
        String expected = "goimer = 5, goimer = 5, goimer = 5;";

        var visitor = new CodeGenVisitor();

        var variableIdMock = mockForVisitorResult(UCELParser.VariableIDContext.class, variableIdTemp, visitor);
        List<UCELParser.VariableIDContext> variableIDContextList = new ArrayList<>();
        variableIDContextList.add(variableIdMock);
        variableIDContextList.add(variableIdMock);
        variableIDContextList.add(variableIdMock);

        var node = mock(UCELParser.VariableDeclContext.class);
        when(node.variableID()).thenReturn(variableIDContextList);

        var actual = visitor.visitVariableDecl(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region variableID

    @Test
    void variableIDNoInitGeneratedCorrectly() {
        Template arrayDecl = new ManualTemplate("[10]");
        String expected = "var1[10]";
        String variableID = "var1";
        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(variableID);

        Scope scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var arrayDeclMock = mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDecl, visitor);
        List<UCELParser.ArrayDeclContext> arrayDeclContextList = new ArrayList<>();
        arrayDeclContextList.add(arrayDeclMock);

        var node = mock(UCELParser.VariableIDContext.class);
        when(node.arrayDecl()).thenReturn(arrayDeclContextList);
        when(node.arrayDecl(0)).thenReturn(arrayDeclMock);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        var actual = visitor.visitVariableID(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void variableIDWithInit() {
        Template initTemplate = new ManualTemplate("5");
        String expected = "var1 = 5";
        String variableID = "var1";
        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(variableID);

        Scope scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var initMock = mockForVisitorResult(UCELParser.InitialiserContext.class, initTemplate, visitor);

        var node = mock(UCELParser.VariableIDContext.class);
        when(node.initialiser()).thenReturn(initMock);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        var actual = visitor.visitVariableID(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void variableIDMultipleArrayDecl() {
        Template arrayDecl1 = new ManualTemplate("[10]");
        Template arrayDecl2 = new ManualTemplate("[5]");
        String expected = "var1[10][5]";
        String variableID = "var1";
        // TODO: set correct array type for declarationinfo maybe
        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(variableID);

        Scope scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var arrayDeclMock1 = mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDecl1, visitor);
        var arrayDeclMock2 = mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDecl2, visitor);
        List<UCELParser.ArrayDeclContext> arrayDeclContextList = new ArrayList<>();
        arrayDeclContextList.add(arrayDeclMock1);
        arrayDeclContextList.add(arrayDeclMock2);

        var node = mock(UCELParser.VariableIDContext.class);
        when(node.arrayDecl()).thenReturn(arrayDeclContextList);
        when(node.arrayDecl(0)).thenReturn(arrayDeclMock1);
        when(node.arrayDecl(1)).thenReturn(arrayDeclMock2);

        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        var actual = visitor.visitVariableID(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void variableIDMultipleArrayWithInitGeneratedCorrectly() {
        Template arrayDecl = new ManualTemplate("[]");
        Template initTemp = new ManualTemplate("{{1,2,3}, {1,2,3,4}}");
        String expected = "var1[][] = {{1,2,3}, {1,2,3,4}}";
        String variableID = "var1";
        // TODO: set correct array type for declarationinfo maybe
        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(variableID);

        Scope scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var arrayDeclMock = mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDecl, visitor);
        var initMock = mockForVisitorResult(UCELParser.InitialiserContext.class, initTemp, visitor);
        List<UCELParser.ArrayDeclContext> arrayDeclContextList = new ArrayList<>();
        arrayDeclContextList.add(arrayDeclMock);
        arrayDeclContextList.add(arrayDeclMock);

        var node = mock(UCELParser.VariableIDContext.class);
        when(node.arrayDecl()).thenReturn(arrayDeclContextList);
        when(node.arrayDecl(0)).thenReturn(arrayDeclMock);
        when(node.arrayDecl(1)).thenReturn(arrayDeclMock);
        when(node.initialiser()).thenReturn(initMock);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        var actual = visitor.visitVariableID(node).toString();

        assertEquals(expected, actual);
    }





    //endregion

    //region arrayDecl
    @Test
    void arrayDeclExprGeneratedCorrectly() {
        Template exprTemplate = new ManualTemplate("10");
        String expected = "[10]";
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.ArrayDeclContext.class);
        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemplate, visitor);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitArrayDecl(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void arrayDeclTypeGeneratedCorrectly() {
        Template typeTemplate = new ManualTemplate("int[0,10]");
        String expected = "[int[0,10]]";

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.ArrayDeclContext.class);
        var type = mockForVisitorResult(UCELParser.TypeContext.class, typeTemplate, visitor);

        when(node.type()).thenReturn(type);

        var actual = visitor.visitArrayDecl(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void arrayDeclNoExpr() {
        String expected = "[]";

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.ArrayDeclContext.class);

        var actual = visitor.visitArrayDecl(node).toString();

        assertEquals(expected, actual);
    }


    //endregion

    //region Assignment

    @ParameterizedTest(name = "{index} => Assignment expression {0} {2} {1}")
    @MethodSource("assignments")
    void assignmentGeneratedCorrectly(String left, String right, String op) {
        Template leftExpr = new ManualTemplate(left);
        Template rightExpr = new ManualTemplate(right);
        Template opTemp = new ManualTemplate(op);
        String expected = String.format("%s %s %s", leftExpr, opTemp, rightExpr);

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.AssignExprContext.class);
        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, leftExpr, visitor);
        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, rightExpr, visitor);
        var assign = mock(UCELParser.AssignContext.class);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(expr2);
        when(assign.getText()).thenReturn(opTemp.toString());
        when(node.assign()).thenReturn(assign);

        var actual = visitor.visitAssignExpr(node).toString();

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> assignments() {

        ArrayList<Arguments> args = new ArrayList<Arguments>();

        args.add(Arguments.arguments("var1", "1 + 2", "="));
        args.add(Arguments.arguments("var1", "true", "&="));
        args.add(Arguments.arguments("var1", "!var2", "|="));
        args.add(Arguments.arguments("var1", "1.02 - 5.2", "="));
        args.add(Arguments.arguments("var1", "false", "="));
        args.add(Arguments.arguments("var1", "goimer", "&="));

        return args.stream();
    }

    //endregion

    //region Expressions



    //region ID expression
    @ParameterizedTest(name = "{index} => ID look-up in expression for ID = \"{0}\"")
    @ValueSource(strings = {"a", "awd901", "Ada"})
    void idExprGeneratedCorrectly(String name) {
        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(name);

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IdExprContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        String actual = visitor.visitIdExpr(node).toString();

        assertEquals(name, actual);

    }

    //endregion

    //region Literal

    @ParameterizedTest(name = "{index} => generating literal for {0} ")
    @ValueSource(strings = {"1", "1.0", "0.1", "0.00005", "123456789", "0", "0.1234506789", "true", "false"})
    void literalGeneratedCorrectly(String expectedLiteral) {
        CodeGenVisitor visitor = new CodeGenVisitor();

        var node = mock(UCELParser.LiteralContext.class);
        when(node.getText()).thenReturn(expectedLiteral);

        var actual = visitor.visitLiteral(node).toString();

        assertEquals(expectedLiteral, actual);
    }
    //endregion

    //region ArrayIndex
    @Test
    void arrayIndexGeneratedCorrectly() {

        CodeGenVisitor visitor = new CodeGenVisitor();
        Template left = generateDefaultExprTemplate("abec");
        Template right = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("%s[%s]", left, right); // abc[0]

        var exprLeft = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var exprRight = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        var node = mock(UCELParser.ArrayIndexContext.class);

        when(node.expression(0)).thenReturn(exprLeft);
        when(node.expression(1)).thenReturn(exprRight);

        var actual = visitor.visitArrayIndex(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region AddSub
    @ParameterizedTest(name = "{index} => generating for expr {0} expr")
    @ValueSource(strings = {"+", "-"})
    void addSubGeneratedCorrectly(String op) {
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.AddSubContext.class);
        var opToken = mock(CommonToken.class);

        node.op = opToken;

        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(opToken.getText()).thenReturn(op);

        var actual = visitor.visitAddSub(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region MultDiv
    @ParameterizedTest(name = "{index} => generating for expr {0} expr")
    @ValueSource(strings = {"*", "/", "%"})
    void multDivGeneratedCorrectly(String op) {
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.MultDivContext.class);
        var opToken = mock(CommonToken.class);

        node.op = opToken;

        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(opToken.getText()).thenReturn(op);

        var actual = visitor.visitMultDiv(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Increment/Decrement expressions
    @Test
    void incrementPostExprGeneratedCorrectly() {
        Template exprResult = generateDefaultExprTemplate("abec");
        String expected = String.format("%s++", exprResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.IncrementPostContext.class);
        var token = mock(TerminalNode.class);

        when(token.getText()).thenReturn("++");
        when(node.INCREMENT()).thenReturn(token);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitIncrementPost(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void incrementPreExprGeneratedCorrectly() {
        Template exprResult = generateDefaultExprTemplate("abec");
        String expected = String.format("++%s", exprResult);


        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.IncrementPreContext.class);
        var token = mock(TerminalNode.class);

        when(token.getText()).thenReturn("++");
        when(node.INCREMENT()).thenReturn(token);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitIncrementPre(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void decrementPostExprGeneratedCorrectly() {
        Template exprResult = generateDefaultExprTemplate("abec");
        String expected = String.format("%s--", exprResult);


        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.DecrementPostContext.class);
        var token = mock(TerminalNode.class);

        when(token.getText()).thenReturn("--");
        when(node.DECREMENT()).thenReturn(token);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitDecrementPost(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void decrementPreExprGeneratedCorrectly() {
        Template exprResult = generateDefaultExprTemplate("abec");
        String expected = String.format("--%s", exprResult);


        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.DecrementPreContext.class);
        var token = mock(TerminalNode.class);

        when(token.getText()).thenReturn("--");
        when(node.DECREMENT()).thenReturn(token);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitDecrementPre(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region Unary expressions
    @ParameterizedTest(name = "{index} => generating for {0} expr")
    @ValueSource(strings = {"+", "-"})
    void unaryPlusMinusExprGeneratedCorrectly(String op) {
        String expected = String.format("%s%s", op, generateDefaultExprTemplate(Type.TypeEnum.intType));
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.UnaryExprContext.class);
        var unaryNode = mockForVisitorResult(UCELParser.UnaryContext.class, new ManualTemplate(op), visitor);

        when(node.expression()).thenReturn(expr);
        when(node.unary()).thenReturn(unaryNode);

        var actual = visitor.visitUnaryExpr(node).toString();

        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{index} => generating for {0} expr")
    @ValueSource(strings = {"not", "!"})
    void unaryNotNegExprGeneratedCorrectly(String op) {
        String sanitizeOp = Objects.equals(op, "not") ? op + " " : op;
        String expected = String.format("%s%s", sanitizeOp, generateDefaultExprTemplate(Type.TypeEnum.boolType));
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.UnaryExprContext.class);
        var unaryNode = mockForVisitorResult(UCELParser.UnaryContext.class, new ManualTemplate(sanitizeOp), visitor);

        when(node.expression()).thenReturn(expr);
        when(node.unary()).thenReturn(unaryNode);

        var actual = visitor.visitUnaryExpr(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region Parenthesis
    @Test
    void parenGeneratedCorrectly() {
        String expected = String.format("(%s)", generateDefaultExprTemplate(Type.TypeEnum.intType));
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.ParenContext.class);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitParen(node).toString();

        assertEquals(expected, actual);
    }


    //endregion

    //region Bitshift
    @ParameterizedTest(name = "{index} => generating for expr {0} expr")
    @ValueSource(strings = {"<<", ">>"})
    void bitshiftGeneratedCorrectly(String op) {
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.BitshiftContext.class);
        var opToken = mock(CommonToken.class);

        node.op = opToken;

        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(opToken.getText()).thenReturn(op);

        var actual = visitor.visitBitshift(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region Bit logic operators
    @Test
    void bitAndGeneratedCorrectly() {
        String op = "&";
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.BitAndContext.class);
        var token = mock(TerminalNode.class);


        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(token.getText()).thenReturn(op);
        when(node.BITAND()).thenReturn(token);

        var actual = visitor.visitBitAnd(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void bitXorGeneratedCorrectly() {
        String op = "^";
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.BitXorContext.class);
        var token = mock(TerminalNode.class);


        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(token.getText()).thenReturn(op);
        when(node.BITXOR()).thenReturn(token);

        var actual = visitor.visitBitXor(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void bitOrGeneratedCorrectly() {
        String op = "|";
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.BitOrContext.class);
        var token = mock(TerminalNode.class);


        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(token.getText()).thenReturn(op);
        when(node.BITOR()).thenReturn(token);

        var actual = visitor.visitBitOr(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region Equality
    @ParameterizedTest(name = "{index} => generating for expr {0} expr")
    @ValueSource(strings = {"!=", "=="})
    void eqGeneratedCorrectly(String op) {
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.EqExprContext.class);
        var opToken = mock(CommonToken.class);

        node.op = opToken;

        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(opToken.getText()).thenReturn(op);

        var actual = visitor.visitEqExpr(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region MinMax
    @ParameterizedTest(name = "{index} => generating for expr {0} expr")
    @ValueSource(strings = {"<?", ">?"})
    void minMaxGeneratedCorrectly(String op) {
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.MinMaxContext.class);
        var opToken = mock(CommonToken.class);

        node.op = opToken;

        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(opToken.getText()).thenReturn(op);

        var actual = visitor.visitMinMax(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Relational expressions
    @ParameterizedTest(name = "{index} => generating for expr {0} expr")
    @ValueSource(strings = {"<", "<=", ">", ">="})
    void relExprGeneratedCorrectly(String op) {
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.RelExprContext.class);
        var opToken = mock(CommonToken.class);

        node.op = opToken;

        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(opToken.getText()).thenReturn(op);

        var actual = visitor.visitRelExpr(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Logical expressions
    @ParameterizedTest(name = "{index} => generating for expr {0} expr")
    @ValueSource(strings = {"&&", "and"})
    void logAndGeneratedCorrectly(String op) {
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.LogAndContext.class);
        var opToken = mock(CommonToken.class);

        node.op = opToken;

        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(opToken.getText()).thenReturn(op);

        var actual = visitor.visitLogAnd(node).toString();

        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{index} => generating for expr {0} expr")
    @ValueSource(strings = {"||", "or", "imply"})
    void logOrGeneratedCorrectly(String op) {
        String expected = String.format("0 %s 0", op);
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.LogOrContext.class);
        var opToken = mock(CommonToken.class);

        node.op = opToken;

        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(opToken.getText()).thenReturn(op);

        var actual = visitor.visitLogOr(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Conditional expression
    @Test
    void conditionalExpressionGeneratedCorrectly() {
        Template intResult = generateDefaultExprTemplate(Type.TypeEnum.intType);
        Template boolResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        String expected = String.format("%s ? %s : %s", boolResult, intResult, intResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var intExpr = mockForVisitorResult(UCELParser.ExpressionContext.class, intResult, visitor);
        var boolExpr = mockForVisitorResult(UCELParser.ExpressionContext.class, boolResult, visitor);
        var node = mock(UCELParser.ConditionalContext.class);

        when(node.expression(0)).thenReturn(boolExpr);
        when(node.expression(1)).thenReturn(intExpr);
        when(node.expression(2)).thenReturn(intExpr);

        var actual = visitor.visitConditional(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region Mark expression
    @ParameterizedTest(name = "{index} => generating for mark expr type {0}")
    @EnumSource(value = Type.TypeEnum.class, names ={"intType", "doubleType", "boolType"})
    void markExpressionGeneratedCorrectly(Type.TypeEnum type) {
        var exprTemplate = generateDefaultExprTemplate(type);
        var expected = exprTemplate + "'";

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemplate, visitor);
        var node = mock(UCELParser.MarkExprContext.class);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitMarkExpr(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void markExpressionIDGeneratedCorrectly() {
        var exprTemplate = generateDefaultExprTemplate("abc");
        var expected = exprTemplate + "'";

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemplate, visitor);
        var node = mock(UCELParser.MarkExprContext.class);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitMarkExpr(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region Verification expression
    @Test
    void verificationGeneratedCorrectly() {
        var verificationTemplate = generateDefaultVerificationTemplate();
        var expected = verificationTemplate.toString();

        CodeGenVisitor visitor = new CodeGenVisitor();

        var node = mock(UCELParser.VerificationExprContext.class);
        var verification = mockForVisitorResult(UCELParser.VerificationContext.class, verificationTemplate, visitor);

        when(node.verification()).thenReturn(verification);

        var actual = visitor.visitVerificationExpr(node).toString();

        assertEquals(expected, actual);

    }

    @ParameterizedTest(name = "{index} => generating for verification expr {0}")
    @ValueSource(strings = {"forall", "exists", "sum"})
    void verificationExpressionGeneratedCorrectly(String op) {
        var name = "abc";
        var typeTemplate = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var exprTemplate = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        var expected = String.format("%s (%s:%s) %s", op, name, typeTemplate, exprTemplate);
        var opToken = mock(CommonToken.class);

        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        when(variable.generateName(anyString())).thenReturn(name);

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.VerificationContext.class);
        node.reference = ref;
        node.op = opToken;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemplate, visitor);
        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeTemplate, visitor);

        when(node.expression()).thenReturn(exprMock);
        when(node.type()).thenReturn(typeMock);
        when(opToken.getText()).thenReturn(op);

        String actual = visitor.visitVerification(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //endregion

    //region Control structure
    //region If-statement
    @Test
    void ifStatementNoElseCorrectlyGenerated() {
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("if (%s) %s", exprResult, stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.IfstatementContext.class);

        when(node.expression()).thenReturn(expr);
        when(node.statement(0)).thenReturn(stmnt);

        var actual = visitor.visitIfstatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void ifStatementWithElseCorrectlyGenerated() {
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmnt1Result = generateDefaultStatementTemplate();
        Template stmnt2Result = generateDefaultStatementTemplate();
        String expected = String.format("if (%s) %s else %s", exprResult, stmnt1Result, stmnt2Result);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var stmnt1 = mockForVisitorResult(UCELParser.StatementContext.class, stmnt1Result, visitor);
        var stmnt2 = mockForVisitorResult(UCELParser.StatementContext.class, stmnt2Result, visitor);
        var node = mock(UCELParser.IfstatementContext.class);

        when(node.expression()).thenReturn(expr);
        when(node.statement(0)).thenReturn(stmnt1);
        when(node.statement(1)).thenReturn(stmnt2);

        var actual = visitor.visitIfstatement(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region While-loop
    @Test
    void whileStatementCorrectlyGenerated() {
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("while (%s) %s", exprResult, stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.WhileLoopContext.class);

        when(node.expression()).thenReturn(expr);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitWhileLoop(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region Do-while-loop
    @Test
    void doWhileStatementCorrectlyGenerated() {
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("do %s while (%s);", stmntResult, exprResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.DowhileContext.class);

        when(node.expression()).thenReturn(expr);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitDowhile(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region For-loop
    // FOR LEFTPAR assignment? END expression? END expression? RIGHTPAR statement;
    @Test
    void forLoopStatementGeneratedCorrectly() {
        Template expr1Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template expr2Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template assignResult = generateDefaultAssignmentTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", assignResult, expr1Result, expr2Result, stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var assign = mockForVisitorResult(UCELParser.AssignmentContext.class, assignResult, visitor);
        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Result, visitor);
        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(expr2);
        when(node.assignment()).thenReturn(assign);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyAssignGeneratedCorrectly() {
        Template expr1Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template expr2Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", generateEmptyAssignTemplate(), expr1Result, expr2Result, stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Result, visitor);
        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(expr2);
        when(node.assignment()).thenReturn(null);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void forLoopEmptyExpr1GeneratedCorrectly() {
        Template expr1Result = generateEmptyExprTemplate();
        Template expr2Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template assignResult = generateDefaultAssignmentTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", assignResult, expr1Result, expr2Result, stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var assign = mockForVisitorResult(UCELParser.AssignmentContext.class, assignResult, visitor);
        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(null);
        when(node.expression(1)).thenReturn(expr2);
        when(node.assignment()).thenReturn(assign);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyExpr2GeneratedCorrectly() {
        Template expr1Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template expr2Result = generateEmptyExprTemplate();
        Template assignResult = generateDefaultAssignmentTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", assignResult, expr1Result, expr2Result, stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var assign = mockForVisitorResult(UCELParser.AssignmentContext.class, assignResult, visitor);
        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(null);
        when(node.assignment()).thenReturn(assign);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyExpr1Expr2GeneratedCorrectly() {
        Template assignResult = generateDefaultAssignmentTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", assignResult, generateEmptyExprTemplate(), generateEmptyExprTemplate(), stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var assign = mockForVisitorResult(UCELParser.AssignmentContext.class, assignResult, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(null);
        when(node.expression(1)).thenReturn(null);
        when(node.assignment()).thenReturn(assign);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyAssignExpr1GeneratedCorrectly() {
        Template expr2Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", generateEmptyAssignTemplate(), generateEmptyExprTemplate(), expr2Result, stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(null);
        when(node.expression(1)).thenReturn(expr2);
        when(node.assignment()).thenReturn(null);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyAssignExpr2GeneratedCorrectly() {
        Template expr1Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", generateEmptyAssignTemplate(), expr1Result, generateEmptyExprTemplate(), stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(null);
        when(node.assignment()).thenReturn(null);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyAssignExprsGeneratedCorrectly() {
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", generateEmptyAssignTemplate(), generateEmptyExprTemplate(), generateEmptyExprTemplate(), stmntResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(null);
        when(node.expression(1)).thenReturn(null);
        when(node.assignment()).thenReturn(null);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Iteration
    //FOR LEFTPAR ID? COLON type? RIGHTPAR statement;
    @Test
    void iterationGeneratedCorrectly() {
        var idResult = new ManualTemplate("name");
        var typeResult = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var stmntResult = generateDefaultStatementTemplate();
        var expected = String.format("for (%s:%s) %s", idResult, typeResult, stmntResult);

        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);
        
        when(variable.generateName(anyString())).thenReturn(idResult.toString());

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IterationContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeResult, visitor);
        var stmntMock = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);

        when(node.type()).thenReturn(typeMock);
        when(node.statement()).thenReturn(stmntMock);

        String actual = visitor.visitIteration(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void iterationEmptyIDGeneratedCorrectly() {
        var idResult = new ManualTemplate("");
        var typeResult = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var stmntResult = generateDefaultStatementTemplate();
        var expected = String.format("for (%s:%s) %s", idResult, typeResult, stmntResult);

        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        when(variable.generateName()).thenReturn(idResult.toString());

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IterationContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeResult, visitor);
        var stmntMock = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);

        when(node.type()).thenReturn(typeMock);
        when(node.statement()).thenReturn(stmntMock);

        String actual = visitor.visitIteration(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void iterationEmptyTypeGeneratedCorrectly() {
        var idResult = new ManualTemplate("");
        var typeResult = new ManualTemplate("");
        var stmntResult = generateDefaultStatementTemplate();
        var expected = String.format("for (%s:%s) %s", idResult, typeResult, stmntResult);

        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        when(variable.generateName()).thenReturn(idResult.toString());

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IterationContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeResult, visitor);
        var stmntMock = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);

        when(node.type()).thenReturn(typeMock);
        when(node.statement()).thenReturn(stmntMock);

        String actual = visitor.visitIteration(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void iterationEmptyIDEmptyTypeGeneratedCorrectly() {
        var idResult = new ManualTemplate("");
        var typeResult = new ManualTemplate("");
        var stmntResult = generateDefaultStatementTemplate();
        var expected = String.format("for (%s:%s) %s", idResult, typeResult, stmntResult);

        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        when(variable.generateName()).thenReturn(idResult.toString());

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IterationContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeResult, visitor);
        var stmntMock = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);

        when(node.type()).thenReturn(typeMock);
        when(node.statement()).thenReturn(stmntMock);

        String actual = visitor.visitIteration(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //region Return statement
    @ParameterizedTest(name = "{index} => generating for return type {0}")
    @EnumSource(value = Type.TypeEnum.class, names ={"intType", "doubleType", "boolType"})
    void returnStatementGeneratedCorrectly(Type.TypeEnum type) {
        var exprResult = generateDefaultExprTemplate(type);
        var expected = String.format("return %s;", exprResult);

        var node = mock(UCELParser.ReturnStatementContext.class);
        CodeGenVisitor visitor = new CodeGenVisitor();

        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);

        when(node.expression()).thenReturn(exprMock);

        String actual = visitor.visitReturnStatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void returnStatementEmptyExprGeneratedCorrectly() {
        var expected = "return;";

        var node = mock(UCELParser.ReturnStatementContext.class);
        CodeGenVisitor visitor = new CodeGenVisitor();

        String actual = visitor.visitReturnStatement(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Block
    @Test
    void blockEmptyGeneratedCorrectly() {
        Template blockResult = generateDefaultStatementTemplate();
        var expected = blockResult.toString();

        CodeGenVisitor visitor = new CodeGenVisitor();

        var node = mock(UCELParser.BlockContext.class);
        var scopeMock = mock(Scope.class);

        when(scopeMock.getParent()).thenReturn(null);
        node.scope = scopeMock;

        var actual = visitor.visitBlock(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void blockOneLocalDeclNoStatementGeneratedCorrectly() {
        Template localDeclTemplate = generateDefaultLocalDeclaration(Type.TypeEnum.intType, "abc");
        Template blockResult = generateDefaultStatementTemplate(localDeclTemplate.toString(), "", true);
        var expected = blockResult.toString();

        CodeGenVisitor visitor = new CodeGenVisitor();

        var localDecls = new ArrayList<UCELParser.LocalDeclarationContext>()
            {{ add(mockForVisitorResult(UCELParser.LocalDeclarationContext.class, localDeclTemplate, visitor)); }};
        var node = mock(UCELParser.BlockContext.class);
        var scopeMock = mock(Scope.class);

        when(node.localDeclaration()).thenReturn(localDecls);
        when(scopeMock.getParent()).thenReturn(null);
        node.scope = scopeMock;


        var actual = visitor.visitBlock(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void blockNoLocalDeclOneStatementGeneratedCorrectly() {
        Template statementTemplate = generateDefaultNonBlockStatementTemplate();
        Template blockResult = generateDefaultStatementTemplate("", statementTemplate.toString(), false);
        var expected = blockResult.toString();

        CodeGenVisitor visitor = new CodeGenVisitor();

        var statements = new ArrayList<UCELParser.StatementContext>()
        {{ add(mockForVisitorResult(UCELParser.StatementContext.class, statementTemplate, visitor)); }};
        var node = mock(UCELParser.BlockContext.class);
        var scopeMock = mock(Scope.class);

        when(node.statement()).thenReturn(statements);
        when(scopeMock.getParent()).thenReturn(null);
        node.scope = scopeMock;

        var actual = visitor.visitBlock(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void blockOneLocalDeclOneStatementGeneratedCorrectly() {
        Template localDeclTemplate = generateDefaultLocalDeclaration(Type.TypeEnum.intType, "abc");
        Template statementTemplate = generateDefaultNonBlockStatementTemplate();
        Template blockResult = generateDefaultStatementTemplate(localDeclTemplate.toString(), statementTemplate.toString(), true);
        var expected = blockResult.toString();

        CodeGenVisitor visitor = new CodeGenVisitor();

        var localDecls = new ArrayList<UCELParser.LocalDeclarationContext>()
            {{ add(mockForVisitorResult(UCELParser.LocalDeclarationContext.class, localDeclTemplate, visitor)); }};
        var statements = new ArrayList<UCELParser.StatementContext>()
            {{ add(mockForVisitorResult(UCELParser.StatementContext.class, statementTemplate, visitor)); }};
        var node = mock(UCELParser.BlockContext.class);
        var scopeMock = mock(Scope.class);

        when(node.statement()).thenReturn(statements);
        when(node.localDeclaration()).thenReturn(localDecls);
        when(scopeMock.getParent()).thenReturn(scopeMock);
        node.scope = scopeMock;

        var actual = visitor.visitBlock(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void blockMultiLocalDeclOneStatementGeneratedCorrectly() {
        var localDeclTemplates = new ArrayList<Template>()
        {{
            add(generateDefaultLocalDeclaration(Type.TypeEnum.intType, "abc1"));
            add(generateDefaultLocalDeclaration(Type.TypeEnum.intType, "abc2"));
        }};
        var statementTemplates = new ArrayList<Template>() {{
            add(generateDefaultNonBlockStatementTemplate());
        }};
        Template blockResult = generateDefaultStatementTemplate(localDeclTemplates, statementTemplates, true);
        var expected = blockResult.toString();

        CodeGenVisitor visitor = new CodeGenVisitor();

        var localDecls = new ArrayList<UCELParser.LocalDeclarationContext>()
        {{
            add(mockForVisitorResult(UCELParser.LocalDeclarationContext.class, localDeclTemplates.get(0), visitor));
            add(mockForVisitorResult(UCELParser.LocalDeclarationContext.class, localDeclTemplates.get(1), visitor));
        }};
        var statements = new ArrayList<UCELParser.StatementContext>()
        {{ add(mockForVisitorResult(UCELParser.StatementContext.class, statementTemplates.get(0), visitor)); }};
        var node = mock(UCELParser.BlockContext.class);
        var scopeMock = mock(Scope.class);

        when(node.statement()).thenReturn(statements);
        when(node.localDeclaration()).thenReturn(localDecls);
        when(scopeMock.getParent()).thenReturn(null);
        node.scope = scopeMock;

        var actual = visitor.visitBlock(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void blockOneLocalDeclMultiStatementGeneratedCorrectly() {
        var localDeclTemplates = new ArrayList<Template>()
        {{
            add(generateDefaultLocalDeclaration(Type.TypeEnum.intType, "abc"));
        }};
        var statementTemplates = new ArrayList<Template>() {{
            add(generateDefaultNonBlockStatementTemplate());
            add(generateDefaultNonBlockStatementTemplate());
        }};
        Template blockResult = generateDefaultStatementTemplate(localDeclTemplates, statementTemplates, true);
        var expected = blockResult.toString();

        CodeGenVisitor visitor = new CodeGenVisitor();

        var localDecls = new ArrayList<UCELParser.LocalDeclarationContext>()
        {{
            add(mockForVisitorResult(UCELParser.LocalDeclarationContext.class, localDeclTemplates.get(0), visitor));
        }};
        var statements = new ArrayList<UCELParser.StatementContext>()
        {{
            add(mockForVisitorResult(UCELParser.StatementContext.class, statementTemplates.get(0), visitor));
            add(mockForVisitorResult(UCELParser.StatementContext.class, statementTemplates.get(1), visitor));
        }};
        var node = mock(UCELParser.BlockContext.class);
        var scopeMock = mock(Scope.class);

        when(node.statement()).thenReturn(statements);
        when(node.localDeclaration()).thenReturn(localDecls);
        when(scopeMock.getParent()).thenReturn(null);
        node.scope = scopeMock;

        var actual = visitor.visitBlock(node).toString();

        assertEquals(expected, actual);
    }
    @Test
    void blockMultiLocalDeclMultiStatementGeneratedCorrectly() {
        var localDeclTemplates = new ArrayList<Template>()
        {{
            add(generateDefaultLocalDeclaration(Type.TypeEnum.intType, "abc1"));
            add(generateDefaultLocalDeclaration(Type.TypeEnum.intType, "abc2"));
        }};
        var statementTemplates = new ArrayList<Template>() {{
            add(generateDefaultNonBlockStatementTemplate());
            add(generateDefaultNonBlockStatementTemplate());
        }};
        Template blockResult = generateDefaultStatementTemplate(localDeclTemplates, statementTemplates, true);
        var expected = blockResult.toString();

        CodeGenVisitor visitor = new CodeGenVisitor();

        var localDecls = new ArrayList<UCELParser.LocalDeclarationContext>()
        {{
            add(mockForVisitorResult(UCELParser.LocalDeclarationContext.class, localDeclTemplates.get(0), visitor));
            add(mockForVisitorResult(UCELParser.LocalDeclarationContext.class, localDeclTemplates.get(1), visitor));
        }};
        var statements = new ArrayList<UCELParser.StatementContext>()
        {{
            add(mockForVisitorResult(UCELParser.StatementContext.class, statementTemplates.get(0), visitor));
            add(mockForVisitorResult(UCELParser.StatementContext.class, statementTemplates.get(1), visitor));
        }};
        var node = mock(UCELParser.BlockContext.class);
        var scopeMock = mock(Scope.class);

        when(node.statement()).thenReturn(statements);
        when(node.localDeclaration()).thenReturn(localDecls);
        when(scopeMock.getParent()).thenReturn(null);
        node.scope = scopeMock;

        var actual = visitor.visitBlock(node).toString();

        assertEquals(expected, actual);
    }
    //endregion

    //region Statement
    @Test
    void statementBlockGeneratedCorrectly() {
        var blockResult = generateDefaultStatementTemplate();
        var expected = blockResult.toString();

        CodeGenVisitor visitor = new CodeGenVisitor();

        var blockMock = mockForVisitorResult(UCELParser.BlockContext.class, blockResult, visitor);
        var node = mock(UCELParser.StatementContext.class);

        when(node.block()).thenReturn(blockMock);

        var actual = visitor.visitStatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void statementExpressionGeneratedCorrectly() {
        var exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);
        var expected = String.format("%s;%n", exprResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.StatementContext.class);

        when(node.expression()).thenReturn(exprMock);

        var actual = visitor.visitStatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void statementEmptyExpressionGeneratedCorrectly() {
        var expected = String.format(";%n");

        CodeGenVisitor visitor = new CodeGenVisitor();
        var node = mock(UCELParser.StatementContext.class);

        var actual = visitor.visitStatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void statementForLoopGeneratedCorrectly() {
        var forloopResult = generateDefaultForLoopTemplate();
        var expected = String.format("%s%n", forloopResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var forloopMock = mockForVisitorResult(UCELParser.ForLoopContext.class, forloopResult, visitor);
        var node = mock(UCELParser.StatementContext.class);

        when(node.forLoop()).thenReturn(forloopMock);

        var actual = visitor.visitStatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void statementIterationGeneratedCorrectly() {
        var iterationResult = generateDefaultIterationTemplate();
        var expected = String.format("%s%n", iterationResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var iterationMock = mockForVisitorResult(UCELParser.IterationContext.class, iterationResult, visitor);
        var node = mock(UCELParser.StatementContext.class);

        when(node.iteration()).thenReturn(iterationMock);

        var actual = visitor.visitStatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void statementWhileLoopGeneratedCorrectly() {
        var whileResult = generateDefaultWhileLoopTemplate();
        var expected = String.format("%s%n", whileResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var whileMock = mockForVisitorResult(UCELParser.WhileLoopContext.class, whileResult, visitor);
        var node = mock(UCELParser.StatementContext.class);

        when(node.whileLoop()).thenReturn(whileMock);

        var actual = visitor.visitStatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void statementDoWhileGeneratedCorrectly() {
        var doWhileResult = generateDefaultDoWhileLoopTemplate();
        var expected = String.format("%s%n", doWhileResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var doWhileMock = mockForVisitorResult(UCELParser.DowhileContext.class, doWhileResult, visitor);
        var node = mock(UCELParser.StatementContext.class);

        when(node.dowhile()).thenReturn(doWhileMock);

        var actual = visitor.visitStatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void statementIfStatementGeneratedCorrectly() {
        var ifResult = generateDefaultIfStatementTemplate();
        var expected = String.format("%s%n", ifResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var ifMock = mockForVisitorResult(UCELParser.IfstatementContext.class, ifResult, visitor);
        var node = mock(UCELParser.StatementContext.class);

        when(node.ifstatement()).thenReturn(ifMock);

        var actual = visitor.visitStatement(node).toString();

        assertEquals(expected, actual);
    }

    @Test
    void statementReturnStatementGeneratedCorrectly() {
        var returnResult = generateDefaultReturnStatementTemplate();
        var expected = String.format("%s%n", returnResult);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var returnMock = mockForVisitorResult(UCELParser.ReturnStatementContext.class, returnResult, visitor);
        var node = mock(UCELParser.StatementContext.class);

        when(node.returnStatement()).thenReturn(returnMock);

        var actual = visitor.visitStatement(node).toString();

        assertEquals(expected, actual);
    }

    //endregion

    //endregion

    //region Helper functions
    private<T extends RuleContext> T mockForVisitorResult(final Class<T> nodeType, final Template visitTemplateResult, CodeGenVisitor visitor) {
        final T mock = mock(nodeType);
        when(mock.accept(visitor)).thenReturn(visitTemplateResult);
        return mock;
    }

    private Template generateDefaultExprTemplate(Type.TypeEnum type) {
        return switch (type) {
            case intType -> new ManualTemplate("0");
            case boolType -> new ManualTemplate("true");
            case doubleType -> new ManualTemplate("0.0");
            case charType -> new ManualTemplate("'a'");
            case stringType -> new ManualTemplate("\"abc\"");
            default -> new ManualTemplate("");
        };
    }

    private Template generateDefaultDeclarationTemplate(String id, String expr) {
        return new ManualTemplate(String.format("%s\n", generateDefaultVariableDeclTemplate(id, expr)));
    }

    private Template generateDefaultSystemTemplate(String id) {
        return new ManualTemplate(String.format("system %s;", id));
    }

    private Template generateEmptyExprTemplate() {
        return new ManualTemplate("");
    }
    private Template generateDefaultVerificationTemplate() {
        return new ManualTemplate("forall (abc:int[0,4]) true");
    }
    private Template generateDefaultStatementTemplate() {
        return new ManualTemplate(String.format("{%n}"));
    }
    private Template generateDefaultStatementTemplate(String localDecls, String statements, boolean withNewline) {
        return new ManualTemplate(String.format("{%n%s%s%s}", localDecls, withNewline ? String.format("%n") : "", statements));
    }
    private Template generateDefaultStatementTemplate(List<Template> localDecls, List<Template> statements, boolean withNewline) {
        var builder = new StringBuilder();
        builder.append(String.format("{%n"));
        for (var decl : localDecls) {
            builder.append(String.format("%s%n", decl));
        }
        for (var st : statements) {
            builder.append(String.format("%s", st));
        }
        builder.append("}");

        return new ManualTemplate(builder.toString());
    }

    private Template generateDefaultNonBlockStatementTemplate() {
        return new ManualTemplate(String.format("a = a;%n"));
    }
    private Template generateDefaultForLoopTemplate() {
        return new ManualTemplate(String.format("for (i = 0;i < 10;i++) {%n}"));
    }
    private Template generateDefaultIterationTemplate() {
        return new ManualTemplate(String.format("for (i:int) {%n}"));
    }
    private Template generateDefaultWhileLoopTemplate() {
        return new ManualTemplate(String.format("while (true) {%n}"));
    }
    private Template generateDefaultDoWhileLoopTemplate() {
        return new ManualTemplate(String.format("do {%n} while (true)"));
    }
    private Template generateDefaultIfStatementTemplate() {
        return new ManualTemplate(String.format("if (true) {%n}"));
    }
    private Template generateDefaultReturnStatementTemplate() {
        return new ManualTemplate("return 1;");
    }
    private Template generateDefaultTypeTemplate(Type.TypeEnum type) {
        return switch (type) {
            case intType -> new ManualTemplate("int");
            case boolType -> new ManualTemplate("bool");
            case doubleType -> new ManualTemplate("double");
            case charType -> new ManualTemplate("char");
            case stringType -> new ManualTemplate("char[]");
            case scalarType -> new ManualTemplate("scalar");
            default -> new ManualTemplate("");
        };
    }

    private Template generateDefaultChanExprTemplate(String id) {
        return new ManualTemplate(String.format("%s", id));
    }

    private Template generateDefaultTypeDeclTemplate(String id, String size) {
        return new ManualTemplate(String.format("typedef int %s[%s];", id, size));
    }

    private Template generateDefaultVariableDeclTemplate(String id, String expr) {
        return new ManualTemplate(String.format("%s = %s;", id, expr));
    }

    private Template generateDefaultParametersTemplate(String type, String id) {
        return new ManualTemplate(String.format("%s %s", type, id));
    }

    private Template generateDefaultArrayDeclTemplate() {
        return new ManualTemplate("[]");
    }

    private Template generateDefaultArgumentsTemplate(Type.TypeEnum type) {
        return generateDefaultExprTemplate(type);
    }

    private Template generateDefaultLocalDeclaration(Type.TypeEnum type, String id) {
        return switch (type) {
            case intType -> new ManualTemplate(String.format("int %s;", id));
            case boolType -> new ManualTemplate(String.format("bool %s;", id));
            case doubleType -> new ManualTemplate(String.format("double %s;", id));
            case charType -> new ManualTemplate(String.format("char %s;", id));
            case stringType -> new ManualTemplate(String.format("char[] %s;", id));
            default -> new ManualTemplate("");
        };
    }
    private Template generateDefaultExprTemplate(String id) {
        return new ManualTemplate(id);
    }

    private Template generateDefaultAssignmentTemplate(Type.TypeEnum type) {
        return switch (type) {
            case intType -> new ManualTemplate("a = 0");
            case boolType -> new ManualTemplate("b = true");
            case doubleType -> new ManualTemplate("d = 0.0");
            case charType -> new ManualTemplate("c = 'a'");
            case stringType -> new ManualTemplate("s = \"abc\"");
            default -> new ManualTemplate("");
        };
    }

    private Template generateDefaultLiteralTemplate(Type.TypeEnum type) {
        return switch (type) {
            case intType -> new ManualTemplate("0");
            case boolType -> new ManualTemplate("true");
            case doubleType -> new ManualTemplate("0.0");
            case charType -> new ManualTemplate("'a'");
            case stringType -> new ManualTemplate("\"abc\"");
            default -> throw new IllegalArgumentException("Invalid type");
        };
    }

    private Template generateEmptyAssignTemplate() {
        return new ManualTemplate("");
    }

    private PDeclarationTemplate generateDefaultPDeclTemplate() {
        return new PDeclarationTemplate(generateDefaultDeclarationTemplate("a", "4"));
    }

    private PSystemTemplate generateDefaultPSystemTemplate() {
        return new PSystemTemplate(generateDefaultDeclarationTemplate("a", "4"), generateDefaultSystemTemplate("a"));
    }

    private PTemplateTemplate generateDefaultPTemplateTemplate() {
        return new PTemplateTemplate();
    }

    private GraphTemplate generateDefaultGraphTemplate() {
        return new GraphTemplate(new ArrayList<>(), new ArrayList<>());
    }

    private Template generateDefaultExponentialTemplate() {
        return new ManualTemplate("1 : 2");
    }

    private Template generateDefaultInvariantTemplate() {
        return new ManualTemplate("a == 5;");
    }

    private Template generateDefaultSelectTemplateMultiple() {
        return new ManualTemplate("c : int, d : int");
    }

    private Template generateDefaultSelectTemplateSingle() {
        return new ManualTemplate("b : int");
    }
    //endregion
}
