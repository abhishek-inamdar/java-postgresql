import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class used for storing constants and utility methods
 *
 * @author Abhishek Inamdar
 */
public class Utility {
    public static final String DUPLICATE_SQL_STATE = "23505";
    public static final String CONSTRAINT_SQL_STATE = "23514";
    public static final String FK_CONSTRAINT_SQL_STATE = "23503";
    public static final String TRANSACTION_BLOCKED_STATE = "25P02";

    public static final int NUM_USERS = 1000;
    public static final String USER_NAME_PREFIX = "user";
    public static final String PASSWORD_PREFIX = "password";

    public static final int NUM_PRODUCTS = 10_000;

    public static final double MIN_PRODUCT_PRICE = 1.00;
    public static final double MAX_PRODUCT_PRICE = 100.00;
    public static final int MIN_PRODUCT_STOCK = 0;
    public static final int MAX_PRODUCT_STOCK = 100;

    public static final int NUM_REVIEWS = 20_000;
    public static final double MIN_REVIEW_RATING = 0.00;
    public static final double MAX_REVIEW_RATING = 5.00;

    public static final int NUM_ORDERS = 10_000;
    public static final long MIN_ORDER_DATE = Timestamp.valueOf("2020-01-01 00:00:00").getTime();
    public static final long MAX_ORDER_DATE = Timestamp.valueOf("2020-12-31 00:59:00").getTime();
    public static final int NUM_PRODUCTS_IN_ORDER = 10;
    public static final int MAX_PRODUCT_QUANTITY_IN_ORDER = 3;

    public static final int MINUTES_TO_RUN = 5;
    public static final int MAX_THREADS_TO_RUN = 2;
    public static final String CREATE_ACCOUNT = "createAccount";
    public static final String ADD_PRODUCT = "addProduct";
    public static final String UPDATE_STOCK_LEVEL = "updateStockLevel";
    public static final String GET_PRODUCT_REVIEWS = "getProductAndReviews";
    public static final String GET_AVERAGE_RATING = "getAverageUserRating";
    public static final String SUBMIT_ORDER = "submitOrder";
    public static final String POST_REVIEW = "postReview";

    public static int intBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static double doubleBetween(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static long longBetween(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    public static LocalDateTime getRandomOrderDate() {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(longBetween(MIN_ORDER_DATE, MAX_ORDER_DATE)),
                TimeZone.getDefault().toZoneId());
    }

    public static void populateProductQuantityMap(Map<Integer, Integer> productQuantities) {
        for (int i = 1; i <= NUM_PRODUCTS_IN_ORDER; i++) {
            int productId = intBetween(1, NUM_PRODUCTS);
            int quantity = intBetween(1, MAX_PRODUCT_QUANTITY_IN_ORDER);
            productQuantities.put(productId, quantity);
        }
    }
}
