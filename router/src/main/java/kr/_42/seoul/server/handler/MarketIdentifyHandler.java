package kr._42.seoul.server.handler;

import kr._42.seoul.fix.FIXMessage;

import java.nio.channels.SocketChannel;

public class MarketIdentifyHandler extends BaseHandler {
    @Override
    protected boolean handleImpl(SocketChannel client, FIXMessage fixMessage) {
        // Market Identify client
        return false;
    }
}
