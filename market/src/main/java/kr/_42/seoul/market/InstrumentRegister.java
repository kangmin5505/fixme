package kr._42.seoul.market;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public abstract class InstrumentRegister {
    private static String dirPath = "instruments/";
    private static Set<String> instruments = new HashSet<>();

    public static boolean register(String[] args) {
        if (args.length == 0) {
            return false;
        }

        for (String filename : args) {
            String filePath = dirPath + filename;
            try (InputStream in = InstrumentRegister.class.getClassLoader().getResourceAsStream(filePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    reader.lines().forEach(instruments::add);
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    public static Set<String> getInstruments() {
        return instruments;
    }
}
