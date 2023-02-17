package application;

import static com.itextpdf.text.Element.ALIGN_CENTER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;


public class Summary_Controller {
	
	/*The Summary_Controller class is intended to manage the process of saving information obtained from the MAG platform.*/

	@FXML
	/*Useful variable to report the percentage*/
    private ProgressBar progress;
	private String trustworthiness; 
	private String accuracy;
	@FXML
	private Text score;
	
	/*Destination Path to save the pdf*/
	
	public static final String DEST = "./reportSummary.pdf";
    public static final String WALLPAPER = "./src/images/wallpaper4.jpg";
    public static final String LOGODIPARTIMENTO = "./src/images/logoDipartimento.png";
    public static final String LOGOMAG="./src/images/MAG_News_Verification_logo.png";
    
    public void showScore(String prediction) {
    	String scoreNews = prediction.substring(2, 4);
    	String progress_Score = prediction.substring(0,3);
    	double prog_stats= Double.valueOf(progress_Score);
    	
    	progress.setProgress(prog_stats);
		score.setText(scoreNews + "% of Fake News");
    	
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
	
	public void downloadSummary() throws IOException, DocumentException {
		
		File file= new File(DEST);
		file.getParentFile().mkdirs();
		new Summary_Controller().createPdf(DEST);	
	}
	
	/*With regard to handling file information, having 
	 dealing with files I enter IOException and DocumentException*/
	
	public void createPdf(String dest) throws IOException, DocumentException {
		URL url = new URL("http://127.0.0.1:5000/detecting");
		HttpURLConnection conn =(HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output;
		while((output = br.readLine())!= null) {
			trustworthiness= output.substring(0, 5);
			accuracy= output.substring(5, 22);
		}
	
		/*I set the size of the pdf ad A4*/
		Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(DEST));
        document.open();
       
        
        PdfContentByte canvas = writer.getDirectContentUnder();
        Image image = Image.getInstance(WALLPAPER);
        image.scaleAbsolute(PageSize.A4);
        image.setAbsolutePosition(0, 0);
        canvas.addImage(image);
     
        /*Here I insert the logo*/
        
        
        Image img=Image.getInstance(LOGODIPARTIMENTO);
        img.setAbsolutePosition(10, 10);
        img.scaleAbsolute(100, 100);
        
        document.add(img);
       
        /*Managing the text to be displayed in the pdf*/
        
        /*Management of the first paragraph*/
        
        document.add( Chunk.NEWLINE );
        
        Font font1 = new Font(FontFamily.TIMES_ROMAN, 35.0f, Font.BOLD, BaseColor.BLACK);
        
        Chunk c1 = new Chunk("MAG News Verification report!", font1);
        
        /*c1.setBackground(BaseColor.BLUE);*/
        
        Paragraph para1 = new Paragraph(c1);
        
        para1.setAlignment(ALIGN_CENTER);
        
        document.add(para1);
        
        document.add( Chunk.NEWLINE );
        
        /*MAG logo insertion management*/
    
        Image imgs=Image.getInstance(LOGOMAG);
        imgs.setAbsolutePosition(75, 350);/*width-height*/
        imgs.scaleAbsolute(400, 400);
        
        document.add(imgs);
             
        /*Management of the second paragraph*/
        
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
       
        Font font2 = new Font(FontFamily.TIMES_ROMAN, 30.0f, Font.BOLD, BaseColor.BLACK);
        
        Chunk c2 = new Chunk("The page analyzed is: " + trustworthiness, font2);
        
        /*c2.setBackground(BaseColor.BLUE);*/
        
        Paragraph para2 = new Paragraph(c2);
        
        para2.setAlignment(ALIGN_CENTER);
        
        document.add(para2);
        
        /*Management of the third paragraph*/
        
        document.add( Chunk.NEWLINE );
        
        Font font3 = new Font(FontFamily.TIMES_ROMAN, 30.0f, Font.BOLD, BaseColor.BLACK);
        
        Chunk c3 = new Chunk("The percentage found is: " + accuracy, font3);
        
        /*c3.setBackground(BaseColor.BLUE);*/
        
        Paragraph para3 = new Paragraph(c3);
        
        para3.setAlignment(ALIGN_CENTER);
        
        document.add(para3);
        
        
        
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );

        
        
        /*Management of the fourth paragraph*/
        
        Font font4 = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.BOLD, BaseColor.BLACK);
        
        Chunk c4 = new Chunk("Università degli studi di Salerno, Via Papa Giovanni Paolo II, 132, 84084 Fisciano SA" , font4);
        
        
        Paragraph para4 = new Paragraph(c4);
        
        para4.setAlignment(ALIGN_CENTER);
        
        document.add(para4);
        
        
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
        
		
    }
	
}


