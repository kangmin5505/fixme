package kr._42.seoul;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.market.InstrumentRegister;
import kr._42.seoul.market.Market;
import kr._42.seoul.repository.DBRepository;
import kr._42.seoul.repository.Repository;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5001;

    public static void main(String[] args) {
        boolean result = InstrumentRegister.register(args);
        if (!result) {
            logger.error("Usage: java -jar market.jar [instrument1] [instrument2] ...");
            System.exit(1);
        }
        
        try {
            Repository repository = DBRepository.getInstance();
            repository.init();

            Set<String> instruments = InstrumentRegister.getInstruments();
            Market market = new Market(instruments, repository);

            market.open();
            market.connect(HOSTNAME, PORT);
            market.run();
        } catch (Exception e) {
            logger.error("Failed to start Market Server", e.getMessage());
            System.exit(1);
        }
    }
}