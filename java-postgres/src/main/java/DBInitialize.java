import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.codearte.jfairy.producer.text.TextProducer;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing Database setup and initialization logic
 *
 * @author Abhishek Inamdar
 */
public class DBInitialize extends Utility {

    /**
     * Method to perform setup and data initialization
     */
    public void setupAndInitializeDB() {
        setupDatabase();
        initializeDatabase();
    }

    /**
     * Method to setup DB schema (DDL statements)
     */
    private void setupDatabase() {
        try {
            DBSetup dbSetup = new DBSetup();
            dbSetup.createTables();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Method to add random data
     */
    private void initializeDatabase() {
        addProducts();
        addUsers();
        postReviews();
        submitOrders();
    }

    /**
     * Method to add random data into Products Table
     */
    private void addProducts() {
        Connection con;
        DBOperation operation = new DBOperation();
        Fairy fairy = Fairy.create();

        String productName;
        String productDesc;
        double productPrice;
        int productStock;
        for (int i = 1; i <= NUM_PRODUCTS; ) {
            try {
                con = DBBase.getConnection();
                TextProducer text = fairy.textProducer();
                productName = text.randomString(15);
                productDesc = text.randomString(50);
                productPrice = doubleBetween(MIN_PRODUCT_PRICE, MAX_PRODUCT_PRICE);
                productStock = intBetween(MIN_PRODUCT_STOCK, MAX_PRODUCT_STOCK);
                operation.addProduct(con, productName, productDesc, productPrice, productStock);
                i++;
            } catch (SQLException e) {
                System.err.println("Something went wrong while initializing Products Data");
                System.err.println(e.getSQLState() + e.getMessage());
            }
        }
    }

    /**
     * Method to add random data into Users Table
     */
    private void addUsers() {
        Connection con;
        DBOperation operation = new DBOperation();
        Fairy fairy = Fairy.create();
        String userName;
        String password;
        String firstName;
        String lastName;
        for (int i = 1; i <= NUM_USERS; ) {
            try {
                con = DBBase.getConnection();
                Person person = fairy.person();
                userName = USER_NAME_PREFIX + i;
                password = PASSWORD_PREFIX + i;
                firstName = person.getFirstName();
                lastName = person.getLastName();
                operation.createAccount(con, userName, password, firstName, lastName);
                i++;
            } catch (SQLException e) {
                System.err.println("Something went wrong while initializing Users Data");
                System.err.println(e.getSQLState() + e.getMessage());
            }
        }
    }

    /**
     * Method to add random data into Reviews Table
     */
    private void postReviews() {
        Connection con;
        DBOperation operation = new DBOperation();
        Fairy fairy = Fairy.create();
        String userName;
        String password;
        int productId;
        double rating;
        String reviewText;
        for (int i = 1; i <= NUM_REVIEWS; ) {
            try {
                con = DBBase.getConnection();
                TextProducer text = fairy.textProducer();
                int userId = intBetween(1, NUM_USERS);
                userName = USER_NAME_PREFIX + userId;
                password = PASSWORD_PREFIX + userId;
                productId = intBetween(1, NUM_PRODUCTS);
                rating = doubleBetween(MIN_REVIEW_RATING, MAX_REVIEW_RATING);
                reviewText = text.text();
                operation.postReview(con, userName, password, productId, rating, reviewText);
                i++;
            } catch (SQLException e) {
                if (!DUPLICATE_SQL_STATE.equals(e.getSQLState())) {
                    System.err.println("Something went wrong while initializing Reviews Data");
                    System.err.println(e.getSQLState() + e.getMessage());
                }

            }
        }
    }

    /**
     * Method to add random data into Orders Table
     */
    private void submitOrders() {
        Connection con;
        DBOperation operation = new DBOperation();
        String userName;
        String password;
        LocalDateTime orderDate;
        Map<Integer, Integer> productQuantities;
        for (int i = 1; i <= NUM_ORDERS; i++) {
            try {
                con = DBBase.getConnection();
                int userId = intBetween(1, NUM_USERS);
                userName = USER_NAME_PREFIX + userId;
                password = PASSWORD_PREFIX + userId;
                orderDate = getRandomOrderDate();
                productQuantities = new HashMap<>();
                populateProductQuantityMap(productQuantities);
                operation.submitOrder(con, orderDate, userName, password, productQuantities);

            } catch (SQLException e) {
                if (!CONSTRAINT_SQL_STATE.equals(e.getSQLState())
                        && !FK_CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                    System.err.println("Something went wrong while initializing Reviews Data");
                    System.err.println(e.getSQLState() + e.getMessage());
                }
            }
        }
    }
}
