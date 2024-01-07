package kr._42.seoul;

import kr._42.seoul.client.BrokerClient;
import kr._42.seoul.parser.ParameterParser;

public class Broker {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;
    private static int FAIL_STATUS = 2;

    public static void main(String[] args) {
        ParameterParser paramParser = new ParameterParser();
        paramParser.parse(args);

        if (!paramParser.isValid()) {
           usage();
           exit();
        }

        try (BrokerClient brokerClient = BrokerClient.open(HOSTNAME, PORT)) {
            brokerClient.setup();
            String response = brokerClient.request();

            System.out.println("RESPONSE: " + response);
        } catch (RuntimeException e) {
            error(e.getMessage());
        }
    }

    private static void usage() {
        System.err.println("Usage");
        System.err.println("\tjava -jar market.jar [sell|buy] [instrument] [quantity] [market] [price]");
        System.err.println("\tjava -jar market.jar markets");
        System.err.println("\tjava -jar market.jar market [market]");
    }

    private static void error(String message) {
        System.err.println(message);
        exit();
    }

    private static void exit() {
        System.exit(FAIL_STATUS);
    }
}
