package application.view;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import application.MainApp;
import application.model.RaspberryPi;
import application.util.PropertiesManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class ScanProgressViewController {

	private static final String SCANING = "掃描中";
	private static final String[] SCANING_ARR = { SCANING + "   ", SCANING + ".  ", SCANING + ".. ", SCANING + "..." };

	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private Label tipLabel;

	private MainApp mainApp;
	private PropertiesManager properties = PropertiesManager.getInstance();;

	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	public ScanProgressViewController() {
		
	}

	@FXML
	private void initialize() {
		
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

			private int counter = 0;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				double progress = progressIndicator.getProgress();				
				if (progress < 1) {
					final double resultProgress = (mainApp != null)?mainApp.getPiData().size() / properties.getIPs().size() : 0;
					Platform.runLater(() -> {
						progressIndicator.setProgress(resultProgress);
						tipLabel.setText(SCANING_ARR[counter % SCANING_ARR.length]);
						counter++;
					});
				} else {
					Platform.runLater(() -> mainApp.ShowRaspberryStatusOverview());
					scheduledExecutorService.shutdown();
				}
			}

		}, 500, 100, TimeUnit.MILLISECONDS);
		
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		ScheduledExecutorService piScheduledExecutor = Executors.newScheduledThreadPool(10);
		for(String ip : properties.getIPs()){
			mainApp.addPiData(new RaspberryPi(ip, properties.getID(), properties.getPW(), piScheduledExecutor));
		}
	}
}
