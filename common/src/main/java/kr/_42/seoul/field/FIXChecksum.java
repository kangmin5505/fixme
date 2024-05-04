package kr._42.seoul.field;

public class FIXChecksum extends Field<String> {
    public FIXChecksum(String value) {
        super(Tag.CHECKSUM, value);
    }
}
