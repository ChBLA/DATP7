package org.Ucel;

public class Location implements ILocation {
    private String id = "";

    @Override
    public String GetId() {
        return id;
    }

    public void SetId(String value) {
        id = value;
    }

    private int posX = 0;

    @Override
    public int GetPosX() {
        return posX;
    }

    public void SetPosX(int value) {
        posX = value;
    }

    private int posY = 0;

    @Override
    public int GetPosY() {
        return posY;
    }

    public void SetPosY(int value) {
        posY = value;
    }

    private String name = "";

    @Override
    public String GetName() {
        return name;
    }

    public void SetName(String value) {
        name = value;
    }

    private String invariant = "";

    @Override
    public String GetInvariant() {
        return invariant;
    }

    public void SetInvariant(String value) {
        invariant = value;
    }

    private String rateOfExponential = "";

    @Override
    public String GetRateOfExponential() {
        return rateOfExponential;
    }

    public void SetRateOfExponential(String value) {
        rateOfExponential = value;
    }

    private boolean initial = false;

    @Override
    public boolean GetInitial() {
        return initial;
    }

    public void SetInitial(boolean value) {
        initial = value;
    }

    private boolean urgent = false;

    @Override
    public boolean GetUrgent() {
        return urgent;
    }

    public void SetUrgent(boolean value) {
        urgent = value;
    }

    private boolean committed = false;

    @Override
    public boolean GetCommitted() {
        return committed;
    }

    public void SetCommitted(boolean value) {
        committed = value;
    }

    private String comments = "";

    @Override
    public String GetComments() {
        return comments;
    }

    public void SetComments(String value) {
        comments = value;
    }

    private String testCodeOnEnter = "";

    @Override
    public String GetTestCodeOnEnter() {
        return testCodeOnEnter;
    }

    public void SetTestCodeOnEnter(String value) {
        testCodeOnEnter = value;
    }

    private String testCodeOnExit = "";

    @Override
    public String GetTestCodeOnExit() {
        return testCodeOnExit;
    }

    public void SetTestCodeOnExit(String value) {
        testCodeOnExit = value;
    }
}
