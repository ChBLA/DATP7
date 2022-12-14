package org.Ucel;

import java.util.Objects;

public class Edge implements IEdge {

    public Edge() {
        this(null, null, "", "", "", "", "", "");
    }

    public Edge(
            ILocation locationStart,
            ILocation locationEnd,
            String select,
            String guard,
            String sync,
            String update,
            String comment,
            String testCode
    ) {
        setLocationStart(locationStart);
        setLocationEnd(locationEnd);
        setSelect(select);
        setGuard(guard);
        setSync(sync);
        setUpdate(update);
        setComment(comment);
        setTestCode(testCode);
    }

    private ILocation locationStart;

    @Override
    public ILocation getLocationStart() {
        return locationStart;
    }

    public void setLocationStart(ILocation value) {
        locationStart = value;
    }

    private ILocation locationEnd;

    @Override
    public ILocation getLocationEnd() {
        return locationEnd;
    }

    public void setLocationEnd(ILocation value) {
        locationEnd = value;
    }

    private String select;

    @Override
    public String getSelect() {
        return select;
    }

    public void setSelect(String value) {
        select = value;
    }

    private String guard;

    @Override
    public String getGuard() {
        return guard;
    }

    public void setGuard(String value) {
        guard = value;
    }

    private String sync;

    @Override
    public String getSync() {
        return sync;
    }

    public void setSync(String value) {
        sync = value;
    }

    private String update;

    @Override
    public String getUpdate() {
        return update;
    }

    public void setUpdate(String value) {
        update = value;
    }

    private String comment;

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String value) {
        comment = value;
    }

    private String testCode;

    @Override
    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String value) {
        testCode = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(locationStart, edge.locationStart) && Objects.equals(locationEnd, edge.locationEnd) && Objects.equals(select, edge.select) && Objects.equals(guard, edge.guard) && Objects.equals(sync, edge.sync) && Objects.equals(update, edge.update) && Objects.equals(comment, edge.comment) && Objects.equals(testCode, edge.testCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationStart, locationEnd, select, guard, sync, update, comment, testCode);
    }

    @Override
    public String toString() {
        return locationStart.toString() + "->" + locationEnd.toString();
    }
}
