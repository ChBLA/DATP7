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
    @MethodSource("validIncrementPostTypes")
    void IncrementPostCorrectlyTyped(Type inType, Type returnType) {
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

    //region DecrementPost
    //endregion

    //region DecrementPre
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
    @ParameterizedTest(name = "{index} => using type {0} + type {1} with logical and expecting {2}")
    @MethodSource("logTypes")
    void LogAndCorrectlyTyped(Type left, Type right, Type returnType) {
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

    private  static Stream<Arguments> validIncrementPostTypes() {
        return Stream.of(
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType))
        );
    }
    private  static Stream<Arguments> invalidIncrementPostTypes() {
        return Stream.of(
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.errorType))
        );
    }

    private static Stream<Arguments> logTypes() {
        return Stream.of(
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.boolType)),
                Arguments.arguments(new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.boolType), new Type(Type.TypeEnum.errorType)),
                Arguments.arguments(new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.invalidType), new Type(Type.TypeEnum.errorType))
        );
    }

    //endregion
}
