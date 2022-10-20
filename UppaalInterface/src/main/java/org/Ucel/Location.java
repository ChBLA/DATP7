package org.Ucel;

public class Location implements ILocation {
    public Location() {
        this(0, 0, "", "", "", false, false, false, "", "", "");
    }

    public Location(int posX, int posY, String name, String invariant, String rateOfExponential, boolean initial, boolean urgent, boolean committed, String comments, String testCodeOnEnter, String testCodeOnExit) {
        setPosX(posX);
        setPosY(posY);
        setName(name);
        setInvariant(invariant);
        setRateOfExponential(rateOfExponential);
        setInitial(initial);
        setUrgent(urgent);
        setCommitted(committed);
        setComments(comments);
        setTestCodeOnEnter(testCodeOnEnter);
        setTestCodeOnExit(testCodeOnExit);
    }

    private int posX;

    @Override
    public int getPosX() {
        return posX;
    }

    public void setPosX(int value) {
        posX = value;
    }

    private int posY;

    @Override
    public int getPosY() {
        return posY;
    }

    public void setPosY(int value) {
        posY = value;
    }

    private String name;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    private String invariant;

    @Override
    public String getInvariant() {
        return invariant;
    }

    public void setInvariant(String value) {
        invariant = value;
    }

    private String rateOfExponential;

    @Override
    public String getRateOfExponential() {
        return rateOfExponential;
    }

    public void setRateOfExponential(String value) {
        rateOfExponential = value;
    }

    private boolean initial;

    @Override
    public boolean getInitial() {
        return initial;
    }

    public void setInitial(boolean value) {
        initial = value;
    }

    private boolean urgent;

    @Override
    public boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(boolean value) {
        urgent = value;
    }

    private boolean committed;

    @Override
    public boolean getCommitted() {
        return committed;
    }

    public void setCommitted(boolean value) {
        committed = value;
    }

    private String comments;

    @Override
    public String getComments() {
        return comments;
    }

    public void setComments(String value) {
        comments = value;
    }

    private String testCodeOnEnter;

    @Override
    public String getTestCodeOnEnter() {
        return testCodeOnEnter;
    }

    public void setTestCodeOnEnter(String value) {
        testCodeOnEnter = value;
    }

    private String testCodeOnExit;

    @Override
    public String getTestCodeOnExit() {
        return testCodeOnExit;
    }

    public void setTestCodeOnExit(String value) {
        testCodeOnExit = value;
    }
}
