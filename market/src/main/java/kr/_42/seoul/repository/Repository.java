package kr._42.seoul.repository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface Repository {
    static Repository getInstance() {
        throw new UnsupportedOperationException(
                "This method must be overridden in the implementing class");
    }

    void addOrder(Order order) throws SQLException;

    List<Order> findOrdersByInstrumentAndPrice(String instrument, int price) throws SQLException;

    void updateOrder(Order order) throws SQLException;

    void deleteOrder(Order order) throws SQLException;

    void init() throws IOException;

}
