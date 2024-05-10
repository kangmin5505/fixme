package kr._42.seoul.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryRepository implements Repository {
    private static final Repository instance = new MemoryRepository();
    private static final Map<Long, Order> orders = new HashMap<>();
    private static Long orderID = 0L;

    private MemoryRepository() {}

    public static Repository getInstance() {
        return instance;
    }

    @Override
    public void addOrder(Order order) {
        order.setOrderID(orderID);
        orders.put(orderID++, order);

        this.printAllOrders();
    }

    public void printAllOrders() {
        System.out.println("All orders:");
        orders.forEach((k, v) -> System.out.println(v));
    }

    @Override
    public List<Order> findOrdersByInstrumentAndPrice(String instrument, int price) {
        return orders.values().stream().filter(
                order -> order.getInstrument().equals(instrument) && order.getPrice() == price)
                .toList();
    }

    @Override
    public void updateOrder(Order order) {
        orders.put(order.getOrderID(), order);
    }

    @Override
    public void deleteOrder(Order order) {
        orders.remove(order.getOrderID());
    }

    @Override
    public void init() {
    }
}
