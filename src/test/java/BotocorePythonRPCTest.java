import com.bureauveritas.modelparser.control.BotocorePythonRPC;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class BotocorePythonRPCTest extends MontoyaTest {

    @BeforeAll
    static void setup() {
        BotocorePythonRPC.getInstance().connect(50055);
    }

    @Test
    void testSerializeRequest() {
        try {
            BotocorePythonRPC.getInstance().healthCheck();
        } catch (Exception e) {
//            fail(e);
        }
    }
}
