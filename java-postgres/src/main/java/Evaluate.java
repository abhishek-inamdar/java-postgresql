import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Evaluate {
    static final String DUPLICATE_SQL_STATE = "23505";
    static final String CONSTRAINT_SQL_STATE = "23514";
    static final String FK_CONSTRAINT_SQL_STATE = "23503";

    public static void main(String[] args) {
        try {
            DBSetup dbSetup = new DBSetup();
            dbSetup.createTables();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        DBOperation operation = new DBOperation();
        try {
            operation.createAccount(DBBase.getConnection(), "user123", "Password.1", "first123", "last123");
        } catch (SQLException e) {
            if (DUPLICATE_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Duplicate");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        }

        try {
            operation.createAccount(DBBase.getConnection(), "user124", "Password.2", "first123", "last123");
        } catch (SQLException e) {
            if (DUPLICATE_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Duplicate");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        }

        try {
            int productId = operation.addProduct(DBBase.getConnection(), "product 1 Name", "product 1 description", 12.87, 7);
        } catch (SQLException e) {
            if (CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Constraint failed");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        }

        try {
            int productId = operation.addProduct(DBBase.getConnection(), "product 2 Name", "product 2 description", 5.8, 2);
        } catch (SQLException e) {
            if (CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Constraint failed");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        }

        try {
            operation.updateStockLevel(DBBase.getConnection(), 3, 2);
        } catch (SQLException e) {
            if (CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Constraint failed");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }

        try {
            operation.postReview(DBBase.getConnection(), "user123", "Password.1",
                    1, 5.0, "Product 1 is awesome");
        } catch (SQLException e) {
            if (CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Constraint failed");
            } else if (FK_CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("FK Constraint failed");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }

        try {
            operation.postReview(DBBase.getConnection(), "user124", "Password.2",
                    1, 4.5, "Product 1 is good");
        } catch (SQLException e) {
            if (CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Constraint failed");
            } else if (FK_CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("FK Constraint failed");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }

        try {
            operation.postReview(DBBase.getConnection(), "user123", "Password.1",
                    2, 2.55, "Product 2 is okay");
        } catch (SQLException e) {
            if (CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Constraint failed");
            } else if (FK_CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("FK Constraint failed");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }


        try {
            Map<Integer, Integer> productQuantities = new HashMap<>();
            productQuantities.put(1, 3);
            productQuantities.put(3, 1);

            operation.submitOrder(DBBase.getConnection(), LocalDateTime.now(), "user123", "Password.1",
                    productQuantities);
        } catch (SQLException e) {
            if (CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Constraint failed");
            } else if (FK_CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("FK Constraint failed");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }


        try {
            double avg = operation.getAverageUserRating(DBBase.getConnection(), "user123");
            System.out.println("Avg is: " + avg);
        } catch (SQLException e) {
            if (CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Constraint failed");
            } else if (FK_CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("FK Constraint failed");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }


        try {
            ProductInformation pInfo = operation.getProductAndReviews(DBBase.getConnection(), 1);
            System.out.println("ProductInformation is: " + pInfo);
        } catch (SQLException e) {
            if (CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("Constraint failed");
            } else if (FK_CONSTRAINT_SQL_STATE.equals(e.getSQLState())) {
                System.err.println("FK Constraint failed");
            } else {
                System.err.println(e.getMessage() + "SQLState: " + e.getSQLState());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }
    }
}
