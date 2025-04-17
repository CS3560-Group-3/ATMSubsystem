package atm_sub_system;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JavaFX App
 */
public class App extends Application {

    private static String url = "jdbc:mysql://localhost:3306/atmsubsystem";
    private static String user = "root"; 
    private static String password = "root"; 

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("startScreen"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static final IntegerProperty balance = new SimpleIntegerProperty(0);
    public static final IntegerProperty savingsBalance = new SimpleIntegerProperty(0);

    public static final DoubleProperty cashBalance = new SimpleDoubleProperty(0.0);
    public static final IntegerProperty sessionCustomerId = new SimpleIntegerProperty(-1);

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM atms WHERE atmId=1")) {
                while (rs.next()) {
                    App.cashBalance.set(rs.getDouble("cashBalance"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        launch();
    }

}