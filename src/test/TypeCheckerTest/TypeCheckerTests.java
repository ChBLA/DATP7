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

    @ParameterizedTest(name = "{index} => using type {0} + type {1} with plus/minus")
    @MethodSource("validAddSubTypes")
    void AddSubCorrectlyTyped(Type left, Type right, Type returnType) {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor();

        final UCELParser.AddSubContext node = mock(UCELParser.AddSubContext.class);
        List<ParseTree> children = new ArrayList<>();
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor));
        children.add(mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor));
        node.children = children;
        Type actual = visitor.visitAddSub(node);

        assertEquals(returnType, actual);
    }

    void FuncCallCorrectlyTyped(Type funcType, Type returnType) {
        fail();
    }

    private<T extends RuleContext> T mockForVisitorResult(final Class<T> nodeType, final Type visitResult, TypeCheckerVisitor visitor) {
        final T mock = mock(nodeType);
        when(mock.accept(visitor)).thenReturn(visitResult);
        return mock;
    }

    private static Stream<Arguments> validAddSubTypes() {
        return Stream.of(
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.intType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType)),
                Arguments.arguments(new Type(Type.TypeEnum.intType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType)),
                Arguments.arguments(new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType), new Type(Type.TypeEnum.doubleType))
        );
    }
}
