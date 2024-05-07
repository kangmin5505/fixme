package kr._42.seoul.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.ClientSocket;
import kr._42.seoul.FIXMessage;
import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.enums.BrokerCommandType;
import kr._42.seoul.enums.ResponseStatusCode;
import kr._42.seoul.server.repository.Repository;

public class BrokerServer extends ClientSocket {
    public final ResponseStatusCode SUCCESS = ResponseStatusCode.SUCCESS;
    public final ResponseStatusCode FAILURE = ResponseStatusCode.FAILURE;

    private final Logger logger = LoggerFactory.getLogger(BrokerServer.class);
    private final Repository repository;

    public BrokerServer(Repository repository) {
        this.repository = repository;
    }

    public void order(Request request) {
        if (request.getCommandType() != BrokerCommandType.BUY
                && request.getCommandType() != BrokerCommandType.SELL) {
            return;
        }
        
        try {
            SelectionKey selectionKey = this.socketChannel.register(this.selector, SelectionKey.OP_WRITE);
            byte[] bytes = requestToFIXMessage(request).toByteBuffer().array();
            selectionKey.attach(bytes);
        } catch (ClosedChannelException e) {
            logger.error("Failed to register channel with selector", e);
        }

        this.selector.wakeup();

        // this.repository.addOrderDetail(OrderDetail.builder().orderType(request.getCommandType())
        // .orderStatus(OrderStatus.PENDING).instrument(request.getInstrument())
        // .quantity(request.getQuantity()).price(request.getPrice())
        // .market(request.getMarket()).build());
    }

    private FIXMessage requestToFIXMessage(Request request) {
        return FIXMessage.builder()
        .id(id)
        .msgType(request.getCommandType().toString())
        .instrument(request.getInstrument())
        .quantity(request.getQuantity())
        .price(request.getPrice())
        .market(request.getMarket())
        .build();
    }

    public Response query(Request request) {
        if (request.getCommandType() != BrokerCommandType.ORDER_DETAILS) {
            return new Response(ResponseStatusCode.FAILURE, null, "Invalid Request");
        }

        List<OrderDetail> orderDetails = this.repository.getOrderDetails();
        return new Response(ResponseStatusCode.SUCCESS, orderDetails, "Order Details");
    }

    protected void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        this.buffer.clear();

        client.read(this.buffer);
        String str = this.bufferToString(this.buffer);

        logger.info("Reading from router: {}", str);
    }

    protected void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        Object attachment = key.attachment();
        byte[] bytes = (byte[]) attachment;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        client.write(byteBuffer);
        key.interestOps(SelectionKey.OP_READ);

        logger.info("Sending to router: {}", new String(bytes));
    }
}
