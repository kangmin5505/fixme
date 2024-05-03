package kr._42.seoul.field;

public class FIXMsgType extends Field<String> {
    public static final int TAG = 2;

    public FIXMsgType(String value) {
        super(TAG, value);
    }
    
    public enum MsgType {
        BUY("BUY"),
        SELL("SELL"),
        EXECUTED("EXECUTED"),
        REJECTED("REJECTED");
        
        private String value;
        
        MsgType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
}
