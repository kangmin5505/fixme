package kr._42.seoul.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.BrokerMediator;
import kr._42.seoul.ClientSocket;
import kr._42.seoul.FIXMessage;
import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.enums.MsgType;

public class BrokerServer extends ClientSocket {
    private final Logger logger = LoggerFactory.getLogger(BrokerServer.class);
    private BrokerMediator brokerMediator;

    public void order(Request request) {
        if (request.getMsgType() != MsgType.BUY && request.getMsgType() != MsgType.SELL) {
            return;
        }

        try {
            SelectionKey selectionKey =
                    this.socketChannel.register(this.selector, SelectionKey.OP_WRITE);
            byte[] bytes = requestToFIXMessage(request).toByteBuffer().array();
            selectionKey.attach(bytes);
        } catch (ClosedChannelException e) {
            logger.error("Failed to register channel with selector", e);
        }

        this.selector.wakeup();
    }

    public void registerBrokerMediator(BrokerMediator brokerMediator) {
        this.brokerMediator = brokerMediator;
    }

    private FIXMessage requestToFIXMessage(Request request) {
        return FIXMessage.builder().id(id).msgType(request.getMsgType())
                .market(request.getMarket()).instrument(request.getInstrument())
                .price(request.getPrice()).quantity(request.getQuantity()).build();
    }

    protected void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        this.buffer.clear();

        int readByte = channel.read(this.buffer);
        if (readByte == -1) {
            this.close();
            logger.error("Connection closed by router");
            System.exit(1);
        }

        this.brokerMediator.sendToBrokerClient(new Response(this.buffer));
    }

    protected void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        Object attachment = key.attachment();
        byte[] bytes = (byte[]) attachment;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        client.write(byteBuffer);
        key.interestOps(SelectionKey.OP_READ);
    }
}
