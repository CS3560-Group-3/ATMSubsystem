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

    public static String db_url = "jdbc:mysql://localhost:3306/atmsubsystem";
    public static String db_user = "root"; 
    public static String db_password = "root"; 

    @SuppressWarnings("exports")
    public static final DoubleProperty cashBalance = new SimpleDoubleProperty(0.0);
    @SuppressWarnings("exports")
    public static final IntegerProperty sessionCustomerId = new SimpleIntegerProperty(-1);

    private static Scene scene;

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        scene = new Scene(loadFXML("startScreen"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("CalPoly Credit Union ATM");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static String capitalizeFirst(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_password)) {
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