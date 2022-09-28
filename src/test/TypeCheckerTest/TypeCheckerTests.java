import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
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

    //region IdExpr
    //endregion

    //region LiteralExpr
    //endregion

    //region ArrayIndex
    //endregion

    //region MarkExpr
    //endregion

    //region Paren
    //endregion

    //region Access
    //endregion

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
    //endregion
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

    //region FuncCall
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

    //region BitShift
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
    //endregion

    //region BitAnd
    //endregion

    //region BitXor
    //endregion

    //region BitOr
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
    //endregion

    //region VerificationExpr
    //endregion

    //region Helper methods
    void FuncCallCorrectlyTyped(Type funcType, Type returnType) {
        fail();
    }

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
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.charType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.charType), new Type(Type.TypeEnum.errorType)),
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



    //endregion
}
