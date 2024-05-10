package kr._42.seoul.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBRepository implements Repository {
    private static final Logger logger = LoggerFactory.getLogger(DBRepository.class);
    private static final Repository instance = new DBRepository();
    private static final String envFile = "env.properties";
    private static DataSource ds = null;

    private DBRepository() {}

    public static Repository getInstance() {
        return instance;
    }

    @Override
    public void init() throws IOException {
        this.initDataSource();
    }

    private void initDataSource() throws IOException {
        try (InputStream is = DBRepository.class.getClassLoader().getResourceAsStream(envFile)) {
            Properties props = new Properties();
            props.load(is);

            HikariConfig config = new HikariConfig(props);
            ds = new HikariDataSource(config);
        }
    }

    @Override
    public void addOrder(Order order) throws SQLException {
        String sql = "INSERT INTO market (clientId, instrument, price, quantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.getBrokerID());
            pstmt.setString(2, order.getInstrument());
            pstmt.setInt(3, order.getPrice());
            pstmt.setInt(4, order.getQuantity());
            pstmt.executeUpdate();
        }

        logger.info("Order added: {}", order);
    }

    @Override
    public List<Order> findOrdersByInstrumentAndPrice(String instrument, int price) throws SQLException {
        String sql = "SELECT * FROM market WHERE instrument = ? AND price = ?";

        try (Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, instrument);
            pstmt.setInt(2, price);
            ResultSet rs = pstmt.executeQuery();
            List<Order> orders = new ArrayList<>();
            
            while (rs.next()) {
                Long id = rs.getLong("id");
                String clientId = rs.getString("clientId");
                int quantity = rs.getInt("quantity");

                orders.add(Order.builder()
                    .id(id)
                    .brokerID(clientId)
                    .instrument(instrument)
                    .price(price)
                    .quantity(quantity)
                    .build());
            }

            return orders;
        }
    }

    @Override
    public void updateOrder(Order order) throws SQLException {
        String sql = "UPDATE market SET quantity = ? WHERE id = ?";

        try (Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getQuantity());
            pstmt.setLong(2, order.getId());
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public void deleteOrder(Order order) throws SQLException {
        String sql = "DELETE FROM market WHERE id = ?";

        try (Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, order.getId());
            pstmt.executeUpdate();
        }
    }

}
