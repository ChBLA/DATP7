import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.PrototypeDocument;
import org.Ucel.IProject;
import org.UcelPlugin.DocumentParser.UcelToUppaalDocumentParser;
import org.UcelPlugin.DocumentParser.UppaalPropertyNames;
import org.UcelPlugin.DocumentParser.UppaalToUcelDocumentParser;
import org.UcelPlugin.DocumentParser.UppaalToUcelGraphParser;
import org.UcelPlugin.Models.SharedInterface.*;
import org.UcelPlugin.UppaalManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {

    /// Tests whether the plugin can set all values in Uppaal and read them back
    @Test
    public void bigUppaalParserTest() {
        Document doc = new Document(new PrototypeDocument());
        var ucelToUp = new UcelToUppaalDocumentParser(doc);
        var uppToUcel = new UppaalToUcelDocumentParser(doc);

        IProject dummyProj1 = createDummyProject("_dummyproj1", 234, true);
        ucelToUp.parseProject(dummyProj1);
        IProject reparsedProject1 = uppToUcel.parseDocument();
        assertEquals(reparsedProject1, dummyProj1);

        IProject dummyProj2 = createDummyProject("_dummyproj2", 634, false);
        ucelToUp.parseProject(dummyProj2);
        IProject reparsedProject2 = uppToUcel.parseDocument();
        assertEquals(reparsedProject2, dummyProj2);
    }


    private IProject createDummyProject(String stringValuesSuffix, int intValues, boolean boolValue) {
        Project proj = new Project();
        proj.setDeclaration("projDecl" + stringValuesSuffix);
        proj.setSystemDeclarations("projSysDecl" + stringValuesSuffix);

        Template temp = new Template();
        temp.setName("tempName" + stringValuesSuffix);
        temp.setParameters("tempParams" + stringValuesSuffix);
        temp.setDeclarations("tempDecls" + stringValuesSuffix);

        Graph graph = new Graph();

        Location loc1 = createDummyLocation("_loc1_"+stringValuesSuffix, intValues+10, boolValue);
        Location loc2 = createDummyLocation("_loc2_"+stringValuesSuffix, intValues+20, !boolValue);

        Edge edge = new Edge();
        edge.setLocationStart(loc1);
        edge.setLocationEnd(loc2);
        edge.setSelect("edgeSelect" + stringValuesSuffix);
        edge.setGuard("edgeGuard" + stringValuesSuffix);
        edge.setSync("edgeSync" + stringValuesSuffix);
        edge.setUpdate("edgeUpdate" + stringValuesSuffix);
        edge.setComment("edgeComment" + stringValuesSuffix);
        edge.setTestCode("edgeTestCode" + stringValuesSuffix);

        proj.putTemplate(temp);
        temp.setGraph(graph);
        graph.addLocation(loc1);
        graph.addLocation(loc2);
        graph.addEdge(edge);

        return proj;
    }

    private Location createDummyLocation(String stringValuesSuffix, int intValueAdd, boolean boolValue) {
        Location location = new Location();
        location.setName("locName" + stringValuesSuffix);

        location.setPosX(1 + intValueAdd);
        location.setPosY(2 + intValueAdd);
        location.setName("locName" + stringValuesSuffix);

        location.setInvariant("locInvariant" + stringValuesSuffix);
        location.setRateOfExponential("locExpo" + stringValuesSuffix);
        location.setInitial(boolValue);
        location.setUrgent(boolValue);
        location.setCommitted(boolValue);
        location.setComments("locComments" + stringValuesSuffix);
        location.setTestCodeOnEnter("locTestEnter" + stringValuesSuffix);
        location.setTestCodeOnExit("locTestExit" + stringValuesSuffix);

        return location;
    }
}

