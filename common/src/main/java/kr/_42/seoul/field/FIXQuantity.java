package kr._42.seoul.field;

public class FIXQuantity extends Field<Integer> {
    public FIXQuantity(Integer value) {
        super(Tag.QUANTITY, value);
    }
}
