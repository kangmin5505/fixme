package kr._42.seoul;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import kr._42.seoul.server.OrderDetail;

public class MemoryRepository implements Repository {
    private static final MemoryRepository instance = new MemoryRepository();
    private static final Queue<OrderDetail> orderDetails = new ConcurrentLinkedQueue<>();

    private MemoryRepository() {}

    @Override
    public List<OrderDetail> getOrderDetails() {
        return List.copyOf(orderDetails);
    }

    public static Repository getInstance() {
        return instance;
    }

    @Override
    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
    }

}
