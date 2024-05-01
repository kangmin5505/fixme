package kr._42.seoul;

import java.io.IOException;

public class Main {
   private static final String HOSTNAME = "localhost";
    private static final int PORT = 5001;

    public static void main(String[] args) {

        Repository repository = MemoryRepository.getInstance();
        // Repository repository = new DBRepository();

        try {
            Market market = new Market(HOSTNAME, PORT, repository);
            market.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}