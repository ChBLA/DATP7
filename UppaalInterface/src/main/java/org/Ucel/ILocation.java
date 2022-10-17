package org.Ucel;

public interface ILocation {
    public String GetId();

    public int GetPosX();

    public int GetPosY();

    public String GetName();

    public String GetInvariant();

    public String GetRateOfExponential();

    public boolean GetInitial();

    public boolean GetUrgent();

    public boolean GetCommitted();

    public String GetComments();

    public String GetTestCodeOnEnter();

    public String GetTestCodeOnExit();
}
