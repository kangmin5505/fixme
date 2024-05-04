package kr._42.seoul.field;

public class FIXBrokerID extends Field<String> {
    public FIXBrokerID(String value) {
        super(Tag.BROKER_ID, value);
    }
}
