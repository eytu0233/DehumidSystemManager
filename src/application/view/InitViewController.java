package application.view;

import java.io.IOException;

import application.MainApp;
import application.util.PropertiesManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InitViewController {
	
	@FXML
	private Button startBtn;
	@FXML
	private CheckBox checkBox;

	private MainApp mainApp;
	
	public InitViewController() {
		super();
		// TODO Auto-generated constructor stub
	}

	@FXML
	private void initialize() {
		PropertiesManager propertiesManager = PropertiesManager.getInstance();
		if(propertiesManager.getIPs().size() == 0){
			checkBox.setSelected(false);
			checkBox.setDisable(true);
		}
	}
	
	@FXML
	private void startScan(){
		if(checkBox.isSelected()){
			mainApp.showScanProgressView();
		}else{
			try {
				Stage dialogStage = new Stage();
				dialogStage.initOwner(mainApp.getPrimaryStage());
				dialogStage.initModality(Modality.WINDOW_MODAL);
				dialogStage.setTitle("設定檔編輯");

				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainApp.class.getResource("view/EditConfigDialog.fxml"));
				AnchorPane editConfigDialog = (AnchorPane) loader.load();
				
				// Give the controller access to the main app.
				EditConfigDialogController controller = loader.getController();
				controller.setDialogStage(dialogStage);

				// Show the scene containing the root layout.
				dialogStage.setScene(new Scene(editConfigDialog));
				dialogStage.showAndWait();
				
				if(controller.isConfirmClicked()){
					mainApp.showScanProgressView();
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
}
