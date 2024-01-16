package kr._42.seoul.server.handler;

import kr._42.seoul.fix.FIXMessage;

import java.nio.channels.SocketChannel;

public interface Handler {
    void setNext(Handler handler);
    boolean handle(SocketChannel client, FIXMessage fixMessage);
}
