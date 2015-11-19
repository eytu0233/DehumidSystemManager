package application.view;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.util.PropertiesManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditConfigDialogController {
	
	private final ObservableList<String> ipData = FXCollections.observableArrayList();

	@FXML
	private TextField ID;
	@FXML
	private PasswordField PW;
	@FXML
	private TextField addIP;
	@FXML
	private ListView<String> IPList;
	@FXML
	private Button addIPBtn;
	@FXML
	private Button confirmBtn;
	@FXML
	private Button cancelBtn;
	

	private PropertiesManager propertiesManager = PropertiesManager.getInstance();
	private Stage dialogStage;	
	private TreeSet<String> addIPSet = new TreeSet<String>(), removeIPSet = new TreeSet<String>();
	private boolean confirmClicked = false;

	public boolean isConfirmClicked() {
		return confirmClicked;
	}

	public EditConfigDialogController() {

	}

	@FXML
	private void initialize() {
		
		ID.setText(propertiesManager.getID());
		PW.setText(propertiesManager.getPW());

		ipData.addAll(propertiesManager.getIPs());
		IPList.setItems(ipData);

	}
	
	@FXML
	private void addIP(){
		String IP = addIP.getText();
		Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Matcher matcher = pattern.matcher(IP);
		
		if(matcher.matches() && !ipData.contains(IP)){
			if(removeIPSet.contains(IP)){
				removeIPSet.remove(IP);
			}
			ipData.add(IP);
			addIPSet.add(IP);
		}
	}
	
	@FXML
	private void removeIP(){
		
		String IP = IPList.getSelectionModel().getSelectedItem();
		
		if(IP != null && ipData.contains(IP)){
			ipData.remove(IP);
			if(addIPSet.contains(IP)){
				addIPSet.remove(IP);
			}else{				
				removeIPSet.add(IP);
			}
		}		
		
	}
	
	@FXML
	private void confirm(){
		
		if(!ID.getText().isEmpty()){
			propertiesManager.updateID(ID.getText());
		}
		
		if(!PW.getText().isEmpty()){
			propertiesManager.updatePW(PW.getText());
		}
		
		for(String addIP : addIPSet){
			propertiesManager.addIP(addIP);
		}
		
		for(String rmIP : removeIPSet){
			propertiesManager.removeIP(rmIP);
		}
		
		confirmClicked = true;
		
		dialogStage.close();
		
	}
	
	@FXML
	private void cancel(){
		
		dialogStage.close();
		
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
}
