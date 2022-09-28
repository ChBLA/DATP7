import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.antlr.v4.tool.ast.TerminalAST;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TypeCheckerTests  {

    //region IdExpr
    //endregion

    //region LiteralExpr


    @Test
    void IntLiteralTypedCorrectly() {
        Type literalType = new Type(Type.TypeEnum.intType);
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.LiteralContext node = mock(UCELParser.LiteralContext.class);
        when(node.NAT()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NAT)));

        Type actual = visitor.visitLiteral(node);
        assertEquals(literalType, actual);
    }

    @Test
    void DoubleLiteralTypedCorrectly() {
        Type literalType = new Type(Type.TypeEnum.doubleType);
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.LiteralContext node = mock(UCELParser.LiteralContext.class);
        when(node.DOUBLE()).thenReturn(new TerminalNodeImpl(new CommonToken(UCELParser.NAT)));

        Type actual = visitor.visitLiteral(node);
        assertEquals(literalType, actual);
    }

    @Test
    void BoolLiteralTypedCorrectly() {
        Type literalType = new Type(Type.TypeEnum.boolType);
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        UCELParser.BooleanContext boolCtx = mock(UCELParser.BooleanContext.class);

        UCELParser.LiteralContext node = mock(UCELParser.LiteralContext.class);
        when(node.boolean_()).thenReturn(boolCtx);

        Type actual = visitor.visitLiteral(node);
        assertEquals(literalType, actual);
    }

    //endregion

    //region ArrayIndex
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

    //region Access
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
    void FuncCallParamsCorrectType(Type param1, Type param2) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.FuncCallContext node = mock(UCELParser.FuncCallContext.class);
        final UCELParser.ArgumentsContext argumentsContext = mock(UCELParser.ArgumentsContext.class);
        List<ParseTree> arguments = new ArrayList<>();
        arguments.add(mockForVisitorResult(UCELParser.ExpressionContext.class, param1, visitor));
        arguments.add(mockForVisitorResult(UCELParser.ExpressionContext.class, param2, visitor));
        argumentsContext.children = arguments;
        when(node.arguments()).thenReturn(argumentsContext);

//        visitor.visitFuncCall(node);
    }

    //endregion

    //region UnaryExpr
    //endregion

    //region MultDiv
    //endregion

    //region AddSub
    @ParameterizedTest(name = "{index} => using type {0} + type {1} with plus/minus")
    @MethodSource("addSubTypes")
    void AddSubTyped(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.AddSubContext node = mock(UCELParser.AddSubContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
        Type actual = visitor.visitAddSub(node);

        assertEquals(returnType, actual);
    }

    //endregion

    //region MinMax
    //endregion

    //region RelExpr
    @ParameterizedTest(name = "{index} => using {0} and {1} with relational operator expecting {2}")
    @MethodSource("relTypes")
    void RelExprTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.RelExprContext node = mock(UCELParser.RelExprContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
        Type actual = visitor.visitRelExpr(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region EqExpr
    @ParameterizedTest(name = "{index} => using {0} and {1} with equality operator expecting {2}")
    @MethodSource("eqTypes")
    void EqExprTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.EqExprContext node = mock(UCELParser.EqExprContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
        Type actual = visitor.visitEqExpr(node);

        assertEquals(returnType, actual);
    }
    //endregion

    //region Bit Expressions
    //region BitShift
    @ParameterizedTest(name = "{index} => using {0} and {1} with bit shift expecting {2}")
    @MethodSource("bitTypes")
    void BitshiftTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.BitshiftContext node = mock(UCELParser.BitshiftContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
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
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
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
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
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
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
        Type actual = visitor.visitBitOr(node);

        assertEquals(returnType, actual);
    }
    //endregion
    private static Stream<Arguments> bitTypes() {
        Type intType = new Type(Type.TypeEnum.intType);
        Type errorType = new Type(Type.TypeEnum.errorType);
        Type intArrayType = new Type(Type.TypeEnum.intType, 1);
        Type invalidType = new Type(Type.TypeEnum.invalidType);

        return Stream.of(
                Arguments.arguments(intType, intType, intType),

                Arguments.arguments(intType, invalidType, errorType),
                Arguments.arguments(invalidType, intType, errorType),

                Arguments.arguments(intType, intArrayType, errorType),
                Arguments.arguments(intArrayType, intType, errorType)
        );
    }
    //endregion

    //region LogAnd
    @ParameterizedTest(name = "{index} => using {0} and {1} with logical and expecting {2}")
    @MethodSource("logTypes")
    void LogAndTypes(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.LogAndContext node = mock(UCELParser.LogAndContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
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
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
        Type actual = visitor.visitLogOr(node);

        assertEquals(returnType, actual);
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
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType)),

                // Conditions - Invalid
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.chanType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.voidType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.errorType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),

                // Unmatched Return Types
                // Unmatched Return Types - Valid
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType)),

                // Unmatched Return Types - Invalid
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.chanType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.chanType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.voidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.voidType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),

                // Error in input
                Arguments.arguments(new Type(Type.TypeEnum.errorType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType), new Type(Type.TypeEnum.errorType))
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
    private static Stream<Arguments> addSubTypes() {
        return Stream.of(
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType)),
                Arguments.arguments(new Type(Type.TypeEnum.stringType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.errorType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType, 1), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.stringType), new Type(Type.TypeEnum.stringType), new Type(Type.TypeEnum.errorType))
        );
    }

    private  static Stream<Arguments> expectedIncrementPostTypes() {

        return Stream.of(
                // Valid input
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType)),

                // Bad input
                Arguments.arguments(new Type(Type.TypeEnum.stringType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.chanType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.scalarType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.structType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.voidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.errorType), new Type(Type.TypeEnum.errorType)),

                // array (Also bad)
                Arguments.arguments(new Type(Type.TypeEnum.intType, 1), new Type(Type.TypeEnum.errorType))
        );
    }


    private static Stream<Arguments> relTypes() {
        return Stream.of(
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.charType), new Type(Type.TypeEnum.charType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.boolType)),

                // Bool to int, not allowed
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.errorType)),

                // Bad types
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.charType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.charType), new Type(Type.TypeEnum.errorType)),

                // Bad types (arrays)
                Arguments.arguments(new Type(Type.TypeEnum.boolType,1), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType,1), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType,1), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.charType,1), new Type(Type.TypeEnum.charType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType,1), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType,1), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType,1), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.charType), new Type(Type.TypeEnum.charType,1), new Type(Type.TypeEnum.errorType))
        );
    }
    private static Stream<Arguments> eqTypes() {
        return Stream.of(
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.boolType)),

                // Bad types
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),

                //Bad types (arrays)
                Arguments.arguments(new Type(Type.TypeEnum.boolType,1), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType,1), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType,1), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType,1), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType,1), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType,1), new Type(Type.TypeEnum.errorType))
        );
    }

    private static Stream<Arguments> logTypes() { //TODO: consider letting char comparable with non-char
        return Stream.of(
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType, 1), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType, 1), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType, 1), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.boolType, 1), new Type(Type.TypeEnum.errorType))
        );
    }

    private static Stream<Arguments> allTypes() {

        ArrayList<Arguments> args = new ArrayList<Arguments>();

        // One of each
        for(Type.TypeEnum t : Type.TypeEnum.values()) {
            args.add(Arguments.arguments(new Type(t)));
        }

        // A couple of array types
        args.add(Arguments.arguments(new Type(Type.TypeEnum.boolType, 1)));
        args.add(Arguments.arguments(new Type(Type.TypeEnum.intType, 2)));
        args.add(Arguments.arguments(new Type(Type.TypeEnum.doubleType, 3)));
        args.add(Arguments.arguments(new Type(Type.TypeEnum.structType, 4)));

        return args.stream();
    }


    //endregion
}
