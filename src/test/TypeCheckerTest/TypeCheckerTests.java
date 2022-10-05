import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class TypeCheckerTests  {

    private static final Type INT_TYPE = new Type(Type.TypeEnum.intType);
    private static final Type DOUBLE_TYPE = new Type(Type.TypeEnum.doubleType);
    private static final Type BOOL_TYPE = new Type(Type.TypeEnum.boolType);
    private static final Type CHAR_TYPE = new Type(Type.TypeEnum.charType);
    private static final Type STRING_TYPE = new Type(Type.TypeEnum.stringType);
    private static final Type ERROR_TYPE = new Type(Type.TypeEnum.errorType);
    private static final Type INT_ARRAY_TYPE = new Type(Type.TypeEnum.intType, 1);
    private static final Type DOUBLE_ARRAY_TYPE = new Type(Type.TypeEnum.doubleType, 1);
    private static final Type BOOL_ARRAY_TYPE = new Type(Type.TypeEnum.boolType, 1);
    private static final Type CHAR_ARRAY_TYPE = new Type(Type.TypeEnum.charType, 1);
    private static final Type INVALID_TYPE = new Type(Type.TypeEnum.invalidType);
    private static final Type VOID_TYPE = new Type(Type.TypeEnum.voidType);
    private static final Type CHAN_TYPE = new Type(Type.TypeEnum.chanType);
    private static final Type STRUCT_TYPE = new Type(Type.TypeEnum.structType);
    private static final Type SCALAR_TYPE = new Type(Type.TypeEnum.scalarType);

    //region declaration

    @Test
    void declarationReturnErrorFromChild() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.DeclarationsContext node = mock(UCELParser.DeclarationsContext.class);
        ParseTree child0 = mock(ParseTree.class);
        ParseTree child1 = mock(ParseTree.class);
        ParseTree child2 = mock(ParseTree.class);

        when(child0.accept(visitor)).thenReturn(VOID_TYPE);
        when(child0.accept(visitor)).thenReturn(ERROR_TYPE);
        when(child0.accept(visitor)).thenReturn(VOID_TYPE);

        ArrayList<ParseTree> children = new ArrayList<>();
        children.add(child0);
        children.add(child1);
        children.add(child2);

        node.children = children;

        Type result = visitor.visitDeclarations(node);

        assertEquals(ERROR_TYPE, result);
    }

    //endregion

    //region block

    @Test
    void declarationsForBlockAreVisited() {

        Scope parentScope = new Scope(null, false);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        block.scope = new Scope(parentScope, false);

        UCELParser.LocalDeclarationContext localDecl = mock(UCELParser.LocalDeclarationContext.class);
        ArrayList<UCELParser.LocalDeclarationContext> localDecls = new ArrayList<>();
        localDecls.add(localDecl);

        when(localDecl.accept(typeCheckerVisitor)).thenReturn(INT_TYPE);
        when(block.localDeclaration()).thenReturn(localDecls);

        typeCheckerVisitor.visitBlock(block);

        verify(localDecl, times(1)).accept(typeCheckerVisitor);
    }

    @Test
    void statementsForBlockAreVisited() {

        Scope parentScope = new Scope(null, false);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        block.scope = new Scope(parentScope, false);

        UCELParser.StatementContext statementContext = mock(UCELParser.StatementContext.class);
        ArrayList<UCELParser.StatementContext> statementContexts = new ArrayList<>();
        statementContexts.add(statementContext);

        when(statementContext.accept(typeCheckerVisitor)).thenReturn(INT_TYPE);
        when(block.statement()).thenReturn(statementContexts);

        typeCheckerVisitor.visitBlock(block);

        verify(statementContext, times(1)).accept(typeCheckerVisitor);
    }

    @Test
    void returnsCorrectlyCommonType() {
        Scope parentScope = new Scope(null, false);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        block.scope = new Scope(parentScope, false);

        UCELParser.StatementContext statementContext0 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext1 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext2 = mock(UCELParser.StatementContext.class);
        ArrayList<UCELParser.StatementContext> statementContexts = new ArrayList<>();
        statementContexts.add(statementContext0);
        statementContexts.add(statementContext1);
        statementContexts.add(statementContext2);

        when(statementContext0.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(statementContext1.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(statementContext2.accept(typeCheckerVisitor)).thenReturn(INT_TYPE);
        when(block.statement()).thenReturn(statementContexts);

        Type result = typeCheckerVisitor.visitBlock(block);

        assertEquals(INT_TYPE, result);
    }

    @Test
    void returnsErrorTypeOnUnreachableCode() {
        Scope parentScope = new Scope(null, false);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        block.scope = new Scope(parentScope, false);

        UCELParser.StatementContext statementContext0 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext1 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext2 = mock(UCELParser.StatementContext.class);
        ArrayList<UCELParser.StatementContext> statementContexts = new ArrayList<>();
        statementContexts.add(statementContext0);
        statementContexts.add(statementContext1);
        statementContexts.add(statementContext2);

        when(statementContext0.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(statementContext1.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(statementContext2.accept(typeCheckerVisitor)).thenReturn(INT_TYPE);
        when(block.statement()).thenReturn(statementContexts);

        Type result = typeCheckerVisitor.visitBlock(block);

        assertEquals(INT_TYPE, result);
    }

    @Test
    void returnsCorrectlyVoidTypeFromStatements() {

        Scope parentScope = new Scope(null, false);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        block.scope = new Scope(parentScope, false);

        UCELParser.StatementContext statementContext0 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext1 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext2 = mock(UCELParser.StatementContext.class);
        ArrayList<UCELParser.StatementContext> statementContexts = new ArrayList<>();
        statementContexts.add(statementContext0);
        statementContexts.add(statementContext1);
        statementContexts.add(statementContext2);

        when(statementContext0.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(statementContext1.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(statementContext2.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(block.statement()).thenReturn(statementContexts);

        Type result = typeCheckerVisitor.visitBlock(block);

        assertEquals(VOID_TYPE, result);
    }

    @Test
    void returnsCorrectlyVoidTypeFromLackOfStatements() {

        Scope parentScope = new Scope(null, false);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        block.scope = new Scope(parentScope, false);

        UCELParser.StatementContext statementContext0 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext1 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext2 = mock(UCELParser.StatementContext.class);
        ArrayList<UCELParser.StatementContext> statementContexts = new ArrayList<>();
        when(block.statement()).thenReturn(statementContexts);

        Type result = typeCheckerVisitor.visitBlock(block);

        assertEquals(VOID_TYPE, result);
    }

    @Test
    void returnsErrorTypeFromStatement() {

        Scope parentScope = new Scope(null, false);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        block.scope = new Scope(parentScope, false);

        UCELParser.StatementContext statementContext0 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext1 = mock(UCELParser.StatementContext.class);
        UCELParser.StatementContext statementContext2 = mock(UCELParser.StatementContext.class);
        ArrayList<UCELParser.StatementContext> statementContexts = new ArrayList<>();
        statementContexts.add(statementContext0);
        statementContexts.add(statementContext1);
        statementContexts.add(statementContext2);

        when(statementContext0.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(statementContext1.accept(typeCheckerVisitor)).thenReturn(ERROR_TYPE);
        when(statementContext2.accept(typeCheckerVisitor)).thenReturn(INT_TYPE);
        when(block.statement()).thenReturn(statementContexts);

        Type result = typeCheckerVisitor.visitBlock(block);

        assertEquals(ERROR_TYPE, result);
    }

    @Test
    void returnsErrorTypeFromLocalDecl() {

        Scope parentScope = new Scope(null, false);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(parentScope);

        UCELParser.BlockContext block = mock(UCELParser.BlockContext.class);
        block.scope = new Scope(parentScope, false);

        UCELParser.LocalDeclarationContext statementContext0 = mock(UCELParser.LocalDeclarationContext.class);
        UCELParser.LocalDeclarationContext statementContext1 = mock(UCELParser.LocalDeclarationContext.class);
        UCELParser.LocalDeclarationContext statementContext2 = mock(UCELParser.LocalDeclarationContext.class);
        ArrayList<UCELParser.LocalDeclarationContext> statementContexts = new ArrayList<>();
        statementContexts.add(statementContext0);
        statementContexts.add(statementContext1);
        statementContexts.add(statementContext2);

        when(statementContext0.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(statementContext1.accept(typeCheckerVisitor)).thenReturn(ERROR_TYPE);
        when(statementContext2.accept(typeCheckerVisitor)).thenReturn(VOID_TYPE);
        when(block.localDeclaration()).thenReturn(statementContexts);

        Type result = typeCheckerVisitor.visitBlock(block);

        assertEquals(ERROR_TYPE, result);
    }

    //endregion

    //region IdExpr
    @Test
    void missingIdentifierDefinition() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();
        UCELParser.IdExprContext node = mock(UCELParser.IdExprContext.class);
        when(node.ID()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.ID)));
        Type actual = visitor.visitIdExpr(node);
        assertEquals(ERROR_TYPE, actual);
    }

    @Test
    void foundIntTypeIdentifierInScope() {
        var scope = new Scope(null, false);

        var variableName = "foo";
        var variable = new DeclarationInfo(variableName);
        variable.setType(INT_TYPE);
        scope.add(new DeclarationInfo(variableName));
        TypeCheckerVisitor visitor = new TypeCheckerVisitor(scope);
        UCELParser.IdExprContext node = mock(UCELParser.IdExprContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(variableName);
        when(node.ID()).thenReturn(idNode);

        when(node.ID()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.ID)));
        Type actual = visitor.visitIdExpr(node);
        assertEquals(INT_TYPE, actual);
    }
    //endregion

    //region LiteralExpr


    @Test
    void intLiteralTypedCorrectly() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.LiteralContext node = mock(UCELParser.LiteralContext.class);
        when(node.NAT()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NAT)));

        Type actual = visitor.visitLiteral(node);
        assertEquals(INT_TYPE, actual);
    }

    @Test
    void doubleLiteralTypedCorrectly() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.LiteralContext node = mock(UCELParser.LiteralContext.class);
        when(node.DOUBLE()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NAT)));

        Type actual = visitor.visitLiteral(node);
        assertEquals(DOUBLE_TYPE, actual);
    }

    @Test
    void boolLiteralTypedCorrectly() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.BooleanContext boolCtx = mock(UCELParser.BooleanContext.class);

        UCELParser.LiteralContext node = mock(UCELParser.LiteralContext.class);
        when(node.boolean_()).thenReturn(boolCtx);

        Type actual = visitor.visitLiteral(node);
        assertEquals(BOOL_TYPE, actual);
    }

    //endregion

    //region ArrayIndex
    @Test
    void arrayIndexErrorIfNotInt() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.ArrayIndexContext node = mock(UCELParser.ArrayIndexContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, BOOL_ARRAY_TYPE, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, CHAR_TYPE, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitArrayIndex(node);
        assertEquals(ERROR_TYPE, actual);
    }

    @Test
    void arrayIndexErrorIfNoArray() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.ArrayIndexContext node = mock(UCELParser.ArrayIndexContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, BOOL_TYPE, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, INT_TYPE, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitArrayIndex(node);
        assertEquals(ERROR_TYPE, actual);
    }

    @Test
    void arrayIndexReturnsArrayType() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.ArrayIndexContext node = mock(UCELParser.ArrayIndexContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, BOOL_ARRAY_TYPE, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, INT_TYPE, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitArrayIndex(node);
        assertEquals(BOOL_ARRAY_TYPE, actual);
    }


    //endregion

    //region MarkExpr
    //endregion

    //region Paren
    @ParameterizedTest(name = "{index} => using type {0} for parenthesis")
    @MethodSource("allTypes")
    void parenthesisExpectedType(Type inType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.ParenContext node = mock(UCELParser.ParenContext.class);
        var child = mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor);
        when(node.expression()).thenReturn(child);

        Type actual = visitor.visitParen(node);

        assertEquals(inType, actual);
    }
    //endregion

    //region StructAccess

    @Test
    void structAccessCorrectStructSetTableReference() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.StructAccessContext node = mock(UCELParser.StructAccessContext.class);
        Type[] structInternalTypes = new Type[]{INT_TYPE, STRING_TYPE};
        String[] structInternalIdentifiers = new String[]{incorrectVariableName, correctVariableName};
        Type type = new Type(Type.TypeEnum.structType, structInternalIdentifiers, structInternalTypes);
        var child = mockForVisitorResult(UCELParser.ExpressionContext.class, type, visitor);

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        when(node.expression()).thenReturn(child);

        Type unused = visitor.visitStructAccess(node);

        assertEquals(new DeclarationReference(-1, 1), node.reference);
    }

    @Test
    void structAccessCorrectStructReturnCorrectType() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.StructAccessContext node = mock(UCELParser.StructAccessContext.class);
        Type[] structInternalTypes = new Type[]{INT_TYPE, STRING_TYPE};
        String[] structInternalIdentifiers = new String[]{incorrectVariableName, correctVariableName};
        Type type = new Type(Type.TypeEnum.structType, structInternalIdentifiers, structInternalTypes);
        var child = mockForVisitorResult(UCELParser.ExpressionContext.class, type, visitor);

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        when(node.expression()).thenReturn(child);

        Type actualType = visitor.visitStructAccess(node);

        assertEquals(STRING_TYPE, actualType);
    }

    @Test
    void structAccessIncorrectStructReturnErrorType() {
        String invalidVariableName = "icvn";

        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.StructAccessContext node = mock(UCELParser.StructAccessContext.class);
        var child = mockForVisitorResult(UCELParser.ExpressionContext.class, INVALID_TYPE, visitor);

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(invalidVariableName);
        when(node.ID()).thenReturn(idNode);

        when(node.expression()).thenReturn(child);

        Type actualType = visitor.visitStructAccess(node);

        assertEquals(ERROR_TYPE, actualType);
    }

    //endregion

    //region Increment / Decrement
    //region IncrementPost
    @ParameterizedTest(name = "{index} => using type {0} for IncrementPost")
    @MethodSource("expectedIncrementPostTypes")
    void incrementPostExpectedOutType(Type inType, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.IncrementPostContext node = mock(UCELParser.IncrementPostContext.class);
        var child = mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor);
        when(node.expression()).thenReturn(child);

        Type actual = visitor.visitIncrementPost(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region IncrementPre
    @ParameterizedTest(name = "{index} => using type {0} for IncrementPost")
    @MethodSource("expectedIncrementPostTypes")
    void incrementPreExpectedOutType(Type inType, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.IncrementPreContext node = mock(UCELParser.IncrementPreContext.class);
        var child = mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor);
        when(node.expression()).thenReturn(child);

        Type actual = visitor.visitIncrementPre(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region DecrementPost
    @ParameterizedTest(name = "{index} => using type {0} for IncrementPost")
    @MethodSource("expectedIncrementPostTypes")
    void decrementPostExpectedOutType(Type inType, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.DecrementPostContext node = mock(UCELParser.DecrementPostContext.class);
        var child = mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor);
        when(node.expression()).thenReturn(child);

        Type actual = visitor.visitDecrementPost(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region DecrementPre
    @ParameterizedTest(name = "{index} => using type {0} for IncrementPost")
    @MethodSource("expectedIncrementPostTypes")
    void decrementPreExpectedOutType(Type inType, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.DecrementPreContext node = mock(UCELParser.DecrementPreContext.class);
        var child = mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor);
        when(node.expression()).thenReturn(child);

        Type actual = visitor.visitDecrementPre(node);

        assertEquals(returnType, actual);
    }
    //endregion

    private  static Stream<Arguments> expectedIncrementPostTypes() {

        return Stream.of(
                // Valid input
                Arguments.arguments(INT_TYPE, INT_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_TYPE),

                // Bad input
                Arguments.arguments(STRING_TYPE, ERROR_TYPE),
                Arguments.arguments(CHAN_TYPE, ERROR_TYPE),
                Arguments.arguments(SCALAR_TYPE, ERROR_TYPE),
                Arguments.arguments(STRUCT_TYPE, ERROR_TYPE),
                Arguments.arguments(VOID_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, ERROR_TYPE),

                // array (Also bad)
                Arguments.arguments(INT_ARRAY_TYPE, ERROR_TYPE)
        );
    }
    //endregion

    //region FuncCall
    // Functions Required: Well-defined, undefined, wrong parameters, error params
    @ParameterizedTest(name = "{index} ({0}) => {3} {2}({4}) -> {3}")
    @MethodSource("expectedFuncCallTypes")
    void FuncCall(String testName, Scope scope, String name, Type expectedReturnType, Type argsType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor(scope);

        // FuncContext
        final UCELParser.FuncCallContext funcCtx = mock(UCELParser.FuncCallContext.class);

        try {
            funcCtx.reference = scope.find(name, true);
        }
        catch (Exception e) {
            funcCtx.reference = null;
        }

        // Args
        final UCELParser.ArgumentsContext argsCtx = mockForVisitorResult(UCELParser.ArgumentsContext.class, argsType, visitor);
        when(funcCtx.arguments()).thenReturn(argsCtx);

        // Act
        Type actualReturnType = visitor.visitFuncCall(funcCtx);

        // Assert
        assertNotNull(actualReturnType);
        assertEquals(expectedReturnType.getEvaluationType(), actualReturnType.getEvaluationType());
    }
    private static Stream<Arguments> expectedFuncCallTypes() {
        ArrayList<Arguments> args = new ArrayList<Arguments>();
        // Scope scope, String name, Type expectedReturnType, Type argTypes

        // Valid types
        args.add(Arguments.arguments(
                "Valid types",
                scopeGen("func1", new Type(Type.TypeEnum.intType, new Type[] {})),
                "func1",
                INT_TYPE,
                new Type(Type.TypeEnum.invalidType, new Type[] {})
        ));
        args.add(Arguments.arguments(
                "Valid types",
                scopeGen("func1", new Type(Type.TypeEnum.boolType, new Type[] {STRING_TYPE})),
                "func1",
                BOOL_TYPE,
                new Type(Type.TypeEnum.invalidType, new Type[] {STRING_TYPE})
        ));

        // Invalid argument type
        args.add(Arguments.arguments(
                "Invalid argument type",
                scopeGen("func1", new Type(Type.TypeEnum.charType, new Type[] {STRING_TYPE})),
                "func1",
                ERROR_TYPE,
                new Type(Type.TypeEnum.invalidType, new Type[] {INT_TYPE})
        ));

        // Error input (Typically not possible in declaration)
        args.add(Arguments.arguments(
                "Error input",
                scopeGen("func1", new Type(Type.TypeEnum.charType, new Type[] {ERROR_TYPE})),
                "func1",
                ERROR_TYPE,
                new Type(Type.TypeEnum.invalidType, new Type[] {INT_TYPE})
        ));

        // Missing Argument
        args.add(Arguments.arguments(
                "Missing Argument",
                scopeGen("func1", new Type(Type.TypeEnum.charType, new Type[] {STRING_TYPE})),
                "func1",
                ERROR_TYPE,
                new Type(Type.TypeEnum.invalidType, new Type[] {})
        ));

        // Too Many Arguments
        args.add(Arguments.arguments(
                "Too Many Arguments",
                scopeGen("func1", new Type(Type.TypeEnum.charType, new Type[] {})),
                "func1",
                ERROR_TYPE,
                new Type(Type.TypeEnum.invalidType, new Type[] {STRING_TYPE})
        ));

        // Undefined
        args.add(Arguments.arguments(
                "Undefined",
                scopeGen("func1", new Type(Type.TypeEnum.charType, new Type[] {INT_TYPE})),
                "funcOther",
                ERROR_TYPE,
                new Type(Type.TypeEnum.invalidType, new Type[] {INT_TYPE})
        ));

        return args.stream();
    }
    private static Scope scopeGen(String name, Type type) {
        Scope scope = new Scope(null, false);

        scope.add(new DeclarationInfo(name, type));

        return scope;
    }
    //endregion

    //region Arguments
    @ParameterizedTest(name = "{index} => using type {0} for IncrementPost")
    @MethodSource("argumentTypesArguments")
    void argumentTypes(Type argTypes) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.ArgumentsContext node = mock(UCELParser.ArgumentsContext.class);

        LinkedList<UCELParser.ExpressionContext> mocks = new LinkedList<>();
        for (int i = 0; i < argTypes.getParameters().length; i++) {
            var c = mockForVisitorResult(UCELParser.ExpressionContext.class, argTypes.getParameters()[i], visitor);
            mocks.add(c);
        }

        when(node.expression()).thenReturn(mocks);

        Type actual = visitor.visitArguments(node);

        assertEquals(argTypes, actual);
    }
    private static Stream<Arguments> argumentTypesArguments() {

        ArrayList<Arguments> args = new ArrayList<Arguments>();
        args.add(Arguments.arguments(new Type(Type.TypeEnum.invalidType, new Type[] {})));
        args.add(Arguments.arguments(new Type(Type.TypeEnum.invalidType, new Type[] {DOUBLE_TYPE})));
        args.add(Arguments.arguments(new Type(Type.TypeEnum.invalidType, new Type[] {DOUBLE_TYPE, INT_TYPE})));
        args.add(Arguments.arguments(new Type(Type.TypeEnum.invalidType, new Type[] {DOUBLE_TYPE, INT_TYPE, BOOL_TYPE})));
        args.add(Arguments.arguments(new Type(Type.TypeEnum.invalidType, new Type[] {DOUBLE_TYPE, INT_TYPE, BOOL_TYPE, CHAR_TYPE})));

        // If it contains an error, base-type becomes error
        args.add(Arguments.arguments(new Type(Type.TypeEnum.errorType, new Type[] {ERROR_TYPE})));
        args.add(Arguments.arguments(new Type(Type.TypeEnum.errorType, new Type[] {DOUBLE_TYPE, ERROR_TYPE})));
        args.add(Arguments.arguments(new Type(Type.TypeEnum.errorType, new Type[] {ERROR_TYPE, INT_TYPE, BOOL_TYPE})));

        return args.stream();
    }



    //endregion

    //region UnaryExpr
    @ParameterizedTest(name = "{index} => using type {0} for unary +")
    @MethodSource("unaryPlusMinusNumberTypes")
    void unaryPlusExpressionTypedCorrectly(Type expectedType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.UnaryExprContext node = mock(UCELParser.UnaryExprContext.class);
        UCELParser.UnaryContext mockedUnary = mock(UCELParser.UnaryContext.class);
        UCELParser.ExpressionContext mockedExpression = mock(UCELParser.ExpressionContext.class);

        when(node.expression()).thenReturn(mockedExpression);
        when(visitor.visit(mockedExpression)).thenReturn(expectedType);
        when(mockedUnary.PLUS()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.PLUS)));
        when(node.unary()).thenReturn(mockedUnary);

        Type actualType = visitor.visitUnaryExpr(node);

        assertEquals(expectedType, actualType);
    }

    @ParameterizedTest(name = "{index} => using type {0} for unary -")
    @MethodSource("unaryPlusMinusNumberTypes")
    void unaryMinusExpressionTypedCorrectly(Type expectedType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.UnaryExprContext node = mock(UCELParser.UnaryExprContext.class);
        UCELParser.UnaryContext mockedUnary = mock(UCELParser.UnaryContext.class);
        UCELParser.ExpressionContext mockedExpression = mock(UCELParser.ExpressionContext.class);

        when(node.expression()).thenReturn(mockedExpression);
        when(visitor.visit(mockedExpression)).thenReturn(expectedType);
        when(mockedUnary.MINUS()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.MINUS)));
        when(node.unary()).thenReturn(mockedUnary);

        Type actualType = visitor.visitUnaryExpr(node);

        assertEquals(expectedType, actualType);
    }

    @ParameterizedTest(name = "{index} => using type {0} for unary Neg")
    @MethodSource("unaryNotNegTypes")
    void unaryNegExpressionTypedCorrectly(Type expectedType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.UnaryExprContext node = mock(UCELParser.UnaryExprContext.class);
        UCELParser.UnaryContext mockedUnary = mock(UCELParser.UnaryContext.class);
        UCELParser.ExpressionContext mockedExpression = mock(UCELParser.ExpressionContext.class);

        when(node.expression()).thenReturn(mockedExpression);
        when(visitor.visit(mockedExpression)).thenReturn(expectedType);
        when(mockedUnary.NEG()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NEG)));
        when(node.unary()).thenReturn(mockedUnary);

        Type actualType = visitor.visitUnaryExpr(node);

        assertEquals(expectedType, actualType);
    }

    @ParameterizedTest(name = "{index} => using type {0} for unary Not")
    @MethodSource("unaryNotNegTypes")
    void unaryNotExpressionTypedCorrectly(Type expectedType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.UnaryExprContext node = mock(UCELParser.UnaryExprContext.class);
        UCELParser.UnaryContext mockedUnary = mock(UCELParser.UnaryContext.class);
        UCELParser.ExpressionContext mockedExpression = mock(UCELParser.ExpressionContext.class);

        when(node.expression()).thenReturn(mockedExpression);
        when(visitor.visit(mockedExpression)).thenReturn(expectedType);
        when(mockedUnary.NOT()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NOT)));
        when(node.unary()).thenReturn(mockedUnary);

        Type actualType = visitor.visitUnaryExpr(node);

        assertEquals(expectedType, actualType);
    }

    @ParameterizedTest(name = "{index} => using wrong type {0} for unary Not")
    @MethodSource("unaryPlusMinusNumberTypes")
    void unaryNotWrongTypesReturnsErrorType(Type wrongType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.UnaryExprContext node = mock(UCELParser.UnaryExprContext.class);
        UCELParser.UnaryContext mockedUnary = mock(UCELParser.UnaryContext.class);
        UCELParser.ExpressionContext mockedExpression = mock(UCELParser.ExpressionContext.class);

        when(node.expression()).thenReturn(mockedExpression);
        when(visitor.visit(mockedExpression)).thenReturn(wrongType);
        when(mockedUnary.NOT()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NOT)));
        when(node.unary()).thenReturn(mockedUnary);

        Type actualType = visitor.visitUnaryExpr(node);

        assertEquals(ERROR_TYPE, actualType);
    }

    @ParameterizedTest(name = "{index} => using wrong type {0} for unary +")
    @MethodSource("unaryNotNegTypes")
    void unaryPlusWrongTypesReturnsErrorType(Type wrongType){
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.UnaryExprContext node = mock(UCELParser.UnaryExprContext.class);
        UCELParser.UnaryContext mockedUnary = mock(UCELParser.UnaryContext.class);
        UCELParser.ExpressionContext mockedExpression = mock(UCELParser.ExpressionContext.class);

        when(node.expression()).thenReturn(mockedExpression);
        when(visitor.visit(mockedExpression)).thenReturn(wrongType);
        when(mockedUnary.PLUS()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.PLUS)));
        when(node.unary()).thenReturn(mockedUnary);

        Type actualType = visitor.visitUnaryExpr(node);

        assertEquals(ERROR_TYPE, actualType);
    }

    @ParameterizedTest(name = "{index} => using type {0} for unary Minus")
    @MethodSource("unaryNotNegTypes")
    void unaryMinusWrongTypesReturnsErrorType(Type wrongType){
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.UnaryExprContext node = mock(UCELParser.UnaryExprContext.class);
        UCELParser.UnaryContext mockedUnary = mock(UCELParser.UnaryContext.class);
        UCELParser.ExpressionContext mockedExpression = mock(UCELParser.ExpressionContext.class);

        when(node.expression()).thenReturn(mockedExpression);
        when(visitor.visit(mockedExpression)).thenReturn(wrongType);
        when(mockedUnary.MINUS()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.MINUS)));
        when(node.unary()).thenReturn(mockedUnary);

        Type actualType = visitor.visitUnaryExpr(node);

        assertEquals(ERROR_TYPE, actualType);
    }

    @ParameterizedTest(name = "{index} => using wrong type {0} for unary Neg")
    @MethodSource("unaryPlusMinusNumberTypes")
    void unaryNegWrongTypesReturnsErrorType(Type wrongType){
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.UnaryExprContext node = mock(UCELParser.UnaryExprContext.class);
        UCELParser.UnaryContext mockedUnary = mock(UCELParser.UnaryContext.class);
        UCELParser.ExpressionContext mockedExpression = mock(UCELParser.ExpressionContext.class);

        when(node.expression()).thenReturn(mockedExpression);
        when(visitor.visit(mockedExpression)).thenReturn(wrongType);
        when(mockedUnary.NEG()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NEG)));
        when(node.unary()).thenReturn(mockedUnary);

        Type actualType = visitor.visitUnaryExpr(node);

        assertEquals(ERROR_TYPE, actualType);
    }

    private static Stream<Arguments> unaryNotNegTypes() {
        return Stream.of(
                Arguments.arguments(BOOL_TYPE)
        );
    }

    private static Stream<Arguments> unaryPlusMinusNumberTypes() {
        return Stream.of(
                Arguments.arguments(INT_TYPE),
                Arguments.arguments(DOUBLE_TYPE)
        );
    }

    //endregion

    //region MultDiv
    @ParameterizedTest(name = "{index} => using type {0} and type {1} with mult/div")
    @MethodSource("multDivTypes")
    void multDivTyped(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.MultDivContext node = mock(UCELParser.MultDivContext.class);

        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);
        
        Type actual = visitor.visitMultDiv(node);

        assertEquals(returnType, actual);
    }

    private static Stream<Arguments> multDivTypes() {
        return Stream.of(
                Arguments.arguments(INT_TYPE, INT_TYPE, INT_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_TYPE, DOUBLE_TYPE),

                Arguments.arguments(INT_TYPE, DOUBLE_TYPE, DOUBLE_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INT_TYPE, DOUBLE_TYPE),

                Arguments.arguments(INT_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, DOUBLE_TYPE, ERROR_TYPE),

                Arguments.arguments(INT_TYPE, INT_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_ARRAY_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_ARRAY_TYPE, DOUBLE_TYPE, ERROR_TYPE),

                Arguments.arguments(INT_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, DOUBLE_TYPE, ERROR_TYPE)
        );
    }

    //endregion

    //region AddSub
    @ParameterizedTest(name = "{index} => using type {0} + type {1} with plus/minus")
    @MethodSource("addSubTypes")
    void addSubTyped(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.AddSubContext node = mock(UCELParser.AddSubContext.class);

        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitAddSub(node);

        assertEquals(returnType, actual);
    }

    private static Stream<Arguments> addSubTypes() {
        return Stream.of(
                Arguments.arguments(INT_TYPE, INT_TYPE, INT_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INT_TYPE, DOUBLE_TYPE),
                Arguments.arguments(INT_TYPE, DOUBLE_TYPE, DOUBLE_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_TYPE, DOUBLE_TYPE),
                Arguments.arguments(STRING_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_TYPE, INT_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(STRING_TYPE, STRING_TYPE, ERROR_TYPE)
        );
    }
    //endregion

    //region MinMax
    @ParameterizedTest(name = "{index} => using {0} and {1} with min/max operator expecting {2}")
    @MethodSource("minMaxTypes")
    void minMaxTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.MinMaxContext node = mock(UCELParser.MinMaxContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitMinMax(node);

        assertEquals(returnType, actual);
    }

    private static Stream<Arguments> minMaxTypes() {
        return Stream.of(
                Arguments.arguments(INT_TYPE, INT_TYPE, INT_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_TYPE, DOUBLE_TYPE),

                Arguments.arguments(INT_TYPE, DOUBLE_TYPE, DOUBLE_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INT_TYPE, DOUBLE_TYPE),

                Arguments.arguments(INT_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, DOUBLE_TYPE, ERROR_TYPE),

                Arguments.arguments(INT_TYPE, INT_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_ARRAY_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_ARRAY_TYPE, DOUBLE_TYPE, ERROR_TYPE),

                Arguments.arguments(INT_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, DOUBLE_TYPE, ERROR_TYPE)
        );
    }
    //endregion

    //region RelExpr
    @ParameterizedTest(name = "{index} => using {0} and {1} with relational operator expecting {2}")
    @MethodSource("relTypes")
    void relExprTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.RelExprContext node = mock(UCELParser.RelExprContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitRelExpr(node);

        assertEquals(returnType, actual);
    }

    private static Stream<Arguments> relTypes() {
        return Stream.of(
                Arguments.arguments(BOOL_TYPE, BOOL_TYPE, BOOL_TYPE),
                Arguments.arguments(INT_TYPE, INT_TYPE, BOOL_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_TYPE, BOOL_TYPE),
                Arguments.arguments(CHAR_TYPE, CHAR_TYPE, BOOL_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INT_TYPE, BOOL_TYPE),
                Arguments.arguments(INT_TYPE, DOUBLE_TYPE, BOOL_TYPE),

                // Bool to int, not allowed
                Arguments.arguments(INT_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, DOUBLE_TYPE, ERROR_TYPE),

                // Bad types
                Arguments.arguments(BOOL_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, DOUBLE_TYPE, ERROR_TYPE),
                Arguments.arguments(CHAR_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, CHAR_TYPE, ERROR_TYPE),

                // Bad types (arrays)
                Arguments.arguments(BOOL_ARRAY_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_ARRAY_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_ARRAY_TYPE, DOUBLE_TYPE, ERROR_TYPE),
                Arguments.arguments(CHAR_ARRAY_TYPE, CHAR_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, BOOL_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_TYPE, INT_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(CHAR_TYPE, CHAR_ARRAY_TYPE, ERROR_TYPE),

                // Bad types (errors)
                Arguments.arguments(BOOL_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, DOUBLE_TYPE, ERROR_TYPE),
                Arguments.arguments(CHAR_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, CHAR_TYPE, ERROR_TYPE)
        );
    }

    //endregion

    //region EqExpr
    @ParameterizedTest(name = "{index} => using {0} and {1} with equality operator expecting {2}")
    @MethodSource("eqTypes")
    void eqExprTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.EqExprContext node = mock(UCELParser.EqExprContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitEqExpr(node);

        assertEquals(returnType, actual);
    }

    private static Stream<Arguments> eqTypes() {
        return Stream.of(
                Arguments.arguments(BOOL_TYPE, BOOL_TYPE, BOOL_TYPE),
                Arguments.arguments(INT_TYPE, INT_TYPE, BOOL_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_TYPE, BOOL_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INT_TYPE, BOOL_TYPE),
                Arguments.arguments(INT_TYPE, DOUBLE_TYPE, BOOL_TYPE),

                // Bad types
                Arguments.arguments(BOOL_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, DOUBLE_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, INT_TYPE, ERROR_TYPE),

                //Bad types (arrays)
                Arguments.arguments(BOOL_ARRAY_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, BOOL_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_ARRAY_TYPE, DOUBLE_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, DOUBLE_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_ARRAY_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_TYPE, INT_ARRAY_TYPE, ERROR_TYPE),

                // Bad types (errors)
                Arguments.arguments(BOOL_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, DOUBLE_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, ERROR_TYPE, ERROR_TYPE)
        );
    }
    //endregion

    //region Bit Expressions
    //region BitShift
    @ParameterizedTest(name = "{index} => using {0} and {1} with bit shift expecting {2}")
    @MethodSource("bitTypes")
    void bitshiftTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.BitshiftContext node = mock(UCELParser.BitshiftContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitBitshift(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region BitAnd
    @ParameterizedTest(name = "{index} => using {0} and {1} with bit and expecting {2}")
    @MethodSource("bitTypes")
    void bitAndTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.BitAndContext node = mock(UCELParser.BitAndContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitBitAnd(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region BitXor
    @ParameterizedTest(name = "{index} => using {0} and {1} with bit xor expecting {2}")
    @MethodSource("bitTypes")
    void bitXorTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.BitXorContext node = mock(UCELParser.BitXorContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitBitXor(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region BitOr
    @ParameterizedTest(name = "{index} => using {0} and {1} with bit or expecting {2}")
    @MethodSource("bitTypes")
    void bitOrTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.BitOrContext node = mock(UCELParser.BitOrContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitBitOr(node);

        assertEquals(returnType, actual);
    }
    //endregion
    private static Stream<Arguments> bitTypes() {
        return Stream.of(
                Arguments.arguments(INT_TYPE, INT_TYPE, INT_TYPE),

                Arguments.arguments(INT_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, INT_TYPE, ERROR_TYPE),

                Arguments.arguments(INT_TYPE, INT_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(INT_ARRAY_TYPE, INT_TYPE, ERROR_TYPE),

                Arguments.arguments(INT_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, ERROR_TYPE, ERROR_TYPE)
        );
    }
    //endregion

    //region Logical expressions
    //region LogAnd
    @ParameterizedTest(name = "{index} => using {0} and {1} with logical and expecting {2}")
    @MethodSource("logTypes")
    void logAndTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.LogAndContext node = mock(UCELParser.LogAndContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitLogAnd(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region LogOr
    @ParameterizedTest(name = "{index} => using {0} and {1} with logical or expecting {2}")
    @MethodSource("logTypes")
    void logOrTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.LogOrContext node = mock(UCELParser.LogOrContext.class);
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitLogOr(node);

        assertEquals(returnType, actual);
    }
    //endregion
    private static Stream<Arguments> logTypes() { //TODO: consider letting char comparable with non-char
        return Stream.of(
                Arguments.arguments(BOOL_TYPE, BOOL_TYPE, BOOL_TYPE),
                Arguments.arguments(BOOL_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_ARRAY_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, BOOL_ARRAY_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_ARRAY_TYPE, INVALID_TYPE, ERROR_TYPE),
                Arguments.arguments(INVALID_TYPE, BOOL_ARRAY_TYPE, ERROR_TYPE),

                Arguments.arguments(ERROR_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, ERROR_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, ERROR_TYPE, ERROR_TYPE)
        );
    }
    //endregion

    //region Conditional
    @ParameterizedTest(name = "{index} => using {0} ? {1} : {2}; expecting {3}")
    @MethodSource("expectedConditionalExpressionTypes")
    void conditionalExpressionTypes(Type conditionType, Type leftReturnType, Type rightReturnType, Type expectedReturnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.ConditionalContext node = mock(UCELParser.ConditionalContext.class);

        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, conditionType, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, leftReturnType, visitor);
        var child3 = mockForVisitorResult(UCELParser.ExpressionContext.class, rightReturnType, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);
        when(node.expression(2)).thenReturn(child3);

        Type actual = visitor.visitConditional(node);

        assertEquals(expectedReturnType, actual);
    }

    private  static Stream<Arguments> expectedConditionalExpressionTypes() {

        return Stream.of(
                // Conditions:
                // Conditions - Valid
                Arguments.arguments(BOOL_TYPE, INT_TYPE, INT_TYPE, INT_TYPE),

                // Conditions - Invalid
                Arguments.arguments(INT_TYPE, INT_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(DOUBLE_TYPE, INT_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(CHAN_TYPE, INT_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(VOID_TYPE, INT_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(ERROR_TYPE, INT_TYPE, INT_TYPE, ERROR_TYPE),

                // Unmatched Return Types
                // Unmatched Return Types - Valid
                Arguments.arguments(BOOL_TYPE, INT_TYPE, DOUBLE_TYPE, DOUBLE_TYPE),
                Arguments.arguments(BOOL_TYPE, DOUBLE_TYPE, INT_TYPE, DOUBLE_TYPE),

                // Unmatched Return Types - Invalid
                Arguments.arguments(BOOL_TYPE, INT_TYPE, BOOL_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, BOOL_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, INT_TYPE, CHAN_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, CHAN_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, INT_TYPE, VOID_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, VOID_TYPE, INT_TYPE, ERROR_TYPE),

                // Error in input
                Arguments.arguments(ERROR_TYPE, INT_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, ERROR_TYPE, INT_TYPE, ERROR_TYPE),
                Arguments.arguments(BOOL_TYPE, INT_TYPE, ERROR_TYPE, ERROR_TYPE)
        );
    }

    //endregion

    //region VerificationExpr
    //endregion

    //region Helper methods

    private<T extends RuleContext> T mockForVisitorResult(final Class<T> nodeType, final Type visitResult, TypeCheckerVisitor visitor) {
        final T mock = mock(nodeType);
        when(mock.accept(visitor)).thenReturn(visitResult);
        return mock;
    }
    //endregion

    //region Arguments for parameterized tests

    private static Stream<Arguments> allTypes() {

        ArrayList<Arguments> args = new ArrayList<Arguments>();

        // One of each
        for(Type.TypeEnum t : Type.TypeEnum.values()) {
            args.add(Arguments.arguments(new Type(t)));
        }

        // A couple of array types
        args.add(Arguments.arguments(BOOL_TYPE, 1));
        args.add(Arguments.arguments(INT_TYPE, 2));
        args.add(Arguments.arguments(DOUBLE_TYPE, 3));
        args.add(Arguments.arguments(STRUCT_TYPE, 4));

        return args.stream();
    }
    //endregion
}
