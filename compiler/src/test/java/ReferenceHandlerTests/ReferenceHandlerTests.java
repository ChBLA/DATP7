package ReferenceHandlerTests;

import org.UcelParser.ReferenceHandler.ReferenceVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.DeclarationInfo;
import org.UcelParser.Util.DeclarationReference;
import org.UcelParser.Util.FuncCallOccurrence;
import org.UcelParser.Util.Scope;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.*;
import java.sql.Array;
import java.sql.Ref;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class ReferenceHandlerTests {


    //region Component Extension
    //region Component
    @Test
    void componentSetsScopeAndAddsItselfToParentScope() {
        String name = "Example";

        var parentScope = mock(Scope.class);

        ReferenceVisitor visitor = new ReferenceVisitor(parentScope);
        var node = mock(UCELParser.ComponentContext.class);
        var paramNode = mock(UCELParser.ParametersContext.class);
        var interfaceNode = mock(UCELParser.InterfacesContext.class);
        var compBodyNode = mock(UCELParser.CompBodyContext.class);

        var idNode = mock(TerminalNode.class);
        var declRef = mock(DeclarationReference.class);

        when(node.parameters()).thenReturn(paramNode);
        when(node.interfaces()).thenReturn(interfaceNode);
        when(node.compBody()).thenReturn(compBodyNode);
        when(idNode.getText()).thenReturn(name);
        when(node.ID()).thenReturn(idNode);
        when(parentScope.add(any())).thenReturn(declRef);
        when(parentScope.isUnique(name, false)).thenReturn(true);

        when(paramNode.accept(visitor)).thenReturn(true);
        when(interfaceNode.accept(visitor)).thenReturn(true);
        when(compBodyNode.accept(visitor)).thenReturn(true);
        var actual = visitor.visitComponent(node);

        assertTrue(actual);
        assertEquals(declRef, node.reference);
        assertNotNull(node.scope);
    }

    @Test
    void componentNoParametersStillSuccess() {
        String name = "Example";

        var parentScope = mock(Scope.class);

        ReferenceVisitor visitor = new ReferenceVisitor(parentScope);
        var node = mock(UCELParser.ComponentContext.class);
        var interfaceNode = mock(UCELParser.InterfacesContext.class);
        var compBodyNode = mock(UCELParser.CompBodyContext.class);

        var idNode = mock(TerminalNode.class);
        var declRef = mock(DeclarationReference.class);

        when(node.interfaces()).thenReturn(interfaceNode);
        when(node.compBody()).thenReturn(compBodyNode);
        when(idNode.getText()).thenReturn(name);
        when(node.ID()).thenReturn(idNode);
        when(parentScope.add(any())).thenReturn(declRef);
        when(parentScope.isUnique(name, false)).thenReturn(true);

        when(interfaceNode.accept(visitor)).thenReturn(true);
        when(compBodyNode.accept(visitor)).thenReturn(true);
        var actual = visitor.visitComponent(node);

        assertTrue(actual);
        assertEquals(declRef, node.reference);
        assertNotNull(node.scope);
    }

    @Test
    void componentNotUnique() {
        String name = "Example";

        var parentScope = mock(Scope.class);

        ReferenceVisitor visitor = new ReferenceVisitor(parentScope);
        var node = mock(UCELParser.ComponentContext.class);
        var paramNode = mock(UCELParser.ParametersContext.class);
        var interfaceNode = mock(UCELParser.InterfacesContext.class);
        var compBodyNode = mock(UCELParser.CompBodyContext.class);

        var idNode = mock(TerminalNode.class);
        var declRef = mock(DeclarationReference.class);

        when(node.parameters()).thenReturn(paramNode);
        when(node.interfaces()).thenReturn(interfaceNode);
        when(node.compBody()).thenReturn(compBodyNode);
        when(idNode.getText()).thenReturn(name);
        when(node.ID()).thenReturn(idNode);
        when(parentScope.add(any())).thenReturn(declRef);
        when(parentScope.isUnique(name, false)).thenReturn(false);

        when(paramNode.accept(visitor)).thenReturn(false);
        when(interfaceNode.accept(visitor)).thenReturn(true);
        when(compBodyNode.accept(visitor)).thenReturn(true);
        var actual = visitor.visitComponent(node);

        assertFalse(actual);
        verify(paramNode, never()).accept(visitor);
        verify(interfaceNode, never()).accept(visitor);
        verify(compBodyNode, never()).accept(visitor);
    }

    @Test
    void componentParametersFails() {
        String name = "Example";

        var parentScope = mock(Scope.class);

        ReferenceVisitor visitor = new ReferenceVisitor(parentScope);
        var node = mock(UCELParser.ComponentContext.class);
        var paramNode = mock(UCELParser.ParametersContext.class);
        var interfaceNode = mock(UCELParser.InterfacesContext.class);
        var compBodyNode = mock(UCELParser.CompBodyContext.class);

        var idNode = mock(TerminalNode.class);
        var declRef = mock(DeclarationReference.class);

        when(node.parameters()).thenReturn(paramNode);
        when(node.interfaces()).thenReturn(interfaceNode);
        when(node.compBody()).thenReturn(compBodyNode);
        when(idNode.getText()).thenReturn(name);
        when(node.ID()).thenReturn(idNode);
        when(parentScope.add(any())).thenReturn(declRef);
        when(parentScope.isUnique(name, false)).thenReturn(true);

        when(paramNode.accept(visitor)).thenReturn(false);
        when(interfaceNode.accept(visitor)).thenReturn(true);
        when(compBodyNode.accept(visitor)).thenReturn(true);
        var actual = visitor.visitComponent(node);

        assertFalse(actual);
        verify(interfaceNode, never()).accept(visitor);
        verify(compBodyNode, never()).accept(visitor);
    }
    @Test
    void componentInterfacesFails() {
        String name = "Example";

        var parentScope = mock(Scope.class);

        ReferenceVisitor visitor = new ReferenceVisitor(parentScope);
        var node = mock(UCELParser.ComponentContext.class);
        var paramNode = mock(UCELParser.ParametersContext.class);
        var interfaceNode = mock(UCELParser.InterfacesContext.class);
        var compBodyNode = mock(UCELParser.CompBodyContext.class);

        var idNode = mock(TerminalNode.class);
        var declRef = mock(DeclarationReference.class);

        when(node.parameters()).thenReturn(paramNode);
        when(node.interfaces()).thenReturn(interfaceNode);
        when(node.compBody()).thenReturn(compBodyNode);
        when(idNode.getText()).thenReturn(name);
        when(node.ID()).thenReturn(idNode);
        when(parentScope.add(any())).thenReturn(declRef);
        when(parentScope.isUnique(name, false)).thenReturn(true);

        when(paramNode.accept(visitor)).thenReturn(true);
        when(interfaceNode.accept(visitor)).thenReturn(false);
        when(compBodyNode.accept(visitor)).thenReturn(true);
        var actual = visitor.visitComponent(node);

        assertFalse(actual);
        verify(compBodyNode, never()).accept(visitor);
    }

    @Test
    void componentCompBodyFails() {
        String name = "Example";

        var parentScope = mock(Scope.class);

        ReferenceVisitor visitor = new ReferenceVisitor(parentScope);
        var node = mock(UCELParser.ComponentContext.class);
        var paramNode = mock(UCELParser.ParametersContext.class);
        var interfaceNode = mock(UCELParser.InterfacesContext.class);
        var compBodyNode = mock(UCELParser.CompBodyContext.class);

        var idNode = mock(TerminalNode.class);
        var declRef = mock(DeclarationReference.class);

        when(node.parameters()).thenReturn(paramNode);
        when(node.interfaces()).thenReturn(interfaceNode);
        when(node.compBody()).thenReturn(compBodyNode);
        when(idNode.getText()).thenReturn(name);
        when(node.ID()).thenReturn(idNode);
        when(parentScope.add(any())).thenReturn(declRef);
        when(parentScope.isUnique(name, false)).thenReturn(true);

        when(paramNode.accept(visitor)).thenReturn(true);
        when(interfaceNode.accept(visitor)).thenReturn(true);
        when(compBodyNode.accept(visitor)).thenReturn(false);
        var actual = visitor.visitComponent(node);

        assertFalse(actual);
    }
    //endregion

    //region Build block
    @Test
    void buildBlockSetsScope() {
        var visitor = new ReferenceVisitor((Scope)null);

        var buildStmntNode = mock(UCELParser.BuildStmntContext.class);
        var node = mock(UCELParser.BuildBlockContext.class);

        var buildStmnts = new ArrayList<UCELParser.BuildStmntContext>() {{ add(buildStmntNode); }};

        when(node.buildStmnt()).thenReturn(buildStmnts);
        when(buildStmntNode.accept(visitor)).thenReturn(true);

        var actual = visitor.visitBuildBlock(node);

        assertTrue(actual);
        assertNotNull(node.scope);
    }

    @Test
    void buildBlockSeveralBuildStatements() {
        var visitor = new ReferenceVisitor((Scope)null);

        var buildStmntNode1 = mock(UCELParser.BuildStmntContext.class);
        var buildStmntNode2 = mock(UCELParser.BuildStmntContext.class);
        var node = mock(UCELParser.BuildBlockContext.class);

        var buildStmnts = new ArrayList<UCELParser.BuildStmntContext>() {{ add(buildStmntNode1); add(buildStmntNode2); }};

        when(node.buildStmnt()).thenReturn(buildStmnts);
        when(buildStmntNode1.accept(visitor)).thenReturn(true);
        when(buildStmntNode2.accept(visitor)).thenReturn(true);

        var actual = visitor.visitBuildBlock(node);

        assertTrue(actual);
        verify(buildStmntNode1, times(1)).accept(visitor);
        verify(buildStmntNode2, times(1)).accept(visitor);
    }

    @Test
    void buildBlockOneFailsAllAfterNeverVisited() {
        var visitor = new ReferenceVisitor((Scope)null);

        var buildStmntNode1 = mock(UCELParser.BuildStmntContext.class);
        var buildStmntNode2 = mock(UCELParser.BuildStmntContext.class);
        var node = mock(UCELParser.BuildBlockContext.class);

        var buildStmnts = new ArrayList<UCELParser.BuildStmntContext>() {{ add(buildStmntNode1); add(buildStmntNode2); }};

        when(node.buildStmnt()).thenReturn(buildStmnts);
        when(buildStmntNode1.accept(visitor)).thenReturn(false);
        when(buildStmntNode2.accept(visitor)).thenReturn(true);

        var actual = visitor.visitBuildBlock(node);

        assertFalse(actual);
        verify(buildStmntNode1, times(1)).accept(visitor);
        verify(buildStmntNode2, never()).accept(visitor);
    }

    @Test
    void buildBlockLastFailsAllAreVisited() {
        var visitor = new ReferenceVisitor((Scope)null);

        var buildStmntNode1 = mock(UCELParser.BuildStmntContext.class);
        var buildStmntNode2 = mock(UCELParser.BuildStmntContext.class);
        var node = mock(UCELParser.BuildBlockContext.class);

        var buildStmnts = new ArrayList<UCELParser.BuildStmntContext>() {{ add(buildStmntNode1); add(buildStmntNode2); }};

        when(node.buildStmnt()).thenReturn(buildStmnts);
        when(buildStmntNode1.accept(visitor)).thenReturn(true);
        when(buildStmntNode2.accept(visitor)).thenReturn(false);

        var actual = visitor.visitBuildBlock(node);

        assertFalse(actual);
        verify(buildStmntNode1, times(1)).accept(visitor);
        verify(buildStmntNode2, times(1)).accept(visitor);
    }
    //endregion

    //region Build declaration
    @Test
    void buildDeclNoArrayDeclCorrect() {
        var scope = mock(Scope.class);

        var name = "tester";
        var typeName = "Test";

        var visitor = new ReferenceVisitor(scope);

        var node = mock(UCELParser.BuildDeclContext.class);
        var compNode = mock(UCELParser.ComponentContext.class);
        var typeIDNode = mock(TerminalNode.class);
        var idNode = mock(TerminalNode.class);

        var typeDeclRef = mock(DeclarationReference.class);
        var declRef = mock(DeclarationReference.class);

        var compVar = mock(UCELParser.CompVarContext.class);

        var typeInfo = mock(DeclarationInfo.class);

        when(typeInfo.getNode()).thenReturn(compNode);

        when(node.ID()).thenReturn(typeIDNode);
        when(node.compVar()).thenReturn(compVar);
        when(compVar.ID()).thenReturn(idNode);
        when(compVar.accept(visitor)).thenReturn(true);

        when(typeIDNode.getText()).thenReturn(typeName);
        when(idNode.getText()).thenReturn(name);

        try {
            when(scope.isUnique(name, true)).thenReturn(true);
            when(scope.find(typeName, false)).thenReturn(typeDeclRef);
            when(scope.get(typeDeclRef)).thenReturn(typeInfo);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {
            fail();
        }

        var actual = visitor.visitBuildDecl(node);

        assertTrue(actual);
        assertEquals(node.typeReference, typeDeclRef);
    }

    @Test
    void buildDeclWithArrayDeclSuccess() {
        var scope = mock(Scope.class);

        var name = "tester";
        var typeName = "Test";

        var visitor = new ReferenceVisitor(scope);

        var node = mock(UCELParser.BuildDeclContext.class);
        var compNode = mock(UCELParser.ComponentContext.class);
        var typeIDNode = mock(TerminalNode.class);
        var idNode = mock(TerminalNode.class);

        var typeDeclRef = mock(DeclarationReference.class);
        var declRef = mock(DeclarationReference.class);

        var compVar = mock(UCELParser.CompVarContext.class);

        var typeInfo = mock(DeclarationInfo.class);

        when(typeInfo.getNode()).thenReturn(compNode);

        when(node.ID()).thenReturn(typeIDNode);
        when(node.compVar()).thenReturn(compVar);
        when(compVar.ID()).thenReturn(idNode);

        when(typeIDNode.getText()).thenReturn(typeName);
        when(idNode.getText()).thenReturn(name);
        when(compVar.accept(visitor)).thenReturn(true);

        try {
            when(scope.isUnique(name, true)).thenReturn(true);
            when(scope.find(typeName, false)).thenReturn(typeDeclRef);
            when(scope.get(typeDeclRef)).thenReturn(typeInfo);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {
            fail();
        }

        var actual = visitor.visitBuildDecl(node);

        assertTrue(actual);
        assertEquals(node.typeReference, typeDeclRef);
    }

    @Test
    void buildDeclDuplicateIDOfDeclFails() {
        var scope = mock(Scope.class);

        var name = "tester";
        var typeName = "Test";

        var visitor = new ReferenceVisitor(scope);

        var node = mock(UCELParser.BuildDeclContext.class);
        var typeIDNode = mock(TerminalNode.class);
        var idNode = mock(TerminalNode.class);

        var typeDeclRef = mock(DeclarationReference.class);
        var declRef = mock(DeclarationReference.class);

        var compVar = mock(UCELParser.CompVarContext.class);

        when(node.ID()).thenReturn(typeIDNode);
        when(node.compVar()).thenReturn(compVar);
        when(compVar.ID()).thenReturn(idNode);
        when(compVar.accept(visitor)).thenReturn(true);

        when(typeIDNode.getText()).thenReturn(typeName);
        when(idNode.getText()).thenReturn(name);

        try {
            when(scope.isUnique(name, true)).thenReturn(false);
            when(scope.find(typeName, false)).thenReturn(typeDeclRef);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {
            fail();
        }

        var actual = visitor.visitBuildDecl(node);

        assertFalse(actual);
    }

    @Test
    void buildDeclTypeNotDefined() {
        var scope = mock(Scope.class);

        var name = "tester";
        var typeName = "Test";

        var visitor = new ReferenceVisitor(scope);

        var node = mock(UCELParser.BuildDeclContext.class);
        var typeIDNode = mock(TerminalNode.class);
        var idNode = mock(TerminalNode.class);

        var typeDeclRef = mock(DeclarationReference.class);
        var declRef = mock(DeclarationReference.class);

        var compVar = mock(UCELParser.CompVarContext.class);

        when(node.ID()).thenReturn(typeIDNode);
        when(node.compVar()).thenReturn(compVar);
        when(compVar.ID()).thenReturn(idNode);
        when(compVar.accept(visitor)).thenReturn(true);

        when(typeIDNode.getText()).thenReturn(typeName);
        when(idNode.getText()).thenReturn(name);

        try {
            when(scope.isUnique(name, true)).thenReturn(true);
            when(scope.find(typeName, false)).thenThrow(new RuntimeException("Not found"));
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {
            fail();
        }

        var actual = visitor.visitBuildDecl(node);

        assertFalse(actual);
    }

    //endregion

    //region Build iteration
    @Test
    void buildIterationCorrect() {
        String name = "tester";

        var scope = mock(Scope.class);

        var visitor = new ReferenceVisitor(scope);

        var idNode = mock(TerminalNode.class);
        var node = mock(UCELParser.BuildIterationContext.class);
        var exprLowerNode = mock(UCELParser.ExpressionContext.class);
        var exprUpperNode = mock(UCELParser.ExpressionContext.class);
        var buildStmtNode = mock(UCELParser.BuildStmntContext.class);
        var declRef = mock(DeclarationReference.class);

        when(node.ID()).thenReturn(idNode);
        when(node.expression(0)).thenReturn(exprLowerNode);
        when(node.expression(1)).thenReturn(exprUpperNode);
        when(node.buildStmnt()).thenReturn(buildStmtNode);
        when(idNode.getText()).thenReturn(name);

        when(exprLowerNode.accept(visitor)).thenReturn(true);
        when(exprUpperNode.accept(visitor)).thenReturn(true);
        when(buildStmtNode.accept(visitor)).thenReturn(true);

        when(scope.isUnique(name, true)).thenReturn(true);
        when(scope.add(any())).thenReturn(declRef);

        var actual = visitor.visitBuildIteration(node);

        assertTrue(actual);
        assertEquals(declRef, node.reference);
        verify(exprLowerNode, times(1)).accept(visitor);
        verify(exprUpperNode, times(1)).accept(visitor);
        verify(buildStmtNode, times(1)).accept(visitor);
    }

    @Test
    void buildIterationNonUniqueIDFails() {
        String name = "tester";

        var scope = mock(Scope.class);

        var visitor = new ReferenceVisitor(scope);

        var idNode = mock(TerminalNode.class);
        var node = mock(UCELParser.BuildIterationContext.class);
        var exprLowerNode = mock(UCELParser.ExpressionContext.class);
        var exprUpperNode = mock(UCELParser.ExpressionContext.class);
        var buildStmtNode = mock(UCELParser.BuildStmntContext.class);
        var declRef = mock(DeclarationReference.class);

        when(node.ID()).thenReturn(idNode);
        when(node.expression(0)).thenReturn(exprLowerNode);
        when(node.expression(1)).thenReturn(exprUpperNode);
        when(node.buildStmnt()).thenReturn(buildStmtNode);
        when(idNode.getText()).thenReturn(name);

        when(exprLowerNode.accept(visitor)).thenReturn(true);
        when(exprUpperNode.accept(visitor)).thenReturn(true);
        when(buildStmtNode.accept(visitor)).thenReturn(true);

        when(scope.isUnique(name, true)).thenReturn(false);
        when(scope.add(any())).thenReturn(declRef);

        var actual = visitor.visitBuildIteration(node);

        assertFalse(actual);
    }

    @Test
    void buildIterationLowerBoundFails() {
        String name = "tester";

        var scope = mock(Scope.class);

        var visitor = new ReferenceVisitor(scope);

        var idNode = mock(TerminalNode.class);
        var node = mock(UCELParser.BuildIterationContext.class);
        var exprLowerNode = mock(UCELParser.ExpressionContext.class);
        var exprUpperNode = mock(UCELParser.ExpressionContext.class);
        var buildStmtNode = mock(UCELParser.BuildStmntContext.class);
        var declRef = mock(DeclarationReference.class);

        when(node.ID()).thenReturn(idNode);
        when(node.expression(0)).thenReturn(exprLowerNode);
        when(node.expression(1)).thenReturn(exprUpperNode);
        when(node.buildStmnt()).thenReturn(buildStmtNode);
        when(idNode.getText()).thenReturn(name);

        when(exprLowerNode.accept(visitor)).thenReturn(false);
        when(exprUpperNode.accept(visitor)).thenReturn(true);
        when(buildStmtNode.accept(visitor)).thenReturn(true);

        when(scope.isUnique(name, true)).thenReturn(true);
        when(scope.add(any())).thenReturn(declRef);

        var actual = visitor.visitBuildIteration(node);

        assertFalse(actual);
    }
    @Test
    void buildIterationUpperBoundFails() {
        String name = "tester";

        var scope = mock(Scope.class);

        var visitor = new ReferenceVisitor(scope);

        var idNode = mock(TerminalNode.class);
        var node = mock(UCELParser.BuildIterationContext.class);
        var exprLowerNode = mock(UCELParser.ExpressionContext.class);
        var exprUpperNode = mock(UCELParser.ExpressionContext.class);
        var buildStmtNode = mock(UCELParser.BuildStmntContext.class);
        var declRef = mock(DeclarationReference.class);

        when(node.ID()).thenReturn(idNode);
        when(node.expression(0)).thenReturn(exprLowerNode);
        when(node.expression(1)).thenReturn(exprUpperNode);
        when(node.buildStmnt()).thenReturn(buildStmtNode);
        when(idNode.getText()).thenReturn(name);

        when(exprLowerNode.accept(visitor)).thenReturn(true);
        when(exprUpperNode.accept(visitor)).thenReturn(false);
        when(buildStmtNode.accept(visitor)).thenReturn(true);

        when(scope.isUnique(name, true)).thenReturn(true);
        when(scope.add(any())).thenReturn(declRef);

        var actual = visitor.visitBuildIteration(node);

        assertFalse(actual);
    }

    @Test
    void buildIterationBodyFails() {
        String name = "tester";

        var scope = mock(Scope.class);

        var visitor = new ReferenceVisitor(scope);

        var idNode = mock(TerminalNode.class);
        var node = mock(UCELParser.BuildIterationContext.class);
        var exprLowerNode = mock(UCELParser.ExpressionContext.class);
        var exprUpperNode = mock(UCELParser.ExpressionContext.class);
        var buildStmtNode = mock(UCELParser.BuildStmntContext.class);
        var declRef = mock(DeclarationReference.class);

        when(node.ID()).thenReturn(idNode);
        when(node.expression(0)).thenReturn(exprLowerNode);
        when(node.expression(1)).thenReturn(exprUpperNode);
        when(node.buildStmnt()).thenReturn(buildStmtNode);
        when(idNode.getText()).thenReturn(name);

        when(exprLowerNode.accept(visitor)).thenReturn(true);
        when(exprUpperNode.accept(visitor)).thenReturn(true);
        when(buildStmtNode.accept(visitor)).thenReturn(false);

        when(scope.isUnique(name, true)).thenReturn(true);
        when(scope.add(any())).thenReturn(declRef);

        var actual = visitor.visitBuildIteration(node);

        assertFalse(actual);
    }

    //endregion

    //region Interface declaration
    @Test
    void interfaceDeclValid() {
        String name = "tester";
        var scope = mock(Scope.class);

        var visitor = new ReferenceVisitor(scope);

        var node = mock(UCELParser.InterfaceDeclContext.class);
        var interfaceVarDeclNode = mock(UCELParser.InterfaceVarDeclContext.class);
        var idNode = mock(TerminalNode.class);
        var declRef = mock(DeclarationReference.class);

        when(node.ID()).thenReturn(idNode);
        when(idNode.getText()).thenReturn(name);
        when(node.interfaceVarDecl()).thenReturn(interfaceVarDeclNode);
        when(interfaceVarDeclNode.accept(visitor)).thenReturn(true);
        when(scope.isUnique(name, false)).thenReturn(true);
        when(scope.add(any())).thenReturn(declRef);

        var actual = visitor.visitInterfaceDecl(node);

        assertTrue(actual);
        assertEquals(declRef, node.reference);
    }

    @Test
    void interfaceDeclNonUniqueIDFails() {
        String name = "tester";
        var scope = mock(Scope.class);

        var visitor = new ReferenceVisitor(scope);

        var node = mock(UCELParser.InterfaceDeclContext.class);
        var interfaceVarDeclNode = mock(UCELParser.InterfaceVarDeclContext.class);
        var idNode = mock(TerminalNode.class);
        var declRef = mock(DeclarationReference.class);

        when(node.ID()).thenReturn(idNode);
        when(idNode.getText()).thenReturn(name);
        when(node.interfaceVarDecl()).thenReturn(interfaceVarDeclNode);
        when(scope.isUnique(name, false)).thenReturn(false);
        when(scope.add(any())).thenReturn(declRef);

        var actual = visitor.visitInterfaceDecl(node);

        assertFalse(actual);
        verify(interfaceVarDeclNode, never()).accept(visitor);
    }

    //endregion

    //region Component construction


    //endregion

    //endregion

    //region Project

    @Test
    void projectSetsScope() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope) null);

        UCELParser.ProjectContext node = mock(UCELParser.ProjectContext.class);
        UCELParser.PdeclarationContext decls = mock(UCELParser.PdeclarationContext.class);
        UCELParser.PtemplateContext template0 = mock(UCELParser.PtemplateContext.class);
        UCELParser.PsystemContext system = mock(UCELParser.PsystemContext.class);
        UCELParser.VerificationListContext verification = mock(UCELParser.VerificationListContext.class);

        ArrayList<UCELParser.PtemplateContext> templates = new ArrayList<>();
        templates.add(template0);

        when(node.pdeclaration()).thenReturn(decls);
        when(node.ptemplate()).thenReturn(templates);
        when(node.psystem()).thenReturn(system);
        when(node.verificationList()).thenReturn(verification);

        when(decls.accept(visitor)).thenReturn(true);
        when(template0.accept(visitor)).thenReturn(true);
        when(system.accept(visitor)).thenReturn(true);
        when(verification.accept(visitor)).thenReturn(true);

        visitor.visitProject(node);

        assertNotNull(node.scope);
    }

    @Test
    void projectVisitDecls() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope) null);

        UCELParser.ProjectContext node = mock(UCELParser.ProjectContext.class);
        UCELParser.PdeclarationContext decls = mock(UCELParser.PdeclarationContext.class);
        UCELParser.PtemplateContext template0 = mock(UCELParser.PtemplateContext.class);
        UCELParser.PsystemContext system = mock(UCELParser.PsystemContext.class);
        UCELParser.VerificationListContext verification = mock(UCELParser.VerificationListContext.class);

        ArrayList<UCELParser.PtemplateContext> templates = new ArrayList<>();
        templates.add(template0);

        when(node.pdeclaration()).thenReturn(decls);
        when(node.ptemplate()).thenReturn(templates);
        when(node.psystem()).thenReturn(system);
        when(node.verificationList()).thenReturn(verification);

        when(decls.accept(visitor)).thenReturn(true);
        when(template0.accept(visitor)).thenReturn(true);
        when(system.accept(visitor)).thenReturn(true);
        when(verification.accept(visitor)).thenReturn(true);

        visitor.visitProject(node);

        verify(decls, times(1)).accept(visitor);
    }

    @Test
    void projectVisitTemplate() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope) null);

        UCELParser.ProjectContext node = mock(UCELParser.ProjectContext.class);
        UCELParser.PdeclarationContext decls = mock(UCELParser.PdeclarationContext.class);
        UCELParser.PtemplateContext template0 = mock(UCELParser.PtemplateContext.class);
        UCELParser.PsystemContext system = mock(UCELParser.PsystemContext.class);
        UCELParser.VerificationListContext verification = mock(UCELParser.VerificationListContext.class);

        ArrayList<UCELParser.PtemplateContext> templates = new ArrayList<>();
        templates.add(template0);

        when(node.pdeclaration()).thenReturn(decls);
        when(node.ptemplate()).thenReturn(templates);
        when(node.psystem()).thenReturn(system);
        when(node.verificationList()).thenReturn(verification);

        when(decls.accept(visitor)).thenReturn(true);
        when(template0.accept(visitor)).thenReturn(true);
        when(system.accept(visitor)).thenReturn(true);
        when(verification.accept(visitor)).thenReturn(true);

        visitor.visitProject(node);

        verify(template0, times(1)).accept(visitor);
    }

    @Test
    void projectVisitSystem() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope) null);

        UCELParser.ProjectContext node = mock(UCELParser.ProjectContext.class);
        UCELParser.PdeclarationContext decls = mock(UCELParser.PdeclarationContext.class);
        UCELParser.PtemplateContext template0 = mock(UCELParser.PtemplateContext.class);
        UCELParser.PsystemContext system = mock(UCELParser.PsystemContext.class);
        UCELParser.VerificationListContext verification = mock(UCELParser.VerificationListContext.class);

        ArrayList<UCELParser.PtemplateContext> templates = new ArrayList<>();
        templates.add(template0);

        when(node.pdeclaration()).thenReturn(decls);
        when(node.ptemplate()).thenReturn(templates);
        when(node.psystem()).thenReturn(system);
        when(node.verificationList()).thenReturn(verification);

        when(decls.accept(visitor)).thenReturn(true);
        when(template0.accept(visitor)).thenReturn(true);
        when(system.accept(visitor)).thenReturn(true);
        when(verification.accept(visitor)).thenReturn(true);

        visitor.visitProject(node);

        verify(system, times(1)).accept(visitor);
    }

    //endregion

    //region pSystem

    @Test
    void pSystemVisitDeclarations() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.PsystemContext node = mock(UCELParser.PsystemContext.class);
        UCELParser.DeclarationsContext decls = mock(UCELParser.DeclarationsContext.class);
        UCELParser.BuildContext build = mock(UCELParser.BuildContext.class);
        UCELParser.SystemContext system = mock(UCELParser.SystemContext.class);

        when(node.declarations()).thenReturn(decls);
        when(node.build()).thenReturn(build);
        when(node.system()).thenReturn(system);

        when(decls.accept(visitor)).thenReturn(true);
        when(build.accept(visitor)).thenReturn(true);
        when(system.accept(visitor)).thenReturn(true);

        visitor.visitPsystem(node);

        verify(decls, times(1)).accept(visitor);
    }

    @Test
    void pSystemVisitBuild() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.PsystemContext node = mock(UCELParser.PsystemContext.class);
        UCELParser.DeclarationsContext decls = mock(UCELParser.DeclarationsContext.class);
        UCELParser.BuildContext build = mock(UCELParser.BuildContext.class);

        when(node.declarations()).thenReturn(decls);
        when(node.build()).thenReturn(build);

        when(decls.accept(visitor)).thenReturn(true);
        when(build.accept(visitor)).thenReturn(true);

        visitor.visitPsystem(node);

        verify(build, times(1)).accept(visitor);
    }

    @Test
    void pSystemVisitSystem() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.PsystemContext node = mock(UCELParser.PsystemContext.class);
        UCELParser.DeclarationsContext decls = mock(UCELParser.DeclarationsContext.class);
        UCELParser.SystemContext system = mock(UCELParser.SystemContext.class);

        when(node.declarations()).thenReturn(decls);
        when(node.system()).thenReturn(system);

        when(decls.accept(visitor)).thenReturn(true);
        when(system.accept(visitor)).thenReturn(true);

        visitor.visitPsystem(node);

        verify(system, times(1)).accept(visitor);
    }

    //endregion

    //region pTemplate

    @Test
    void pTemplateVisitsGraph() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String templateIdentifier = "p";
        UCELParser.PtemplateContext node = mock(UCELParser.PtemplateContext.class);
        UCELParser.GraphContext graph = mock(UCELParser.GraphContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.DeclarationsContext decls = mock(UCELParser.DeclarationsContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.graph()).thenReturn(graph);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(templateIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.declarations()).thenReturn(decls);

        when(graph.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(decls.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(templateIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitPtemplate(node);

        verify(graph, times(1)).accept(visitor);
    }

    @Test
    void templateVisitsParameters() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String templateIdentifier = "p";
        UCELParser.PtemplateContext node = mock(UCELParser.PtemplateContext.class);
        UCELParser.GraphContext graph = mock(UCELParser.GraphContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.DeclarationsContext decls = mock(UCELParser.DeclarationsContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.graph()).thenReturn(graph);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(templateIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.declarations()).thenReturn(decls);

        when(graph.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(decls.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(templateIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitPtemplate(node);

        verify(parameters, times(1)).accept(visitor);
    }

    @Test
    void templateVisitsDeclaration() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String templateIdentifier = "p";
        UCELParser.PtemplateContext node = mock(UCELParser.PtemplateContext.class);
        UCELParser.GraphContext graph = mock(UCELParser.GraphContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.DeclarationsContext decls = mock(UCELParser.DeclarationsContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.graph()).thenReturn(graph);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(templateIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.declarations()).thenReturn(decls);

        when(graph.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(decls.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(templateIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitPtemplate(node);

        verify(decls, times(1)).accept(visitor);
    }

    @Test
    void templateAddedToScope() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String templateIdentifier = "p";
        UCELParser.PtemplateContext node = mock(UCELParser.PtemplateContext.class);
        UCELParser.GraphContext graph = mock(UCELParser.GraphContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.DeclarationsContext decls = mock(UCELParser.DeclarationsContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.graph()).thenReturn(graph);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(templateIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.declarations()).thenReturn(decls);

        when(graph.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(decls.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(templateIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitPtemplate(node);

        assertEquals(declRef, node.reference);
    }


    //endregion

    //region Graph
    @Test
    void graphVisitLocation() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.GraphContext node = mock(UCELParser.GraphContext.class);
        UCELParser.LocationContext locations0 = mock(UCELParser.LocationContext.class);
        UCELParser.EdgeContext edge0 = mock(UCELParser.EdgeContext.class);

        when(node.getChildCount()).thenReturn(2);
        when(node.getChild(0)).thenReturn(locations0);
        when(node.getChild(1)).thenReturn(edge0);

        when(locations0.accept(visitor)).thenReturn(true);
        when(edge0.accept(visitor)).thenReturn(true);

        visitor.visitGraph(node);

        verify(locations0, times(1)).accept(visitor);
    }

    @Test
    void graphVisitSecondLocation() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.GraphContext node = mock(UCELParser.GraphContext.class);
        UCELParser.LocationContext locations0 = mock(UCELParser.LocationContext.class);
        UCELParser.LocationContext locations1 = mock(UCELParser.LocationContext.class);
        UCELParser.EdgeContext edge0 = mock(UCELParser.EdgeContext.class);

        when(node.getChildCount()).thenReturn(3);
        when(node.getChild(0)).thenReturn(locations0);
        when(node.getChild(1)).thenReturn(locations1);
        when(node.getChild(2)).thenReturn(edge0);

        when(locations0.accept(visitor)).thenReturn(true);
        when(locations1.accept(visitor)).thenReturn(true);
        when(edge0.accept(visitor)).thenReturn(true);

        visitor.visitGraph(node);

        verify(locations1, times(1)).accept(visitor);
    }

    @Test
    void graphVisitEdge() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.GraphContext node = mock(UCELParser.GraphContext.class);
        UCELParser.LocationContext locations0 = mock(UCELParser.LocationContext.class);
        UCELParser.LocationContext locations1 = mock(UCELParser.LocationContext.class);
        UCELParser.EdgeContext edge0 = mock(UCELParser.EdgeContext.class);

        when(node.getChildCount()).thenReturn(3);
        when(node.getChild(0)).thenReturn(locations0);
        when(node.getChild(1)).thenReturn(locations1);
        when(node.getChild(2)).thenReturn(edge0);

        when(locations0.accept(visitor)).thenReturn(true);
        when(locations1.accept(visitor)).thenReturn(true);
        when(edge0.accept(visitor)).thenReturn(true);

        visitor.visitGraph(node);

        verify(edge0, times(1)).accept(visitor);
    }

    //endregion

    //region Location
    @Test
    void locationVisitInvariant() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.LocationContext node = mock(UCELParser.LocationContext.class);
        UCELParser.InvariantContext invariant = mock(UCELParser.InvariantContext.class);
        UCELParser.ExponentialContext exponential = mock(UCELParser.ExponentialContext.class);

        when(node.getChildCount()).thenReturn(2);
        when(node.getChild(0)).thenReturn(invariant);
        when(node.getChild(1)).thenReturn(exponential);

        when(invariant.accept(visitor)).thenReturn(true);
        when(exponential.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitLocation(node);

        assertTrue(actual);
        verify(invariant, times(1)).accept(visitor);
    }

    @Test
    void locationVisitExponential() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.LocationContext node = mock(UCELParser.LocationContext.class);
        UCELParser.InvariantContext invariant = mock(UCELParser.InvariantContext.class);
        UCELParser.ExponentialContext exponential = mock(UCELParser.ExponentialContext.class);

        when(node.getChildCount()).thenReturn(2);
        when(node.getChild(0)).thenReturn(invariant);
        when(node.getChild(1)).thenReturn(exponential);

        when(invariant.accept(visitor)).thenReturn(true);
        when(exponential.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitLocation(node);

        assertTrue(actual);
        verify(exponential, times(1)).accept(visitor);
    }

    //endregion

    //region Exponential
    @Test
    void exponentialVisitsExpressions() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.ExponentialContext node = mock(UCELParser.ExponentialContext.class);
        UCELParser.ExpressionContext expressionLeft = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext expressionRight = mock(UCELParser.ExpressionContext.class);

        when(node.getChildCount()).thenReturn(2);

        ArrayList<UCELParser.ExpressionContext> expressions = new ArrayList<>();
        expressions.add(expressionLeft);
        expressions.add(expressionRight);
        when(node.expression()).thenReturn(expressions);

        when(expressionLeft.accept(visitor)).thenReturn(true);
        when(expressionRight.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitExponential(node);

        assertTrue(actual);
        verify(expressionLeft, times(1)).accept(visitor);
    }

    @Test
    void exponentialVisitsExpressionRight() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.ExponentialContext node = mock(UCELParser.ExponentialContext.class);
        UCELParser.ExpressionContext expressionLeft = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext expressionRight = mock(UCELParser.ExpressionContext.class);

        when(node.getChildCount()).thenReturn(2);

        ArrayList<UCELParser.ExpressionContext> expressions = new ArrayList<>();
        expressions.add(expressionLeft);
        expressions.add(expressionRight);
        when(node.expression()).thenReturn(expressions);

        when(expressionLeft.accept(visitor)).thenReturn(true);
        when(expressionRight.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitExponential(node);

        assertTrue(actual);
        verify(expressionRight, times(1)).accept(visitor);
    }
    //endregion

    //region Invariant

    @Test
    void invariantVisitExpression() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.InvariantContext node = mock(UCELParser.InvariantContext.class);
        UCELParser.ExpressionContext expression = mock(UCELParser.ExpressionContext.class);

        when(node.getChildCount()).thenReturn(1);
        when(node.getChild(0)).thenReturn(expression);

        when(expression.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitInvariant(node);

        assertTrue(actual);
        verify(expression, times(1)).accept(visitor);
    }

    @Test
    void invariantEmpty() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.InvariantContext node = mock(UCELParser.InvariantContext.class);

        when(node.getChildCount()).thenReturn(0);

        boolean actual = visitor.visitInvariant(node);

        assertTrue(actual);
    }

    //endregion

    //region Edge

    @Test
    void edgeAddsScope() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.EdgeContext node = mock(UCELParser.EdgeContext.class);
        UCELParser.SelectContext select = mock(UCELParser.SelectContext.class);
        UCELParser.GuardContext guard = mock(UCELParser.GuardContext.class);
        UCELParser.SyncContext sync = mock(UCELParser.SyncContext.class);
        UCELParser.UpdateContext update = mock(UCELParser.UpdateContext.class);

        when(node.select()).thenReturn(select);
        when(node.guard()).thenReturn(guard);
        when(node.sync()).thenReturn(sync);
        when(node.update()).thenReturn(update);

        when(select.accept(visitor)).thenReturn(true);
        when(guard.accept(visitor)).thenReturn(true);
        when(sync.accept(visitor)).thenReturn(true);
        when(update.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitEdge(node);

        assertTrue(actual);
        assertTrue(node.scope instanceof Scope);
    }

    @Test
    void edgeVisitsSelect() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.EdgeContext node = mock(UCELParser.EdgeContext.class);
        UCELParser.SelectContext select = mock(UCELParser.SelectContext.class);
        UCELParser.GuardContext guard = mock(UCELParser.GuardContext.class);
        UCELParser.SyncContext sync = mock(UCELParser.SyncContext.class);
        UCELParser.UpdateContext update = mock(UCELParser.UpdateContext.class);

        when(node.select()).thenReturn(select);
        when(node.guard()).thenReturn(guard);
        when(node.sync()).thenReturn(sync);
        when(node.update()).thenReturn(update);

        when(select.accept(visitor)).thenReturn(true);
        when(guard.accept(visitor)).thenReturn(true);
        when(sync.accept(visitor)).thenReturn(true);
        when(update.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitEdge(node);

        assertTrue(actual);
        verify(select, times(1)).accept(visitor);
    }

    @Test
    void edgeVisitsGuard() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.EdgeContext node = mock(UCELParser.EdgeContext.class);
        UCELParser.SelectContext select = mock(UCELParser.SelectContext.class);
        UCELParser.GuardContext guard = mock(UCELParser.GuardContext.class);
        UCELParser.SyncContext sync = mock(UCELParser.SyncContext.class);
        UCELParser.UpdateContext update = mock(UCELParser.UpdateContext.class);

        when(node.select()).thenReturn(select);
        when(node.guard()).thenReturn(guard);
        when(node.sync()).thenReturn(sync);
        when(node.update()).thenReturn(update);

        when(select.accept(visitor)).thenReturn(true);
        when(guard.accept(visitor)).thenReturn(true);
        when(sync.accept(visitor)).thenReturn(true);
        when(update.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitEdge(node);

        assertTrue(actual);
        verify(guard, times(1)).accept(visitor);
    }

    @Test
    void edgeVisitsSync() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.EdgeContext node = mock(UCELParser.EdgeContext.class);
        UCELParser.SelectContext select = mock(UCELParser.SelectContext.class);
        UCELParser.GuardContext guard = mock(UCELParser.GuardContext.class);
        UCELParser.SyncContext sync = mock(UCELParser.SyncContext.class);
        UCELParser.UpdateContext update = mock(UCELParser.UpdateContext.class);

        when(node.select()).thenReturn(select);
        when(node.guard()).thenReturn(guard);
        when(node.sync()).thenReturn(sync);
        when(node.update()).thenReturn(update);

        when(select.accept(visitor)).thenReturn(true);
        when(guard.accept(visitor)).thenReturn(true);
        when(sync.accept(visitor)).thenReturn(true);
        when(update.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitEdge(node);

        assertTrue(actual);
        verify(sync, times(1)).accept(visitor);
    }

    @Test
    void edgeVisitsUpdate() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        UCELParser.EdgeContext node = mock(UCELParser.EdgeContext.class);
        UCELParser.SelectContext select = mock(UCELParser.SelectContext.class);
        UCELParser.GuardContext guard = mock(UCELParser.GuardContext.class);
        UCELParser.SyncContext sync = mock(UCELParser.SyncContext.class);
        UCELParser.UpdateContext update = mock(UCELParser.UpdateContext.class);

        when(node.select()).thenReturn(select);
        when(node.guard()).thenReturn(guard);
        when(node.sync()).thenReturn(sync);
        when(node.update()).thenReturn(update);

        when(select.accept(visitor)).thenReturn(true);
        when(guard.accept(visitor)).thenReturn(true);
        when(sync.accept(visitor)).thenReturn(true);
        when(update.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitEdge(node);

        assertTrue(actual);
        verify(update, times(1)).accept(visitor);
    }

    //endregion

    //region Select

    @Test
    void selectHasReferences() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String s0 = "a", s1 = "b", s2 = "c";

        UCELParser.SelectContext node = mock(UCELParser.SelectContext.class);
        ArrayList<TerminalNode> ids = new ArrayList<>();
        ArrayList<UCELParser.TypeContext> types = new ArrayList<>();

        TerminalNode id0 = mock(TerminalNode.class);
        TerminalNode id1 = mock(TerminalNode.class);
        TerminalNode id2 = mock(TerminalNode.class);

        ids.add(id0);
        ids.add(id1);
        ids.add(id2);

        UCELParser.TypeContext type0 = mock(UCELParser.TypeContext.class);
        UCELParser.TypeContext type1 = mock(UCELParser.TypeContext.class);
        UCELParser.TypeContext type2 = mock(UCELParser.TypeContext.class);

        types.add(type0);
        types.add(type1);
        types.add(type2);

        when(node.ID()).thenReturn(ids);
        when(node.type()).thenReturn(types);
        when(id0.getText()).thenReturn(s0);
        when(id1.getText()).thenReturn(s1);
        when(id2.getText()).thenReturn(s2);

        when(type0.accept(visitor)).thenReturn(true);
        when(type1.accept(visitor)).thenReturn(true);
        when(type2.accept(visitor)).thenReturn(true);

        DeclarationReference declRef0 = new DeclarationReference(0, 0);
        DeclarationReference declRef1 = new DeclarationReference(0, 1);
        DeclarationReference declRef2 = new DeclarationReference(0, 2);

        try {
            when(scope.isUnique(s0, true)).thenReturn(true);
            when(scope.isUnique(s1, true)).thenReturn(true);
            when(scope.isUnique(s2, true)).thenReturn(true);
            when(scope.add(eq(new DeclarationInfo(s0)))).thenReturn(declRef0);
            when(scope.add(eq(new DeclarationInfo(s1)))).thenReturn(declRef1);
            when(scope.add(eq(new DeclarationInfo(s2)))).thenReturn(declRef2);
        } catch (Exception e) {fail();}

        boolean actual = visitor.visitSelect(node);

        assertTrue(actual);
        assertTrue(node.references.size() == 3);
    }

    @Test
    void selectHasCorrectReference() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String s0 = "a", s1 = "b", s2 = "c";

        UCELParser.SelectContext node = mock(UCELParser.SelectContext.class);
        ArrayList<TerminalNode> ids = new ArrayList<>();
        ArrayList<UCELParser.TypeContext> types = new ArrayList<>();

        TerminalNode id0 = mock(TerminalNode.class);
        TerminalNode id1 = mock(TerminalNode.class);
        TerminalNode id2 = mock(TerminalNode.class);

        ids.add(id0);
        ids.add(id1);
        ids.add(id2);

        UCELParser.TypeContext type0 = mock(UCELParser.TypeContext.class);
        UCELParser.TypeContext type1 = mock(UCELParser.TypeContext.class);
        UCELParser.TypeContext type2 = mock(UCELParser.TypeContext.class);

        types.add(type0);
        types.add(type1);
        types.add(type2);

        when(node.ID()).thenReturn(ids);
        when(node.type()).thenReturn(types);
        when(id0.getText()).thenReturn(s0);
        when(id1.getText()).thenReturn(s1);
        when(id2.getText()).thenReturn(s2);

        when(type0.accept(visitor)).thenReturn(true);
        when(type1.accept(visitor)).thenReturn(true);
        when(type2.accept(visitor)).thenReturn(true);

        DeclarationReference declRef0 = new DeclarationReference(0, 0);
        DeclarationReference declRef1 = new DeclarationReference(0, 1);
        DeclarationReference declRef2 = new DeclarationReference(0, 2);

        try {
            when(scope.isUnique(s0, true)).thenReturn(true);
            when(scope.isUnique(s1, true)).thenReturn(true);
            when(scope.isUnique(s2, true)).thenReturn(true);
            when(scope.add(eq(new DeclarationInfo(s0)))).thenReturn(declRef0);
            when(scope.add(eq(new DeclarationInfo(s1)))).thenReturn(declRef1);
            when(scope.add(eq(new DeclarationInfo(s2)))).thenReturn(declRef2);
        } catch (Exception e) {fail();}

        boolean actual = visitor.visitSelect(node);

        assertTrue(actual);
        assertEquals(declRef1, node.references.get(1));
    }

    @Test
    void selectVisitsTypes() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String s0 = "a", s1 = "b", s2 = "c";

        UCELParser.SelectContext node = mock(UCELParser.SelectContext.class);
        ArrayList<TerminalNode> ids = new ArrayList<>();
        ArrayList<UCELParser.TypeContext> types = new ArrayList<>();

        TerminalNode id0 = mock(TerminalNode.class);
        TerminalNode id1 = mock(TerminalNode.class);
        TerminalNode id2 = mock(TerminalNode.class);

        ids.add(id0);
        ids.add(id1);
        ids.add(id2);

        UCELParser.TypeContext type0 = mock(UCELParser.TypeContext.class);
        UCELParser.TypeContext type1 = mock(UCELParser.TypeContext.class);
        UCELParser.TypeContext type2 = mock(UCELParser.TypeContext.class);

        types.add(type0);
        types.add(type1);
        types.add(type2);

        when(node.ID()).thenReturn(ids);
        when(node.type()).thenReturn(types);
        when(id0.getText()).thenReturn(s0);
        when(id1.getText()).thenReturn(s1);
        when(id2.getText()).thenReturn(s2);

        when(type0.accept(visitor)).thenReturn(true);
        when(type1.accept(visitor)).thenReturn(true);
        when(type2.accept(visitor)).thenReturn(true);

        DeclarationReference declRef0 = new DeclarationReference(0, 0);
        DeclarationReference declRef1 = new DeclarationReference(0, 1);
        DeclarationReference declRef2 = new DeclarationReference(0, 2);

        try {
            when(scope.isUnique(s0, true)).thenReturn(true);
            when(scope.isUnique(s1, true)).thenReturn(true);
            when(scope.isUnique(s2, true)).thenReturn(true);
            when(scope.add(eq(new DeclarationInfo(s0)))).thenReturn(declRef0);
            when(scope.add(eq(new DeclarationInfo(s1)))).thenReturn(declRef1);
            when(scope.add(eq(new DeclarationInfo(s2)))).thenReturn(declRef2);
        } catch (Exception e) {fail();}

        boolean actual = visitor.visitSelect(node);

        assertTrue(actual);
        verify(type0, times(1)).accept(visitor);
        verify(type1, times(1)).accept(visitor);
        verify(type2, times(1)).accept(visitor);

    }

    //endregion

    //region start
    @Test
    void startVisitsDeclarationsAndSystem() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope)null);

        var node = mock(UCELParser.StartContext.class);
        var decls = mock(UCELParser.DeclarationsContext.class);
        var systemNode = mock(UCELParser.SystemContext.class);

        when(decls.accept(visitor)).thenReturn(true);
        when(systemNode.accept(visitor)).thenReturn(true);
        when(node.declarations()).thenReturn(decls);
        when(node.system()).thenReturn(systemNode);

        var actual = visitor.visitStart(node);

        verify(decls, times(1)).accept(visitor);
        verify(systemNode, times(1)).accept(visitor);
        assertTrue(actual);
    }

    @Test
    void startVisitsDeclarationsButThenStops() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope)null);

        var node = mock(UCELParser.StartContext.class);
        var decls = mock(UCELParser.DeclarationsContext.class);
        var systemNode = mock(UCELParser.SystemContext.class);
        var stmnt = mock(UCELParser.StatementContext.class);
        var stmnts = new ArrayList<UCELParser.StatementContext>() {{ add(stmnt); }};

        when(decls.accept(visitor)).thenReturn(false);
        when(node.declarations()).thenReturn(decls);
        when(node.statement()).thenReturn(stmnts);
        when(node.system()).thenReturn(systemNode);

        var actual = visitor.visitStart(node);

        verify(decls, times(1)).accept(visitor);
        verify(stmnt, never()).accept(visitor);
        verify(systemNode, never()).accept(visitor);
        assertFalse(actual);
    }

    @Test
    void startVisitsOneStatementButThenStops() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope)null);

        var node = mock(UCELParser.StartContext.class);
        var decls = mock(UCELParser.DeclarationsContext.class);
        var systemNode = mock(UCELParser.SystemContext.class);
        var stmnt1 = mock(UCELParser.StatementContext.class);
        var stmnt2 = mock(UCELParser.StatementContext.class);
        var stmnts = new ArrayList<UCELParser.StatementContext>() {{ add(stmnt1); add(stmnt2); }};

        when(decls.accept(visitor)).thenReturn(true);
        when(stmnt1.accept(visitor)).thenReturn(false);
        when(node.declarations()).thenReturn(decls);
        when(node.statement()).thenReturn(stmnts);
        when(node.system()).thenReturn(systemNode);

        var actual = visitor.visitStart(node);

        verify(decls, times(1)).accept(visitor);
        verify(stmnt1, times(1)).accept(visitor);
        verify(stmnt2, never()).accept(visitor);
        verify(systemNode, never()).accept(visitor);
        assertFalse(actual);
    }

    @Test
    void startVisitsAllStatementButSystemFails() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope)null);

        var node = mock(UCELParser.StartContext.class);
        var decls = mock(UCELParser.DeclarationsContext.class);
        var systemNode = mock(UCELParser.SystemContext.class);
        var stmnt1 = mock(UCELParser.StatementContext.class);
        var stmnt2 = mock(UCELParser.StatementContext.class);
        var stmnts = new ArrayList<UCELParser.StatementContext>() {{ add(stmnt1); add(stmnt2); }};

        when(decls.accept(visitor)).thenReturn(true);
        when(stmnt1.accept(visitor)).thenReturn(true);
        when(stmnt2.accept(visitor)).thenReturn(true);
        when(systemNode.accept(visitor)).thenReturn(false);
        when(node.declarations()).thenReturn(decls);
        when(node.statement()).thenReturn(stmnts);
        when(node.system()).thenReturn(systemNode);

        var actual = visitor.visitStart(node);

        verify(decls, times(1)).accept(visitor);
        verify(stmnt1, times(1)).accept(visitor);
        verify(stmnt2, times(1)).accept(visitor);
        verify(systemNode, times(1)).accept(visitor);
        assertFalse(actual);
    }

    void startVisitsAllAndSucceeds() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope)null);

        var node = mock(UCELParser.StartContext.class);
        var decls = mock(UCELParser.DeclarationsContext.class);
        var systemNode = mock(UCELParser.SystemContext.class);
        var stmnt1 = mock(UCELParser.StatementContext.class);
        var stmnt2 = mock(UCELParser.StatementContext.class);
        var stmnts = new ArrayList<UCELParser.StatementContext>() {{ add(stmnt1); add(stmnt2); }};

        when(decls.accept(visitor)).thenReturn(true);
        when(stmnt1.accept(visitor)).thenReturn(true);
        when(stmnt2.accept(visitor)).thenReturn(true);
        when(systemNode.accept(visitor)).thenReturn(true);
        when(node.declarations()).thenReturn(decls);
        when(node.statement()).thenReturn(stmnts);
        when(node.system()).thenReturn(systemNode);

        var actual = visitor.visitStart(node);

        verify(decls, times(1)).accept(visitor);
        verify(stmnt1, times(1)).accept(visitor);
        verify(stmnt2, never()).accept(visitor);
        verify(systemNode, never()).accept(visitor);
        assertTrue(actual);
    }
    //endregion

    //region system
    @Test
    void systemFirstExprSucceeds() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope)null);

        var node = mock(UCELParser.SystemContext.class);
        var expr = mock(UCELParser.ExpressionContext.class);

        var exprs = new ArrayList<UCELParser.ExpressionContext>() {{ add(expr); }};

        when(expr.accept(visitor)).thenReturn(true);
        when(node.expression()).thenReturn(exprs);

        var actual = visitor.visitSystem(node);

        verify(expr, times(1)).accept(visitor);
        assertTrue(actual);
    }
    @Test
    void systemFirstExprFailsSecondNotVisited() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope)null);

        var node = mock(UCELParser.SystemContext.class);
        var expr1 = mock(UCELParser.ExpressionContext.class);
        var expr2 = mock(UCELParser.ExpressionContext.class);

        var exprs = new ArrayList<UCELParser.ExpressionContext>() {{ add(expr1); add(expr2); }};

        when(expr1.accept(visitor)).thenReturn(false);
        when(node.expression()).thenReturn(exprs);

        var actual = visitor.visitSystem(node);

        verify(expr1, times(1)).accept(visitor);
        verify(expr2, never()).accept(visitor);
        assertFalse(actual);
    }

    @Test
    void systemSecondExprFails() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope)null);

        var node = mock(UCELParser.SystemContext.class);
        var expr1 = mock(UCELParser.ExpressionContext.class);
        var expr2 = mock(UCELParser.ExpressionContext.class);

        var exprs = new ArrayList<UCELParser.ExpressionContext>() {{ add(expr1); add(expr2); }};

        when(expr1.accept(visitor)).thenReturn(true);
        when(expr2.accept(visitor)).thenReturn(false);
        when(node.expression()).thenReturn(exprs);

        var actual = visitor.visitSystem(node);

        verify(expr1, times(1)).accept(visitor);
        verify(expr2, times(1)).accept(visitor);
        assertFalse(actual);
    }

    @Test
    void systemAllExprsSucceed() {
        ReferenceVisitor visitor = new ReferenceVisitor((Scope)null);

        var node = mock(UCELParser.SystemContext.class);
        var expr1 = mock(UCELParser.ExpressionContext.class);
        var expr2 = mock(UCELParser.ExpressionContext.class);

        var exprs = new ArrayList<UCELParser.ExpressionContext>() {{ add(expr1); add(expr2); }};

        when(expr1.accept(visitor)).thenReturn(true);
        when(expr2.accept(visitor)).thenReturn(true);
        when(node.expression()).thenReturn(exprs);

        var actual = visitor.visitSystem(node);

        verify(expr1, times(1)).accept(visitor);
        verify(expr2, times(1)).accept(visitor);
        assertTrue(actual);
    }
    //endregion

    //region instantiation

    @Test
    void instantiationSetsInstantiationRef() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String instantiationIdentifier = "f", constructorIdentifier = "c";

        UCELParser.InstantiationContext node = mock(UCELParser.InstantiationContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.ArgumentsContext arguments = mock(UCELParser.ArgumentsContext.class);
        TerminalNode instantiationID = mock(TerminalNode.class);
        TerminalNode constructorID = mock(TerminalNode.class);
        ArrayList<TerminalNode> ids = new ArrayList<>();
        ids.add(instantiationID);
        ids.add(constructorID);

        when(node.ID()).thenReturn(ids);
        when(node.parameters()).thenReturn(parameters);
        when(node.arguments()).thenReturn(arguments);
        when(instantiationID.getText()).thenReturn(instantiationIdentifier);
        when(constructorID.getText()).thenReturn(constructorIdentifier);

        when(parameters.accept(visitor)).thenReturn(true);
        when(arguments.accept(visitor)).thenReturn(true);

        DeclarationReference instantiationReference = new DeclarationReference(0,0);
        DeclarationReference constructorReference = new DeclarationReference(0,0);

        try{
            when(scope.isUnique(instantiationIdentifier, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(instantiationReference);
            when(scope.find(constructorIdentifier, false)).thenReturn(constructorReference);
        } catch (Exception e) {
            fail();
        }

        visitor.visitInstantiation(node);

        assertEquals(instantiationReference, node.instantiatedReference);
    }

    @Test
    void instantiationSetsConstructorRef() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String instantiationIdentifier = "f", constructorIdentifier = "c";

        UCELParser.InstantiationContext node = mock(UCELParser.InstantiationContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.ArgumentsContext arguments = mock(UCELParser.ArgumentsContext.class);
        TerminalNode instantiationID = mock(TerminalNode.class);
        TerminalNode constructorID = mock(TerminalNode.class);
        ArrayList<TerminalNode> ids = new ArrayList<>();
        ids.add(instantiationID);
        ids.add(constructorID);

        when(node.ID()).thenReturn(ids);
        when(node.parameters()).thenReturn(parameters);
        when(node.arguments()).thenReturn(arguments);
        when(instantiationID.getText()).thenReturn(instantiationIdentifier);
        when(constructorID.getText()).thenReturn(constructorIdentifier);

        when(parameters.accept(visitor)).thenReturn(true);
        when(arguments.accept(visitor)).thenReturn(true);

        DeclarationReference instantiationReference = new DeclarationReference(0,0);
        DeclarationReference constructorReference = new DeclarationReference(0,0);

        try{
            when(scope.isUnique(instantiationIdentifier, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(instantiationReference);
            when(scope.find(constructorIdentifier, false)).thenReturn(constructorReference);
        } catch (Exception e) {
            fail();
        }

        visitor.visitInstantiation(node);

        assertEquals(constructorReference, node.constructorReference);
    }

    @Test
    void instantiationSetsScope() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String instantiationIdentifier = "f", constructorIdentifier = "c";

        UCELParser.InstantiationContext node = mock(UCELParser.InstantiationContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.ArgumentsContext arguments = mock(UCELParser.ArgumentsContext.class);
        TerminalNode instantiationID = mock(TerminalNode.class);
        TerminalNode constructorID = mock(TerminalNode.class);
        ArrayList<TerminalNode> ids = new ArrayList<>();
        ids.add(instantiationID);
        ids.add(constructorID);

        when(node.ID()).thenReturn(ids);
        when(node.parameters()).thenReturn(parameters);
        when(node.arguments()).thenReturn(arguments);
        when(instantiationID.getText()).thenReturn(instantiationIdentifier);
        when(constructorID.getText()).thenReturn(constructorIdentifier);

        when(parameters.accept(visitor)).thenReturn(true);
        when(arguments.accept(visitor)).thenReturn(true);

        DeclarationReference instantiationReference = new DeclarationReference(0,0);
        DeclarationReference constructorReference = new DeclarationReference(0,0);

        try{
            when(scope.isUnique(instantiationIdentifier, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(instantiationReference);
            when(scope.find(constructorIdentifier, false)).thenReturn(constructorReference);
        } catch (Exception e) {
            fail();
        }

        visitor.visitInstantiation(node);

        assertTrue(node.scope instanceof Scope);
    }

    @Test
    void instantiationVisitsParameters() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String instantiationIdentifier = "f", constructorIdentifier = "c";

        UCELParser.InstantiationContext node = mock(UCELParser.InstantiationContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.ArgumentsContext arguments = mock(UCELParser.ArgumentsContext.class);
        TerminalNode instantiationID = mock(TerminalNode.class);
        TerminalNode constructorID = mock(TerminalNode.class);
        ArrayList<TerminalNode> ids = new ArrayList<>();
        ids.add(instantiationID);
        ids.add(constructorID);

        when(node.ID()).thenReturn(ids);
        when(node.parameters()).thenReturn(parameters);
        when(node.arguments()).thenReturn(arguments);
        when(instantiationID.getText()).thenReturn(instantiationIdentifier);
        when(constructorID.getText()).thenReturn(constructorIdentifier);

        when(parameters.accept(visitor)).thenReturn(true);
        when(arguments.accept(visitor)).thenReturn(true);

        DeclarationReference instantiationReference = new DeclarationReference(0,0);
        DeclarationReference constructorReference = new DeclarationReference(0,0);

        try{
            when(scope.isUnique(instantiationIdentifier, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(instantiationReference);
            when(scope.find(constructorIdentifier, false)).thenReturn(constructorReference);
        } catch (Exception e) {
            fail();
        }

        visitor.visitInstantiation(node);

        verify(parameters, times(1)).accept(visitor);
    }

    @Test
    void instantiationVisitsArgiments() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String instantiationIdentifier = "f", constructorIdentifier = "c";

        UCELParser.InstantiationContext node = mock(UCELParser.InstantiationContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.ArgumentsContext arguments = mock(UCELParser.ArgumentsContext.class);
        TerminalNode instantiationID = mock(TerminalNode.class);
        TerminalNode constructorID = mock(TerminalNode.class);
        ArrayList<TerminalNode> ids = new ArrayList<>();
        ids.add(instantiationID);
        ids.add(constructorID);

        when(node.ID()).thenReturn(ids);
        when(node.parameters()).thenReturn(parameters);
        when(node.arguments()).thenReturn(arguments);
        when(instantiationID.getText()).thenReturn(instantiationIdentifier);
        when(constructorID.getText()).thenReturn(constructorIdentifier);

        when(parameters.accept(visitor)).thenReturn(true);
        when(arguments.accept(visitor)).thenReturn(true);

        DeclarationReference instantiationReference = new DeclarationReference(0,0);
        DeclarationReference constructorReference = new DeclarationReference(0,0);

        try{
            when(scope.isUnique(instantiationIdentifier, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(instantiationReference);
            when(scope.find(constructorIdentifier, false)).thenReturn(constructorReference);
        } catch (Exception e) {
            fail();
        }

        visitor.visitInstantiation(node);

        verify(arguments, times(1)).accept(visitor);
    }

    //endregion

    //region parameters

    @Test
    void parameterAddIdToScope() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String parameterName = "a";

        UCELParser.ParameterContext node = mock(UCELParser.ParameterContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(type.accept(visitor)).thenReturn(true);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(parameterName);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(parameterName, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail();}

        visitor.visitParameter(node);

        verify(scope, times(1)).add(any());
    }

    @Test
    void parameterAddSetReference() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String parameterName = "a";

        UCELParser.ParameterContext node = mock(UCELParser.ParameterContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(type.accept(visitor)).thenReturn(true);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(parameterName);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(parameterName, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail();}

        visitor.visitParameter(node);

        assertEquals(declRef, node.reference);
    }

    //endregion

    //region TypeDecl aka typedef

    @Test
    void visitTypeDeclAddIdToScope() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String identifierName = "a";

        UCELParser.TypeDeclContext node = mock(UCELParser.TypeDeclContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);

        when(node.type()).thenReturn(type);
        when(type.accept(visitor)).thenReturn(true);

        TerminalNode id = mock(TerminalNode.class);
        UCELParser.ArrayDeclIDContext arrayDeclID = mock(UCELParser.ArrayDeclIDContext.class);
        ArrayList<UCELParser.ArrayDeclIDContext> arrayDeclIDList = new ArrayList<>();
        arrayDeclIDList.add(arrayDeclID);

        when(node.arrayDeclID()).thenReturn(arrayDeclIDList);
        when(arrayDeclID.ID()).thenReturn(id);
        when(id.getText()).thenReturn(identifierName);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(identifierName, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail();}

        visitor.visitTypeDecl(node);

        verify(scope, times(1)).add(any());
    }

    @Test
    void visitTypeDeclSetReference() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String identifierName = "a";

        UCELParser.TypeDeclContext node = mock(UCELParser.TypeDeclContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);

        when(node.type()).thenReturn(type);
        when(type.accept(visitor)).thenReturn(true);

        TerminalNode id = mock(TerminalNode.class);
        UCELParser.ArrayDeclIDContext arrayDeclID = mock(UCELParser.ArrayDeclIDContext.class);
        ArrayList<UCELParser.ArrayDeclIDContext> arrayDeclIDList = new ArrayList<>();
        arrayDeclIDList.add(arrayDeclID);

        when(node.arrayDeclID()).thenReturn(arrayDeclIDList);
        when(arrayDeclID.ID()).thenReturn(id);
        when(id.getText()).thenReturn(identifierName);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(identifierName, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail();}

        visitor.visitTypeDecl(node);

        assertEquals(declRef, node.references.get(0));

    }

    //endregion

    //region TypeIDID

    @Test
    void typeIDIDAddSetReference() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);
        String typeID = "a";

        UCELParser.TypeIDIDContext node = mock(UCELParser.TypeIDIDContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(typeID);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.find(typeID, false)).thenReturn(declRef);
        } catch (Exception e) {fail();}

        visitor.visitTypeIDID(node);

        assertEquals(declRef, node.reference);
    }


    //endregion

    //region FuncCall
    @Test
    void funcCallExpressionSuccesfulLookup() {
        String correctFunctionName = "cfn";
        String incorrectFunctionName = "icfn";

        ArrayList<DeclarationInfo> lvl1Variables = new ArrayList<>();
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));
        lvl1Variables.add(new DeclarationInfo(correctFunctionName));
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));

        ArrayList<DeclarationInfo> lvl0Variables = new ArrayList<>();

        Scope lvl1Scope = new Scope(null, false, lvl1Variables);
        Scope lvl0Scope = new Scope(lvl1Scope, false, lvl0Variables);
        ReferenceVisitor visitor = new ReferenceVisitor(lvl0Scope);

        UCELParser.FuncCallContext funcCallContext = mock(UCELParser.FuncCallContext.class);
        UCELParser.ArgumentsContext argumentsContext = mock(UCELParser.ArgumentsContext.class);

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctFunctionName);
        when(funcCallContext.ID()).thenReturn(idNode);
        when(funcCallContext.arguments()).thenReturn(argumentsContext);

        visitor.visitFuncCall(funcCallContext);

        assertEquals(funcCallContext.reference, new DeclarationReference(1,2));
    }

    @Test
    void funcCallExpressionVisitArguments() {
        String correctFunctionName = "cfn";
        String incorrectFunctionName = "icfn";

        ArrayList<DeclarationInfo> lvl1Variables = new ArrayList<>();
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));
        lvl1Variables.add(new DeclarationInfo(correctFunctionName));
        lvl1Variables.add(new DeclarationInfo(incorrectFunctionName));

        ArrayList<DeclarationInfo> lvl0Variables = new ArrayList<>();

        Scope lvl1Scope = new Scope(null, false, lvl1Variables);
        Scope lvl0Scope = new Scope(lvl1Scope, false, lvl0Variables);
        ReferenceVisitor visitor = new ReferenceVisitor(lvl0Scope);

        UCELParser.FuncCallContext funcCallContext = mock(UCELParser.FuncCallContext.class);
        UCELParser.ArgumentsContext argumentsContext = mock(UCELParser.ArgumentsContext.class);

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctFunctionName);
        when(funcCallContext.ID()).thenReturn(idNode);
        when(funcCallContext.arguments()).thenReturn(argumentsContext);

        visitor.visitFuncCall(funcCallContext);

        verify(argumentsContext, times(1)).accept(visitor);
    }

    //endregion

    //region Arguments
    @Test
    void argumentsWithNoExpressionsDoesNothing() {
        var scopeMock = mock(Scope.class);
        try {
            when(scopeMock.get(any(DeclarationReference.class))).thenThrow(new RuntimeException());
            when(scopeMock.find(any(String.class), anyBoolean())).thenThrow(new RuntimeException());
        } catch (Exception e) {
            fail();
        }

        ReferenceVisitor visitor = new ReferenceVisitor(scopeMock);
        var node = mock(UCELParser.ArgumentsContext.class);

        assertDoesNotThrow(() -> visitor.visitArguments(node));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void argumentsOneValidExpression(Boolean expected) {
        var scopeMock = mock(Scope.class);
        try {
            when(scopeMock.get(any(DeclarationReference.class))).thenThrow(new RuntimeException());
            when(scopeMock.find(any(String.class), anyBoolean())).thenThrow(new RuntimeException());
        } catch (Exception e) {
            fail();
        }

        ReferenceVisitor visitor = new ReferenceVisitor(scopeMock);

        var node = mock(UCELParser.ArgumentsContext.class);
        var exprMock = mock(UCELParser.ExpressionContext.class);
        var exprs = new ArrayList<UCELParser.ExpressionContext>() {{ add(exprMock); }};
        when(exprMock.accept(visitor)).thenReturn(expected);
        when(node.expression()).thenReturn(exprs);

        var actual = visitor.visitArguments(node);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void argumentsMultipleExpressions(Boolean expected) {
        var scopeMock = mock(Scope.class);
        try {
            when(scopeMock.get(any(DeclarationReference.class))).thenThrow(new RuntimeException());
            when(scopeMock.find(any(String.class), anyBoolean())).thenThrow(new RuntimeException());
        } catch (Exception e) {
            fail();
        }

        ReferenceVisitor visitor = new ReferenceVisitor(scopeMock);

        var node = mock(UCELParser.ArgumentsContext.class);
        var expr1Mock = mock(UCELParser.ExpressionContext.class);
        var expr2Mock = mock(UCELParser.ExpressionContext.class);
        var exprs = new ArrayList<UCELParser.ExpressionContext>() {{ add(expr1Mock); add(expr2Mock); }};
        when(expr1Mock.accept(visitor)).thenReturn(expected);
        when(expr2Mock.accept(visitor)).thenReturn(true);
        when(node.expression()).thenReturn(exprs);

        var actual = visitor.visitArguments(node);

        assertEquals(expected, actual);
    }

    //endregion

    //region IDExpr: look in immediate scope, look through multiple scope, fail to find
    @Test
    void idExprSuccessfulLookupInImmediateScope() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(correctVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.IdExprContext idExprContext = mock(UCELParser.IdExprContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(idExprContext.ID()).thenReturn(idNode);

        visitor.visitIdExpr(idExprContext);

        assertEquals(idExprContext.reference, new DeclarationReference(0,2));
    }

    @Test
    void idExprSuccessfulLookupInDistantScope() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<DeclarationInfo> lvl1Variables = new ArrayList<>();
        lvl1Variables.add(new DeclarationInfo(incorrectVariableName));
        lvl1Variables.add(new DeclarationInfo(incorrectVariableName));
        lvl1Variables.add(new DeclarationInfo(correctVariableName));
        lvl1Variables.add(new DeclarationInfo(incorrectVariableName));
        ArrayList<DeclarationInfo> lvl0Variables = new ArrayList<>();

        Scope lvl1Scope = new Scope(null, false, lvl1Variables);
        Scope lvl0Scope = new Scope(lvl1Scope, false, lvl0Variables);
        ReferenceVisitor visitor = new ReferenceVisitor(lvl0Scope);

        UCELParser.IdExprContext idExprContext = mock(UCELParser.IdExprContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(idExprContext.ID()).thenReturn(idNode);

        visitor.visitIdExpr(idExprContext);

        assertEquals(idExprContext.reference, new DeclarationReference(1,2));
    }

    @Test
    void idExprUnsuccessfulLookupThrows() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.IdExprContext idExprContext = mock(UCELParser.IdExprContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(idExprContext.ID()).thenReturn(idNode);

        boolean actualReturnValue = visitor.visitIdExpr(idExprContext);

        assertFalse(actualReturnValue);
    }
    //endregion

    //region variableDecl

    @ParameterizedTest
    @ValueSource(ints = {0,1,2})
    void variableDeclVisitAllVariableIDsDespiteFaultyDecls(int i) {
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false));
        var type = mock(UCELParser.TypeContext.class);
        UCELParser.VariableDeclContext node = mock(UCELParser.VariableDeclContext.class);
        ArrayList<UCELParser.VariableIDContext> varIDs = new ArrayList<>();
        varIDs.add(mock(UCELParser.VariableIDContext.class));
        varIDs.add(mock(UCELParser.VariableIDContext.class));
        varIDs.add(mock(UCELParser.VariableIDContext.class));

        when(type.accept(visitor)).thenReturn(true);
        when(node.type()).thenReturn(type);
        when(node.variableID()).thenReturn(varIDs);

        var actual = visitor.visitVariableDecl(node);

        verify(varIDs.get(i), times(1)).accept(visitor);
        assertFalse(actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {0,1,2})
    void variableDeclVisitAllVariableIDsAllSucceed(int i) {
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false));
        var type = mock(UCELParser.TypeContext.class);
        UCELParser.VariableDeclContext node = mock(UCELParser.VariableDeclContext.class);
        var varID1Mock = mock(UCELParser.VariableIDContext.class);
        var varID2Mock = mock(UCELParser.VariableIDContext.class);
        var varID3Mock = mock(UCELParser.VariableIDContext.class);
        ArrayList<UCELParser.VariableIDContext> varIDs = new ArrayList<>() {{ add(varID1Mock); add(varID2Mock); add(varID3Mock); }};

        when(type.accept(visitor)).thenReturn(true);
        when(varID1Mock.accept(visitor)).thenReturn(true);
        when(varID2Mock.accept(visitor)).thenReturn(true);
        when(varID3Mock.accept(visitor)).thenReturn(true);
        when(node.type()).thenReturn(type);
        when(node.variableID()).thenReturn(varIDs);

        var actual = visitor.visitVariableDecl(node);

        verify(varIDs.get(i), times(1)).accept(visitor);
        assertTrue(actual);
    }

    //endregion

    //region VariableID

    @Test
    void variableIDAddVariableToScope() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.VariableIDContext node = mock(UCELParser.VariableIDContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        visitor.visitVariableID(node);

        assertEquals(new DeclarationInfo(correctVariableName), variables.get(2));
    }

    @Test
    void variableIDVisitInitialiser() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.VariableIDContext node = mock(UCELParser.VariableIDContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        UCELParser.InitialiserContext initialiser = mock(UCELParser.InitialiserContext.class);

        when(node.initialiser()).thenReturn(initialiser);
        when(initialiser.accept(visitor)).thenReturn(true);

        visitor.visitVariableID(node);

        verify(initialiser, times(1)).accept(visitor);
    }

    @Test
    void variableIDVisitArrayDecl() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.VariableIDContext node = mock(UCELParser.VariableIDContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        ArrayList<UCELParser.ArrayDeclContext> arrayDecls = new ArrayList<>();

        UCELParser.ArrayDeclContext arrayDecl0 = mock(UCELParser.ArrayDeclContext.class);
        UCELParser.ArrayDeclContext arrayDecl1 = mock(UCELParser.ArrayDeclContext.class);
        UCELParser.ArrayDeclContext arrayDecl2 = mock(UCELParser.ArrayDeclContext.class);

        arrayDecls.add(arrayDecl0);
        arrayDecls.add(arrayDecl1);
        arrayDecls.add(arrayDecl2);

        when(node.arrayDecl()).thenReturn(arrayDecls);
        when(arrayDecl0.accept(visitor)).thenReturn(true);
        when(arrayDecl1.accept(visitor)).thenReturn(true);
        when(arrayDecl2.accept(visitor)).thenReturn(true);

        visitor.visitVariableID(node);

        verify(arrayDecl1, times(1)).accept(visitor);
    }

    @Test
    void variableIDSetVariableReference() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(incorrectVariableName));
        variables.add(new DeclarationInfo(incorrectVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.VariableIDContext node = mock(UCELParser.VariableIDContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        visitor.visitVariableID(node);

        assertEquals(node.reference, new DeclarationReference(0, 2));
    }

    @Test
    void variableIDReturnFalseOnDuplicate() {
        String uniqueVariableName = "uvn";
        String doubleicateVariableName = "dvn";

        ArrayList<DeclarationInfo> variables =new ArrayList<>();
        variables.add(new DeclarationInfo(uniqueVariableName));
        variables.add(new DeclarationInfo(doubleicateVariableName));
        ReferenceVisitor visitor = new ReferenceVisitor(new Scope(null, false, variables));

        UCELParser.VariableIDContext node = mock(UCELParser.VariableIDContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(doubleicateVariableName);
        when(node.ID()).thenReturn(idNode);

        boolean result = visitor.visitVariableID(node);

        assertFalse(result);
    }

    //endregion

    //region Block

    @Test
    void scopeForBlockIsAdded() {

        Scope parentScope = new Scope(null, false);
        ReferenceVisitor referenceVisitor = new ReferenceVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);

        referenceVisitor.visitBlock(block);

        assertTrue(block.scope instanceof Scope);
        assertNotEquals(block.scope, parentScope);
    }

    @Test
    void declarationsForBlockAreVisited() {

        Scope parentScope = new Scope(null, false);
        ReferenceVisitor referenceVisitor = new ReferenceVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        UCELParser.LocalDeclarationContext localDecl = mock(UCELParser.LocalDeclarationContext.class);
        ArrayList<UCELParser.LocalDeclarationContext> localDecls = new ArrayList<>();
        localDecls.add(localDecl);

        when(localDecl.accept(referenceVisitor)).thenReturn(true);
        when(block.localDeclaration()).thenReturn(localDecls);

        referenceVisitor.visitBlock(block);

        verify(localDecl, times(1)).accept(referenceVisitor);
    }

    @Test
    void statementsForBlockAreVisited() {

        Scope parentScope = new Scope(null, false);
        ReferenceVisitor referenceVisitor = new ReferenceVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        UCELParser.StatementContext statementContext = mock(UCELParser.StatementContext.class);
        ArrayList<UCELParser.StatementContext> statementContexts = new ArrayList<>();
        statementContexts.add(statementContext);

        when(statementContext.accept(referenceVisitor)).thenReturn(true);
        when(block.statement()).thenReturn(statementContexts);

        referenceVisitor.visitBlock(block);

        verify(statementContext, times(1)).accept(referenceVisitor);
    }

    //endregion

    //region iteration

    @Test
    void iterationReferenceSet() {
        String identifier = "a";
        Scope scope = mock(Scope.class);

        ReferenceVisitor referenceVisitor = new ReferenceVisitor(scope);

        UCELParser.IterationContext node = mock(UCELParser.IterationContext.class);
        TerminalNode id = mock(TerminalNode.class);
        UCELParser.StatementContext statement = mock(UCELParser.StatementContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        when(node.type()).thenReturn(type);
        when(node.statement()).thenReturn(statement);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(identifier);

        when(type.accept(referenceVisitor)).thenReturn(true);
        when(statement.accept(referenceVisitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(identifier, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {
            fail();
        }

        referenceVisitor.visitIteration(node);

        assertEquals(declRef, node.reference);
    }

    @Test
    void iterationStatementVisited() {
        String identifier = "a";
        Scope scope = mock(Scope.class);

        ReferenceVisitor referenceVisitor = new ReferenceVisitor(scope);

        UCELParser.IterationContext node = mock(UCELParser.IterationContext.class);
        TerminalNode id = mock(TerminalNode.class);
        UCELParser.StatementContext statement = mock(UCELParser.StatementContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        when(node.type()).thenReturn(type);
        when(node.statement()).thenReturn(statement);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(identifier);

        when(type.accept(referenceVisitor)).thenReturn(true);
        when(statement.accept(referenceVisitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(identifier, true)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {
            fail();
        }

        referenceVisitor.visitIteration(node);

        verify(statement, times(1)).accept(referenceVisitor);
    }

    //endregion

    //region increment/decrement

    @Test
    void incrementPostAcceptIDExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPostContext node = mock(UCELParser.IncrementPostContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPost(node);

        assertTrue(actual);
    }

    @Test
    void incrementPreAcceptIDExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPreContext node = mock(UCELParser.IncrementPreContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPre(node);

        assertTrue(actual);
    }

    @Test
    void decrementPostAcceptIDExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPostContext node = mock(UCELParser.DecrementPostContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPost(node);

        assertTrue(actual);
    }

    @Test
    void decrementPreAcceptExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPreContext node = mock(UCELParser.DecrementPreContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPre(node);

        assertTrue(actual);
    }

    @Test
    void incrementPostAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPostContext node = mock(UCELParser.IncrementPostContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPost(node);

        assertTrue(actual);
    }

    @Test
    void incrementPreAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPreContext node = mock(UCELParser.IncrementPreContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPre(node);

        assertTrue(actual);
    }

    @Test
    void decrementPostAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPostContext node = mock(UCELParser.DecrementPostContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPost(node);

        assertTrue(actual);
    }

    @Test
    void decrementPreAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPreContext node = mock(UCELParser.DecrementPreContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPre(node);

        assertTrue(actual);
    }

    @Test
    void incrementPostInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPostContext node = mock(UCELParser.IncrementPostContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPost(node);

        assertFalse(actual);
    }

    @Test
    void incrementPreInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.IncrementPreContext node = mock(UCELParser.IncrementPreContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitIncrementPre(node);

        assertFalse(actual);
    }

    @Test
    void decrementPostInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPostContext node = mock(UCELParser.DecrementPostContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPost(node);

        assertFalse(actual);
    }

    @Test
    void decrementPreInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.DecrementPreContext node = mock(UCELParser.DecrementPreContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.getRuleContext(UCELParser.ExpressionContext.class, 0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitDecrementPre(node);

        assertFalse(actual);
    }

    //endregion

    //region assignment
    @Test
    void assignmentAcceptIDExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.AssignExprContext node = mock(UCELParser.AssignExprContext.class);
        UCELParser.IdExprContext id = mock(UCELParser.IdExprContext.class);
        UCELParser.ExpressionContext expr = mock(UCELParser.IdExprContext.class);
        when(node.expression(0)).thenReturn(id);
        when(node.expression(1)).thenReturn(expr);
        when(expr.accept(visitor)).thenReturn(true);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitAssignExpr(node);

        assertTrue(actual);
    }

    @Test
    void assignmentPostAcceptStructExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.AssignExprContext node = mock(UCELParser.AssignExprContext.class);
        UCELParser.StructAccessContext id = mock(UCELParser.StructAccessContext.class);
        UCELParser.ExpressionContext expr = mock(UCELParser.ExpressionContext.class);
        when(node.expression(0)).thenReturn(id);
        when(node.expression(1)).thenReturn(expr);
        when(expr.accept(visitor)).thenReturn(true);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitAssignExpr(node);

        assertTrue(actual);
    }


    @Test
    void assignmentInvalidExpr() {
        ReferenceVisitor visitor = new ReferenceVisitor(mock(Scope.class));

        UCELParser.AssignExprContext node = mock(UCELParser.AssignExprContext.class);
        UCELParser.AddSubContext id = mock(UCELParser.AddSubContext.class);
        when(node.expression(0)).thenReturn(id);
        when(id.accept(visitor)).thenReturn(true);

        boolean actual = visitor.visitAssignExpr(node);

        assertFalse(actual);
    }

    //endregion

    //region Function

    @Test
    void functionVisitsType() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String funcIdentifier = "f";
        UCELParser.FunctionContext node = mock(UCELParser.FunctionContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(funcIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.block()).thenReturn(block);

        when(type.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(block.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(funcIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitFunction(node);

        verify(type, times(1)).accept(visitor);
    }

    @Test
    void functionVisitsParameters() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String funcIdentifier = "f";
        UCELParser.FunctionContext node = mock(UCELParser.FunctionContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(funcIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.block()).thenReturn(block);

        when(type.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(block.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(funcIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitFunction(node);

        verify(parameters, times(1)).accept(visitor);
    }

    @Test
    void functionVisitsBlock() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String funcIdentifier = "f";
        UCELParser.FunctionContext node = mock(UCELParser.FunctionContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(funcIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.block()).thenReturn(block);

        when(type.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(block.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(funcIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitFunction(node);

        verify(block, times(1)).accept(visitor);
    }

    @Test
    void functionSetsOccurrences() {
        Scope scope = mock(Scope.class);
        ReferenceVisitor visitor = new ReferenceVisitor(scope);

        String funcIdentifier = "f";
        UCELParser.FunctionContext node = mock(UCELParser.FunctionContext.class);
        UCELParser.TypeContext type = mock(UCELParser.TypeContext.class);
        UCELParser.ParametersContext parameters = mock(UCELParser.ParametersContext.class);
        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.type()).thenReturn(type);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(funcIdentifier);
        when(node.parameters()).thenReturn(parameters);
        when(node.block()).thenReturn(block);

        when(type.accept(visitor)).thenReturn(true);
        when(parameters.accept(visitor)).thenReturn(true);
        when(block.accept(visitor)).thenReturn(true);

        DeclarationReference declRef = new DeclarationReference(0,0);

        try {
            when(scope.isUnique(funcIdentifier, false)).thenReturn(true);
            when(scope.add(any())).thenReturn(declRef);
        } catch (Exception e) {fail(); }

        visitor.visitFunction(node);

        assertTrue(node.occurrences != null && node.occurrences instanceof ArrayList);
    }

    //endregion

}
