import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.codearte.jfairy.producer.text.TextProducer;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Evaluate class to perform testing
 *
 * @author Abhishek Inamdar
 */
public class Evaluate extends Utility {
    /**
     * Method to choose operation based on the probability - integer based
     * @param random random integer in the range 1 to 100
     * @return Chosen DB operation
     */
    public static String getOperation(final int random) {
        String operation;
        if (random <= 3) {
            // 3% probability
            operation = CREATE_ACCOUNT;
        } else if (random <= 5) {
            // 2% probability
            operation = ADD_PRODUCT;
        } else if (random <= 15) {
            // 10% probability
            operation = UPDATE_STOCK_LEVEL;
        } else if (random <= 80) {
            // 65% probability
            operation = GET_PRODUCT_REVIEWS;
        } else if (random <= 85) {
            // 5% probability
            operation = GET_AVERAGE_RATING;
        } else if (random <= 95) {
            // 10% probability
            operation = SUBMIT_ORDER;
        } else {
            // 5% probability
            operation = POST_REVIEW;
        }
        return operation;
    }

    public static void main(String[] args) {
        //Loop for threadCount 1 to 10
        for (int threadCount = 1; threadCount <= MAX_THREADS_TO_RUN; threadCount++) {
            try {

                //DB Initialization and setup
                DBInitialize initialize = new DBInitialize();
                initialize.setupAndInitializeDB();

                //creating threadpool and executing threads
                ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
                final long startTime = System.nanoTime();
                long elapsedNanos = System.nanoTime() - startTime;
                while ((elapsedNanos / (1e9 * 60)) < MINUTES_TO_RUN) {
                    executorService.execute(() -> {
                        Connection con = null;
                        try {
                            con = DBBase.getConnection();
                        } catch (SQLException e) {
                            System.err.println("Can not establish the DB connection");
                        }
                        Objects.requireNonNull(con);
                        DBOperation operation = new DBOperation();
                        String randomOperation = getOperation(intBetween(1, 100));
                        //System.out.println(Thread.currentThread().getId() + ": About to execute:  " + randomOperation);
                        try {
                            Fairy fairy;
                            TextProducer text;
                            int userId, productId;
                            String userName, password;
                            switch (Objects.requireNonNull(randomOperation)) {
                                case CREATE_ACCOUNT:
                                    fairy = Fairy.create();
                                    Person person = fairy.person();
                                    userId = intBetween(1, NUM_USERS);
                                    userName = USER_NAME_PREFIX + userId;
                                    password = PASSWORD_PREFIX + userId;
                                    String firstName = person.getFirstName();
                                    String lastName = person.getLastName();
                                    operation.createAccount(con, userName, password, firstName, lastName);
                                    break;
                                case ADD_PRODUCT:
                                    fairy = Fairy.create();
                                    text = fairy.textProducer();
                                    String productName = text.randomString(15);
                                    String productDesc = text.randomString(50);
                                    double productPrice = doubleBetween(MIN_PRODUCT_PRICE, MAX_PRODUCT_PRICE);
                                    int productStock = intBetween(MIN_PRODUCT_STOCK, MAX_PRODUCT_STOCK);
                                    operation.addProduct(con, productName, productDesc, productPrice, productStock);
                                    break;
                                case UPDATE_STOCK_LEVEL:
                                    productId = intBetween(1, NUM_PRODUCTS);
                                    int quantity = intBetween(MIN_PRODUCT_STOCK, MAX_PRODUCT_STOCK);
                                    operation.updateStockLevel(con, productId, quantity);
                                    break;
                                case GET_PRODUCT_REVIEWS:
                                    productId = intBetween(1, NUM_PRODUCTS);
                                    operation.getProductAndReviews(con, productId);
                                    break;
                                case GET_AVERAGE_RATING:
                                    userId = intBetween(1, NUM_USERS);
                                    userName = USER_NAME_PREFIX + userId;
                                    operation.getAverageUserRating(con, userName);
                                    break;
                                case SUBMIT_ORDER:
                                    userId = intBetween(1, NUM_USERS);
                                    userName = USER_NAME_PREFIX + userId;
                                    password = PASSWORD_PREFIX + userId;
                                    LocalDateTime orderDate = getRandomOrderDate();
                                    Map<Integer, Integer> productQuantities = new HashMap<>();
                                    populateProductQuantityMap(productQuantities);
                                    operation.submitOrder(con, orderDate, userName, password, productQuantities);
                                    break;
                                case POST_REVIEW:
                                    fairy = Fairy.create();
                                    text = fairy.textProducer();
                                    userId = intBetween(1, NUM_USERS);
                                    userName = USER_NAME_PREFIX + userId;
                                    password = PASSWORD_PREFIX + userId;
                                    productId = intBetween(1, NUM_PRODUCTS);
                                    double rating = doubleBetween(MIN_REVIEW_RATING, MAX_REVIEW_RATING);
                                    String reviewText = text.text();
                                    operation.postReview(con, userName, password, productId, rating, reviewText);
                                    break;
                                default:
                                    break;
                            }
                        } catch (SQLException e) {
                            if (!DUPLICATE_SQL_STATE.equals(e.getSQLState())
                                    && !FK_CONSTRAINT_SQL_STATE.equals(e.getSQLState())
                                    && !CONSTRAINT_SQL_STATE.equals(e.getSQLState())
                                    && !TRANSACTION_BLOCKED_STATE.equals(e.getSQLState())) {
                                System.err.println("Unexpected SQLException occurred while performing " + randomOperation
                                        + ", " + e.getSQLState() + e.getMessage());
                            }
                        }
                    });
                    elapsedNanos = System.nanoTime() - startTime;
                }
                //Shutting down all the threads
                executorService.shutdownNow();
            } catch (Exception e) {
                System.err.println("SOMETHING WENT WRONG!!" + e.getMessage());
            }
        }
    }
}
