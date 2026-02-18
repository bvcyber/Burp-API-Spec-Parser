import com.bureauveritas.modelparser.control.file.handler.mcp.McpServersJsonFileHandler;
import com.bureauveritas.modelparser.control.file.loader.McpServersJsonFileLoader;
import com.bureauveritas.modelparser.model.OpenModelFile;
import com.bureauveritas.modelparser.model.mcp.McpServersJsonFileModel;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class McpTest extends MontoyaTest {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final McpServersJsonFileLoader loader = new McpServersJsonFileLoader();

    @Test
    public void loadValid() throws Exception {
        McpServersJsonFileModel model =
            loader.loadModel(new File(getClass().getResource("mcp-servers-config-2026-02-11-05-21-06.json").toURI()));
    }

    @Test
    public void loadGarbage() {
        try {
            McpServersJsonFileModel model =
                loader.loadModel(new File(getClass().getResource("garbage.json").toURI()));
            fail("Expected an exception!");
        }
        catch (MismatchedInputException e) {
            // This is expected
            System.out.println(e);
        }
        catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void loadMissingRequired() {
        try {
            McpServersJsonFileModel model =
                loader.loadModel(new File(getClass().getResource("mcp-missing-required.json").toURI()));
            McpServersJsonFileModel model2 =
                loader.loadModel(new File(getClass().getResource("mcp-missing-required-2.json").toURI()));
            fail("Expected an exception!");
        }
        catch (MismatchedInputException e) {
            // This is expected
            System.out.println(e);
        }
        catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void loadInvalidType() {
        try {
            McpServersJsonFileModel model =
                loader.loadModel(new File(getClass().getResource("mcp-invalid-type.json").toURI()));
            fail("Expected an exception!");
        }
        catch (InvalidFormatException e) {
            // This is expected
            System.out.println(e);
        }
        catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void loadValidWithOpenModelFile() throws Exception {
        OpenModelFile openModelFile = new OpenModelFile();
        openModelFile.loadModelFromFile(
            new File(getClass().getResource("mcp-servers-config-2026-02-11-05-21-06.json").toURI()));
        assertEquals("MCP Servers JSON", openModelFile.getModelFileHandler().getModelType());
        McpServersJsonFileModel model = (McpServersJsonFileModel) openModelFile.getModelFileHandler().getModel();

        Set<String> expectedServers = new HashSet<>();
        expectedServers.add("burp-mcp");
        expectedServers.add("intellij-mcp");
        expectedServers.add("test-stdio");
        expectedServers.add("test-stdio2");
        assertEquals(expectedServers, model.getMcpServers().keySet());

        System.out.println(openModelFile.getModelFileHandler().getOperations());
        System.out.println(openModelFile.getModelFileHandler().getShapeNames());
        System.out.println(openModelFile.getModelFileHandler().getShapeDefinition("burp-mcp"));
        System.out.println(openModelFile.getModelFileHandler().getOperationDefinition("tool/execute_run_configuration (intellij-mcp)"));
        System.out.println(openModelFile.getModelFileHandler().getHosts("initialize (intellij-mcp)"));
        ((McpServersJsonFileHandler) openModelFile.getModelFileHandler()).debug();
    }
}
