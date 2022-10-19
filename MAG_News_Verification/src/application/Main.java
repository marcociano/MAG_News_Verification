package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
    	
        Parent root = FXMLLoader.load(getClass().getResource("/WebView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("MAG News Verification");
        java.net.CookieHandler.setDefault(null); //Questa linea di codice risolve il bug di crash all'avvio dell'applicazione
        stage.setResizable(false);
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
