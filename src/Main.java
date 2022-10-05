import org.antlr.v4.runtime.*;

public class Main {
    public static void main(String[] args) {
        //UCELBaseListener a = new UCELBaseListener();

        String input = "{ \n " +
                          "bool b = true; \n " +
                          "bool a = true > 12 && true || b;\n" +
                       "}";
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

        UCELParser.BlockContext block = parser.block();

        referenceVisitor.visit(block);
        Type type = typeCheckerVisitor.visit(block);

        System.out.println(type);
        logger.printLogs();
    }
}