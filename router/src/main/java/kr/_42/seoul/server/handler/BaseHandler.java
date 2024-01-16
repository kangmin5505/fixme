package kr._42.seoul.server.handler;

import kr._42.seoul.fix.FIXMessage;

import java.nio.channels.SocketChannel;

public abstract class BaseHandler implements Handler {
    private Handler next;

    @Override
    public void setNext(Handler handler) {
        this.next = handler;
    }

    @Override
    public boolean handle(SocketChannel client, FIXMessage fixMessage) {
        if (!handleImpl(client, fixMessage)) {
            return false;
        }
        return this.next == null || this.next.handle(client, fixMessage);
    }

    protected abstract boolean handleImpl(SocketChannel client, FIXMessage fixMessage);
}
