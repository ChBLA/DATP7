package org.Ucel;

import java.util.List;

public interface IGraph {

    public List<ILocation> GetLocations();
    public List<IEdge> GetEdges();

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

    public interface IEdge {
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
