package kr._42.seoul;

import java.util.List;
import kr._42.seoul.client.Response;
import kr._42.seoul.server.OrderDetail;

public class ConsoleResponseHandler implements ResponseHandler {

    @Override
    public void handle(Response response) {
        List<OrderDetail> orderDetails = (List<OrderDetail>) response.getData();
        orderDetails.forEach(System.out::println);
    }

}
