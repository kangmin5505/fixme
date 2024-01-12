package kr._42.seoul;

import kr._42.seoul.client.AsynchronousBrokerClient;
import kr._42.seoul.enums.Command;
import kr._42.seoul.parser.ParameterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Broker {
    private static final Logger logger = LoggerFactory.getLogger(Broker.class);
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static int FAIL_STATUS = 2;
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        try (AsynchronousClient broker = new AsynchronousBrokerClient(HOSTNAME, PORT)) {
            broker.run();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(FAIL_STATUS);
        }


//        try (ClientMultiplexer brokerClient = new BrokerClient(HOSTNAME, PORT)) {
//            executorService.submit(brokerClient::run);
//
//            inputLoop(brokerClient);
//
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//            System.exit(FAIL_STATUS);
//        }
//
//        executorService.shutdown();
    }


    public static void inputLoop(ClientMultiplexer brokerClient) {
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
        }
    }

    private static void usage() {
        System.out.println();

        System.out.println("Usage");
        System.out.println("\t[sell|buy] [instrument] [quantity] [market] [price]");
        System.out.println("\texit");

        System.out.println();
    }

    private static void inputSymbol() {
        System.out.print("> ");
    }
}
