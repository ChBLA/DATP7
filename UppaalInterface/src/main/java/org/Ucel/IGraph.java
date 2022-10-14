package org.Ucel;

import java.util.List;

public interface IGraph {

    public List<Location> GetLocations();
    public List<Edge> GetEdges();

    public interface Location {
        public String GetId();
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

    public interface Edge {
        public String GetId();
        public String GetLocationIdStart();
        public String GetLocationIdEnd();
        public String GetSelect();
        public String GetGuard();
        public String GetSync();
        public String GetUpdate();
        public String GetComment();
        public String GetTestCode();
    }
}
