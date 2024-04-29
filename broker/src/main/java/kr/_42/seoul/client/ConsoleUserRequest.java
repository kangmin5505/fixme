package kr._42.seoul.client;

import java.util.Scanner;

public class ConsoleUserRequest implements UserRequest {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public Request getUserRequest() { 
        this.printMessage();

        ConsoleParser consoleParser = ConsoleParser.parse(scanner.nextLine());

        return Request.builder()
            .command(consoleParser.getCommand())
            .msgType(consoleParser.getMsgType())
            .instrument(consoleParser.getInstrument())
            .quantity(consoleParser.getQuantity())
            .price(consoleParser.getPrice())
            .market(consoleParser.getMarket())
            .build();
    }

    private void printMessage() {
        System.out.println("Usage");
        System.out.println("\t- [post] [sell|buy] [instrument] [quantity] [market] [price]");
        System.out.println("\t- [get] [result]");
        System.out.println("\t- [exit]");
        System.out.print("> ");
    }

}
