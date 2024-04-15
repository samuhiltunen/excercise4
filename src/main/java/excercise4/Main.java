package excercise4;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.bson.Document;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

public class Main extends Application {
    Properties prop = new Properties();
    String dbConnectionString;
    ConnectionString connString;
    MongoClientSettings settings;
    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collection;

    public Main() {
        try {
            prop.load(new FileInputStream(".env"));
            dbConnectionString = prop.getProperty("DB_CONNECTION_STRING");
            if (dbConnectionString == null) {
                throw new RuntimeException("DB_CONNECTION_STRING not found in .env file");
            }
            connString = new ConnectionString(dbConnectionString);
            settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .build();
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("OTP2");
            collection = database.getCollection("Users");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load .env file", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX MongoDB CRUD");

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField ageField = new TextField();
        TextField cityField = new TextField();

        Button createButton = new Button("Create");
        Button readButton = new Button("Read");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");

        createButton.setOnAction(e -> create(idField.getText(), nameField.getText(), ageField.getText(), cityField.getText()));
        readButton.setOnAction(e -> read(idField.getText()));
        updateButton.setOnAction(e -> update(idField.getText(), nameField.getText(), ageField.getText(), cityField.getText()));
        deleteButton.setOnAction(e -> delete(idField.getText()));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(new Label("ID"), idField, new Label("Name"), nameField, new Label("Age"), ageField, new Label("City"), cityField, createButton, readButton, updateButton, deleteButton);

        Scene scene = new Scene(layout, 300, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void create(String id, String name, String age, String city) {
        Document doc = new Document("id", id)
                .append("name", name)
                .append("age", age)
                .append("city", city);
        collection.insertOne(doc);
        showAlert(AlertType.INFORMATION, "Create Operation", "Document created successfully");
    }
    
    private void read(String id) {
        Document doc = collection.find(new Document("id", id)).first();
        System.out.println(doc.toJson());
        showAlert(AlertType.INFORMATION, "Read Operation", "Document read successfully");
    }
    
    private void update(String id, String name, String age, String city) {
        Document doc = new Document("id", id);
        Document newDoc = new Document("id", id)
                .append("name", name)
                .append("age", age)
                .append("city", city);
        collection.replaceOne(doc, newDoc);
        showAlert(AlertType.INFORMATION, "Update Operation", "Document updated successfully");
    }
    
    private void delete(String id) {
        Document doc = new Document("id", id);
        collection.deleteOne(doc);
        showAlert(AlertType.INFORMATION, "Delete Operation", "Document deleted successfully");
    }
    
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}