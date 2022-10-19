package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MAG_Controller implements Initializable {

    @FXML
    private TextField txtFieldUrl;
    @FXML
    private ProgressBar progress;
    @FXML
    private WebView webview;
    @FXML
    private Button searchPage;
    @FXML
    private Button cronoHistory;
    private WebEngine engine;
    private WebHistory history;
    private String homepage= "https://github.com/marcociano/MAG_News_Verification";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	engine=webview.getEngine();
    	engine.load(homepage);
    	txtFieldUrl.setText(homepage);
    }

    private void loadUrl() {
    	engine = webview.getEngine();
        engine.load("https://" + txtFieldUrl.getText()); //Caricamento della URL

        progress.progressProperty().bind(engine.getLoadWorker().progressProperty());

        engine.getLoadWorker().stateProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                System.out.println("Pagina caricata con successo");

                history = webview.getEngine().getHistory();
                ObservableList<WebHistory.Entry> entries = history.getEntries();
                txtFieldUrl.setText(entries.get(history.getCurrentIndex()).getUrl());
                
  

            } else if (newValue == Worker.State.FAILED) {
                System.out.println("Caricamento fallito");
            }
        });
    }

    @FXML
    private void back(ActionEvent event) {
        history = webview.getEngine().getHistory();
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        history.go(-1);
        txtFieldUrl.setText(entries.get(history.getCurrentIndex()).getUrl());
    }

    @FXML
    private void reload(ActionEvent event) {
        engine.reload();
    }
    @FXML
    private void forward(ActionEvent event) {
    	history= webview.getEngine().getHistory();
    	ObservableList<WebHistory.Entry> entries= history.getEntries();
    	history.go(1);
    	txtFieldUrl.setText(entries.get(history.getCurrentIndex()).getUrl());
    }

    @FXML
    private void homepage(ActionEvent event) {
        engine=webview.getEngine();
        engine.load(homepage);
    }
    
    @FXML
    private void historyPages(ActionEvent event) {
    	 history = webview.getEngine().getHistory();
         ObservableList<WebHistory.Entry> entries = history.getEntries();
         for(WebHistory.Entry entry : entries) {
         	System.out.println(entry);
         }
    }

    @FXML
    private void txtEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loadUrl();
        }
    }

    @FXML
    private void zoomIn(ActionEvent event) {
        webview.setZoom(webview.getZoom() + 0.10);
    }

    @FXML
    private void zoomOut(ActionEvent event) {
        webview.setZoom(webview.getZoom() - 0.10);
    }
    
    @FXML
    private void searchPage(ActionEvent event) {
    	searchPage.setOnAction(new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent event) {
    			loadUrl();
    		}
    	});
    }
    
    @FXML
    private void scrapPage(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/ScrapedView.fxml"));
    	Parent root= loader.load();
    	
    	Scraping_Controller scraping_controller=loader.getController();
    	scraping_controller.showInformation(txtFieldUrl.getText());
    	Stage stage= new Stage();
    	stage.setScene(new Scene(root));
    	stage.setTitle("Scrape Page");
    	stage.setResizable(false);
    	stage.show();
    }
}
