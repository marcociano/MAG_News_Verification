package application;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;


public class Scraping_Controller implements Initializable {

    @FXML
    private Button fetchButton;

    @FXML
    private TextField queryTextField;
    
    @FXML
    private TextArea docTextArea;

    @FXML
    private TextField urlTextField;
    
    @FXML
    private Accordion accordionElements;
    private Document doc;
    private PauseTransition debouncer;

    
    public void initialize (URL location, ResourceBundle resources) {
    	fetchButton.disableProperty().bind(
    			urlTextField.textProperty().isEmpty()
    			);
    	
        debouncer = new PauseTransition(Duration.seconds(1));
   
        queryTextField.textProperty().addListener((obs, ov, nv) -> {
            
            debouncer.setOnFinished(evt -> {
        
                accordionElements.getPanes().clear();

                var querySelector = queryTextField.getText();
                if (querySelector != null && !querySelector.isBlank()) {
                    
                 
                    Elements elements = doc.select(querySelector);
                    elements.forEach(el -> {
                      
                        var textArea = new TextArea(el.html());
                        textArea.setWrapText(true);
                        textArea.setEditable(false);
                        textArea.setPrefSize(600, 200);
                        accordionElements.getPanes().add(
                            new TitledPane(el.tagName(), new StackPane(textArea))
                        );
                    });
                }
            });
            debouncer.playFromStart();
        });
    }
   
    
	public void showInformation(String url) {
		urlTextField.setText(url);
	}
 
    @FXML
    void handlefetchPage(ActionEvent event) {
    	try {
            doc = Jsoup.connect(urlTextField.getText())
            		.maxBodySize(0)
            		.timeout(600000)
            		.get();
            docTextArea.setText(doc.body().html());
            FileWriter writer=new FileWriter("./docJsoup.html");
            Elements neededTag= doc.select("article");          
  
           for(Element e : neededTag) {
        	  writer.write(e.text());
           }
            writer.close();
            String title= "docJsoup.html ready to be analyzed";
        	TrayNotification tray = new TrayNotification();
        	AnimationType type= AnimationType.POPUP;
        	tray.setAnimationType(type);
        	tray.setTitle(title);
        	tray.setNotificationType(NotificationType.SUCCESS);
        	tray.showAndDismiss(Duration.seconds(5));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
