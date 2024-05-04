package kr._42.seoul.field;

public enum Tag {
    ID("1"),
    MSG_TYPE("2"),
    INSTRUMENT("3"),
    QUANTITY("4"),
    PRICE("5"),
    MARKET("6"),
    BROKER_ID("7"),
    CHECKSUM("8");

    private final String value;

    Tag(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static Tag toTag(String value) {
        for (Tag tag : Tag.values()) {
            if (tag.toString().equals(value)) {
                return tag;
            }
        }
        return null;
    }
}
