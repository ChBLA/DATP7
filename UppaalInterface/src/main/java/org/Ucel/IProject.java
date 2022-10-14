package org.Ucel;

import java.util.List;

public interface IProject {
    public String GetDeclaration();
    public List<ITemplate> GetTemplates();
    public String GetSystemDeclarations();
}
