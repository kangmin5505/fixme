package kr._42.seoul.fix;

import kr._42.seoul.fix.field.Field;

import java.util.Iterator;
import java.util.TreeMap;

public abstract class FieldMap implements Iterable<Field<?>> {

    private final TreeMap<Integer, Field<?>> fields = new TreeMap<>();

    protected void setField(Field<?> field) {
        fields.put(field.getTag(), field);
    }

    @Override
    public Iterator<Field<?>> iterator() {
        return fields.values().iterator();
    }

    public void toString(StringBuilder sb) {
        for (Field<?> field : fields.values()) {
            field.toString(sb);
        }
    }

    public int getStringFieldsLength() {
        int length = 0;
        for (Field<?> field : fields.values()) {
            length += field.getStringFieldLength();
        }
        return length;
    }
}
