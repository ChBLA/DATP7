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
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TypeCheckerTests  {

    private static final Type intType = new Type(Type.TypeEnum.intType);
    private static final Type doubleType = new Type(Type.TypeEnum.doubleType);
    private static final Type boolType = new Type(Type.TypeEnum.boolType);
    private static final Type charType = new Type(Type.TypeEnum.charType);
    private static final Type stringType = new Type(Type.TypeEnum.stringType);
    private static final Type errorType = new Type(Type.TypeEnum.errorType);
    private static final Type intArrayType = new Type(Type.TypeEnum.intType, 1);
    private static final Type doubleArrayType = new Type(Type.TypeEnum.doubleType, 1);
    private static final Type boolArrayType = new Type(Type.TypeEnum.boolType, 1);
    private static final Type charArrayType = new Type(Type.TypeEnum.charType, 1);
    private static final Type invalidType = new Type(Type.TypeEnum.invalidType);
    private static final Type voidType = new Type(Type.TypeEnum.voidType);
    private static final Type chanType = new Type(Type.TypeEnum.chanType);
    private static final Type structType = new Type(Type.TypeEnum.structType);
    private static final Type scalarType = new Type(Type.TypeEnum.scalarType);

    //region IdExpr
    @Test
    void MissingIdentifierDefinition() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();
        UCELParser.IdExprContext node = mock(UCELParser.IdExprContext.class);
        when(node.ID()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.ID)));
        Type actual = visitor.visitIdExpr(node);
        assertEquals(errorType, actual);
    }


    void FoundIntTypeIdentifierInScope() {
        var scope = new Scope(null, false);

        var variableName = "foo";
        var variable = new Variable(variableName);
        variable.setType(intType);
        scope.add(new Variable(variableName));
        TypeCheckerVisitor visitor = new TypeCheckerVisitor(scope);
        UCELParser.IdExprContext node = mock(UCELParser.IdExprContext.class);
        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(variableName);
        when(node.ID()).thenReturn(idNode);

        when(node.ID()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.ID)));
        Type actual = visitor.visitIdExpr(node);
        assertEquals(intType, actual);
    }
    //endregion

    //region LiteralExpr


    @Test
    void IntLiteralTypedCorrectly() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.LiteralContext node = mock(UCELParser.LiteralContext.class);
        when(node.NAT()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NAT)));

        Type actual = visitor.visitLiteral(node);
        assertEquals(intType, actual);
    }

    @Test
    void DoubleLiteralTypedCorrectly() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.LiteralContext node = mock(UCELParser.LiteralContext.class);
        when(node.DOUBLE()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NAT)));

        Type actual = visitor.visitLiteral(node);
        assertEquals(doubleType, actual);
    }

    @Test
    void BoolLiteralTypedCorrectly() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.BooleanContext boolCtx = mock(UCELParser.BooleanContext.class);

        UCELParser.LiteralContext node = mock(UCELParser.LiteralContext.class);
        when(node.boolean_()).thenReturn(boolCtx);

        Type actual = visitor.visitLiteral(node);
        assertEquals(boolType, actual);
    }

    //endregion

    //region ArrayIndex
    @Test
    void ArrayIndexErrorIfNotInt() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.ArrayIndexContext node = mock(UCELParser.ArrayIndexContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, boolArrayType, visitor));
        children.add(mockForVisitorResult(UCELParser.ArrayIndexContext.class, charType, visitor));
        node.children = children;

        Type actual = visitor.visitArrayIndex(node);
        assertEquals(errorType, actual);
    }

    @Test
    void ArrayIndexErrorIfNoArray() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.ArrayIndexContext node = mock(UCELParser.ArrayIndexContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, boolType, visitor));
        children.add(mockForVisitorResult(UCELParser.ArrayIndexContext.class, intType, visitor));
        node.children = children;

        Type actual = visitor.visitArrayIndex(node);
        assertEquals(errorType, actual);
    }

    @Test
    void ArrayIndexReturnsArrayType() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.ArrayIndexContext node = mock(UCELParser.ArrayIndexContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, boolArrayType, visitor));
        children.add(mockForVisitorResult(UCELParser.ArrayIndexContext.class, intType, visitor));
        node.children = children;

        Type actual = visitor.visitArrayIndex(node);
        assertEquals(boolArrayType, actual);
    }


    //endregion

    //region MarkExpr
    //endregion

    //region Paren
    @ParameterizedTest(name = "{index} => using type {0} for parenthesis")
    @MethodSource("allTypes")
    void ParenthesisExpectedType(Type inType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.ParenContext node = mock(UCELParser.ParenContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor));
        node.children = children;

        Type actual = visitor.visitParen(node);

        assertEquals(inType, actual);
    }
    //endregion

    //region StructAccess

    @Test
    void StructAccessCorrectStructSetTableReference() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.StructAccessContext node = mock(UCELParser.StructAccessContext.class);
        List<ParseTree> children = new ArrayList<>();
        Type[] structInternalTypes = new Type[]{intType, stringType};
        String[] structInternalIdentifiers = new String[]{incorrectVariableName, correctVariableName};
        Type type = new Type(Type.TypeEnum.structType, structInternalIdentifiers, structInternalTypes);
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, type, visitor));

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        node.children = children;

        Type unused = visitor.visitStructAccess(node);

        assertEquals(new TableReference(-1, 1), node.reference);
    }

    @Test
    void StructAccessCorrectStructReturnCorrectType() {
        String correctVariableName = "cvn";
        String incorrectVariableName = "icvn";

        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.StructAccessContext node = mock(UCELParser.StructAccessContext.class);
        List<ParseTree> children = new ArrayList<>();
        Type[] structInternalTypes = new Type[]{intType, stringType};
        String[] structInternalIdentifiers = new String[]{incorrectVariableName, correctVariableName};
        Type type = new Type(Type.TypeEnum.structType, structInternalIdentifiers, structInternalTypes);
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, type, visitor));

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(correctVariableName);
        when(node.ID()).thenReturn(idNode);

        node.children = children;

        Type actualType = visitor.visitStructAccess(node);

        assertEquals(stringType, actualType);
    }

    @Test
    void StructAccessIncorrectStructReturnErrorType() {
        String invalidVariableName = "icvn";

        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.StructAccessContext node = mock(UCELParser.StructAccessContext.class);
        List<ParseTree> children = new ArrayList<>();
        Type type = invalidType;
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, type, visitor));

        TerminalNode idNode = mock(TerminalNode.class);
        when(idNode.getText()).thenReturn(invalidVariableName);
        when(node.ID()).thenReturn(idNode);

        node.children = children;

        Type actualType = visitor.visitStructAccess(node);

        assertEquals(errorType, actualType);
    }

    //endregion

    //region Increment / Decrement
    //region IncrementPost
    @ParameterizedTest(name = "{index} => using type {0} for IncrementPost")
    @MethodSource("expectedIncrementPostTypes")
    void IncrementPostExpectedOutType(Type inType, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.IncrementPostContext node = mock(UCELParser.IncrementPostContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor));
        node.children = children;

        Type actual = visitor.visitIncrementPost(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region IncrementPre
    @ParameterizedTest(name = "{index} => using type {0} for IncrementPost")
    @MethodSource("expectedIncrementPostTypes")
    void IncrementPreExpectedOutType(Type inType, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.IncrementPreContext node = mock(UCELParser.IncrementPreContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor));
        node.children = children;

        Type actual = visitor.visitIncrementPre(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region DecrementPost
    @ParameterizedTest(name = "{index} => using type {0} for IncrementPost")
    @MethodSource("expectedIncrementPostTypes")
    void DecrementPostExpectedOutType(Type inType, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.DecrementPostContext node = mock(UCELParser.DecrementPostContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor));
        node.children = children;

        Type actual = visitor.visitDecrementPost(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region DecrementPre
    @ParameterizedTest(name = "{index} => using type {0} for IncrementPost")
    @MethodSource("expectedIncrementPostTypes")
    void DecrementPreExpectedOutType(Type inType, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.DecrementPreContext node = mock(UCELParser.DecrementPreContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, inType, visitor));
        node.children = children;

        Type actual = visitor.visitDecrementPre(node);

        assertEquals(returnType, actual);
    }
    //endregion
    //endregion
    
    //region FuncCall

    //endregion

    //region UnaryExpr
    @ParameterizedTest(name = "{index} => using type {0} for unary +")
    @MethodSource("unaryPlusMinusNumberTypes")
    void UnaryPlusExpressionTypedCorrectly(Type expectedType) {
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
    void UnaryMinusExpressionTypedCorrectly(Type expectedType) {
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
    void UnaryNegExpressionTypedCorrectly(Type expectedType) {
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
    void UnaryNotExpressionTypedCorrectly(Type expectedType) {
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
    void UnaryNotWrongTypesReturnsErrorType(Type wrongType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.UnaryExprContext node = mock(UCELParser.UnaryExprContext.class);
        UCELParser.UnaryContext mockedUnary = mock(UCELParser.UnaryContext.class);
        UCELParser.ExpressionContext mockedExpression = mock(UCELParser.ExpressionContext.class);

        when(node.expression()).thenReturn(mockedExpression);
        when(visitor.visit(mockedExpression)).thenReturn(wrongType);
        when(mockedUnary.NOT()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NOT)));
        when(node.unary()).thenReturn(mockedUnary);

        Type actualType = visitor.visitUnaryExpr(node);

        assertEquals(errorType, actualType);
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

        assertEquals(errorType, actualType);
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

        assertEquals(errorType, actualType);
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

        assertEquals(errorType, actualType);
    }



    //endregion

    //region MultDiv
    @ParameterizedTest(name = "{index} => using type {0} and type {1} with mult/div")
    @MethodSource("multDivTypes")
    void MultDivTyped(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.MultDivContext node = mock(UCELParser.MultDivContext.class);
        List<ParseTree> children = new ArrayList<>();
        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);
        
        Type actual = visitor.visitMultDiv(node);

        assertEquals(returnType, actual);
    }

    private static Stream<Arguments> multDivTypes() {
        return Stream.of(
                Arguments.arguments(intType, intType, intType),
                Arguments.arguments(doubleType, doubleType, doubleType),

                Arguments.arguments(intType, doubleType, doubleType),
                Arguments.arguments(doubleType, intType, doubleType),

                Arguments.arguments(intType, invalidType, errorType),
                Arguments.arguments(invalidType, intType, errorType),
                Arguments.arguments(doubleType, invalidType, errorType),
                Arguments.arguments(invalidType, doubleType, errorType),

                Arguments.arguments(intType, intArrayType, errorType),
                Arguments.arguments(intArrayType, intType, errorType),
                Arguments.arguments(doubleType, doubleArrayType, errorType),
                Arguments.arguments(doubleArrayType, doubleType, errorType),

                Arguments.arguments(intType, errorType, errorType),
                Arguments.arguments(errorType, intType, errorType),
                Arguments.arguments(doubleType, errorType, errorType),
                Arguments.arguments(errorType, doubleType, errorType)
        );
    }

    //endregion

    //region AddSub
    @ParameterizedTest(name = "{index} => using type {0} + type {1} with plus/minus")
    @MethodSource("addSubTypes")
    void AddSubTyped(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.AddSubContext node = mock(UCELParser.AddSubContext.class);
        List<ParseTree> children = new ArrayList<>();

        var child1 = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var child2 = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        when(node.expression(0)).thenReturn(child1);
        when(node.expression(1)).thenReturn(child2);

        Type actual = visitor.visitAddSub(node);

        assertEquals(returnType, actual);
    }

    //endregion

    //region MinMax
    @ParameterizedTest(name = "{index} => using {0} and {1} with min/max operator expecting {2}")
    @MethodSource("minMaxTypes")
    void MinMaxTypes(Type left, Type right, Type returnType) {
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
                Arguments.arguments(intType, intType, intType),
                Arguments.arguments(doubleType, doubleType, doubleType),

                Arguments.arguments(intType, doubleType, doubleType),
                Arguments.arguments(doubleType, intType, doubleType),

                Arguments.arguments(intType, invalidType, errorType),
                Arguments.arguments(invalidType, intType, errorType),
                Arguments.arguments(doubleType, invalidType, errorType),
                Arguments.arguments(invalidType, doubleType, errorType),

                Arguments.arguments(intType, intArrayType, errorType),
                Arguments.arguments(intArrayType, intType, errorType),
                Arguments.arguments(doubleType, doubleArrayType, errorType),
                Arguments.arguments(doubleArrayType, doubleType, errorType),

                Arguments.arguments(intType, errorType, errorType),
                Arguments.arguments(errorType, intType, errorType),
                Arguments.arguments(doubleType, errorType, errorType),
                Arguments.arguments(errorType, doubleType, errorType)
        );
    }
    //endregion

    //region RelExpr
    @ParameterizedTest(name = "{index} => using {0} and {1} with relational operator expecting {2}")
    @MethodSource("relTypes")
    void RelExprTypes(Type left, Type right, Type returnType) {
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
                Arguments.arguments(boolType, boolType, boolType),
                Arguments.arguments(intType, intType, boolType),
                Arguments.arguments(doubleType, doubleType, boolType),
                Arguments.arguments(charType, charType, boolType),
                Arguments.arguments(doubleType, intType, boolType),
                Arguments.arguments(intType, doubleType, boolType),

                // Bool to int, not allowed
                Arguments.arguments(intType, boolType, errorType),
                Arguments.arguments(boolType, intType, errorType),
                Arguments.arguments(doubleType, boolType, errorType),
                Arguments.arguments(boolType, doubleType, errorType),

                // Bad types
                Arguments.arguments(boolType, invalidType, errorType),
                Arguments.arguments(invalidType, boolType, errorType),
                Arguments.arguments(intType, invalidType, errorType),
                Arguments.arguments(invalidType, intType, errorType),
                Arguments.arguments(doubleType, invalidType, errorType),
                Arguments.arguments(invalidType, doubleType, errorType),
                Arguments.arguments(charType, invalidType, errorType),
                Arguments.arguments(invalidType, charType, errorType),

                // Bad types (arrays)
                Arguments.arguments(boolArrayType, boolType, errorType),
                Arguments.arguments(intArrayType, intType, errorType),
                Arguments.arguments(doubleArrayType, doubleType, errorType),
                Arguments.arguments(charArrayType, charType, errorType),
                Arguments.arguments(boolType, boolArrayType, errorType),
                Arguments.arguments(intType, intArrayType, errorType),
                Arguments.arguments(doubleType, doubleArrayType, errorType),
                Arguments.arguments(charType, charArrayType, errorType),

                // Bad types (errors)
                Arguments.arguments(boolType, errorType, errorType),
                Arguments.arguments(errorType, boolType, errorType),
                Arguments.arguments(intType, errorType, errorType),
                Arguments.arguments(errorType, intType, errorType),
                Arguments.arguments(doubleType, errorType, errorType),
                Arguments.arguments(errorType, doubleType, errorType),
                Arguments.arguments(charType, errorType, errorType),
                Arguments.arguments(errorType, charType, errorType)
        );
    }

    //endregion

    //region EqExpr
    @ParameterizedTest(name = "{index} => using {0} and {1} with equality operator expecting {2}")
    @MethodSource("eqTypes")
    void EqExprTypes(Type left, Type right, Type returnType) {
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
                Arguments.arguments(boolType, boolType, boolType),
                Arguments.arguments(intType, intType, boolType),
                Arguments.arguments(doubleType, doubleType, boolType),
                Arguments.arguments(doubleType, intType, boolType),
                Arguments.arguments(intType, doubleType, boolType),

                // Bad types
                Arguments.arguments(boolType, invalidType, errorType),
                Arguments.arguments(invalidType, boolType, errorType),
                Arguments.arguments(doubleType, invalidType, errorType),
                Arguments.arguments(invalidType, doubleType, errorType),
                Arguments.arguments(intType, invalidType, errorType),
                Arguments.arguments(invalidType, intType, errorType),

                //Bad types (arrays)
                Arguments.arguments(boolArrayType, boolType, errorType),
                Arguments.arguments(boolType, boolArrayType, errorType),
                Arguments.arguments(doubleArrayType, doubleType, errorType),
                Arguments.arguments(doubleType, doubleArrayType, errorType),
                Arguments.arguments(intArrayType, intType, errorType),
                Arguments.arguments(intType, intArrayType, errorType),

                // Bad types (errors)
                Arguments.arguments(boolType, errorType, errorType),
                Arguments.arguments(errorType, boolType, errorType),
                Arguments.arguments(doubleType, errorType, errorType),
                Arguments.arguments(errorType, doubleType, errorType),
                Arguments.arguments(intType, errorType, errorType),
                Arguments.arguments(errorType, intType, errorType),
                Arguments.arguments(errorType, errorType, errorType)
        );
    }
    //endregion

    //region Bit Expressions
    //region BitShift
    @ParameterizedTest(name = "{index} => using {0} and {1} with bit shift expecting {2}")
    @MethodSource("bitTypes")
    void BitshiftTypes(Type left, Type right, Type returnType) {
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
    void BitAndTypes(Type left, Type right, Type returnType) {
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
    void BitXorTypes(Type left, Type right, Type returnType) {
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
    void BitOrTypes(Type left, Type right, Type returnType) {
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
                Arguments.arguments(intType, intType, intType),

                Arguments.arguments(intType, invalidType, errorType),
                Arguments.arguments(invalidType, intType, errorType),

                Arguments.arguments(intType, intArrayType, errorType),
                Arguments.arguments(intArrayType, intType, errorType),

                Arguments.arguments(intType, errorType, errorType),
                Arguments.arguments(errorType, intType, errorType),
                Arguments.arguments(errorType, errorType, errorType)
        );
    }
    //endregion

    //region Logical expressions
    //region LogAnd
    @ParameterizedTest(name = "{index} => using {0} and {1} with logical and expecting {2}")
    @MethodSource("logTypes")
    void LogAndTypes(Type left, Type right, Type returnType) {
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
    void LogOrTypes(Type left, Type right, Type returnType) {
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
                Arguments.arguments(boolType, boolType, boolType),
                Arguments.arguments(boolType, invalidType, errorType),
                Arguments.arguments(invalidType, boolType, errorType),
                Arguments.arguments(invalidType, invalidType, errorType),
                Arguments.arguments(boolArrayType, boolType, errorType),
                Arguments.arguments(boolType, boolArrayType, errorType),
                Arguments.arguments(boolArrayType, invalidType, errorType),
                Arguments.arguments(invalidType, boolArrayType, errorType),

                Arguments.arguments(errorType, boolType, errorType),
                Arguments.arguments(boolType, errorType, errorType),
                Arguments.arguments(errorType, errorType, errorType)
        );
    }
    //endregion

    //region Conditional
    @ParameterizedTest(name = "{index} => using {0} ? {1} : {2}; expecting {3}")
    @MethodSource("expectedConditionalExpressionTypes")
    void ConditionalExpressionTypes(Type conditionType, Type leftReturnType, Type rightReturnType, Type expectedReturnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.ConditionalContext node = mock(UCELParser.ConditionalContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, conditionType, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, leftReturnType, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, rightReturnType, visitor));
        node.children = children;
        Type actual = visitor.visitConditional(node);

        assertEquals(expectedReturnType, actual);
    }

    private  static Stream<Arguments> expectedConditionalExpressionTypes() {

        return Stream.of(
                // Conditions:
                // Conditions - Valid
                Arguments.arguments(boolType, intType, intType, intType),

                // Conditions - Invalid
                Arguments.arguments(intType, intType, intType, errorType),
                Arguments.arguments(doubleType, intType, intType, errorType),
                Arguments.arguments(chanType, intType, intType, errorType),
                Arguments.arguments(voidType, intType, intType, errorType),
                Arguments.arguments(errorType, intType, intType, errorType),

                // Unmatched Return Types
                // Unmatched Return Types - Valid
                Arguments.arguments(boolType, intType, doubleType, doubleType),
                Arguments.arguments(boolType, doubleType, intType, doubleType),

                // Unmatched Return Types - Invalid
                Arguments.arguments(boolType, intType, boolType, errorType),
                Arguments.arguments(boolType, boolType, intType, errorType),
                Arguments.arguments(boolType, intType, chanType, errorType),
                Arguments.arguments(boolType, chanType, intType, errorType),
                Arguments.arguments(boolType, intType, voidType, errorType),
                Arguments.arguments(boolType, voidType, intType, errorType),

                // Error in input
                Arguments.arguments(errorType, intType, intType, errorType),
                Arguments.arguments(boolType, errorType, intType, errorType),
                Arguments.arguments(boolType, intType, errorType, errorType)
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

    private static Stream<Arguments> unaryNotNegTypes() {
        return Stream.of(
                Arguments.arguments(boolType)
        );
    }

    private static Stream<Arguments> unaryPlusMinusNumberTypes() {
        return Stream.of(
                Arguments.arguments(intType),
                Arguments.arguments(doubleType)
        );
    }

    private static Stream<Arguments> addSubTypes() {
        return Stream.of(
                Arguments.arguments(intType, intType, intType),
                Arguments.arguments(doubleType, intType, doubleType),
                Arguments.arguments(intType, doubleType, doubleType),
                Arguments.arguments(doubleType, doubleType, doubleType),
                Arguments.arguments(stringType, intType, errorType),
                Arguments.arguments(errorType, intType, errorType),
                Arguments.arguments(intType, errorType, errorType),
                Arguments.arguments(intType, intArrayType, errorType),
                Arguments.arguments(stringType, stringType, errorType)
        );
    }

    private  static Stream<Arguments> expectedIncrementPostTypes() {

        return Stream.of(
                // Valid input
                Arguments.arguments(intType, intType),
                Arguments.arguments(doubleType, doubleType),

                // Bad input
                Arguments.arguments(stringType, errorType),
                Arguments.arguments(chanType, errorType),
                Arguments.arguments(scalarType, errorType),
                Arguments.arguments(structType, errorType),
                Arguments.arguments(voidType, errorType),
                Arguments.arguments(errorType, errorType),

                // array (Also bad)
                Arguments.arguments(intArrayType, errorType)
        );
    }

    private static Stream<Arguments> allTypes() {

        ArrayList<Arguments> args = new ArrayList<Arguments>();

        // One of each
        for(Type.TypeEnum t : Type.TypeEnum.values()) {
            args.add(Arguments.arguments(new Type(t)));
        }

        // A couple of array types
        args.add(Arguments.arguments(boolType, 1));
        args.add(Arguments.arguments(intType, 2));
        args.add(Arguments.arguments(doubleType, 3));
        args.add(Arguments.arguments(structType, 4));

        return args.stream();
    }
    //endregion
}
