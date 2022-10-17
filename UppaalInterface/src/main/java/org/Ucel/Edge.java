package org.Ucel;

public class Edge implements IEdge {
    private String id = "";

    @Override
    public String GetId() {
        return id;
    }

    public void SetId(String value) {
        id = value;
    }

    private String locationIdStart = "";

    @Override
    public String GetLocationIdStart() {
        return locationIdStart;
    }

    public void SetLocationIdStart(String value) {
        locationIdStart = value;
    }

    private String locationIdEnd = "";

    @Override
    public String GetLocationIdEnd() {
        return locationIdEnd;
    }

    public void SetLocationIdEnd(String value) {
        locationIdEnd = value;
    }

    private String select = "";

    @Override
    public String GetSelect() {
        return select;
    }

    public void SetSelect(String value) {
        select = value;
    }

    private String guard = "";

    @Override
    public String GetGuard() {
        return guard;
    }

    public void SetGuard(String value) {
        guard = value;
    }

    private String sync = "";

    @Override
    public String GetSync() {
        return sync;
    }

    public void SetSync(String value) {
        sync = value;
    }

    private String update = "";

    @Override
    public String GetUpdate() {
        return update;
    }

    public void SetUpdate(String value) {
        update = value;
    }

    private String comment = "";

    @Override
    public String GetComment() {
        return comment;
    }

    public void SetComment(String value) {
        comment = value;
    }

    private String testCode = "";

    @Override
    public String GetTestCode() {
        return testCode;
    }

    public void SetTestCode(String value) {
        testCode = value;
    }
}
