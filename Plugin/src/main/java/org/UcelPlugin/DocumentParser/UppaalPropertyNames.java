package org.UcelPlugin.DocumentParser;

public class UppaalPropertyNames {
    public class Project {
        public static final String declaration = "declaration";
        public static final String systemDeclaration = "system";
    }

    public class Template {
        public static final String name = "name";
        public static final String parameter = "parameter";
        public static final String declaration = "declaration";
    }

    public class Location {
        public static final String posX = "x";
        public static final String posY = "y";
        public static final String name = "name";
        public static final String invariant = "invariant";
        public static final String rateOfExponential = "exponentialrate";
        public static final String init = "init";
        public static final String urgent = "urgent";
        public static final String committed = "committed";
        public static final String comments = "comments";
        public static final String testCodeOnEnter = "testcodeEnter";
        public static final String testCodeOnExit = "testcodeExit";
    }

    public class Edge {
        public static final String select = "select";
        public static final String guard = "guard";
        public static final String sync = "synchronisation";
        public static final String update = "assignment";
        public static final String comment = "comments";
        public static final String testCode = "testcode";
    }
}
