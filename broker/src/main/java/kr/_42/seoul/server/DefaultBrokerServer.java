package kr._42.seoul.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.enums.BrokerCommandType;
import kr._42.seoul.enums.OrderStatus;
import kr._42.seoul.enums.ResponseStatusCode;
import kr._42.seoul.server.repository.Repository;

public class DefaultBrokerServer implements BrokerServer {
    private final Logger logger = LoggerFactory.getLogger(DefaultBrokerServer.class);
    private final String hostname;
    private final int port;
    private final Repository repository;
    private final SocketChannel socketChannel;
    private final Selector selector;

    // TODO: close socket
    public DefaultBrokerServer(String hostname, int port, Repository repository)
            throws IOException {
        this.hostname = hostname;
        this.port = port;
        this.repository = repository;
        this.socketChannel = SocketChannel.open();
        this.selector = Selector.open();
    }

    @Override
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

    @Override
    public Response query(Request request) {
        if (request.getCommandType() != BrokerCommandType.ORDER_DETAILS) {
            return new Response(ResponseStatusCode.FAILURE, null, "Invalid Request");
        }

        List<OrderDetail> orderDetails = this.repository.getOrderDetails();
        return new Response(ResponseStatusCode.SUCCESS, orderDetails, "Order Details");
    }

    @Override
    public void run() throws IOException {
        this.logger.debug("BrokerServer is running");

        this.setup();

        while (true) {
            try {
                this.selector.select();

                for (SelectionKey key : this.selector.selectedKeys()) {
                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isWritable()) {
                        this.write(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    }
                }
    
                this.selector.selectedKeys().clear();
            } catch (IOException e) {
                this.logger.error("Error occurred while selecting keys", e);
                e.printStackTrace();
            }            
        }
    }

    private void setup() throws IOException {
        this.socketChannel.connect(new InetSocketAddress(this.hostname, this.port));
        this.socketChannel.configureBlocking(false);
        this.socketChannel.register(this.selector, SelectionKey.OP_READ);
    }


    private void read(SelectionKey key) {
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

    private void write(SelectionKey key) throws IOException {
        logger.debug("Writing Ping to client");

        SocketChannel client = (SocketChannel) key.channel();

        client.write(ByteBuffer.wrap("Ping".getBytes()));
        key.interestOps(SelectionKey.OP_READ);
    }

}
