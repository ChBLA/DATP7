import org.antlr.v4.runtime.*;

public class Main {
    public static void main(String[] args) {
        //UCELBaseListener a = new UCELBaseListener();

        String input = "true > 12 && true || b";
        new Main(input);
    }

    public Main(String input) {

        CharStream charStream = CharStreams.fromString(input);
        UCELLexer lexer = new UCELLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        UCELParser parser = new UCELParser(tokenStream);

        Logger logger = new Logger(input);
        ReferenceVisitor referenceVisitor = new ReferenceVisitor(logger);
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor(logger);

        UCELParser.ExpressionContext expressionContext = parser.expression();

        referenceVisitor.visit(expressionContext);
        Type type = typeCheckerVisitor.visit(expressionContext);

        System.out.println(type);
        logger.printLogs();
    }
}