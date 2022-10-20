package org.Ucel;

public interface IEdge {
    public ILocation getLocationStart();

    public ILocation getLocationEnd();

    public String getSelect();

    public String getGuard();

    public String getSync();

    public String getUpdate();

    public String getComment();

    public String getTestCode();
}