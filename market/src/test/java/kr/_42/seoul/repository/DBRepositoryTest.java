package kr._42.seoul.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBRepositoryTest {
    private static final String envFile = "env.properties";
    private static final Repository repository = DBRepository.getInstance();
    private static DataSource ds;

    @BeforeAll
    static void beforeAll() {
        try (InputStream is = DBRepository.class.getClassLoader().getResourceAsStream(envFile)) {
            Properties props = new Properties();
            props.load(is);

            HikariConfig config = new HikariConfig(props);
            ds = new HikariDataSource(config);

            try (Connection conn = ds.getConnection();
                    Statement stmt = conn.createStatement();
                    InputStream is2 = DBRepositoryTest.class.getClassLoader().getResourceAsStream("sql/create-tables.sql");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is2))) {
                String sql = "";
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sql += line;
                }

                stmt.executeUpdate(sql);
            } catch (Exception e) {
                e.printStackTrace();
                assert false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

        try {
            repository.init();
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @AfterAll
    static void afterAll() {
        try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement();) {
            stmt.executeUpdate("DROP TABLE market");
        } catch (SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void addOrderTest() {
        String instrument = "instrument1";
        int price = 100;
        int repeat = 3;

        try {
            for (int i = 0; i < repeat; i++) {
                repository.addOrder(Order.builder().brokerID("000000").instrument(instrument)
                        .price(price).quantity(10 + 10 * i).build());
            }
            List<Order> orders = repository.findOrdersByInstrumentAndPrice(instrument, price);

            assertEquals(orders.size(), 3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateOrderTest() {

        try {
            List<Order> orders = repository.findOrdersByInstrumentAndPrice("SAMSUNG", 10);
            orders.forEach(order -> {
                try {
                    order.minusQuantity(5);
                    repository.updateOrder(order);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            List<Order> orders2 = repository.findOrdersByInstrumentAndPrice("SAMSUNG", 10);
            orders2.forEach(order -> {
                assertEquals(order.getQuantity(), 5);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteOrderTest() {
        try {
            List<Order> orders = repository.findOrdersByInstrumentAndPrice("SAMSUNG", 10);
            orders.forEach(order -> {
                try {
                    repository.deleteOrder(order);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            List<Order> orders2 = repository.findOrdersByInstrumentAndPrice("SAMSUNG", 10);
            assertEquals(orders2.size(), 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
