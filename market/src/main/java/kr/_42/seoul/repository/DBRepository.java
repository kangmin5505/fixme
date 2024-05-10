package kr._42.seoul.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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
        String sql = "INSERT INTO market (clientId, instrument, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.getBrokerID());
            pstmt.setString(2, order.getInstrument());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setInt(4, order.getPrice());
            pstmt.executeUpdate();
        }

        logger.info("Order added: {}", order);
    }

    @Override
    public List<Order> findOrdersByInstrumentAndPrice(String instrument, int price) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findOrdersByInstrumentAndPrice'");
    }

    @Override
    public void updateOrder(Order order) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateOrder'");
    }
    
    @Override
    public void deleteOrder(Order order) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteOrder'");
    }

}
