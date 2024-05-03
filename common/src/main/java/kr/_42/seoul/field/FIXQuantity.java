package kr._42.seoul.field;

public class FIXQuantity extends Field<Integer> {
    public static final int TAG = 4;

    public FIXQuantity(Integer value) {
        super(TAG, value);
    }
}
