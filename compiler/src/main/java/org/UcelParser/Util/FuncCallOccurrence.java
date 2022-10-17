package org.UcelParser.Util;

import org.UcelParser.UCELParser_Generated.UCELParser;

public class FuncCallOccurrence {
    private UCELParser.FuncCallContext funcCallContext;
    private DeclarationInfo[] refParams;

    public FuncCallOccurrence(UCELParser.FuncCallContext funcCallContext, DeclarationInfo[] refParams) {
        this.funcCallContext = funcCallContext;
        this.refParams = refParams;
    }

    public UCELParser.FuncCallContext getFuncCallContext() {
        return funcCallContext;
    }

    public DeclarationInfo[] getRefParams() {
        return refParams;
    }
}
