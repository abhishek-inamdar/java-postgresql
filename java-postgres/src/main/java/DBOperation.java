import java.sql.*;
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
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
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
    private boolean isUserAuthorized(Connection con, String userName, String password) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT USER_NAME FROM USERS WHERE USER_NAME = ? AND PASSWORD = ? ");
            stmt.setString(1, userName);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            con.commit();
            return rs.next() && userName.equals(rs.getString(1));
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
                            Map<Integer, Integer> productQuantities) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        PreparedStatement stmtUpdateProducts = null;
        PreparedStatement stmtCreateOrders = null;
        PreparedStatement stmtCreateOrderDetails = null;
        ResultSet rsCreateOrders = null;
        try {
            if (isUserAuthorized(con, username, password)) {
                stmtUpdateProducts = con.prepareStatement("UPDATE PRODUCTS SET STOCK = STOCK - ? " +
                        " WHERE PRODUCT_ID = ?");
                stmtCreateOrders = con.prepareStatement("INSERT INTO ORDERS(USER_NAME, ORDER_DATE) " +
                        " VALUES (?, ?) RETURNING ORDER_ID");
                stmtCreateOrderDetails = con.prepareStatement("INSERT INTO ORDER_DETAILS(ORDER_ID, " +
                        " PRODUCT_ID, QUANTITY) VALUES (?, ?, ?)");

                stmtCreateOrders.setString(1, username);
                stmtCreateOrders.setTimestamp(2, Timestamp.valueOf(date));
                rsCreateOrders = stmtCreateOrders.executeQuery();
                int orderId = 0;
                if (rsCreateOrders.next()) {
                    orderId = rsCreateOrders.getInt("ORDER_ID");
                }
                if (orderId != 0) {
                    for (Integer productId : productQuantities.keySet()) {
                        Integer quantity = productQuantities.get(productId);

                        //Update Products table
                        stmtUpdateProducts.setInt(1, quantity);
                        stmtUpdateProducts.setInt(2, productId);
                        stmtUpdateProducts.execute();

                        //Insert Order Details
                        stmtCreateOrderDetails.setInt(1, orderId);
                        stmtCreateOrderDetails.setInt(2, productId);
                        stmtCreateOrderDetails.setInt(3, quantity);
                        stmtCreateOrderDetails.execute();
                    }
                }
                con.commit();
            }
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            if (!Objects.isNull(rsCreateOrders)) {
                rsCreateOrders.close();
            }
            if (!Objects.isNull(stmtCreateOrders)) {
                stmtCreateOrders.close();
            }
            if (!Objects.isNull(stmtCreateOrderDetails)) {
                stmtCreateOrderDetails.close();
            }
            if (!Objects.isNull(stmtUpdateProducts)) {
                stmtUpdateProducts.close();
            }
            con.close();
        }
    }

    /**
     * Adds review for particular product given by particular user
     *
     * @param con        Connection
     * @param userName   Username
     * @param password   Password
     * @param productId  Product ID
     * @param rating     rating
     * @param reviewText Review Text
     * @throws SQLException If SQL error occurs
     */
    public void postReview(Connection con, String userName, String password, int productId,
                           double rating, String reviewText) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        PreparedStatement stmt = null;
        try {
            if (isUserAuthorized(con, userName, password)) {
                stmt = con.prepareStatement("INSERT INTO REVIEWS(USER_NAME, PRODUCT_ID, " +
                        " REVIEW_TEXT, RATING, REVIEW_DATE) VALUES (?, ?, ?, ?, ?) ");
                stmt.setString(1, userName);
                stmt.setInt(2, productId);
                stmt.setString(3, reviewText);
                stmt.setDouble(4, rating);
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                stmt.execute();
                con.commit();
            }
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
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
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
     * @param productId      Product ID
     * @param itemCountToAdd Quantity of products to Add
     * @throws SQLException If SQL error occurs
     */
    public void updateStockLevel(Connection con, int productId, int itemCountToAdd) throws SQLException {
        assert itemCountToAdd > 0;
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        PreparedStatement stmt = null;
        try {
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
     * @param productId Product ID
     * @return Product information with all product reviews
     * @throws SQLException If SQL error occurs
     */
    public ProductInformation getProductAndReviews(Connection con, int productId) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        PreparedStatement stmtReadProduct = null;
        PreparedStatement stmtReadReviews = null;
        ResultSet rsReadProduct = null;
        ResultSet rsReadReviews = null;
        try {
            stmtReadProduct = con.prepareStatement("SELECT PRODUCT_ID, NAME, DESCRIPTION, PRICE, STOCK FROM PRODUCTS WHERE PRODUCT_ID = ? ");
            stmtReadProduct.setInt(1, productId);

            stmtReadReviews = con.prepareStatement("SELECT PRODUCT_ID, USER_NAME, REVIEW_TEXT, RATING, REVIEW_DATE FROM REVIEWS WHERE PRODUCT_ID = ? ");
            stmtReadReviews.setInt(1, productId);

            rsReadProduct = stmtReadProduct.executeQuery();
            rsReadReviews = stmtReadReviews.executeQuery();
            con.commit();
            ProductInformation pInfo = null;
            while (rsReadProduct.next()) {
                String name = rsReadProduct.getString("NAME");
                String description = rsReadProduct.getString("DESCRIPTION");
                double price = rsReadProduct.getDouble("PRICE");

                pInfo = new ProductInformation(productId, name, description, price);
            }
            if (!Objects.isNull(pInfo)) {
                while (rsReadReviews.next()) {
                    String reviewUser = rsReadReviews.getString("USER_NAME");
                    String reviewText = rsReadReviews.getString("REVIEW_TEXT");
                    double rating = rsReadReviews.getDouble("RATING");
                    LocalDateTime reviewDate = rsReadReviews.getTimestamp("REVIEW_DATE").toLocalDateTime();
                    pInfo.addReview(new Review(reviewUser, productId, reviewText, rating, reviewDate));
                }
            }
            return pInfo;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            if (!Objects.isNull(rsReadProduct)) {
                rsReadProduct.close();
            }
            if (!Objects.isNull(rsReadReviews)) {
                rsReadReviews.close();
            }
            if (!Objects.isNull(stmtReadProduct)) {
                stmtReadProduct.close();
            }
            if (!Objects.isNull(stmtReadReviews)) {
                stmtReadReviews.close();
            }
            con.close();
        }
    }

    /**
     * Calculates average rating of the user for all the products
     *
     * @param con      Connection
     * @param userName Username
     * @return Average rating by the User
     * @throws SQLException If SQL error occurs
     */
    public double getAverageUserRating(Connection con, String userName) throws SQLException {
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        con.setAutoCommit(false);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double userRating = 0.0;
        try {
            stmt = con.prepareStatement("SELECT AVG(RATING) AS AVG_RATING FROM REVIEWS WHERE USER_NAME = ? ");
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            con.commit();
            while (rs.next()) {
                userRating = rs.getDouble("AVG_RATING");
            }
            return userRating;
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
}
