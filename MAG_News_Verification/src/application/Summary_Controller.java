package application;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;


public class Summary_Controller {
	
	/*The Summary_Controller class is intended to manage the process of saving information obtained from the MAG platform.*/
	/*Useful variable to report the percentage*/
	@FXML
    private ProgressBar progress;
	private String subOutputTrue, subOutputFalse;
	private String scoreNews, progress_Score;
	public TableView<News> tableView;
	public Integer id=1;
	
	@FXML
	private Text score;
	private static final AtomicInteger count = new AtomicInteger(0);
	
	/*Destination Path to save the pdf*/
	
	public static final String DEST = "./reportSummary.pdf";
    public static final String WALLPAPER = "./src/images/wallpaperPDF.jpg";
    public static final String LOGODIPARTIMENTO = "./src/images/logoDipartimento.png";
    public static final String LOGOMAG="./src/images/MAG_News_Verification_logo.png";
    
    public void showScore(String statment, String prediction) {
    	subOutputTrue= statment.substring(0, 4);
		subOutputFalse= statment.substring(0, 5);
		if(subOutputTrue.equals("True")) {
			scoreNews = prediction.substring(2, 4);
			progress_Score = prediction.substring(0,3);
			double prog_statsT= Double.valueOf(progress_Score);
    		progress.setProgress(prog_statsT);
    		Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(progress.progressProperty(), 0)),
    		        new KeyFrame(Duration.seconds(5), new KeyValue(progress.progressProperty(), prog_statsT)));
    		timeline.play();
    		progress.setStyle("-fx-background-color: linear-gradient(to right, #00ff00, #ffff00, #ff0000); -fx-padding: 3px; -fx-background-radius: 10;");
    		score.setText(scoreNews + "% of Fake News");
		}else if(subOutputFalse.equals("False")) {
			scoreNews = prediction.substring(3, 5);
			progress_Score = prediction.substring(2, 4);
			double prog_statsF= Double.valueOf(progress_Score);
    		progress.setProgress(prog_statsF);
    		Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(progress.progressProperty(), 0)),
    		        new KeyFrame(Duration.seconds(5), new KeyValue(progress.progressProperty(), prog_statsF)));
    		timeline.play();
    		progress.setStyle("-fx-background-color: linear-gradient(to right, #00ff00, #ffff00, #ff0000); -fx-padding: 3px; -fx-background-radius: 10;");
    		score.setText(scoreNews + "% of Fake News");
		}
    	
    }
    
	public int getNextCountValue() {
		return count.incrementAndGet();
	}

	
	public void reportPage(){
					
			/*Managing the entry of a report*/
		
			ChoiceDialog<String> dialog= new ChoiceDialog<>("Phishing","Malware","Other types of hazards");
			dialog.setTitle("Report page");
			dialog.setHeaderText("Be careful, choose well");
			dialog.setContentText("Choose the type of signaling");
			
			Optional<String> result=dialog.showAndWait();
			
			if(result.isPresent()) {
				System.out.println("Your choice: " + result.get());
				
				/*Popup message to indicate successful reporting*/
				
				String title= "The report was successful";
		    	TrayNotification tray = new TrayNotification();
		    	AnimationType type= AnimationType.POPUP;
		    	tray.setAnimationType(type);
		    	tray.setTitle(title);
		    	tray.setNotificationType(NotificationType.SUCCESS);
		    	tray.showAndDismiss(Duration.seconds(5));
					
			}
			
			
		}
	
	
	/*With regard to handling file information, having 
	 dealing with files I enter IOException and DocumentException*/
	
	public void createPdf(TableView<News> tableView, String dest) throws IOException, DocumentException {
	
		/*I set the size of the pdf ad A4*/
		Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(DEST));
        document.open();
       
        
        PdfContentByte canvas = writer.getDirectContentUnder();
        Image image = Image.getInstance(WALLPAPER);
        image.scaleAbsolute(PageSize.A4);
        image.setAbsolutePosition(0, 0);
        canvas.addImage(image);
        
        Paragraph header = new Paragraph();
        header.add(new Chunk("MAG News Verification Report", new Font(Font.FontFamily.HELVETICA, 25)));
        header.setAlignment(ALIGN_CENTER);

        document.add(header);
        
        Image logo = Image.getInstance(LOGOMAG);// Creazione dell'oggetto Image con l'immagine da inserire
        logo.setAlignment(ALIGN_CENTER);
        logo.scaleToFit(200, 200); // Impostazione delle dimensioni dell'immagine
        document.add(logo);
        document.add(Chunk.NEWLINE);
  
        PdfPTable table = new PdfPTable(4); // Creazione della tabella con 3 colonne
        table.setWidthPercentage(100); // Impostazione della larghezza della tabella al 100% della pagina

        PdfPCell header1 = new PdfPCell(new Phrase("#")); // Creazione della cella dell'intestazione della colonna 1
        table.addCell(header1).setHorizontalAlignment(ALIGN_CENTER);

        PdfPCell header2 = new PdfPCell(new Phrase("Url Text"));; // Creazione della cella dell'intestazione della colonna 2
        table.addCell(header2).setHorizontalAlignment(ALIGN_CENTER);

        PdfPCell header3 = new PdfPCell(new Phrase("Trustworthiness")); // Creazione della cella dell'intestazione della colonna 3
        table.addCell(header3).setHorizontalAlignment(ALIGN_CENTER);
        
        PdfPCell header4 = new PdfPCell(new Phrase("Accuracy %")); // Creazione della cella dell'intestazione della colonna 3
        table.addCell(header4).setHorizontalAlignment(ALIGN_CENTER);
        
        ObservableList<News> data = tableView.getItems();
        for (News news : data) {
            table.addCell(new PdfPCell(new Phrase(String.valueOf(count.incrementAndGet())))).setHorizontalAlignment(ALIGN_CENTER);
            table.addCell(new PdfPCell(new Phrase(news.getTextArticle()))).setHorizontalAlignment(ALIGN_CENTER);
            table.addCell(new PdfPCell(new Phrase(news.getTrustworthiness()))).setHorizontalAlignment(ALIGN_CENTER);
            table.addCell(new PdfPCell(new Phrase(news.getPredictionPercentage()))).setHorizontalAlignment(ALIGN_CENTER);
        }
        document.add(table);
        
        /*Here I insert the logo*/
        
        Image dip=Image.getInstance(LOGODIPARTIMENTO);
        dip.setAbsolutePosition(10, 10);
        dip.scaleAbsolute(50, 50);
        
        document.add(dip);
        
       
        
   
        /*Management of the first paragraph*/
       /* 
        document.add( Chunk.NEWLINE );
        
        Font font1 = new Font(FontFamily.TIMES_ROMAN, 30.0f, Font.BOLD, BaseColor.BLACK);
        Chunk c1 = new Chunk("MAG News Verification report!", font1);
        Paragraph para1 = new Paragraph(c1);
        
        para1.setAlignment(ALIGN_CENTER);        
        document.add(para1); 
        document.add( Chunk.NEWLINE );
        */
        /*MAG logo insertion management*/
        /*
        Image imgs=Image.getInstance(LOGOMAG);
        imgs.setAbsolutePosition(75, 350);
        imgs.scaleAbsolute(250, 250);
        
        document.add(imgs);
         */
        /*Management of the second paragraph*/
        /*
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
       
        Font font2 = new Font(FontFamily.TIMES_ROMAN, 20.0f, Font.BOLD, BaseColor.BLACK); 
        Chunk c2 = new Chunk("The page analyzed is: " + trustworthiness, font2);       
        Paragraph para2 = new Paragraph(c2);
        
        para2.setAlignment(ALIGN_CENTER);
        
        document.add(para2);
        */
        /*Management of the third paragraph*/
        /*
        document.add( Chunk.NEWLINE );
        
        Font font3 = new Font(FontFamily.TIMES_ROMAN, 20.0f, Font.BOLD, BaseColor.BLACK);        
        Chunk c3 = new Chunk("The percentage found is: " + accuracy, font3);
        
        
        Paragraph para3 = new Paragraph(c3);   
        para3.setAlignment(ALIGN_CENTER);  
        document.add(para3);
        
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
		*/
        /*Management of the fourth paragraph*/
        /*
        Font font4 = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.BOLD, BaseColor.BLACK);
        
        Chunk c4 = new Chunk("Università degli studi di Salerno, Via Papa Giovanni Paolo II, 132, 84084 Fisciano SA" , font4);
        
        
        Paragraph para4 = new Paragraph(c4);
        
        para4.setAlignment(ALIGN_CENTER);
        document.add(para4);
       
        */
         document.close();
        /*Once the pdf is saved you will see the message that it was saved*/
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setContentText("We would like to inform you that the file was successfully saved.");
        alert.showAndWait();
        
        
        /*Popup message to indicate Notice that the pdf has been downloaded*/
		
		String title= "The pdf has been downloaded.";
    	TrayNotification tray = new TrayNotification();
    	AnimationType type= AnimationType.POPUP;
    	tray.setAnimationType(type);
    	tray.setTitle(title);
    	tray.setNotificationType(NotificationType.SUCCESS);
    	tray.showAndDismiss(Duration.seconds(5));
        
    	File file= new File(DEST);
		file.getParentFile().mkdirs();
    }


	
}


