package org.UcelParser.Interpreter;

import org.UcelParser.Interpreter.Value.InterpreterValue;
import org.UcelParser.Interpreter.Value.ParameterValue;
import org.UcelParser.UCELParser_Generated.UCELBaseVisitor;
import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Logging.Logger;
import org.UcelParser.Util.Scope;

import java.util.ArrayList;

public class InterpreterVisitor extends UCELBaseVisitor<InterpreterValue> {

    private Scope currentScope;
    private Logger logger;

    public InterpreterVisitor(Scope scope) {
        currentScope = scope;
    }

    public InterpreterVisitor(Logger logger) {
        this.logger = logger;
    }

    public ArrayList<InterpreterValue> interpret(UCELParser.ProjectContext ctx) {
        ParameterValue values = (ParameterValue) visitProject(ctx);
        return values.getParameters();
    }


}
