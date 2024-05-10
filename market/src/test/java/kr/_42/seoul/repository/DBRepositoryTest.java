package kr._42.seoul.repository;

public class DBRepositoryTest {
    // private static final String envFile = "env.properties";
    // private static Connection conn = null;

    // @BeforeAll
    // static void beforeAll() {
    //         URL url = DBRepositoryTest.class.getClassLoader().getResource(envFile);
    //         String path = url.getPath();

    //         HikariConfig config = new HikariConfig(path);
    //         HikariDataSource ds = new HikariDataSource(config);

    //         try {
    //             conn = ds.getConnection();
    //         } catch (SQLException e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         }
    //         // conn = DriverManager.getConnection(dbUrl, username, password);

    //         try (Statement stmt = conn.createStatement();
    //             InputStream is1 = DBRepositoryTest.class.getClassLoader().getResourceAsStream("sql/create-tables.sql");
    //             InputStream is2 = DBRepositoryTest.class.getClassLoader().getResourceAsStream("sql/insert-data.sql");
    //             BufferedReader reader1 = new BufferedReader(new java.io.InputStreamReader(is1));
    //             BufferedReader reader2 = new BufferedReader(new java.io.InputStreamReader(is2))
    //             ) {
    //             String sql = "";
    //             String line = null;
    //             while ((line = reader1.readLine()) != null) {
    //                 sql += line;
    //             }

    //             stmt.executeUpdate(sql);

    //             sql = "";
    //             line = null;
    //             while ((line = reader2.readLine()) != null) {
    //                 sql += line;
    //             }
    //             stmt.executeUpdate(sql);
    //         } catch (SQLException e) {
    //             e.printStackTrace();
    //             assert false;
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //             assert false;
    //         }

    //     // } catch (Exception e) {
    //     //     e.printStackTrace();
    //     //     assert false;
    //     // }        
    // }

    // @AfterAll
    // static void afterAll() {
    //     try {
    //         try (Statement stmt = conn.createStatement()) {
    //             stmt.executeUpdate("DROP TABLE market");
    //         }
    //         conn.close();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         assert false;
    //     }
    // }

    // @Test
    // void test() {
    //     try (Statement stmt = conn.createStatement();
    //         ResultSet rs = stmt.executeQuery("SELECT * FROM market");) {
    //         while (rs.next()) {
    //             System.out.println(rs.getInt("id") + " " + rs.getInt("clientId") + " " + rs.getString("instrument") + " " + rs.getInt("price") + " " + rs.getInt("quantity"));
    //         }
    //     } catch (SQLException e) {
    //         assert false;
    //         e.printStackTrace();
    //     }
    // }
}
