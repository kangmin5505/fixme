package kr._42.seoul.parser;

import kr._42.seoul.enums.Command;

public class ParameterParser {
    private Command command;
    private String instrument;
    private Integer quantity;
    private String market;
    private Integer price;
    private boolean valid;

    public ParameterParser() {}

    public void parse(String[] args) {
        this.valid = false;

        if (args == null || args.length < 1) {
            return;
        }

        // arg list
        // [sell|buy] [instrument] [quantity] [market] [price]
        // markets
        // market [market]
        try {
            this.command = Command.valueOf(args[0].toUpperCase());
            switch (this.command) {
                case SELL:
                case BUY:
                    if (args.length == 5) {
                        this.instrument = args[1];
                        this.quantity = Integer.valueOf(args[2]);
                        this.market = args[3];
                        this.price = Integer.valueOf(args[4]);

                        this.valid = true;
                    }
                    break;
                case EXIT:
                case MARKETS:
                    this.valid = true;
                    break;
                case MARKET:
                    if (args.length == 2) {
                        this.market = args[1];
                        this.valid = true;
                    }
                    break;
            }
        } catch (IllegalArgumentException e) {
        }
    }


    public boolean isValid() {
        return this.valid;
    }

    public Command getCommand() {
        return command;
    }

    public String getInstrument() {
        return instrument;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getMarket() {
        return market;
    }

    public Integer getPrice() {
        return price;
    }
}
