package kr._42.seoul.repository;

import java.util.List;

public interface Repository {
    static Repository getInstance() {
        throw new UnsupportedOperationException(
                "This method must be overridden in the implementing class");
    }

    void addOrder(Order order);

    List<Order> findOrdersByInstrumentAndPrice(String instrument, int price);

    void updateOrder(Order order);

    void deleteOrder(Order order);

}
