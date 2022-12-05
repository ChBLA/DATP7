package org.Ucel;

import java.util.List;

public interface IProject {
    public String getDeclaration();
    public List<ITemplate> getTemplates();
    public String getSystemDeclarations();
    public List<IVerificationQuery> getVerificationQueries();
}
