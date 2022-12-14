import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.PrototypeDocument;
import org.Ucel.IProject;
import org.UcelParser.Compiler;
import org.UcelParser.Util.Exception.ErrorsFoundException;
import org.UcelPlugin.DocumentParser.UcelToUppaalDocumentParser;
import org.UcelPlugin.DocumentParser.UppaalToUcelDocumentParser;
import org.UcelPlugin.UppaalManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class IntegrationTest_DemoProjects {
    private static final String UPPAAL_PATH = System.getenv("UPPAAL_HOME");

    @ParameterizedTest
    @ValueSource(strings = {
            "bridge.xml",
            "2doors.xml"
    })
    public void testFile_Compile_Success(String demoName) throws IOException {
        if(true) // Disabled until we find out how to make uppaal read its own files
            return;

        Path inputPath = Paths.get(UPPAAL_PATH, "demo", demoName);

        IProject inputProject = UppaalManager.getProject(inputPath);

        var compiler = new Compiler();
        try {
            compiler.compileProject(inputProject);
        } catch (ErrorsFoundException e) {
            fail();
        }
    }
}
