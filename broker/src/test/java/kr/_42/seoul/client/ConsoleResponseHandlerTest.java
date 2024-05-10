package kr._42.seoul.client;

import org.junit.jupiter.api.Test;
import kr._42.seoul.enums.MsgType;

public class ConsoleResponseHandlerTest {

    @Test
    void test() {
        System.out.println(MsgType.EXECUTED.toString().equals("EXECUTED"));
    }
}
