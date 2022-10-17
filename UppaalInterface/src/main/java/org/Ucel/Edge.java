package org.Ucel;

public class Edge implements IEdge {
    private String id = "";

    @Override
    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    private String locationIdStart = "";

    @Override
    public String getLocationIdStart() {
        return locationIdStart;
    }

    public void setLocationIdStart(String value) {
        locationIdStart = value;
    }

    private String locationIdEnd = "";

    @Override
    public String getLocationIdEnd() {
        return locationIdEnd;
    }

    public void setLocationIdEnd(String value) {
        locationIdEnd = value;
    }

    private String select = "";

    @Override
    public String getSelect() {
        return select;
    }

    public void setSelect(String value) {
        select = value;
    }

    private String guard = "";

    @Override
    public String getGuard() {
        return guard;
    }

    public void setGuard(String value) {
        guard = value;
    }

    private String sync = "";

    @Override
    public String getSync() {
        return sync;
    }

    public void setSync(String value) {
        sync = value;
    }

    private String update = "";

    @Override
    public String getUpdate() {
        return update;
    }

    public void setUpdate(String value) {
        update = value;
    }

    private String comment = "";

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String value) {
        comment = value;
    }

    private String testCode = "";

    @Override
    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String value) {
        testCode = value;
    }
}
