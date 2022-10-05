import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.print.DocFlavor;
import java.io.Console;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class CodeGenTests {

    //region Expressions

    //region Assignment

    @ParameterizedTest(name = "{index} => Assignment expression {0} = {1}")
    @MethodSource("assignments")
    void assignmentGeneratedCorrectly(String left, String right) {
        Template leftExpr = new ManualTemplate(left);
        Template rightExpr = new ManualTemplate(right);
        String expected = String.format("%s = %s", leftExpr.getOutput(), rightExpr.getOutput());

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.AssignExprContext.class);
        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, leftExpr, visitor);
        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, rightExpr, visitor);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(expr2);

        var actual = visitor.visitAssignExpr(node).getOutput();

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> assignments() {

        ArrayList<Arguments> args = new ArrayList<Arguments>();

        args.add(Arguments.arguments("var1", "1 + 2"));
        args.add(Arguments.arguments("var1", "true"));
        args.add(Arguments.arguments("var1", "!var2"));
        args.add(Arguments.arguments("var1", "1.02 - 5.2"));
        args.add(Arguments.arguments("var1", "false"));
        args.add(Arguments.arguments("var1", "goimer"));

        return args.stream();
    }

    //endregion

    //region ID expression
    @ParameterizedTest(name = "{index} => ID look-up in expression for ID = \"{0}\"")
    @ValueSource(strings = {"a", "awd901", "Ada"})
    void idExprGeneratedCorrectly(String name) {
        DeclarationInfo variable = new DeclarationInfo(name, new Type(Type.TypeEnum.intType));
        DeclarationReference ref = new DeclarationReference(0, 1);

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IdExprContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        String actual = visitor.visitIdExpr(node).getOutput();

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

        var actual = visitor.visitLiteral(node).getOutput();

        assertEquals(expectedLiteral, actual);
    }
    //endregion

    //region ArrayIndex
    @Test
    void arrayIndexGeneratedCorrectly() {

        CodeGenVisitor visitor = new CodeGenVisitor();
        Template left = generateDefaultExprTemplate("abec");
        Template right = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("%s[%s]", left.getOutput(), right.getOutput()); // abc[0]

        var exprLeft = mockForVisitorResult(UCELParser.ExpressionContext.class, left, visitor);
        var exprRight = mockForVisitorResult(UCELParser.ExpressionContext.class, right, visitor);

        var node = mock(UCELParser.ArrayIndexContext.class);

        when(node.expression(0)).thenReturn(exprLeft);
        when(node.expression(1)).thenReturn(exprRight);

        var actual = visitor.visitArrayIndex(node).getOutput();

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

        var actual = visitor.visitAddSub(node).getOutput();

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

        var actual = visitor.visitMultDiv(node).getOutput();

        assertEquals(expected, actual);
    }
    //endregion

    //region Increment/Decrement expressions
    @Test
    void incrementPostExprGeneratedCorrectly() {
        Template exprResult = generateDefaultExprTemplate("abec");
        String expected = String.format("%s++", exprResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.IncrementPostContext.class);
        var token = mock(TerminalNode.class);

        when(token.getText()).thenReturn("++");
        when(node.INCREMENT()).thenReturn(token);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitIncrementPost(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void incrementPreExprGeneratedCorrectly() {
        Template exprResult = generateDefaultExprTemplate("abec");
        String expected = String.format("++%s", exprResult.getOutput());


        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.IncrementPreContext.class);
        var token = mock(TerminalNode.class);

        when(token.getText()).thenReturn("++");
        when(node.INCREMENT()).thenReturn(token);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitIncrementPre(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void decrementPostExprGeneratedCorrectly() {
        Template exprResult = generateDefaultExprTemplate("abec");
        String expected = String.format("%s--", exprResult.getOutput());


        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.DecrementPostContext.class);
        var token = mock(TerminalNode.class);

        when(token.getText()).thenReturn("--");
        when(node.DECREMENT()).thenReturn(token);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitDecrementPost(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void decrementPreExprGeneratedCorrectly() {
        Template exprResult = generateDefaultExprTemplate("abec");
        String expected = String.format("--%s", exprResult.getOutput());


        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.DecrementPreContext.class);
        var token = mock(TerminalNode.class);

        when(token.getText()).thenReturn("--");
        when(node.DECREMENT()).thenReturn(token);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitDecrementPre(node).getOutput();

        assertEquals(expected, actual);
    }

    //endregion

    //region Unary expressions
    @ParameterizedTest(name = "{index} => generating for {0} expr")
    @ValueSource(strings = {"+", "-"})
    void unaryPlusMinusExprGeneratedCorrectly(String op) {
        String expected = String.format("%s%s", op, generateDefaultExprTemplate(Type.TypeEnum.intType).getOutput());
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.UnaryExprContext.class);
        var unaryNode = mockForVisitorResult(UCELParser.UnaryContext.class, new ManualTemplate(op), visitor);

        when(node.expression()).thenReturn(expr);
        when(node.unary()).thenReturn(unaryNode);

        var actual = visitor.visitUnaryExpr(node).getOutput();

        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{index} => generating for {0} expr")
    @ValueSource(strings = {"not", "!"})
    void unaryNotNegExprGeneratedCorrectly(String op) {
        String sanitizeOp = Objects.equals(op, "not") ? op + " " : op;
        String expected = String.format("%s%s", sanitizeOp, generateDefaultExprTemplate(Type.TypeEnum.boolType).getOutput());
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.UnaryExprContext.class);
        var unaryNode = mockForVisitorResult(UCELParser.UnaryContext.class, new ManualTemplate(sanitizeOp), visitor);

        when(node.expression()).thenReturn(expr);
        when(node.unary()).thenReturn(unaryNode);

        var actual = visitor.visitUnaryExpr(node).getOutput();

        assertEquals(expected, actual);
    }

    //endregion

    //region Parenthesis
    @Test
    void parenGeneratedCorrectly() {
        String expected = String.format("(%s)", generateDefaultExprTemplate(Type.TypeEnum.intType).getOutput());
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.intType);

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var node = mock(UCELParser.ParenContext.class);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitParen(node).getOutput();

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

        var actual = visitor.visitBitshift(node).getOutput();

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

        var actual = visitor.visitBitAnd(node).getOutput();

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

        var actual = visitor.visitBitXor(node).getOutput();

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

        var actual = visitor.visitBitOr(node).getOutput();

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

        var actual = visitor.visitEqExpr(node).getOutput();

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

        var actual = visitor.visitMinMax(node).getOutput();

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

        var actual = visitor.visitRelExpr(node).getOutput();

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

        var actual = visitor.visitLogAnd(node).getOutput();

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

        var actual = visitor.visitLogOr(node).getOutput();

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

        var actual = visitor.visitConditional(node).getOutput();

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

    private Template generateDefaultExprTemplate(String id) {
        return new ManualTemplate(id);
    }

    //endregion
}
