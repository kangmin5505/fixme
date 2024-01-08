package kr._42.seoul.idgenerator;

public class MarketIDGenerator {
    private static int id = 0;
    private static final int MAX_ID = 999999;

    public static String generate() {
        String formattedID = String.format("%06d", id++);

        if (id > MAX_ID) {
            id = 0;
        }
        return formattedID;
    }
}
