import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * This class establishes DB connection based on database properties
 *
 * @author Abhishek Inamdar
 */
public class DBBase {
    /**
     * Database Properties file name
     */
    private static final String DB_PROPERTY_FILE = "db.properties";
    /**
     * Connection Object
     */
    private static Connection connection = null;

    /**
     * Returns Connection
     *
     * @return Connection object
     */
    public static Connection getConnection() throws SQLException {
        if(Objects.isNull(connection) || connection.isClosed()){
            establishConnection();
        }
        return connection;
    }

    /**
     * Establishes connection based on database properties
     *
     * @throws SQLException If an SQL Error occurs
     */
    public static void establishConnection() throws SQLException {
        if (Objects.isNull(connection) || connection.isClosed()) {
            // Read the properties file
            // it should contain following properties
            // URL, USERNAME, PASSWORD
            File dbProperties = null;
            try {
                DBBase base = new DBBase();
                dbProperties = base.getFileFromResource(DB_PROPERTY_FILE);
            } catch (URISyntaxException e) {
                System.err.println("Error loading db.properties file" + e.getMessage());
            }
            String url = null, username = null, password = null;

            if (!Objects.isNull(dbProperties)) {
                try {
                    List<String> lines = Files.readAllLines(dbProperties.toPath(),
                            StandardCharsets.UTF_8);
                    for (String line : lines) {
                        String[] parts = line.split("==");
                        String property = parts[0];
                        String value = null;
                        if (parts.length > 1) {
                            value = parts[1];
                        }
                        switch (property) {
                            case "URL":
                                url = value;
                                break;
                            case "USERNAME":
                                username = value;
                                break;
                            case "PASSWORD":
                                password = value;
                                break;
                            default:
                                break;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading db.properties file");
                }
            }

            if (url != null && username != null) {
                connection = DriverManager.getConnection(url, username, password);
            }
            if (Objects.isNull(connection)) {
                System.err.println("Can not establish the DB connection");
            }
        }
    }

    /**
     * Utility method to read file from the resources
     *
     * @param fileName name of the file
     * @return File object
     * @throws URISyntaxException In case of File URI Syntax error
     */
    private File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }
}

