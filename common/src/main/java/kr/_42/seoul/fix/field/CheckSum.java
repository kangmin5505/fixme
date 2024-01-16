package kr._42.seoul.fix.field;

public class CheckSum extends StringField {
    public static final int TAG = 10;
    public CheckSum(String field) {
        super(TAG, field);
    }

    public static String createField(int checksum) {
        return String.format("%03d", checksum);
    }
}
