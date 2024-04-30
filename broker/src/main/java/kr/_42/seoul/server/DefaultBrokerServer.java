package kr._42.seoul.server;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.Repository;
import kr._42.seoul.client.Request;
import kr._42.seoul.client.Response;
import kr._42.seoul.enums.BrokerCommandType;

public class DefaultBrokerServer implements BrokerServer {
    private final Logger logger = LoggerFactory.getLogger(DefaultBrokerServer.class);
    private final String hostname;
    private final int port;
    private final Repository repository;

    public DefaultBrokerServer(String hostname, int port, Repository repository) {
        this.hostname = hostname;
        this.port = port;
        this.repository = repository;
    }

    @Override
    public void order(Request request) {
        if (request.getCommandType() != BrokerCommandType.BUY
                && request.getCommandType() != BrokerCommandType.SELL) {
            return;
        }
        logger.debug("Order Request: {}", request);
        // Send Message to Router

        repository.addOrderDetail(OrderDetail.builder().orderType(request.getCommandType())
                .orderStatus(OrderStatus.Pending).instrument(request.getInstrument())
                .quantity(request.getQuantity()).price(request.getPrice())
                .market(request.getMarket()).build());
    }

    @Override
    public Response query(Request request) {
        if (request.getCommandType() != BrokerCommandType.ORDER_DETAILS) {
            return new Response(ResponseStatusCode.FAILURE, null, "Invalid Request");
        }

        List<OrderDetail> orderDetails = repository.getOrderDetails();
        return new Response(ResponseStatusCode.SUCCESS, orderDetails, "Order Details");
    }

}
