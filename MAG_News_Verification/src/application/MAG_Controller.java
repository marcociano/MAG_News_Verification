package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import com.itextpdf.text.DocumentException;
import javafx.animation.TranslateTransition;
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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

public class MAG_Controller implements Initializable{

    @FXML
    private TextField txtFieldUrl;
    @FXML
    private ProgressBar progress;
    @FXML
    private WebView webview;
    @FXML
    private Button searchPage;
    @FXML
    public TableView<News> tableView;
    @FXML
    public TableColumn<News, Integer> id;
    @FXML
    public TableColumn<News, String> textArticle;
    @FXML
    public TableColumn<News, String> trustworthiness;
    @FXML
    public TableColumn<News, String> prediction_percentage;
    @FXML
    private ImageView menu;
    @FXML
    private ImageView menuBack;
    @FXML
    private AnchorPane slider;
    @FXML
    private MenuButton cronoHistory;
    private WebEngine engine;
    private static final AtomicInteger count = new AtomicInteger(0);
    public static final String DEST = "./reportSummary.pdf";
    private WebHistory history;
    private String homepage= "https://github.com/marcociano/MAG_News_Verification";
    public String statment, prediction, scoreNews;
    private Integer index = 1;
    private int prog_stats;
    
    @FXML
    private LineChart<Integer, Integer> lineChart;
    private XYChart.Series<Integer, Integer> dataSeries;
    
    @FXML
    private NumberAxis x;
      
    @FXML
    private NumberAxis y;
  
    /**
      Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	slider.setTranslateX(610);
    	menu.setOnMouseClicked(event -> {
    		TranslateTransition slide = new TranslateTransition();
    		slide.setDuration(Duration.seconds(0.4));
    		slide.setNode(slider);
    		slide.setToX(0);
    		slide.play();
    		DropShadow shadow = new DropShadow();
    		shadow.setColor(Color.BLACK);
    		shadow.setOffsetX(0);
    		shadow.setOffsetY(50);
    		shadow.setRadius(10);
    		slider.setEffect(shadow);
    		slider.setTranslateX(610);
    		slide.setOnFinished((ActionEvent e) -> {
    			menu.setVisible(false);
    			menuBack.setVisible(true);
    		});
    	});
    	menuBack.setOnMouseClicked(event -> {
    		TranslateTransition slide = new TranslateTransition();
    		slide.setDuration(Duration.seconds(0.4));
    		slide.setNode(slider);
    		slide.setToX(610);
    		slide.play();
    		DropShadow shadow = new DropShadow();
    		shadow.setColor(Color.BLACK);
    		shadow.setOffsetX(0);
    		shadow.setOffsetY(50);
    		shadow.setRadius(10);
    		slider.setEffect(shadow);
    		slider.setTranslateX(0);
    		slide.setOnFinished((ActionEvent e) -> {
    			menu.setVisible(true);
    			menuBack.setVisible(false);
    		});
    	});
    	engine=webview.getEngine();
    	engine.load(homepage);
    	txtFieldUrl.setText(homepage);
    	id.setCellValueFactory(new PropertyValueFactory<>("Id"));
    	textArticle.setCellValueFactory(new PropertyValueFactory<>("TextArticle"));
    	trustworthiness.setCellValueFactory(new PropertyValueFactory<>("Trustworthiness"));
    	prediction_percentage.setCellValueFactory(new PropertyValueFactory<>("PredictionPercentage"));
    	tableView.setItems(getNewsList());
    	x.setTickUnit(1);
    	x.setAutoRanging(false);
    	x.setLowerBound(0);
    	y.setLowerBound(0);
    	x.setUpperBound(20);
    	y.setUpperBound(1);
    	dataSeries = new XYChart.Series<>();
    	dataSeries.setName("Andamento"); // Imposta il nome della serie
    	
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
                
            	
            	/*Graph that monitors news trends. 
            	 * There are two lines: one indicates the percentage of fake news found and the other is used to track the overall page trend i.e., 
            	 * it takes only two values to indicate whether the news is fake or not.*/
                if(subOutputTrue.equals("True")) {
   				 	scoreNews = prediction.substring(2, 3);
   				 	prog_stats= Integer.valueOf(scoreNews);
   				
                }
                
                else if(subOutputFalse.equals("False")) {
                	scoreNews = prediction.substring(3, 4);
                	prog_stats= Integer.valueOf(scoreNews);
                	
                }
              
                dataSeries.getData().add(new XYChart.Data<>(index, prog_stats));
                if(index == 1)
                	lineChart.getData().add(dataSeries);
                index= index +1;
                
                if(index >= 20) {
                	x.setAutoRanging(true);
                }

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
    private void viewSummary(ActionEvent event) throws IOException, DocumentException{
    	if(prediction == null) {
    		String title= "The report doesn't contain any information";
    		TrayNotification tray = new TrayNotification();
    		AnimationType type= AnimationType.POPUP;
    		tray.setAnimationType(type);
    		tray.setTitle(title);
    		tray.setNotificationType(NotificationType.ERROR);
    		tray.showAndDismiss(Duration.seconds(5));
    	}else {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/SummaryView.fxml"));  
    	Parent root= loader.load();
    	Summary_Controller summary_controller=loader.getController();
    	summary_controller.showScore(statment, prediction);
    	summary_controller.createPdf(tableView, DEST);
    	Stage stage= new Stage();
    	stage.setScene(new Scene(root));
    	stage.setTitle("Summary Page");
    	stage.setResizable(false);
    	stage.show();
    	}
    	
    }
       
}
