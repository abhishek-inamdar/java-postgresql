import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Utility class to set up schema by creating tables
 *
 * @author Abhishek Inamdar
 */
public class DBSetup extends DBBase {
    static final String DROP_TABLES = "DROP TABLE IF EXISTS ORDER_DETAILS, ORDERS, REVIEWS, PRODUCTS, USERS";
    static final String CREATE_USERS_TABLE =
            "CREATE TABLE USERS (" +
                    "  USER_NAME VARCHAR(15)," +
                    "  PASSWORD VARCHAR(15) NOT NULL," +
                    "  FIRST_NAME TEXT NOT NULL," +
                    "  LAST_NAME TEXT NOT NULL," +
                    "  PRIMARY KEY (USER_NAME));";
    static final String CREATE_PRODUCTS_TABLE =
            "CREATE TABLE PRODUCTS (" +
                    "  PRODUCT_ID SERIAL," +
                    "  NAME TEXT NOT NULL," +
                    "  DESCRIPTION TEXT," +
                    "  PRICE NUMERIC NOT NULL CHECK (PRICE > 0)," +
                    "  STOCK INTEGER NOT NULL CHECK (STOCK > 0)," +
                    "  PRIMARY KEY (PRODUCT_ID));";
    static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE REVIEWS (" +
                    "  USER_NAME VARCHAR(15)," +
                    "  PRODUCT_ID INTEGER," +
                    "  REVIEW_TEXT TEXT," +
                    "  RATING FLOAT NOT NULL CHECK (RATING > 0) CHECK (RATING < 5)," +
                    "  REVIEW_DATE TIMESTAMP NOT NULL," +
                    "  PRIMARY KEY (USER_NAME, PRODUCT_ID)," +
                    "  FOREIGN KEY (USER_NAME) REFERENCES USERS(USER_NAME)," +
                    "  FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS(PRODUCT_ID));";
    static final String CREATE_ORDERS_TABLE =
            "CREATE TABLE ORDERS (" +
                    "  ORDER_ID VARCHAR(15)," +
                    "  USER_NAME VARCHAR(15) NOT NULL," +
                    "  ORDER_DATE TIMESTAMP NOT NULL," +
                    "  PRIMARY KEY (ORDER_ID)," +
                    "  FOREIGN KEY (USER_NAME) REFERENCES USERS(USER_NAME));";
    static final String CREATE_ORDER_DETAILS_TABLE =
            "CREATE TABLE ORDER_DETAILS (" +
                    "  ORDER_ID VARCHAR(15)," +
                    "  PRODUCT_ID INTEGER," +
                    "  QUANTITY INTEGER NOT NULL CHECK (QUANTITY > 0)," +
                    "  PRIMARY KEY (ORDER_ID, PRODUCT_ID)," +
                    "  FOREIGN KEY (ORDER_ID) REFERENCES ORDERS(ORDER_ID)," +
                    "  FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS(PRODUCT_ID));";

    /**
     * Recreates all the required tables
     *
     * @throws SQLException If an SQl Error occurs
     */
    public void createTables() throws SQLException {
        Connection con = getConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.execute(DROP_TABLES);
            stmt.execute(CREATE_USERS_TABLE);
            stmt.execute(CREATE_PRODUCTS_TABLE);
            stmt.execute(CREATE_REVIEWS_TABLE);
            stmt.execute(CREATE_ORDERS_TABLE);
            stmt.execute(CREATE_ORDER_DETAILS_TABLE);
        } finally {
            try {
                if (!Objects.isNull(stmt)) {
                    stmt.close();
                }
                if (!Objects.isNull(con)) {
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Something went REALLY wrong.");
            }
        }
    }
}
