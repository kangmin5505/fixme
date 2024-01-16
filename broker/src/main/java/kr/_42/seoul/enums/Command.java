package kr._42.seoul.enums;

public enum Command {
    BUY("BUY"),
    SELL("SELL"),
    EXIT(null);

    private final String s;

    Command(String s) {
        this.s = s;
    }
}
