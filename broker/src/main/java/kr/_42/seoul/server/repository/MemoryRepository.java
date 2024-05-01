package kr._42.seoul.server.repository;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import kr._42.seoul.server.OrderDetail;

public class MemoryRepository implements Repository {
    private static final MemoryRepository instance = new MemoryRepository();
    private static final Queue<OrderDetail> orderDetails = new ConcurrentLinkedQueue<>();

    private MemoryRepository() {}

    public static Repository getInstance() {
        return instance;
    }

    @Override
    public List<OrderDetail> getOrderDetails() {
        return List.copyOf(orderDetails);
    }

    @Override
    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
    }

}
