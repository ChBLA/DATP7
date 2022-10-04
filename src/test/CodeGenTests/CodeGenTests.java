import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import javax.print.DocFlavor;
import java.io.Console;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class CodeGenTests {

    @Test
    void plusGeneratedCorrectly() {
        String op = "+";
        String expected = String.format("0 %s 0", op);
        ArrayList<Template> exprResult = new ArrayList<>(){{
            add(new ManualTemplate("0"));
        }};

        CodeGenVisitor visitor = mock(CodeGenVisitor.class);

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.AddSubContext.class);
        var opToken = mock(CommonToken.class);

        node.op = opToken;

        when(node.expression(0)).thenReturn(expr);
        when(node.expression(1)).thenReturn(expr);
        when(opToken.getText()).thenReturn(op);

        var actual = visitor.visitAddSub(node).get(1).getOutput();

        assertEquals(expected, actual);
    }

    private<T extends RuleContext> T mockForVisitorResult(final Class<T> nodeType, final ArrayList<Template> visitTemplateResult, CodeGenVisitor visitor) {
        final T mock = mock(nodeType);
        when(mock.accept(visitor)).thenReturn(visitTemplateResult);
        return mock;
    }
}
