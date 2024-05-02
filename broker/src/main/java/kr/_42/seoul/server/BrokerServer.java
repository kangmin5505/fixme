package kr._42.seoul.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.ClientSocket;
import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.enums.BrokerCommandType;
import kr._42.seoul.enums.OrderStatus;
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

        logger.debug("register write event to selector");
        try {
            this.socketChannel.register(this.selector, SelectionKey.OP_WRITE);
            this.selector.wakeup();

            this.repository.addOrderDetail(OrderDetail.builder().orderType(request.getCommandType())
            .orderStatus(OrderStatus.PENDING).instrument(request.getInstrument())
            .quantity(request.getQuantity()).price(request.getPrice())
            .market(request.getMarket()).build());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Response query(Request request) {
        if (request.getCommandType() != BrokerCommandType.ORDER_DETAILS) {
            return new Response(ResponseStatusCode.FAILURE, null, "Invalid Request");
        }

        List<OrderDetail> orderDetails = this.repository.getOrderDetails();
        return new Response(ResponseStatusCode.SUCCESS, orderDetails, "Order Details");
    }

    protected void read(SelectionKey key) throws IOException {
        logger.debug("Reading from client");

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        SocketChannel client = (SocketChannel) key.channel();

        try {
            client.read(buffer);
            buffer.flip();
            
            logger.debug(new String(buffer.array()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void write(SelectionKey key) throws IOException {
        logger.debug("Writing Ping to client");

        SocketChannel client = (SocketChannel) key.channel();

        client.write(ByteBuffer.wrap("Ping".getBytes()));
        key.interestOps(SelectionKey.OP_READ);
    }

}
