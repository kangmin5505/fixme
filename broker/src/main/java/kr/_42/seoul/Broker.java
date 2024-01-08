package kr._42.seoul;

import kr._42.seoul.client.BrokerChannelClient;
import kr._42.seoul.enums.Command;
import kr._42.seoul.parser.ParameterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Broker {
    private static final Logger logger = LoggerFactory.getLogger(Broker.class);
    private static int FAIL_STATUS = 2;
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        logger.debug("Start broker");

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        try (BrokerChannelClient brokerClient = SocketChannelClient.open(HOSTNAME, PORT, BrokerChannelClient.class)) {
            executorService.submit(brokerClient);

            inputLoop(brokerClient);

        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void inputLoop(BrokerChannelClient brokerClient) {
        logger.debug("Running input loop");

        Scanner scanner = new Scanner(System.in);
        ParameterParser paramParser = new ParameterParser();

        while (true) {
            usage();
            inputSymbol();

            String line = scanner.nextLine();
            String[] stringArray = line.split("\\s+");

            paramParser.parse(stringArray);

            if (!paramParser.isValid()) {
                System.out.println("Invalid input");
                continue;
            }

            Command command = paramParser.getCommand();
            if (command == Command.EXIT) {
                System.out.println("Bye");
                brokerClient.close();
                break;
            }

            brokerClient.request("message");
        }
    }

    private static void usage() {
        System.out.println();

        System.out.println("Usage");
        System.out.println("\t[sell|buy] [instrument] [quantity] [market] [price]");
        System.out.println("\tmarkets");
        System.out.println("\tmarket [market]");
        System.out.println("\texit");

        System.out.println();
    }

    private static void inputSymbol() {
        System.out.print("> ");
    }
}
