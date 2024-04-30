package kr._42.seoul.server.repository;

import java.util.List;
import kr._42.seoul.server.OrderDetail;

public interface Repository {

    static Repository getInstance() {
        throw new UnsupportedOperationException("This method must be overridden in the implementing class");
    }

    List<OrderDetail> getOrderDetails();

    void addOrderDetail(OrderDetail orderDetail);

}
