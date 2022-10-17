package org.Ucel;

public class Location implements ILocation {
    private String id = "";

    @Override
    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    private int posX = 0;

    @Override
    public int getPosX() {
        return posX;
    }

    public void setPosX(int value) {
        posX = value;
    }

    private int posY = 0;

    @Override
    public int getPosY() {
        return posY;
    }

    public void setPosY(int value) {
        posY = value;
    }

    private String name = "";

    @Override
    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    private String invariant = "";

    @Override
    public String getInvariant() {
        return invariant;
    }

    public void setInvariant(String value) {
        invariant = value;
    }

    private String rateOfExponential = "";

    @Override
    public String getRateOfExponential() {
        return rateOfExponential;
    }

    public void setRateOfExponential(String value) {
        rateOfExponential = value;
    }

    private boolean initial = false;

    @Override
    public boolean getInitial() {
        return initial;
    }

    public void setInitial(boolean value) {
        initial = value;
    }

    private boolean urgent = false;

    @Override
    public boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(boolean value) {
        urgent = value;
    }

    private boolean committed = false;

    @Override
    public boolean getCommitted() {
        return committed;
    }

    public void setCommitted(boolean value) {
        committed = value;
    }

    private String comments = "";

    @Override
    public String getComments() {
        return comments;
    }

    public void setComments(String value) {
        comments = value;
    }

    private String testCodeOnEnter = "";

    @Override
    public String getTestCodeOnEnter() {
        return testCodeOnEnter;
    }

    public void setTestCodeOnEnter(String value) {
        testCodeOnEnter = value;
    }

    private String testCodeOnExit = "";

    @Override
    public String getTestCodeOnExit() {
        return testCodeOnExit;
    }

    public void setTestCodeOnExit(String value) {
        testCodeOnExit = value;
    }
}
