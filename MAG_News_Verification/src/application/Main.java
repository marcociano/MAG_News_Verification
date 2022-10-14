/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author KENSOFT
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/Scene.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("MAG News Verification");
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
