import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.codearte.jfairy.producer.text.TextProducer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

public class Evaluate {
    static final String DUPLICATE_SQL_STATE = "23505";
    static final String CONSTRAINT_SQL_STATE = "23514";
    static final String FK_CONSTRAINT_SQL_STATE = "23503";

    static final int NUM_USERS = 1000;
    static final String USER_NAME_PREFIX = "user";
    static final String PASSWORD_PREFIX = "password";

    static final int NUM_PRODUCTS = 10_000;

    static final double MIN_PRODUCT_PRICE = 1.00;
    static final double MAX_PRODUCT_PRICE = 100.00;
    static final int MIN_PRODUCT_STOCK = 0;
    static final int MAX_PRODUCT_STOCK = 100;

    static final int NUM_REVIEWS = 20_000;
    static final double MIN_REVIEW_RATING = 0.00;
    static final double MAX_REVIEW_RATING = 5.00;

    static final int NUM_ORDERS = 10_000;
    static final long MIN_ORDER_DATE = Timestamp.valueOf("2020-01-01 00:00:00").getTime();
    static final long MAX_ORDER_DATE = Timestamp.valueOf("2020-12-31 00:59:00").getTime();
    static final int NUM_PRODUCTS_IN_ORDER = 10;
    static final int MAX_PRODUCT_QUANTITY_IN_ORDER = 5;

    public static int intBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static double doubleBetween(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static long longBetween(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }


    public static void main(String[] args) {
        setupDatabase();
        System.out.println("Setup complete!");

        initializeDatabase();
    }

    private static void setupDatabase() {
        try {
            DBSetup dbSetup = new DBSetup();
            dbSetup.createTables();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void initializeDatabase() {
        addProducts();
        System.out.println("Products added!");
        addUsers();
        System.out.println("Users added!");
        postReviews();
        System.out.println("Reviews added!");
        submitOrders();
        System.out.println("Orders submitted!");
    }

    private static void addProducts() {
        Connection con;
        DBOperation operation = new DBOperation();
        Fairy fairy = Fairy.create();

        String productName;
        String productDesc;
        double productPrice;
        int productStock;
        //Add Products
        for (int i = 1; i <= NUM_PRODUCTS; ) {
            try {
                con = DBSetup.getConnection();
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

    private static void addUsers() {
        Connection con;
        DBOperation operation = new DBOperation();
        Fairy fairy = Fairy.create();
        String userName;
        String password;
        String firstName;
        String lastName;
        for (int i = 1; i <= NUM_USERS; ) {
            try {
                con = DBSetup.getConnection();
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

    private static void postReviews() {
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
                con = DBSetup.getConnection();
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

    private static void submitOrders() {
        Connection con;
        DBOperation operation = new DBOperation();
        String userName;
        String password;
        LocalDateTime orderDate;
        Map<Integer, Integer> productQuantities;
        for (int i = 1; i <= NUM_ORDERS; i++) {
            try {
                con = DBSetup.getConnection();
                int userId = intBetween(1, NUM_USERS);
                userName = USER_NAME_PREFIX + userId;
                password = PASSWORD_PREFIX + userId;
                orderDate = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(longBetween(MIN_ORDER_DATE, MAX_ORDER_DATE)),
                        TimeZone.getDefault().toZoneId());
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

    private static void populateProductQuantityMap(Map<Integer, Integer> productQuantities) {
        for (int i = 1; i <= NUM_PRODUCTS_IN_ORDER; i++) {
            int productId = intBetween(1, NUM_PRODUCTS);
            int quantity = intBetween(1, MAX_PRODUCT_QUANTITY_IN_ORDER);
            productQuantities.put(productId, quantity);
        }
    }
}
