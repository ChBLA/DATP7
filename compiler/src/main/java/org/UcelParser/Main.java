package org.UcelParser;

import org.Ucel.IProject;
import org.UcelParser.CodeGeneration.CodeGenVisitor;
import org.UcelParser.CodeGeneration.templates.Template;
import org.UcelParser.Util.Exception.ErrorsFoundException;
import org.antlr.runtime.tree.ParseTree;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import org.UcelParser.UCELParser_Generated.*;
import org.UcelParser.Util.*;
import org.UcelParser.ReferenceHandler.ReferenceVisitor;
import org.UcelParser.TypeChecker.TypeCheckerVisitor;
import org.UcelParser.Util.Logging.Logger;

public class Main {
    public static void main(String[] args) {
        String input = "{\n" +
                        "int i; \n" +
                        "for (i = 0; i < 10; i++) { \n " +
                          "bool b = true; \n " +
                          "bool a = true > 12 && true || b;\n" +
                       "}}";
        new Compiler().compile(input);
    }
}