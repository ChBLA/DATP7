import org.antlr.v4.runtime.*;

public class Main {
    public static void main(String[] args) {
        //UCELBaseListener a = new UCELBaseListener();
        new Main();
    }

    public Main() {
        CharStream charStream = CharStreams.fromString("1+2*7");
        UCELLexer lexer = new UCELLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        UCELParser parser = new UCELParser(tokenStream);

        ReferenceVisitor referenceVisitor = new ReferenceVisitor();
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor();

        UCELParser.ExpressionContext expressionContext = parser.expression();

        referenceVisitor.visit(expressionContext);
        //Type type = typeCheckerVisitor.visit(expressionContext);

        //System.out.println(type);
    }
}