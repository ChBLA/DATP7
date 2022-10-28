package org.UcelPlugin;


import org.UcelParser.UCELParser_Generated.UCELParser;

import java.util.ArrayList;

public class CodeTree {
    private final UCELParser.PdeclarationContext declaration;
    private final ArrayList<UCELParser.PtemplateContext> templates;
    private final UCELParser.PsystemContext system;

    public CodeTree(UCELParser.PdeclarationContext declaration, ArrayList<UCELParser.PtemplateContext> templates, UCELParser.PsystemContext system) {
        this.declaration = declaration;
        this.templates = templates;
        this.system = system;
    }

    public UCELParser.PdeclarationContext getDeclaration() {
        return declaration;
    }

    public ArrayList<UCELParser.PtemplateContext> getTemplates() {
        return templates;
    }

    public UCELParser.PsystemContext getSystem() {
        return system;
    }


}
