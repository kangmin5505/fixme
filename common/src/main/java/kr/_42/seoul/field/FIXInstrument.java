package kr._42.seoul.field;

public class FIXInstrument extends Field<String> {
    public FIXInstrument(String value) {
        super(Tag.INSTRUMENT, value);
    }
}