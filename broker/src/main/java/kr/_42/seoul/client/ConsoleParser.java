package kr._42.seoul.client;

import kr._42.seoul.enums.BrokerCommand;
import kr._42.seoul.enums.BrokerCommandType;

public class ConsoleParser {
    private BrokerCommand command;
    private BrokerCommandType commandType;
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
            case ORDER:
                return parsePost(split);
            case QUERY:
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

        return ConsoleParser.builder().command(BrokerCommand.QUERY)
                .commandType(BrokerCommandType.valueOf(split[1].toUpperCase())).build();
    }

    private static ConsoleParser parseExit(String[] split) {
        return ConsoleParser.builder().command(BrokerCommand.EXIT).build();
    }

    private static ConsoleParser parsePost(String[] split) {
        if (split.length < 6) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        return ConsoleParser.builder().command(BrokerCommand.ORDER)
                .commandType(BrokerCommandType.valueOf(split[1].toUpperCase())).instrument(split[2])
                .quantity(Integer.parseInt(split[3])).market(split[4])
                .price(Integer.parseInt(split[5])).build();
    }

    public BrokerCommand getCommand() {
        return this.command;
    }

    public BrokerCommandType getCommandType() {
        return this.commandType;
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
        private ConsoleParser consoleParser = new ConsoleParser();

        private ConsoleParserBuilder command(BrokerCommand command) {
            consoleParser.command = command;
            return this;
        }

        private ConsoleParserBuilder commandType(BrokerCommandType commandType) {
            consoleParser.commandType = commandType;
            return this;
        }

        private ConsoleParserBuilder instrument(String instrument) {
            consoleParser.instrument = instrument;
            return this;
        }

        private ConsoleParserBuilder quantity(int quantity) {
            consoleParser.quantity = quantity;
            return this;
        }

        private ConsoleParserBuilder price(int price) {
            consoleParser.price = price;
            return this;
        }

        private ConsoleParserBuilder market(String market) {
            consoleParser.market = market;
            return this;
        }

        private ConsoleParser build() {
            return consoleParser;
        }
    }

    @Override
    public String toString() {
        return "ConsoleParser [command=" + command + ", commandType=" + commandType
                + ", instrument=" + instrument + ", quantity=" + quantity + ", price=" + price
                + ", market=" + market + "]";
    }
}
