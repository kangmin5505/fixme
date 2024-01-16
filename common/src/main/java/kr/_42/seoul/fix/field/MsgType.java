package kr._42.seoul.fix.field;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MsgType extends StringField {

    public static final int TAG = 3;
    public static final Set<String> TYPES = new HashSet<>(List.of(new String[]{"SELL", "BUY", "EXECUTED", "REJECTED"}));
    public MsgType(String field) {
        super(TAG, field);
    }

    public static boolean contains(String type) {
        return TYPES.contains(type);
    }
}
