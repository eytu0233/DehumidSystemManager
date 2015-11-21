package application.view;

import application.MainApp;
import application.model.RaspberryPi;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RaspberryStatusOverviewController {

	@FXML
	private TableView<RaspberryPi> piTable;
	@FXML
	private TableColumn<RaspberryPi, String> ipColumn;
	@FXML
	private TableColumn<RaspberryPi, String> statusColumn;
	@FXML
	private TableColumn<RaspberryPi, String> pidColumn;

	public RaspberryStatusOverviewController() {

	}

	@FXML
	private void initialize() {
		// Initialize the person table with the two columns.
		ipColumn.setCellValueFactory(cellData -> cellData.getValue().ipProperty());
		statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
		pidColumn.setCellValueFactory(cellData -> cellData.getValue().pidProperty());
	}

	@FXML
	private void shutdown() {
		RaspberryPi selectPi = piTable.getSelectionModel().getSelectedItem();
		if (selectPi != null){
			selectPi.setStatusShutdowning();
			selectPi.shutdown();
		}
	}

	@FXML
	private void restart() {
		RaspberryPi selectPi = piTable.getSelectionModel().getSelectedItem();
		if (selectPi != null){
			selectPi.setStatusStarting();
			selectPi.restart();
		}
	}
	
	@FXML
	private void restartAll(){
		for(RaspberryPi pi : piTable.getItems()){
			pi.restart();
		}
	}
	
	@FXML
	private void shutdownAll(){
		for(RaspberryPi pi : piTable.getItems()){
			pi.shutdown();
		}
	}

	public void setMainApp(MainApp mainApp) {
		// Add observable list data to the table
		piTable.setItems(mainApp.getPiData());
	}
}
