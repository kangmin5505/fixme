package kr._42.seoul.field;

public class FIXPrice extends Field<Integer> {
    public FIXPrice(Integer value) {
        super(Tag.PRICE, value);
    }
}
