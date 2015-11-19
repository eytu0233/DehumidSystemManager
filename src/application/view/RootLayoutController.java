package application.view;

import java.io.IOException;

import application.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootLayoutController {

	@FXML
	private MenuItem updateSystemMenuItem;
	
	private MainApp mainApp;
	
	public void setUpUpdateSyetemMenuItem(){
		updateSystemMenuItem.setDisable(false);
	}

	@FXML
	private void showEditPropertiesDialog() {
		try {
			Stage dialogStage = new Stage();
			dialogStage.initOwner(mainApp.getPrimaryStage());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setTitle("設定檔編輯");

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/EditConfigDialog.fxml"));
			AnchorPane editConfigDialog;
			editConfigDialog = (AnchorPane) loader.load();
			
			// Give the controller access to the main app.
			EditConfigDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			// Show the scene containing the root layout.
			Scene scene = new Scene(editConfigDialog);
			dialogStage.setScene(scene);
			dialogStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	private void showUpdateSystemFileDialog() {
		try {

			Stage dialogStage = new Stage();
			dialogStage.initOwner(mainApp.getPrimaryStage());
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.setTitle("更新系統檔案");

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/UpdateSystemFileDialog.fxml"));
			AnchorPane updateSystemFileDialog;
			updateSystemFileDialog = (AnchorPane) loader.load();
			
			// Give the controller access to the main app.
			UpdateSystemFileController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(mainApp);

			// Show the scene containing the root layout.
			Scene scene = new Scene(updateSystemFileDialog);
			dialogStage.setScene(scene);
			dialogStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	private void closeRootLayout(){
		mainApp.getPrimaryStage().close();
		System.exit(0);
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
