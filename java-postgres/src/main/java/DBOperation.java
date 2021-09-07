import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Interface containing all the DB operation functions
 *
 * @author Abhishek Inamdar
 */
public class DBOperation {
    /**
     * Creates account for new user
     *
     * @param con       Connection
     * @param userName  Username
     * @param password  Password
     * @param firstName First name
     * @param lastName  Last name
     * @throws SQLException If SQL error occurs
     */
    public void createAccount(Connection con, String userName, String password,
                              String firstName, String lastName) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        con.setAutoCommit(false);
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("INSERT INTO USERS(USER_NAME, PASSWORD, FIRST_NAME, LAST_NAME) " +
                    " VALUES (?, ?, ?, ?)");
            stmt.setString(1, userName);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.execute();
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            if (!Objects.isNull(stmt)) {
                stmt.close();
            }
            con.close();
        }
    }

    /**
     * Checks if the given user is authorized or not
     *
     * @param con      Connection
     * @param userName Username
     * @param password Password
     * @return true if user is authorized, false otherwise
     * @throws SQLException If SQL error occurs
     */
    public boolean isUserAuthorized(Connection con, String userName, String password) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        con.setAutoCommit(false);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT USER_NAME FROM USERS WHERE USER_NAME = ? AND PASSWORD = ? ");
            stmt.setString(1, userName);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            if (rs.next() && userName.equals(rs.getString(1))) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if (!Objects.isNull(rs)) {
                rs.close();
            }
            if (!Objects.isNull(stmt)) {
                stmt.close();
            }
            con.close();
        }
    }

    /**
     * Creates Order with specified products and quantities
     * Fails if sufficient quantities are not available
     *
     * @param con               Connection
     * @param date              Date of the order
     * @param username          Username
     * @param password          Password
     * @param productQuantities List of products and their desired quantities
     * @throws SQLException If SQL error occurs
     */
    public void submitOrder(Connection con, LocalDateTime date, String username, String password,
                            Map<String, Integer> productQuantities) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        con.setAutoCommit(false);
        //TODO
    }

    /**
     * Adds review for particular product given by particular user
     *
     * @param con        Connection
     * @param userName   Username
     * @param password   Password
     * @param productId  Product Id
     * @param rating     rating
     * @param reviewText Review Text
     * @throws SQLException If SQL error occurs
     */
    public void postReview(Connection con, String userName, String password, String productId,
                           float rating, String reviewText) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        con.setAutoCommit(false);
        //TODO
    }

    /**
     * Adds new product and returns its product ID
     *
     * @param con          Connection
     * @param name         Product Name
     * @param description  Product Description
     * @param price        Price of the product
     * @param initialStock Initial stock quantity
     * @return Product ID of newly added product
     * @throws SQLException If SQL error occurs
     */
    public int addProduct(Connection con, String name, String description, double price,
                          int initialStock) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        con.setAutoCommit(false);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int productId = 0;
        try {
            stmt = con.prepareStatement("INSERT INTO PRODUCTS(NAME, DESCRIPTION, PRICE, STOCK) " +
                    " VALUES (?, ?, ?, ?) RETURNING PRODUCT_ID ");
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setInt(4, initialStock);
            rs = stmt.executeQuery();
            con.commit();
            if (rs.next()) {
                productId = rs.getInt("PRODUCT_ID");
            }
            return productId;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            if (!Objects.isNull(rs)) {
                rs.close();
            }
            if (!Objects.isNull(stmt)) {
                stmt.close();
            }
            con.close();
        }
    }

    /**
     * Updates the Stock quantity of the product
     *
     * @param con            Connection
     * @param productId      Product Id
     * @param itemCountToAdd Quantity of products to Add
     * @throws SQLException If SQL error occurs
     */
    public void updateStockLevel(Connection con, int productId, int itemCountToAdd) throws SQLException {
        assert itemCountToAdd > 0;
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        con.setAutoCommit(false);
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement("UPDATE PRODUCTS SET STOCK = STOCK + ? WHERE PRODUCT_ID = ? ");
            stmt.setInt(1, itemCountToAdd);
            stmt.setInt(2, productId);
            stmt.execute();
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            if (!Objects.isNull(stmt)) {
                stmt.close();
            }
            con.close();
        }
    }

    /**
     * Fetches Product information based on given product ID and the related reviews
     *
     * @param con       Connection
     * @param productId Product Id
     * @return Product information with all product reviews
     * @throws SQLException If SQL error occurs
     */
    public ProductInformation getProductAndReviews(Connection con, String productId) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        con.setAutoCommit(false);
        //TODO
        return null;
    }

    /**
     * Calculates average rating of the user for all the products
     *
     * @param con      Connection
     * @param userName Username
     * @return Average rating by the User
     * @throws SQLException If SQL error occurs
     */
    public float getAverageUserRating(Connection con, String userName) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        con.setAutoCommit(false);
        //TODO
        return 0f;
    }
}