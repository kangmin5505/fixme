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
                .msgType(consoleParser.getMsgType()).market(consoleParser.getMarket())
                .instrument(consoleParser.getInstrument()).price(consoleParser.getPrice())
                .quantity(consoleParser.getQuantity()).build();
    }

    private void printMessage() {
        System.out.println("Usage");
        System.out.println("\t- [order] [sell|buy] [market] [instrument] [price] [quantity]");
        System.out.println("\t- [message]");
        System.out.println("\t- [exit]");
        System.out.print("> ");
    }

}
