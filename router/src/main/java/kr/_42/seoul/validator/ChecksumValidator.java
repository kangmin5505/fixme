package kr._42.seoul.validator;

import java.nio.ByteBuffer;

public class ChecksumValidator extends BaseValidator {

    @Override
    public boolean validateImpl(ByteBuffer byteBuffer) {
        String str = new String(byteBuffer.array()).trim();
        String originChecksum = this.getChecksum(str);
        int checksum = this.calcChecksum(str);

        return String.format("%03d", checksum).equals(originChecksum);
    }

    private String getChecksum(String str) {
        String[] fields = str.split("\001");
        return fields[fields.length - 1].split("=")[1];
    }

    private int calcChecksum(String str) {
        int sum = 0;
        int lastIndexOfSOH = str.lastIndexOf("\001");

        for (int i = 0; i <= lastIndexOfSOH; i++) {
            sum += str.charAt(i);
        }
        return sum % 256;
    }

    
}
