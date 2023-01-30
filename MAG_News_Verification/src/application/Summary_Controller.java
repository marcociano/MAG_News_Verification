package application;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;

public class Summary_Controller {

	@FXML
    private ProgressBar progress;

	

	public void reportPage(){
					
			ChoiceDialog<String> dialog= new ChoiceDialog<>("Phishing","Malware","Other types of hazards");
			dialog.setTitle("Report page");
			dialog.setHeaderText("Be careful, choose well");
			dialog.setContentText("Choose the type of signaling");
			
			Optional<String> result=dialog.showAndWait();
			
			if(result.isPresent()) {
				System.out.println("Your choice: "+result.get());
			}
		}
	
	public void downloadSummary() {
		try{
			
			Document document = new Document();
			PdfWriter.getInstance(document,new FileOutputStream("C:/Users/angel/git/MAG_News_Verification/MAG_News_Verification/reportSummary.pdf"));
			document.open();
			document.add(new Paragraph("Summary report pdf"));
			document.close();
			
		}catch(Exception e){
			System.out.println(e);
		}
		System.out.println("itext PDF program executed");
	}

}
