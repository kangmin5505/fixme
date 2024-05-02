package kr._42.seoul;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int FAIL_CODE = 1;
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5001;

    public static void main(String[] args) {

        Repository repository = MemoryRepository.getInstance();
        // Repository repository = new DBRepository();

        Market market = new Market(repository);

        try {
            market.open();
            market.connect(HOSTNAME, PORT);
        } catch (IOException e) {
            logger.error("Failed to start Market Server", e.getMessage());
            System.exit(FAIL_CODE);
        }

        market.run();
    }
}