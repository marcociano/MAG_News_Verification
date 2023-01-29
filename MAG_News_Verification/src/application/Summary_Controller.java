package application;

import java.util.Optional;

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

}
