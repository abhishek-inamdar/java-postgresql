import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductInformation {
    private int productId;
    private String name;
    private String description;
    private double price;
    private List<Review> reviews;

    public ProductInformation(int productId, String name, String description, double price) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.reviews = new ArrayList<>();
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public String toString() {
        return "ProductInformation{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", reviews=" + reviews +
                '}';
    }
}

class Review {
    private String userName;
    private int productId;
    private String reviewText;
    private double rating;
    private LocalDateTime reviewDate;

    public Review(String userName, int productId, String reviewText, double rating, LocalDateTime reviewDate) {
        this.userName = userName;
        this.productId = productId;
        this.reviewText = reviewText;
        this.rating = rating;
        this.reviewDate = reviewDate;
    }

    public String getUserName() {
        return userName;
    }

    public int getProductId() {
        return productId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public double getRating() {
        return rating;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    @Override
    public String toString() {
        return "Review{" +
                "userName='" + userName + '\'' +
                ", productId='" + productId + '\'' +
                ", reviewText='" + reviewText + '\'' +
                ", rating=" + rating +
                ", reviewDate=" + reviewDate +
                '}';
    }
}
