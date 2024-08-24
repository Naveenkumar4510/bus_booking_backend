

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBTest {
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://naveen:12345@cluster0.arybrtd.mongodb.net/?retryWrites=true&w=majority&ssl=true";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("bus-db");
            System.out.println("Connected to the database successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

