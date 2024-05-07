package kr._42.seoul.common;

public class IDGenerator {
    private int id = 0;
    private static final int MAX_ID = 999999;

    public synchronized String generateID() {
        String formattedID = String.format("%06d", id++);

        if (id > MAX_ID) {
            id = 0;
        }
        return formattedID;
    }
}
