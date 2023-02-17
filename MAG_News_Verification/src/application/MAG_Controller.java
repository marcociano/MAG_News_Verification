package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

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
    @FXML
    private TableView<News> tableView;
    @FXML
    private TableColumn<News, Integer> id;
    @FXML
    private TableColumn<News, String> textArticle;
    @FXML
    private TableColumn<News, String> trustworthiness;
    @FXML
    private TableColumn<News, String> prediction_percentage;
    private WebEngine engine;
    private static final AtomicInteger count = new AtomicInteger(0);
    private WebHistory history;
    private String homepage= "https://github.com/marcociano/MAG_News_Verification";
    public String statment, prediction;
    
    @FXML
    private LineChart<Integer,Integer> lineChart;
    
    @FXML
    private CategoryAxis x;

    @FXML
    private NumberAxis y;
    
    //private int counter=0;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	engine=webview.getEngine();
    	engine.load(homepage);
    	txtFieldUrl.setText(homepage);
    	id.setCellValueFactory(new PropertyValueFactory<>("Id"));
    	textArticle.setCellValueFactory(new PropertyValueFactory<>("TextArticle"));
    	trustworthiness.setCellValueFactory(new PropertyValueFactory<>("Trustworthiness"));
    	prediction_percentage.setCellValueFactory(new PropertyValueFactory<>("PredictionPercentage"));
    	tableView.setItems(getNewsList());
    	
    }

    private void loadUrl() {
    	engine = webview.getEngine();
        engine.load("https://" + txtFieldUrl.getText()); //URL Loading

        progress.progressProperty().bind(engine.getLoadWorker().progressProperty());

        engine.getLoadWorker().stateProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
            	engine.setUserStyleSheetLocation(getClass().getResource("/stylesheet/unhighlighted.css").toString());
            	String title= "Loading Page Successful";
            	TrayNotification tray = new TrayNotification();
            	AnimationType type= AnimationType.POPUP;
            	tray.setAnimationType(type);
            	tray.setTitle(title);
            	tray.setNotificationType(NotificationType.SUCCESS);
            	tray.showAndDismiss(Duration.seconds(5));
                history = webview.getEngine().getHistory();
                ObservableList<WebHistory.Entry> entries = history.getEntries();
                txtFieldUrl.setText(entries.get(history.getCurrentIndex()).getUrl());
                System.out.println("Pagina caricata con successo");

            } else if (newValue == Worker.State.FAILED) {
            	Image warning = new Image("/images/warning.png");
            	String title= "Loading Page Failed";
            	String message= "Try re-entering the url correctly by specifying\n the domain(e.g. bbc.co.uk)";
            	TrayNotification tray = new TrayNotification();
            	AnimationType type= AnimationType.POPUP;
            	tray.setAnimationType(type);
            	tray.setTitle(title);
            	tray.setMessage(message);
            	tray.setNotificationType(NotificationType.WARNING);
            	tray.setImage(warning);
            	tray.showAndDismiss(Duration.seconds(5));
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
    private void scrapePage(ActionEvent event) throws IOException {
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
    

	@FXML
    private void detectionFakeNews(ActionEvent event) throws IOException{
    
    	try {
    		URL url = new URL("http://127.0.0.1:5000/detecting");
    		HttpURLConnection conn =(HttpURLConnection) url.openConnection();
    		conn.setRequestMethod("GET");
    		conn.setRequestProperty("Accept", "Flask server");
    		
    		if(conn.getResponseCode()!= 200) {
    			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
    		}
    		
    		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		
    		String output, subOutputTrue, subOutputFalse;
    		
    		System.out.println("Output from Server: \n");
    		while((output = br.readLine())!= null) {
    			System.out.println(output);
    			subOutputTrue= output.substring(0, 4);
    			subOutputFalse= output.substring(0, 5);
    			statment= output.substring(0, 5);
    			prediction= output.substring(5, 22);
    			if(subOutputTrue.equals("True"))
    		    	engine.setUserStyleSheetLocation(getClass().getResource("/stylesheet/highlighted_text_notFakeNews.css").toString());
    			else if(subOutputFalse.equals("False"))
    				engine.setUserStyleSheetLocation(getClass().getResource("/stylesheet/highlighted_text_FakeNews.css").toString());
            	
    			
    			News news= new News();
            	news.setId(getNextCountValue());
                news.setTextArticle(txtFieldUrl.getText());
                news.setTrustworthiness(statment);
                news.setPredictionPercentage(prediction);
                tableView.getItems().add(news);
            	
            	
            	/*counter++;
            	Series<Integer, Integer> series = new XYChart.Series<Integer,Integer>();
            	series.getData().add(new XYChart.Data<Integer, Integer>());
            	lineChart.getData().add(series);
            	*/
    		}
    		
    		conn.disconnect();
    		
    		
    		
    		
    	}catch(MalformedURLException e) {
    		e.printStackTrace();
    	}
    	
    }
	
	public int getNextCountValue() {
		return count.incrementAndGet();
	}

	ObservableList<News> getNewsList(){
		ObservableList<News> observableList = FXCollections.observableArrayList();
			return observableList;
	}

      
    @FXML
    private void viewSummary(ActionEvent event) throws IOException{
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/SummaryView.fxml"));  
    	Parent root= loader.load();
    	Summary_Controller summary_controller=loader.getController();
    	summary_controller.showScore(prediction);
    	Stage stage= new Stage();
    	stage.setScene(new Scene(root));
    	stage.setTitle("Summary Page");
    	stage.setResizable(false);
    	stage.show();
    }
    
  
    
}
