package kr._42.seoul.client;

import kr._42.seoul.enums.BrokerCommand;
import kr._42.seoul.enums.BrokerMessageType;

public class ConsoleParser {
    private BrokerCommand command;
    private BrokerMessageType msgType;
    private String instrument;
    private int quantity;
    private int price;
    private String market;

    private ConsoleParser() {}

    public static ConsoleParser parse(String nextLine) {
        String[] split = nextLine.split("\\s+");

        if (split.length < 1) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        BrokerCommand command = BrokerCommand.valueOf(split[0].toUpperCase());

        switch (command) {
            case POST:
                return parsePost(split);
            case GET:
                return parseGet(split);
            case EXIT:
                return parseExit(split);
            default:
                throw new IllegalArgumentException("Invalid arguments");
        }
    }

    private static ConsoleParser parseGet(String[] split) {
        if (split.length < 2) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        return ConsoleParser.builder()
            .command(BrokerCommand.GET)
            .msgType(BrokerMessageType.valueOf(split[1].toUpperCase()))
            .build();
    }

    private static ConsoleParser parseExit(String[] split) {
        return ConsoleParser.builder()
            .command(BrokerCommand.EXIT)
            .build();
    }

    private static ConsoleParser parsePost(String[] split) {
        if (split.length < 6) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        return ConsoleParser.builder()
            .command(BrokerCommand.POST)
            .msgType(BrokerMessageType.valueOf(split[1].toUpperCase()))
            .instrument(split[2])
            .quantity(Integer.parseInt(split[3]))
            .price(Integer.parseInt(split[4]))
            .market(split[5])
            .build();
    }

    public BrokerCommand getCommand() {
        return this.command;
    }

    public BrokerMessageType getMsgType() {
        return this.msgType;
    }

    public String getInstrument() {
        return this.instrument;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getPrice() {
        return this.price;
    }

    public String getMarket() {
        return this.market;
    }

    private static ConsoleParserBuilder builder() {
        return new ConsoleParserBuilder();
    }

    private static class ConsoleParserBuilder {
        private BrokerCommand command;
        private BrokerMessageType msgType;
        private String instrument;
        private int quantity;
        private int price;
        private String market;

        private ConsoleParserBuilder command(BrokerCommand command) {
            this.command = command;
            return this;
        }

        private ConsoleParserBuilder msgType(BrokerMessageType msgType) {
            this.msgType = msgType;
            return this;
        }

        private ConsoleParserBuilder instrument(String instrument) {
            this.instrument = instrument;
            return this;
        }

        private ConsoleParserBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        private ConsoleParserBuilder price(int price) {
            this.price = price;
            return this;
        }

        private ConsoleParserBuilder market(String market) {
            this.market = market;
            return this;
        }

        private ConsoleParser build() {
            ConsoleParser consoleParser = new ConsoleParser();
            consoleParser.command = this.command;
            consoleParser.msgType = this.msgType;
            consoleParser.instrument = this.instrument;
            consoleParser.quantity = this.quantity;
            consoleParser.price = this.price;
            consoleParser.market = this.market;
            return consoleParser;
        }
    }

    @Override
    public String toString() {
        return "ConsoleParser [command=" + command + ", msgType=" + msgType + ", instrument=" + instrument
                + ", quantity=" + quantity + ", price=" + price + ", market=" + market + "]";
    }    
}
