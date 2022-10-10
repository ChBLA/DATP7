import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.print.DocFlavor;
import java.io.Console;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class CodeGenTests {

    //region initialiser

    @Test
    void InitialiserExpressionGeneratedCorrectly() {
        Template expr = generateDefaultExprTemplate(Type.TypeEnum.intType);

        var visitor = new CodeGenVisitor();
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, expr, visitor);

        var node = mock(UCELParser.InitialiserContext.class);

        when(node.expression()).thenReturn(exprMock);

        String actual = visitor.visitInitialiser(node).getOutput();

        assertEquals(expr.getOutput(), actual);
    }

    @Test
    void InitialiserNoExpr() {
        String expected = "{}";
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.InitialiserContext.class);

        String actual = visitor.visitInitialiser(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void InitialiserGeneratedCorrectly() {
        Template expr = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("{%s, %s}", expr.getOutput(), expr.getOutput());

        var visitor = new CodeGenVisitor();
        var initialiserMock = mockForVisitorResult(UCELParser.InitialiserContext.class, expr, visitor);
        List<UCELParser.InitialiserContext> initialiserContextList = new ArrayList<>();
        initialiserContextList.add(initialiserMock);
        initialiserContextList.add(initialiserMock);

        var node = mock(UCELParser.InitialiserContext.class);

        when(node.initialiser()).thenReturn(initialiserContextList);

        String actual = visitor.visitInitialiser(node).getOutput();

        assertEquals(expected, actual);
    }

    //endregion

    //region TypeID

    @Test
    void TypeIDIDGeneratedCorrectly() {
        String variableID = "var1";
        DeclarationInfo variable = mock(DeclarationInfo.class);
        DeclarationReference ref = mock(DeclarationReference.class);

        var scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);

        var node = mock(UCELParser.TypeIDIDContext.class);
        node.reference = ref;

        when(variable.getIdentifier()).thenReturn(variableID);
        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("could not mock scope");
        }

        String actual = visitor.visitTypeIDID(node).getOutput();

        assertEquals(variableID, actual);
    }

    @Test
    void TypeIDTypeGeneratedCorrectly() {
        String expected = generateDefaultTypeTemplate(Type.TypeEnum.doubleType).getOutput();

        var visitor = new CodeGenVisitor();
        var node = mock(UCELParser.TypeIDTypeContext.class);
        // Maybe set correct type, but should not matter
        node.op = new CommonToken(0, expected);

        String actual = visitor.visitTypeIDType(node).getOutput();

        assertEquals(expected, actual);
    }


    @Test
    void TypeIDIntGeneratedCorrectly() {
        Template exprTemp = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("int[%s,%s]", exprTemp.getOutput(),
                                                      exprTemp.getOutput());

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.TypeIDIntContext.class);
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemp, visitor);

        when(node.expression(0)).thenReturn(exprMock);
        when(node.expression(1)).thenReturn(exprMock);

        String actual = visitor.visitTypeIDInt(node).getOutput();

        assertEquals(expected, actual);
   }

    @Test
    void TypeIDIntNoLeftExprGeneratedCorrectly() {
        Template exprTemp = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("int[,%s]", exprTemp.getOutput());

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.TypeIDIntContext.class);
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemp, visitor);

        when(node.expression(1)).thenReturn(exprMock);

        String actual = visitor.visitTypeIDInt(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void TypeIDIntNoRightExprGeneratedCorrectly() {
        Template exprTemp = generateDefaultExprTemplate(Type.TypeEnum.intType);
        String expected = String.format("int[%s,]", exprTemp.getOutput());

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.TypeIDIntContext.class);
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemp, visitor);

        when(node.expression(0)).thenReturn(exprMock);

        String actual = visitor.visitTypeIDInt(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void TypeIDScalarGeneratedCorrectly() {
        Template exprTemp = generateDefaultExprTemplate(Type.TypeEnum.intType);
        Template scalarTemp = generateDefaultTypeTemplate(Type.TypeEnum.scalarType);
        String expected = String.format("%s[%s]", scalarTemp.getOutput(), exprTemp.getOutput());

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.TypeIDScalarContext.class);
        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemp, visitor);

        when(node.expression()).thenReturn(exprMock);

        String actual = visitor.visitTypeIDScalar(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void TypeIDStructGeneratedCorrectly() {
        Template fieldDecl1Temp = new ManualTemplate("int a;");
        Template fieldDecl2Temp = new ManualTemplate("int b;");
        String expected = String.format("struct {\n%s\n%s\n}",
                fieldDecl1Temp.getOutput(),
                fieldDecl2Temp.getOutput());

        var visitor = new CodeGenVisitor();

        var fieldDecl1Mock = mockForVisitorResult(UCELParser.FieldDeclContext.class, fieldDecl1Temp, visitor);
        var fieldDecl2Mock = mockForVisitorResult(UCELParser.FieldDeclContext.class, fieldDecl2Temp, visitor);
        List<UCELParser.FieldDeclContext> fieldDeclContextList = new ArrayList<>();
        fieldDeclContextList.add(fieldDecl1Mock);
        fieldDeclContextList.add(fieldDecl2Mock);

        var node = mock(UCELParser.TypeIDStructContext.class);

        when(node.fieldDecl()).thenReturn(fieldDeclContextList);

        String actual = visitor.visitTypeIDStruct(node).getOutput();

        assertEquals(expected, actual);
    }

    //endregion
    //region Type
    @Test
    void TypeWithPrefixGeneratedCorrectly() {
        Template prefixTemplate = new ManualTemplate("urgent");
        Template typeIDTemplate = new ManualTemplate("int[0,10]");
        String expected = "urgent int[0,10]";

        var visitor = new CodeGenVisitor();

        var prefixMock = mockForVisitorResult(UCELParser.PrefixContext.class, prefixTemplate, visitor);
        var typeIDMock = mockForVisitorResult(UCELParser.TypeIdContext.class, typeIDTemplate, visitor);

        var node = mock(UCELParser.TypeContext.class);
        when(node.prefix()).thenReturn(prefixMock);
        when(node.typeId()).thenReturn(typeIDMock);

        String actual = visitor.visitType(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void TypeNoPrefixGeneratedCorrectly() {
        Template typeIDTemplate = new ManualTemplate("int[0,10]");
        String expected = "int[0,10]";

        var visitor = new CodeGenVisitor();

        var typeIDMock = mockForVisitorResult(UCELParser.TypeIdContext.class, typeIDTemplate, visitor);

        var node = mock(UCELParser.TypeContext.class);
        when(node.typeId()).thenReturn(typeIDMock);

        String actual = visitor.visitType(node).getOutput();

        assertEquals(expected, actual);
    }
    //endregion

    //region variableDecl
    @Test
    void variableDeclSingleVarWithType() {
        Template typeTemp = new ManualTemplate("int[0,10]");
        Template variableIdTemp = new ManualTemplate("goimer = 5");
        String expected = "int[0,10] goimer = 5;";

        var visitor = new CodeGenVisitor();

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeTemp, visitor);
        var variableIdMock = mockForVisitorResult(UCELParser.VariableIDContext.class, variableIdTemp, visitor);
        List<UCELParser.VariableIDContext> variableIDContextList = new ArrayList<>();
        variableIDContextList.add(variableIdMock);

        var node = mock(UCELParser.VariableDeclContext.class);
        when(node.variableID()).thenReturn(variableIDContextList);
        when(node.type()).thenReturn(typeMock);

        var actual = visitor.visitVariableDecl(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void variableDeclMultipleVarWithType() {
        Template typeTemp = new ManualTemplate("int[0,10]");
        Template variableIdTemp = new ManualTemplate("goimer = 5");
        // Not valid but should not matter for these tests
        String expected = "int[0,10] goimer = 5, goimer = 5, goimer = 5;";

        var visitor = new CodeGenVisitor();

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeTemp, visitor);
        var variableIdMock = mockForVisitorResult(UCELParser.VariableIDContext.class, variableIdTemp, visitor);
        List<UCELParser.VariableIDContext> variableIDContextList = new ArrayList<>();
        variableIDContextList.add(variableIdMock);
        variableIDContextList.add(variableIdMock);
        variableIDContextList.add(variableIdMock);

        var node = mock(UCELParser.VariableDeclContext.class);
        when(node.variableID()).thenReturn(variableIDContextList);
        when(node.type()).thenReturn(typeMock);

        var actual = visitor.visitVariableDecl(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void variableDeclSingleVarNoType() {
        Template variableIdTemp = new ManualTemplate("goimer = 5");
        String expected = "goimer = 5;";

        var visitor = new CodeGenVisitor();

        var variableIdMock = mockForVisitorResult(UCELParser.VariableIDContext.class, variableIdTemp, visitor);
        List<UCELParser.VariableIDContext> variableIDContextList = new ArrayList<>();
        variableIDContextList.add(variableIdMock);

        var node = mock(UCELParser.VariableDeclContext.class);
        when(node.variableID()).thenReturn(variableIDContextList);

        var actual = visitor.visitVariableDecl(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void variableDeclMultipleVarNoType() {
        Template variableIdTemp = new ManualTemplate("goimer = 5");
        // Not valid but should not matter for these tests
        String expected = "goimer = 5, goimer = 5, goimer = 5;";

        var visitor = new CodeGenVisitor();

        var variableIdMock = mockForVisitorResult(UCELParser.VariableIDContext.class, variableIdTemp, visitor);
        List<UCELParser.VariableIDContext> variableIDContextList = new ArrayList<>();
        variableIDContextList.add(variableIdMock);
        variableIDContextList.add(variableIdMock);
        variableIDContextList.add(variableIdMock);

        var node = mock(UCELParser.VariableDeclContext.class);
        when(node.variableID()).thenReturn(variableIDContextList);

        var actual = visitor.visitVariableDecl(node).getOutput();

        assertEquals(expected, actual);
    }
    //endregion

    //region variableID

    @Test
    void variableIDNoInitGeneratedCorrectly() {
        Template arrayDecl = new ManualTemplate("[10]");
        String expected = "var1[10]";
        String variableID = "var1";
        DeclarationInfo variable = new DeclarationInfo(variableID, new Type(Type.TypeEnum.intType, 10));
        DeclarationReference ref = new DeclarationReference(0, 1);

        Scope scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var arrayDeclMock = mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDecl, visitor);
        List<UCELParser.ArrayDeclContext> arrayDeclContextList = new ArrayList<>();
        arrayDeclContextList.add(arrayDeclMock);

        var node = mock(UCELParser.VariableIDContext.class);
        when(node.arrayDecl()).thenReturn(arrayDeclContextList);
        when(node.arrayDecl(0)).thenReturn(arrayDeclMock);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        var actual = visitor.visitVariableID(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void variableIDWithInit() {
        Template initTemplate = new ManualTemplate("5");
        String expected = "var1 = 5";
        String variableID = "var1";
        DeclarationInfo variable = new DeclarationInfo(variableID, new Type(Type.TypeEnum.intType));
        DeclarationReference ref = new DeclarationReference(0, 1);

        Scope scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var initMock = mockForVisitorResult(UCELParser.InitialiserContext.class, initTemplate, visitor);

        var node = mock(UCELParser.VariableIDContext.class);
        when(node.initialiser()).thenReturn(initMock);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        var actual = visitor.visitVariableID(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void variableIDMultipleArrayDecl() {
        Template arrayDecl1 = new ManualTemplate("[10]");
        Template arrayDecl2 = new ManualTemplate("[5]");
        String expected = "var1[10][5]";
        String variableID = "var1";
        // TODO: set correct array type for declarationinfo maybe
        DeclarationInfo variable = new DeclarationInfo(variableID, new Type(Type.TypeEnum.intType));
        DeclarationReference ref = new DeclarationReference(0, 1);

        Scope scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var arrayDeclMock1 = mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDecl1, visitor);
        var arrayDeclMock2 = mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDecl2, visitor);
        List<UCELParser.ArrayDeclContext> arrayDeclContextList = new ArrayList<>();
        arrayDeclContextList.add(arrayDeclMock1);
        arrayDeclContextList.add(arrayDeclMock2);

        var node = mock(UCELParser.VariableIDContext.class);
        when(node.arrayDecl()).thenReturn(arrayDeclContextList);
        when(node.arrayDecl(0)).thenReturn(arrayDeclMock1);
        when(node.arrayDecl(1)).thenReturn(arrayDeclMock2);

        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        var actual = visitor.visitVariableID(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void variableIDMultipleArrayWithInitGeneratedCorrectly() {
        Template arrayDecl = new ManualTemplate("[]");
        Template initTemp = new ManualTemplate("{{1,2,3}, {1,2,3,4}}");
        String expected = "var1[][] = {{1,2,3}, {1,2,3,4}}";
        String variableID = "var1";
        // TODO: set correct array type for declarationinfo maybe
        DeclarationInfo variable = new DeclarationInfo(variableID, new Type(Type.TypeEnum.intType));
        DeclarationReference ref = new DeclarationReference(0, 1);

        Scope scopeMock = mock(Scope.class);
        var visitor = new CodeGenVisitor(scopeMock);
        var arrayDeclMock = mockForVisitorResult(UCELParser.ArrayDeclContext.class, arrayDecl, visitor);
        var initMock = mockForVisitorResult(UCELParser.InitialiserContext.class, initTemp, visitor);
        List<UCELParser.ArrayDeclContext> arrayDeclContextList = new ArrayList<>();
        arrayDeclContextList.add(arrayDeclMock);
        arrayDeclContextList.add(arrayDeclMock);

        var node = mock(UCELParser.VariableIDContext.class);
        when(node.arrayDecl()).thenReturn(arrayDeclContextList);
        when(node.arrayDecl(0)).thenReturn(arrayDeclMock);
        when(node.arrayDecl(1)).thenReturn(arrayDeclMock);
        when(node.initialiser()).thenReturn(initMock);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        var actual = visitor.visitVariableID(node).getOutput();

        assertEquals(expected, actual);
    }





    //endregion

    //region arrayDecl
    @Test
    void arrayDeclExprGeneratedCorrectly() {
        Template exprTemplate = new ManualTemplate("10");
        String expected = "[10]";
        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.ArrayDeclContext.class);
        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprTemplate, visitor);

        when(node.expression()).thenReturn(expr);

        var actual = visitor.visitArrayDecl(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void arrayDeclTypeGeneratedCorrectly() {
        Template typeTemplate = new ManualTemplate("int[0,10]");
        String expected = "[int[0,10]]";

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.ArrayDeclContext.class);
        var type = mockForVisitorResult(UCELParser.TypeContext.class, typeTemplate, visitor);

        when(node.type()).thenReturn(type);

        var actual = visitor.visitArrayDecl(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void arrayDeclNoExpr() {
        String expected = "[]";

        var visitor = new CodeGenVisitor();

        var node = mock(UCELParser.ArrayDeclContext.class);

        var actual = visitor.visitArrayDecl(node).getOutput();

        assertEquals(expected, actual);
    }


    //endregion

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

    //region Expressions



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

    //region Control structure
    //region If-statement
    @Test
    void ifStatementNoElseCorrectlyGenerated() {
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("if (%s) %s", exprResult.getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.IfstatementContext.class);

        when(node.expression()).thenReturn(expr);
        when(node.statement(0)).thenReturn(stmnt);

        var actual = visitor.visitIfstatement(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void ifStatementWithElseCorrectlyGenerated() {
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmnt1Result = generateDefaultStatementTemplate();
        Template stmnt2Result = generateDefaultStatementTemplate();
        String expected = String.format("if (%s) %s else %s", exprResult.getOutput(), stmnt1Result.getOutput(), stmnt2Result.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var stmnt1 = mockForVisitorResult(UCELParser.StatementContext.class, stmnt1Result, visitor);
        var stmnt2 = mockForVisitorResult(UCELParser.StatementContext.class, stmnt2Result, visitor);
        var node = mock(UCELParser.IfstatementContext.class);

        when(node.expression()).thenReturn(expr);
        when(node.statement(0)).thenReturn(stmnt1);
        when(node.statement(1)).thenReturn(stmnt2);

        var actual = visitor.visitIfstatement(node).getOutput();

        assertEquals(expected, actual);
    }

    //endregion

    //region While-loop
    @Test
    void whileStatementCorrectlyGenerated() {
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("while (%s) %s", exprResult.getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.WhileLoopContext.class);

        when(node.expression()).thenReturn(expr);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitWhileLoop(node).getOutput();

        assertEquals(expected, actual);
    }

    //endregion

    //region Do-while-loop
    @Test
    void doWhileStatementCorrectlyGenerated() {
        Template exprResult = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("do %s while (%s);", stmntResult.getOutput(), exprResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.DowhileContext.class);

        when(node.expression()).thenReturn(expr);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitDowhile(node).getOutput();

        assertEquals(expected, actual);
    }

    //endregion

    //region For-loop
    // FOR LEFTPAR assignment? END expression? END expression? RIGHTPAR statement;
    @Test
    void forLoopStatementGeneratedCorrectly() {
        Template expr1Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template expr2Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template assignResult = generateDefaultAssignmentTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", assignResult.getOutput(), expr1Result.getOutput(), expr2Result.getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var assign = mockForVisitorResult(UCELParser.AssignmentContext.class, assignResult, visitor);
        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Result, visitor);
        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(expr2);
        when(node.assignment()).thenReturn(assign);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyAssignGeneratedCorrectly() {
        Template expr1Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template expr2Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", generateEmptyAssignTemplate().getOutput(), expr1Result.getOutput(), expr2Result.getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Result, visitor);
        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(expr2);
        when(node.assignment()).thenReturn(null);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void forLoopEmptyExpr1GeneratedCorrectly() {
        Template expr1Result = generateEmptyExprTemplate();
        Template expr2Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template assignResult = generateDefaultAssignmentTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", assignResult.getOutput(), expr1Result.getOutput(), expr2Result.getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var assign = mockForVisitorResult(UCELParser.AssignmentContext.class, assignResult, visitor);
        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(null);
        when(node.expression(1)).thenReturn(expr2);
        when(node.assignment()).thenReturn(assign);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyExpr2GeneratedCorrectly() {
        Template expr1Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template expr2Result = generateEmptyExprTemplate();
        Template assignResult = generateDefaultAssignmentTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", assignResult.getOutput(), expr1Result.getOutput(), expr2Result.getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var assign = mockForVisitorResult(UCELParser.AssignmentContext.class, assignResult, visitor);
        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(null);
        when(node.assignment()).thenReturn(assign);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyExpr1Expr2GeneratedCorrectly() {
        Template assignResult = generateDefaultAssignmentTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", assignResult.getOutput(), generateEmptyExprTemplate().getOutput(), generateEmptyExprTemplate().getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var assign = mockForVisitorResult(UCELParser.AssignmentContext.class, assignResult, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(null);
        when(node.expression(1)).thenReturn(null);
        when(node.assignment()).thenReturn(assign);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyAssignExpr1GeneratedCorrectly() {
        Template expr2Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", generateEmptyAssignTemplate().getOutput(), generateEmptyExprTemplate().getOutput(), expr2Result.getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr2 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr2Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(null);
        when(node.expression(1)).thenReturn(expr2);
        when(node.assignment()).thenReturn(null);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyAssignExpr2GeneratedCorrectly() {
        Template expr1Result = generateDefaultExprTemplate(Type.TypeEnum.boolType);
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", generateEmptyAssignTemplate().getOutput(), expr1Result.getOutput(), generateEmptyExprTemplate().getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var expr1 = mockForVisitorResult(UCELParser.ExpressionContext.class, expr1Result, visitor);
        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(expr1);
        when(node.expression(1)).thenReturn(null);
        when(node.assignment()).thenReturn(null);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void forLoopEmptyAssignExprsGeneratedCorrectly() {
        Template stmntResult = generateDefaultStatementTemplate();
        String expected = String.format("for (%s;%s;%s) %s", generateEmptyAssignTemplate().getOutput(), generateEmptyExprTemplate().getOutput(), generateEmptyExprTemplate().getOutput(), stmntResult.getOutput());

        CodeGenVisitor visitor = new CodeGenVisitor();

        var stmnt = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);
        var node = mock(UCELParser.ForLoopContext.class);

        when(node.expression(0)).thenReturn(null);
        when(node.expression(1)).thenReturn(null);
        when(node.assignment()).thenReturn(null);
        when(node.statement()).thenReturn(stmnt);

        var actual = visitor.visitForLoop(node).getOutput();

        assertEquals(expected, actual);
    }
    //endregion

    //region Iteration
    //FOR LEFTPAR ID? COLON type? RIGHTPAR statement;
    @Test
    void iterationGeneratedCorrectly() {
        var idResult = new ManualTemplate("name");
        var typeResult = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var stmntResult = generateDefaultStatementTemplate();
        var expected = String.format("for (%s:%s) %s", idResult.getOutput(), typeResult.getOutput(), stmntResult.getOutput());

        DeclarationInfo variable = new DeclarationInfo(idResult.getOutput(), new Type(Type.TypeEnum.intType, 1));
        DeclarationReference ref = new DeclarationReference(0, 1);

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IterationContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeResult, visitor);
        var stmntMock = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);

        when(node.type()).thenReturn(typeMock);
        when(node.statement()).thenReturn(stmntMock);

        String actual = visitor.visitIteration(node).getOutput();

        assertEquals(expected, actual);
    }

    @Test
    void iterationEmptyIDGeneratedCorrectly() {
        var idResult = new ManualTemplate("");
        var typeResult = generateDefaultTypeTemplate(Type.TypeEnum.intType);
        var stmntResult = generateDefaultStatementTemplate();
        var expected = String.format("for (%s:%s) %s", idResult.getOutput(), typeResult.getOutput(), stmntResult.getOutput());

        DeclarationInfo variable = new DeclarationInfo(idResult.getOutput(), new Type(Type.TypeEnum.intType, 1));
        DeclarationReference ref = new DeclarationReference(0, 1);

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IterationContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeResult, visitor);
        var stmntMock = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);

        when(node.type()).thenReturn(typeMock);
        when(node.statement()).thenReturn(stmntMock);

        String actual = visitor.visitIteration(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void iterationEmptyTypeGeneratedCorrectly() {
        var idResult = new ManualTemplate("");
        var typeResult = new ManualTemplate("");
        var stmntResult = generateDefaultStatementTemplate();
        var expected = String.format("for (%s:%s) %s", idResult.getOutput(), typeResult.getOutput(), stmntResult.getOutput());

        DeclarationInfo variable = new DeclarationInfo(idResult.getOutput(), new Type(Type.TypeEnum.intType, 1));
        DeclarationReference ref = new DeclarationReference(0, 1);

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IterationContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeResult, visitor);
        var stmntMock = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);

        when(node.type()).thenReturn(typeMock);
        when(node.statement()).thenReturn(stmntMock);

        String actual = visitor.visitIteration(node).getOutput();

        assertEquals(expected, actual);
    }
    @Test
    void iterationEmptyIDEmptyTypeGeneratedCorrectly() {
        var idResult = new ManualTemplate("");
        var typeResult = new ManualTemplate("");
        var stmntResult = generateDefaultStatementTemplate();
        var expected = String.format("for (%s:%s) %s", idResult.getOutput(), typeResult.getOutput(), stmntResult.getOutput());

        DeclarationInfo variable = new DeclarationInfo(idResult.getOutput(), new Type(Type.TypeEnum.intType, 1));
        DeclarationReference ref = new DeclarationReference(0, 1);

        var scopeMock = mock(Scope.class);

        var node = mock(UCELParser.IterationContext.class);
        node.reference = ref;

        try {
            when(scopeMock.get(ref)).thenReturn(variable);
        } catch (Exception e) {
            fail("Error in mock. Cannot mock declaration reference");
        }

        CodeGenVisitor visitor = new CodeGenVisitor(scopeMock);

        var typeMock = mockForVisitorResult(UCELParser.TypeContext.class, typeResult, visitor);
        var stmntMock = mockForVisitorResult(UCELParser.StatementContext.class, stmntResult, visitor);

        when(node.type()).thenReturn(typeMock);
        when(node.statement()).thenReturn(stmntMock);

        String actual = visitor.visitIteration(node).getOutput();

        assertEquals(expected, actual);
    }

    //endregion

    //region Return statement
    @ParameterizedTest()
    @EnumSource(value = Type.TypeEnum.class, names ={"intType", "doubleType", "boolType"})
    void returnStatementGeneratedCorrectly(Type.TypeEnum type) {
        var exprResult = generateDefaultExprTemplate(type);
        var expected = String.format("return %s;", exprResult.getOutput());

        var node = mock(UCELParser.ReturnstatementContext.class);
        CodeGenVisitor visitor = new CodeGenVisitor();

        var exprMock = mockForVisitorResult(UCELParser.ExpressionContext.class, exprResult, visitor);

        when(node.expression()).thenReturn(exprMock);

        String actual = visitor.visitReturnstatement(node).getOutput();

        assertEquals(expected, actual);
    }
    //endregion

    //region Statement


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

    private Template generateEmptyExprTemplate() {
        return new ManualTemplate("");
    }

    private Template generateDefaultStatementTemplate() {
        return new ManualTemplate("{ }");
    }

    private Template generateDefaultTypeTemplate(Type.TypeEnum type) {
        return switch (type) {
            case intType -> new ManualTemplate("int");
            case boolType -> new ManualTemplate("bool");
            case doubleType -> new ManualTemplate("double");
            case charType -> new ManualTemplate("char");
            case stringType -> new ManualTemplate("char[]");
            case scalarType -> new ManualTemplate("scalar");
            default -> new ManualTemplate("");
        };
    }

    private Template generateDefaultExprTemplate(String id) {
        return new ManualTemplate(id);
    }

    private Template generateDefaultAssignmentTemplate(Type.TypeEnum type) {
        return switch (type) {
            case intType -> new ManualTemplate("a = 0");
            case boolType -> new ManualTemplate("b = true");
            case doubleType -> new ManualTemplate("d = 0.0");
            case charType -> new ManualTemplate("c = 'a'");
            case stringType -> new ManualTemplate("s = \"abc\"");
            default -> new ManualTemplate("");
        };
    }

    private Template generateEmptyAssignTemplate() {
        return new ManualTemplate("");
    }

    //endregion
}
