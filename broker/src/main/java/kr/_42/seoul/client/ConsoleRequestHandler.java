package kr._42.seoul.client;

import java.util.Scanner;
import kr._42.seoul.common.Request;

public class ConsoleRequestHandler implements RequestHandler {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public Request getRequest() {
        this.printMessage();

        ConsoleParser consoleParser = ConsoleParser.parse(scanner.nextLine());

        return Request.builder().command(consoleParser.getCommand())
                .commandType(consoleParser.getCommandType()).market(consoleParser.getMarket())
                .instrument(consoleParser.getInstrument()).quantity(consoleParser.getQuantity())
                .price(consoleParser.getPrice()).build();
    }

    private void printMessage() {
        System.out.println("Usage");
        System.out.println("\t- [order] [sell|buy] [market] [instrument] [quantity] [price]");
        System.out.println("\t- [query] [order_details]");
        System.out.println("\t- [exit]");
        System.out.print("> ");
    }

}
