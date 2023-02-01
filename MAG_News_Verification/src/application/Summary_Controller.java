package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Phrase;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;


public class Summary_Controller {
	
	/*The Summary_Controller class is intended to manage the process of saving information obtained from the MAG platform.*/

	@FXML
	
    private ProgressBar progress;
	
	
	/*Destination Path to save the pdf*/
	
	public static final String DEST="C:/Users/angel/git/MAG_News_Verification/MAG_News_Verification/reportSummary.pdf";
	
	/*Destination Path to get the wallpaper image*/
	
	public static final String IMAGE="C:\\Users\\angel\\git\\MAG_News_Verification\\MAG_News_Verification\\src\\images\\wallpaper1.png";
	

	
	public void reportPage(){
					
			/*Managing the entry of a report*/
		
			ChoiceDialog<String> dialog= new ChoiceDialog<>("Phishing","Malware","Other types of hazards");
			dialog.setTitle("Report page");
			dialog.setHeaderText("Be careful, choose well");
			dialog.setContentText("Choose the type of signaling");
			
			Optional<String> result=dialog.showAndWait();
			
			if(result.isPresent()) {
				System.out.println("Your choice: "+result.get());
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
		
		/*I set the size of the pdf ad A4*/
		Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
       
        
        PdfContentByte canvas = writer.getDirectContentUnder();
        Image image = Image.getInstance(IMAGE);
        image.scaleAbsolute(PageSize.A4);
        image.setAbsolutePosition(0, 0);
        canvas.addImage(image);
     
        /*Here I insert the logo*/
        
        /*String imageFile = "/MAG_News_Verification/src/images/MAG_News_Verification_logo.png"; 
        ImageData data = ImageDataFactory.create(imageFile);
        
        Image img = new Image(data); 
        
        document.add(img);
        */
        
        
        /*Here I will create the table*/
        PdfPTable table = new PdfPTable(8);
        PdfPCell cell;
        
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
       
        
        table = new PdfPTable(16);
        
        for(int aw = 0; aw < 16; aw++){
        	
        	cell = new PdfPCell(new Phrase("MAG News Verification Report!", font));
            cell = new PdfPCell(new Phrase("The page analyzed produced the following result", font));
            cell.setBackgroundColor(BaseColor.BLUE);
            cell.setBorder(Rectangle.OUT_BOTTOM);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
        }
        
        document.add(table);
        document.close();
        
        /*Once the pdf is saved you will see the message that it was saved*/
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setContentText("We would like to inform you that the file was successfully saved");
        alert.showAndWait();
        
		
    }
	
}


