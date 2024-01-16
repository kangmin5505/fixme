package kr._42.seoul.server.handler;

import kr._42.seoul.fix.FIXMessage;

import java.nio.channels.SocketChannel;

public class ValidateHandler extends BaseHandler {
    @Override
    protected boolean handleImpl(SocketChannel client, FIXMessage fixMessage) {
        return fixMessage.validateChecksum();
    }
}
