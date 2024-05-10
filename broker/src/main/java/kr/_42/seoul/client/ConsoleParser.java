package kr._42.seoul.client;

import kr._42.seoul.enums.BrokerCommand;
import kr._42.seoul.enums.MsgType;

public class ConsoleParser {
    private BrokerCommand command;
    private MsgType msgType;
    private String market;
    private String instrument;
    private int price;
    private int quantity;

    private ConsoleParser() {}

    public static ConsoleParser parse(String nextLine) {
        String[] split = nextLine.trim().split("\\s+");

        if (split.length < 1) {
            throw new IllegalArgumentException("Arguments are empty");
        }

        String commandString = split[0].toUpperCase();
        BrokerCommand command;
        try {
            command = BrokerCommand.valueOf(commandString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid command");
        }

        switch (command) {
            case ORDER:
                return parseOrder(split);
            case MESSAGE:
                return parseMessage(split);
            case EXIT:
                return parseExit(split);
            default:
                throw new IllegalArgumentException("Command not found");
        }
    }

    private static ConsoleParser parseMessage(String[] split) {
        return ConsoleParser.builder().command(BrokerCommand.MESSAGE).build();
    }

    private static ConsoleParser parseExit(String[] split) {
        return ConsoleParser.builder().command(BrokerCommand.EXIT).build();
    }

    private static ConsoleParser parseOrder(String[] split) {
        if (split.length < 6) {
            throw new IllegalArgumentException("Arguments must be more than 6");
        }

        int price = Integer.parseInt(split[4]);
        int quantity = Integer.parseInt(split[5]);
        if (price <= 0 || quantity <= 0) {
            throw new IllegalArgumentException("Quantity and price must be more than 0");
        }

        return ConsoleParser.builder().command(BrokerCommand.ORDER)
                .msgType(MsgType.valueOf(split[1].toUpperCase())).market(split[2])
                .instrument(split[3].toUpperCase()).price(price).quantity(quantity).build();
    }

    public BrokerCommand getCommand() {
        return this.command;
    }

    public MsgType getMsgType() {
        return this.msgType;
    }

    public String getMarket() {
        return this.market;
    }

    public String getInstrument() {
        return this.instrument;
    }

    public int getPrice() {
        return this.price;
    }

    public int getQuantity() {
        return this.quantity;
    }

    private static ConsoleParserBuilder builder() {
        return new ConsoleParserBuilder();
    }

    private static class ConsoleParserBuilder {
        private ConsoleParser consoleParser = new ConsoleParser();

        private ConsoleParserBuilder command(BrokerCommand command) {
            consoleParser.command = command;
            return this;
        }

        private ConsoleParserBuilder msgType(MsgType msgType) {
            consoleParser.msgType = msgType;
            return this;
        }

        private ConsoleParserBuilder market(String market) {
            consoleParser.market = market;
            return this;
        }

        private ConsoleParserBuilder instrument(String instrument) {
            consoleParser.instrument = instrument;
            return this;
        }

        private ConsoleParserBuilder price(int price) {
            consoleParser.price = price;
            return this;
        }

        private ConsoleParserBuilder quantity(int quantity) {
            consoleParser.quantity = quantity;
            return this;
        }

        private ConsoleParser build() {
            return consoleParser;
        }
    }

    @Override
    public String toString() {
        return "ConsoleParser [command=" + command + ", msgType=" + msgType + ", market=" + market
                + ", instrument=" + instrument + ", price=" + price + ", quantity=" + quantity
                + "]";
    }
}
