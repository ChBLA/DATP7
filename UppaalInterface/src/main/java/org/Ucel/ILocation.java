package org.Ucel;

public interface ILocation {
    public int getPosX();

    public int getPosY();

    public String getName();

    public String getInvariant();

    public String getRateOfExponential();

    public boolean getInitial();

    public boolean getUrgent();

    public boolean getCommitted();

    public String getComments();

    public String getTestCodeOnEnter();

    public String getTestCodeOnExit();
}
