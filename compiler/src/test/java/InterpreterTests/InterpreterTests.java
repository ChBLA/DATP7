package InterpreterTests;

import org.UcelParser.Interpreter.InterpreterVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.ComponentOccurrence;
import org.UcelParser.Util.DeclarationInfo;
import org.UcelParser.Util.DeclarationReference;
import org.UcelParser.Util.Logging.ILogger;
import org.UcelParser.Util.Scope;
import org.UcelParser.Util.Value.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.ArrayList;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InterpreterTests {

    //region Build and blocks and statments

    //region CompCon

    /*

    Reference handler sets a declInfo on scope for the buildDecl (comp or template variable) and points
    the left hand side of the compCon to it
    the interpreter sets a ListValue on the declInfo
    visitCompCon in the interpreter adds a CompOccurrenceValue to the ListValue on declInfo for the buildDecl
    visitCompCon returns a successValue or null
    at the end of visitBuild the occurrences on the comp node are set before that node is visited by the interpreter.
    visiting must be done last as all linking must have taken place.
    it must be checked by the end of build that all indices for buildDecls are used, that there are no duplicates and that all
    interfaces have been set.

     */

    @ParameterizedTest
    @MethodSource("compConValues")
    void compConReturnsExpected(InterpreterValue i0, InterpreterValue i1,
                 InterpreterValue a0, InterpreterValue a1,
                 InterpreterValue expected) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.CompConContext node = mock(UCELParser.CompConContext.class);

        var indices = new ArrayList<UCELParser.ExpressionContext>();
        UCELParser.ExpressionContext index0 = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext index1 = mock(UCELParser.ExpressionContext.class);
        indices.add(index0);
        indices.add(index1);
        when(node.expression()).thenReturn(indices);
        when(index0.accept(visitor)).thenReturn(i0);
        when(index1.accept(visitor)).thenReturn(i1);

        DeclarationInfo varInfo = new DeclarationInfo("v");
        varInfo.setValue(new ListValue(new ArrayList<>()));
        DeclarationInfo conInfo = new DeclarationInfo("c", mock(UCELParser.ComponentContext.class));

        DeclarationReference varRef = new DeclarationReference(0,0);
        DeclarationReference conRef = new DeclarationReference(0,1);

        node.constructorReference = conRef;
        node.variableReference = varRef;

        try{
            when(scope.get(varRef)).thenReturn(varInfo);
            when(scope.get(conRef)).thenReturn(conInfo);
        } catch (Exception e) {fail();}

        UCELParser.ArgumentsContext arguments = mock(UCELParser.ArgumentsContext.class);
        when(node.arguments()).thenReturn(arguments);

        UCELParser.ExpressionContext arg0 = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext arg1 = mock(UCELParser.ExpressionContext.class);
        ArrayList<UCELParser.ExpressionContext> args = new ArrayList<>();
        args.add(arg0);
        args.add(arg1);

        when(arguments.expression()).thenReturn(args);
        when(arg0.accept(visitor)).thenReturn(a0);
        when(arg1.accept(visitor)).thenReturn(a1);

        var result = visitor.visitCompCon(node);

        var list = ((ListValue) varInfo.getValue()).getValues();
        var actual = list.size() > 0 ? list.get(0) : null;
        assertEquals(expected, actual);
    }

    private static Stream<Arguments> compConValues() {
        return Stream.of(
                Arguments.arguments(value(2), value(4), value(true), value(17),
                        new CompOccurrenceValue("v", new int[]{2,4},
                                new ComponentOccurrence(null, new InterpreterValue[]{value(true), value(17)}))),
                Arguments.arguments(value(92), value(4), value(true), value("17"),
                        new CompOccurrenceValue("v", new int[]{92,4},
                                new ComponentOccurrence(null, new InterpreterValue[]{value(true), value("17")}))),
                Arguments.arguments(value(2), null, value(true), value(17), null)
        );
    }

    //endregion

    //endregion

    //region Expressions

    //region StructAccess

    @ParameterizedTest
    @MethodSource("structValues")
    void structIndex(InterpreterValue v, String identifier, InterpreterValue expected) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.StructAccessContext node = mock(UCELParser.StructAccessContext.class);
        UCELParser.ExpressionContext exp = mock(UCELParser.ExpressionContext.class);
        TerminalNode id = mock(TerminalNode.class);

        when(node.expression()).thenReturn(exp);
        when(node.ID()).thenReturn(id);
        when(id.getText()).thenReturn(identifier);
        when(visitor.visit(exp)).thenReturn(v);

        var actual = visitor.visitStructAccess(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> structValues() {
        return Stream.of(
                Arguments.arguments(value("cbuffer") , "i", value("cbuffer.i")),
                Arguments.arguments(value("a") , "s", value("a.s")),
                Arguments.arguments(value("a") , "chan", value("a.chan")),
                Arguments.arguments(value("a") , null, null),
                Arguments.arguments(null , "s", null),
                Arguments.arguments(null, null, null)
        );
    }

    //endregion

    //region ArrayIndex

    @ParameterizedTest
    @MethodSource("arrayValues")
    void arrayIndex(InterpreterValue v1, InterpreterValue v2, InterpreterValue expected) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.ArrayIndexContext node = mock(UCELParser.ArrayIndexContext.class);
        UCELParser.ExpressionContext expL = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext expR = mock(UCELParser.ExpressionContext.class);

        var exprs = new ArrayList<UCELParser.ExpressionContext>();
        exprs.add(expL);
        exprs.add(expR);
        when(node.expression()).thenReturn(exprs);
        when(visitor.visit(expL)).thenReturn(v1);
        when(visitor.visit(expR)).thenReturn(v2);

        var actual = visitor.visitArrayIndex(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> arrayValues() {
        return Stream.of(
                Arguments.arguments(value("cbuffer") , value(134), value("cbuffer_134")),
                Arguments.arguments(value("a") , value(3), value("a_3")),
                Arguments.arguments(value("a") , value(-17), null),
                Arguments.arguments(value("a") , null, null),
                Arguments.arguments(value("a") , value("s"), null),
                Arguments.arguments(null, null, null)
        );
    }

    //endregion

    //region addSub


    @ParameterizedTest
    @MethodSource("addSubValues")
    void addSubIntegers(InterpreterValue v1, InterpreterValue v2, InterpreterValue expected, String op) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.AddSubContext node = mock(UCELParser.AddSubContext.class);
        UCELParser.ExpressionContext expL = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext expR = mock(UCELParser.ExpressionContext.class);

        var exprs = new ArrayList<UCELParser.ExpressionContext>();
        exprs.add(expL);
        exprs.add(expR);
        when(node.expression()).thenReturn(exprs);
        when(visitor.visit(expL)).thenReturn(v1);
        when(visitor.visit(expR)).thenReturn(v2);

        var operatorToken = mock(Token.class);
        when(operatorToken.getText()).thenReturn(op);
        node.op = operatorToken;

        var actual = visitor.visitAddSub(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> addSubValues() {
        return Stream.of(
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(137), "+"),
                Arguments.arguments(new IntegerValue(-3) , new IntegerValue(7), new IntegerValue(4), "+"),
                Arguments.arguments(null , new IntegerValue(134), null, "+"),
                Arguments.arguments(null , null, null, "+"),
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(-131), "-"),
                Arguments.arguments(new IntegerValue(-3) , new IntegerValue(7), new IntegerValue(-10), "-"),
                Arguments.arguments(null , new IntegerValue(134), null, "-"),
                Arguments.arguments(null , null, null, "-")
                );
    }
    //endregion

    //region MultDiv

    @ParameterizedTest
    @MethodSource("multDivValues")
    void multDivIntegers(InterpreterValue v1, InterpreterValue v2, InterpreterValue expected, String op) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.MultDivContext node = mock(UCELParser.MultDivContext.class);
        UCELParser.ExpressionContext expL = mock(UCELParser.ExpressionContext.class);
        UCELParser.ExpressionContext expR = mock(UCELParser.ExpressionContext.class);

        var exprs = new ArrayList<UCELParser.ExpressionContext>();
        exprs.add(expL);
        exprs.add(expR);
        when(node.expression()).thenReturn(exprs);
        when(visitor.visit(expL)).thenReturn(v1);
        when(visitor.visit(expR)).thenReturn(v2);

        var operatorToken = mock(Token.class);
        when(operatorToken.getText()).thenReturn(op);
        node.op = operatorToken;

        var actual = visitor.visitMultDiv(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> multDivValues() {
        return Stream.of(
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(14), new IntegerValue(42), "*"),
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(-14), new IntegerValue(-42), "*"),
                Arguments.arguments(new IntegerValue(-3) , new IntegerValue(7), new IntegerValue(-21), "*"),
                Arguments.arguments(null , new IntegerValue(134), null, "*"),
                Arguments.arguments(null , null, null, "*"),
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(0), "/"),
                Arguments.arguments(new IntegerValue(-27) , new IntegerValue(7), new IntegerValue(-3), "/"),
                Arguments.arguments(null , new IntegerValue(134), null, "/"),
                Arguments.arguments(null , null, null, "/"),
                Arguments.arguments(new IntegerValue(3) , new IntegerValue(134), new IntegerValue(3), "%"),
                Arguments.arguments(new IntegerValue(-3) , new IntegerValue(7), new IntegerValue(-3), "%"),
                Arguments.arguments(null , new IntegerValue(134), null, "%"),
                Arguments.arguments(null , null, null, "%")
        );
    }
    //endregion

    //region idExpr

    @ParameterizedTest
    @MethodSource("idValues")
    void idValues(InterpreterValue v, InterpreterValue expected) {
        Scope scope = mock(Scope.class);
        InterpreterVisitor visitor = new InterpreterVisitor(scope);

        UCELParser.IdExprContext node = mock(UCELParser.IdExprContext.class);
        DeclarationReference declRef = new DeclarationReference(0,0);
        DeclarationInfo declInfo = mock(DeclarationInfo.class);
        node.reference = declRef;
        when(declInfo.getValue()).thenReturn(v);
        when(declInfo.generateName()).thenReturn("aaaaaa_id");

        try{
            when(scope.get(declRef)).thenReturn(declInfo);
        } catch (Exception e) {fail();}

        var actual = visitor.visitIdExpr(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> idValues() {
        return Stream.of(
                Arguments.arguments(new IntegerValue(3), new IntegerValue(3)),
                Arguments.arguments(new IntegerValue(-19), new IntegerValue(-19)),
                Arguments.arguments(new BooleanValue(true), new BooleanValue(true)),
                Arguments.arguments(new StringValue("s"), new StringValue("s")),
                Arguments.arguments(null, new StringValue("aaaaaa_id"))

                );
    }


    //endregion

    //region eqExpr
    @ParameterizedTest
    @MethodSource("equalityTestValues")
    void eqExprTests(InterpreterValue v0, InterpreterValue v1, String operatorStr, boolean expected) {
        // Arrange
        var visitor = testVisitor();

        var val0 = mockForVisitorResult(UCELParser.ExpressionContext.class, v0, visitor);
        var val1 = mockForVisitorResult(UCELParser.ExpressionContext.class, v1, visitor);
        var op = mock(Token.class);
        when(op.getText()).thenReturn(operatorStr);

        var node = mock(UCELParser.EqExprContext.class);
        when(node.expression(0)).thenReturn(val0);
        when(node.expression(1)).thenReturn(val1);
        node.op = op;

        // Act
        var result = visitor.visitEqExpr(node);

        // Assert
        assertInstanceOf(BooleanValue.class, result);
        var actual = ((BooleanValue)result).getBool();

        assertEquals(expected, actual);
    }
    private static Stream<Arguments> equalityTestValues() {
        List<Arguments> arguments = new ArrayList<>();

        //region Ints
        for(int i = -3; i<3; i++) {
            arguments.add(Arguments.of(value(i), value(i), "==", true));
            arguments.add(Arguments.of(value(i), value(i), "!=", false));
        }

        arguments.add(Arguments.of(value(0),value(1), "==", false));
        arguments.add(Arguments.of(value(0),value(1), "!=", true));
        arguments.add(Arguments.of(value(1),value(0), "==", false));
        arguments.add(Arguments.of(value(1),value(0), "!=", true));
        arguments.add(Arguments.of(value(-1),value(1), "==", false));
        arguments.add(Arguments.of(value(-1),value(1), "!=", true));
        arguments.add(Arguments.of(value(1),value(-1), "==", false));
        arguments.add(Arguments.of(value(1),value(-1), "!=", true));
        //endregion

        //region Strings
        arguments.add(Arguments.of(value(""),value(""), "==", true));
        arguments.add(Arguments.of(value(""),value(""), "!=", false));
        arguments.add(Arguments.of(value("abc"),value("abc"), "==", true));
        arguments.add(Arguments.of(value("abc"),value("abc"), "!=", false));
        arguments.add(Arguments.of(value("def"),value("def"), "==", true));
        arguments.add(Arguments.of(value("def"),value("def"), "!=", false));

        arguments.add(Arguments.of(value(" "),value(""), "==", false));
        arguments.add(Arguments.of(value(" "),value(""), "!=", true));
        arguments.add(Arguments.of(value("def "),value("def"), "==", false));
        arguments.add(Arguments.of(value("def "),value("def"), "!=", true));
        arguments.add(Arguments.of(value("abc"),value("def"), "==", false));
        arguments.add(Arguments.of(value("abc"),value("def"), "!=", true));
        arguments.add(Arguments.of(value("def"),value("abc"), "==", false));
        arguments.add(Arguments.of(value("def"),value("abc"), "!=", true));
        //endregion

        //region Bools
        arguments.add(Arguments.of(value(true),value(true), "==", true));
        arguments.add(Arguments.of(value(true),value(true), "!=", false));
        arguments.add(Arguments.of(value(false),value(false), "==", true));
        arguments.add(Arguments.of(value(false),value(false), "!=", false));
        arguments.add(Arguments.of(value(true),value(false), "==", false));
        arguments.add(Arguments.of(value(true),value(false), "!=", true));
        arguments.add(Arguments.of(value(false),value(true), "==", false));
        arguments.add(Arguments.of(value(false),value(true), "!=", true));
        //endregion

        //region Mixed types
        arguments.add(Arguments.of(value(0),value(""), "==", false));
        arguments.add(Arguments.of(value(0),value(""), "!=", true));
        arguments.add(Arguments.of(value(0),value("0"), "==", false));
        arguments.add(Arguments.of(value(0),value("0"), "!=", true));
        arguments.add(Arguments.of(value(0),value(false), "==", false));
        arguments.add(Arguments.of(value(0),value(false), "!=", true));
        arguments.add(Arguments.of(value(1),value(true), "==", false));
        arguments.add(Arguments.of(value(1),value(true), "!=", true));
        //endregion

        return arguments.stream();
    }
    //endregion

    //region Paren
    @ParameterizedTest
    @MethodSource("parenExpressions")
    public void ParenTestPassThrough(InterpreterValue expected, Class exprType) {
        var visitor = testVisitor();

        var node = mock(UCELParser.ParenContext.class);
        var expr = (UCELParser.ExpressionContext) mockForVisitorResult(exprType, expected, visitor);
        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitParen(node);

        assertEquals(expected, actual);
    }
    private static Stream<Arguments> parenExpressions() {
        return Stream.of(
                Arguments.of(value(-3), UCELParser.AddSubContext.class),
                Arguments.of(value(0), UCELParser.AddSubContext.class),
                Arguments.of(value(5), UCELParser.AddSubContext.class),
                Arguments.of(value(true), UCELParser.EqExprContext.class),
                Arguments.of(value(false), UCELParser.EqExprContext.class)
        );
    }
    //endregion

    //region Literals
    @ParameterizedTest
    @MethodSource("literalNATSource")
    public void literalNATTest(IntegerValue expected, String literalStr) {
        var visitor = testVisitor();

        var valMock = mock(TerminalNode.class);
        when(valMock.getText()).thenReturn(literalStr);

        var node = mock(UCELParser.LiteralContext.class);
        node.children = new ArrayList<>();
        when(node.NAT()).thenReturn(valMock);

        var actual = visitor.visitLiteral(node);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> literalNATSource() {
        return Stream.of(
            Arguments.of (  value(0), "0"),
            Arguments.of (value(432), "432"),
            Arguments.of (  value(5), "5"),
            Arguments.of (value(-63), "-63")
        );
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void literalBoolTest(boolean bool) {
        BooleanValue expected = value(bool);

        var visitor = testVisitor();

        var valMock = mockForVisitorResult(UCELParser.BoolContext.class, expected, visitor);

        var node = mock(UCELParser.LiteralContext.class);
        node.children = new ArrayList<>();
        when(node.bool()).thenReturn(valMock);

        var actual = visitor.visitLiteral(node);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("literalBoolSource")
    public void literalBoolStringTest(boolean boolVal, String literalStr) {
        var expected = value(boolVal);


        var visitor = testVisitor();

        var valMock = mock(TerminalNode.class);
        when(valMock.getText()).thenReturn(literalStr);

        var node = mock(UCELParser.BoolContext.class);
        node.children = new ArrayList<>();
        if(boolVal == true) {
            when(node.TRUE()).thenReturn(valMock);
        }
        else {
            when(node.FALSE()).thenReturn(valMock);
        }

        var actual = visitor.visitBool(node);

        assertEquals(expected, actual);
    }
    private static Stream<Arguments> literalBoolSource() {
        return Stream.of(
                Arguments.of(true, "true"),
                Arguments.of(false, "true")
        );
    }

    @Test
    public void literalDoubleTest() {
        Object expected = null;
        String literalStr = "54.5";


        var visitor = testVisitor();

        var valMock = mock(TerminalNode.class);
        when(valMock.getText()).thenReturn(literalStr);

        var node = mock(UCELParser.LiteralContext.class);
        node.children = new ArrayList<>();
        when(node.DOUBLE()).thenReturn(valMock);

        var actual = visitor.visitLiteral(node);

        assertEquals(expected, actual);
    }

    @Test
    public void literalDeadlockTest() {
        Object expected = null;
        String literalStr = "deadlock";


        var visitor = testVisitor();

        var valMock = mock(TerminalNode.class);
        when(valMock.getText()).thenReturn(literalStr);

        var node = mock(UCELParser.LiteralContext.class);
        node.children = new ArrayList<>();
        when(node.DEADLOCK()).thenReturn(valMock);

        var actual = visitor.visitLiteral(node);

        assertEquals(expected, actual);
    }

    //endregion

    //region Unary expressions
    @ParameterizedTest
    @MethodSource("unaryTestSource")
    void unaryTest(InterpreterValue inputValue, UCELParser.UnaryContext operatorMock, InterpreterValue expectedOutput) {
        var visitor = testVisitor();
        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, inputValue, visitor);
        var node = mock(UCELParser.UnaryExprContext.class);
        when(node.expression()).thenReturn(expr);
        when(node.unary()).thenReturn(operatorMock);

        var actual = visitor.visitUnaryExpr(node);

        assertEquals(expectedOutput, actual);
    }
    private static Stream<Arguments> unaryTestSource() {
        // PLUS | MINUS | NEG | NOT;

        var plus = mock(UCELParser.UnaryContext.class);
        plus.children = new ArrayList<>();
        when(plus.PLUS()).thenReturn(mock(TerminalNode.class));

        var minus = mock(UCELParser.UnaryContext.class);
        minus.children = new ArrayList<>();
        when(minus.MINUS()).thenReturn(mock(TerminalNode.class));

        var neg = mock(UCELParser.UnaryContext.class);
        neg.children = new ArrayList<>();
        when(neg.NEG()).thenReturn(mock(TerminalNode.class));

        var not = mock(UCELParser.UnaryContext.class);
        not.children = new ArrayList<>();
        when(not.NOT()).thenReturn(mock(TerminalNode.class));



        return Stream.of(
            Arguments.arguments(value(3), plus, value(3)),
            Arguments.arguments(value(5), plus, value(5)),

            Arguments.arguments(value(3), minus, value(-3)),
            Arguments.arguments(value(5), minus, value(-5)),
            Arguments.arguments(value(-3), minus, value(3)),
            Arguments.arguments(value(-5), minus, value(5)),

            Arguments.arguments(value(true), neg, value(false)),
            Arguments.arguments(value(false), neg, value(true)),

            Arguments.arguments(value(true), not, value(false)),
            Arguments.arguments(value(false), not, value(true))
        );
    }

    //endregion

    //region RelExpr
    @ParameterizedTest
    @MethodSource("relExprTestSource")
    void relExprTest(InterpreterValue leftValue, InterpreterValue rightValue, String opStr, InterpreterValue expectedOutput) {
        var visitor = testVisitor();
        var exprLeft = mockForVisitorResult(UCELParser.ExpressionContext.class, leftValue, visitor);
        var exprRight = mockForVisitorResult(UCELParser.ExpressionContext.class, rightValue, visitor);
        var op = mock(Token.class);
        when(op.getText()).thenReturn(opStr);

        var node = mock(UCELParser.RelExprContext.class);
        when(node.expression(0)).thenReturn(exprLeft);
        when(node.expression(1)).thenReturn(exprRight);
        node.op = op;

        var actual = visitor.visitRelExpr(node);

        assertEquals(expectedOutput, actual);
    }
    private static Stream<Arguments> relExprTestSource() {
        // op=('<' | '<=' | '>=' | '>')

        return Stream.of(
            Arguments.arguments(value(3), value(3), "<", value(false)),
            Arguments.arguments(value(3), value(3), "<=", value(true)),
            Arguments.arguments(value(3), value(3), ">=", value(true)),
            Arguments.arguments(value(3), value(3), ">", value(false)),

            Arguments.arguments(value(-5), value(3), "<", value(true)),
            Arguments.arguments(value(-5), value(3), "<=", value(true)),
            Arguments.arguments(value(-5), value(3), ">=", value(false)),
            Arguments.arguments(value(-5), value(3), ">", value(false)),

            Arguments.arguments(value(3), value(-5), "<", value(false)),
            Arguments.arguments(value(3), value(-5), "<=", value(false)),
            Arguments.arguments(value(3), value(-5), ">=", value(true)),
            Arguments.arguments(value(3), value(-5), ">", value(true)),

            Arguments.arguments(value(-7), value(-10), "<", value(false)),
            Arguments.arguments(value(-7), value(-10), "<=", value(false)),
            Arguments.arguments(value(-7), value(-10), ">=", value(true)),
            Arguments.arguments(value(-7), value(-10), ">", value(true)),

            Arguments.arguments(value(-7), value(true), "<", null),
            Arguments.arguments(value(-7), value(true), "<=", null),
            Arguments.arguments(value(-7), value(true), ">=", null),
            Arguments.arguments(value(-7), value(true), ">", null),

            Arguments.arguments(null, value(-10), "<", null),
            Arguments.arguments(null, value(-10), "<=", null),
            Arguments.arguments(null, value(-10), ">=", null),
            Arguments.arguments(null, value(-10), ">", null)
        );
    }

    //endregion

    //endregion

    //region Control Flow
    //region BuildIf
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void buildIfTestSuccess(boolean predicateValue) {
        var visitor = testVisitor();

        var predicate = mockForVisitorResult(UCELParser.ExpressionContext.class, value(predicateValue), visitor);
        var stmtTrue = mockForVisitorResult(UCELParser.BuildStmntContext.class, value(), visitor);
        var stmtFalse = mockForVisitorResult(UCELParser.BuildStmntContext.class, value(), visitor);


        var node = mock(UCELParser.BuildIfContext.class);
        when(node.expression()).thenReturn(predicate);
        when(node.buildStmnt(0)).thenReturn(stmtTrue);
        when(node.buildStmnt(1)).thenReturn(stmtFalse);

        var actual = visitor.visitBuildIf(node);

        if(predicateValue) {
            verify(stmtTrue, times(1)).accept(any());
            verify(stmtFalse, times(0)).accept(any());
        }
        else {
            verify(stmtTrue, times(0)).accept(any());
            verify(stmtFalse, times(1)).accept(any());
        }

        assertInstanceOf(VoidValue.class, actual);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void buildIfTestSuccessNoElse(boolean predicateValue) {
        var visitor = testVisitor();

        var predicate = mockForVisitorResult(UCELParser.ExpressionContext.class, value(predicateValue), visitor);
        var stmtTrue = mockForVisitorResult(UCELParser.BuildStmntContext.class, value(), visitor);

        var node = mock(UCELParser.BuildIfContext.class);
        when(node.expression()).thenReturn(predicate);
        when(node.buildStmnt(0)).thenReturn(stmtTrue);

        var actual = visitor.visitBuildIf(node);

        if(predicateValue) {
            verify(stmtTrue, times(1)).accept(any());
        }
        else {
            verify(stmtTrue, times(0)).accept(any());
        }

        assertInstanceOf(VoidValue.class, actual);
    }

    @Test
    void buildIfTestInvalidPredicate() {
        var logger = mock(ILogger.class);
        var visitor = testVisitor(logger);

        var predVal = value(28);

        var predicate = mockForVisitorResult(UCELParser.ExpressionContext.class, predVal, visitor);
        var stmtTrue = mockForVisitorResult(UCELParser.BuildStmntContext.class, value(), visitor);
        var stmtFalse = mockForVisitorResult(UCELParser.BuildStmntContext.class, value(), visitor);

        var node = mock(UCELParser.BuildIfContext.class);
        when(node.expression()).thenReturn(predicate);
        when(node.buildStmnt(0)).thenReturn(stmtTrue);
        when(node.buildStmnt(1)).thenReturn(stmtFalse);

        var actual = visitor.visitBuildIf(node);

        verify(stmtTrue, times(0)).accept(any());
        verify(stmtFalse, times(0)).accept(any());
        verify(logger, atLeast(1)).log(any());
        assertNull(actual);
    }

    @Test
    void buildIfTestInvalidStmt() {
        var visitor = testVisitor();

        var predVal = value(true);

        var predicate = mockForVisitorResult(UCELParser.ExpressionContext.class, predVal, visitor);
        var stmtTrue = mockForVisitorResult(UCELParser.BuildStmntContext.class, null, visitor);

        var node = mock(UCELParser.BuildIfContext.class);
        when(node.expression()).thenReturn(predicate);
        when(node.buildStmnt(0)).thenReturn(stmtTrue);

        var actual = visitor.visitBuildIf(node);

        verify(stmtTrue, times(1)).accept(any());
        assertNull(actual);
    }
    //endregion

    //region BuildIteration
    @ParameterizedTest
    @MethodSource("buildIterationTestSuccessSource")
    void buildIterationTestSuccess(int lowerBound, int upperBound) throws Exception {
        var expected = value(); // void
        int expectedItrCount = upperBound - lowerBound + 1; // bounds are inclusive

        var scope = mock(Scope.class);
        var visitor = testVisitor(scope);

        var iteratorDeclRef = mock(DeclarationReference.class);
        var iteratorVarRef = mock(DeclarationInfo.class);
        when(scope.get(iteratorDeclRef)).thenReturn(iteratorVarRef);

        var lowerBoundExpr = mockForVisitorResult(UCELParser.ExpressionContext.class, value(lowerBound), visitor);
        var upperBoundExpr = mockForVisitorResult(UCELParser.ExpressionContext.class, value(upperBound), visitor);
        var stmt = mockForVisitorResult(UCELParser.BuildStmntContext.class, value(), visitor);

        var node = mock(UCELParser.BuildIterationContext.class);
        node.reference = iteratorDeclRef;
        when(node.expression(0)).thenReturn(lowerBoundExpr);
        when(node.expression(1)).thenReturn(upperBoundExpr);
        when(node.buildStmnt()).thenReturn(stmt);

        var actual = visitor.visitBuildIteration(node);

        verify(stmt, times(expectedItrCount)).accept(any());
        verify(iteratorVarRef, times(expectedItrCount)).setValue(any());
        assertEquals(expected, actual);
    }
    private static Stream<Arguments> buildIterationTestSuccessSource() {
        // int lowerBound, int upperBound
        return Stream.of(
            Arguments.of(0, 4),
            Arguments.of(8, 12),
            Arguments.of(-12, -5)
        );
    }

    @ParameterizedTest
    @MethodSource("buildIterationTestMalformedBoundsSource")
    void buildIterationTestMalformedBounds(int lowerBound, int upperBound) {
        InterpreterValue expected = null; // error
        int expectedItrCount = 0;

        var visitor = testVisitor();

        var lowerBoundExpr = mockForVisitorResult(UCELParser.ExpressionContext.class, value(lowerBound), visitor);
        var upperBoundExpr = mockForVisitorResult(UCELParser.ExpressionContext.class, value(upperBound), visitor);
        var stmt = mockForVisitorResult(UCELParser.BuildStmntContext.class, value(), visitor);

        var node = mock(UCELParser.BuildIterationContext.class);
        when(node.expression(0)).thenReturn(lowerBoundExpr);
        when(node.expression(1)).thenReturn(upperBoundExpr);
        when(node.buildStmnt()).thenReturn(stmt);

        var actual = visitor.visitBuildIteration(node);

        verify(stmt, times(expectedItrCount)).accept(any());
        assertEquals(expected, actual);
    }
    private static Stream<Arguments> buildIterationTestMalformedBoundsSource() {
        // int lowerBound, int upperBound
        return Stream.of(
                Arguments.of(4, 0),
                Arguments.of(12, 8),
                Arguments.of(-5, -12)
        );
    }

    @Test
    void buildIterationTestStmtError() throws Exception {
        int lowerBound = 0;
        int upperBound = 4;
        InterpreterValue expected = null; // error

        var scope = mock(Scope.class);
        var visitor = testVisitor(scope);

        var iteratorDeclRef = mock(DeclarationReference.class);
        var iteratorVarRef = mock(DeclarationInfo.class);
        when(scope.get(iteratorDeclRef)).thenReturn(iteratorVarRef);

        var lowerBoundExpr = mockForVisitorResult(UCELParser.ExpressionContext.class, value(lowerBound), visitor);
        var upperBoundExpr = mockForVisitorResult(UCELParser.ExpressionContext.class, value(upperBound), visitor);
        var stmt = mockForVisitorResult(UCELParser.BuildStmntContext.class, null, visitor);

        var node = mock(UCELParser.BuildIterationContext.class);
        node.reference = iteratorDeclRef;
        when(node.expression(0)).thenReturn(lowerBoundExpr);
        when(node.expression(1)).thenReturn(upperBoundExpr);
        when(node.buildStmnt()).thenReturn(stmt);

        var actual = visitor.visitBuildIteration(node);

        assertEquals(expected, actual);
    }

    //endregion

    //endregion

    //region Build / Linker

    //endregion

    //region Helper methods

    private<T extends ParseTree> T mockForVisitorResult(final Class<T> nodeType, final InterpreterValue visitResult, InterpreterVisitor visitor) {
        final T mock = mock(nodeType);
        when(mock.accept(visitor)).thenReturn(visitResult);
        return mock;
    }

    private InterpreterVisitor testVisitor() {
        var logger = mock(ILogger.class);
        var scope = mock(Scope.class);
        return new InterpreterVisitor(logger, scope);
    }

    private InterpreterVisitor testVisitor(ILogger logger) {
        var scope = mock(Scope.class);
        return new InterpreterVisitor(logger, scope);
    }

    private InterpreterVisitor testVisitor(Scope scope) {
        var logger = mock(ILogger.class);
        return new InterpreterVisitor(logger, scope);
    }

    //region Value
    private static VoidValue value() {
        return new VoidValue();
    }

    private static IntegerValue value(int val) {
        return new IntegerValue(val);
    }

    private static StringValue value(String val) {
        return new StringValue(val);
    }
    private static BooleanValue value(boolean val) {
        return new BooleanValue(val);
    }
    //endregion
    //endregion
}
