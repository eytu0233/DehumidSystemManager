package application.view;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import application.MainApp;
import application.model.RaspberryPi;
import application.util.PropertiesManager;
import application.util.SystemUpdater;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UpdateSystemFileController {

	private static final int N_THREAD = 5;
	@FXML
	private Button confirmBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private TextField filePathText;
	@FXML
	private TextArea promptText;
	@FXML
	private ProgressBar updateProgress;

	private MainApp mainApp;
	private Stage dialogStage;

	private File selectedFile;

	private int updatePi, updatePiSum;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	@FXML
	private void initialize() {
		confirmBtn.setDisable(true);
	}

	@FXML
	private void browser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("jar files (*.jar)", "*.jar"));
		selectedFile = fileChooser.showOpenDialog(dialogStage);

		if (selectedFile != null && selectedFile.exists() && selectedFile.isFile()) {
			filePathText.setText(selectedFile.getAbsolutePath());
			confirmBtn.setDisable(false);
		}

	}

	@FXML
	private void confirm() {

		confirmBtn.setDisable(true);

		Thread updateThread = new Thread(() -> {

			PropertiesManager propertiesManager = PropertiesManager.getInstance();

			ExecutorService executor = Executors.newFixedThreadPool(N_THREAD, new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					// TODO Auto-generated method stub
					Thread t = new Thread(r);
					t.setDaemon(true);
					return t;
				}
			});

			final Object lock = new Object();
			updatePi = 0;

			updatePiSum = mainApp.getPiData().size();

			for (RaspberryPi pi : mainApp.getPiData()) {
				pi.shutdown();
			}

			for (RaspberryPi pi : mainApp.getPiData()) {

				promptText.setText(promptText.getText() + pi.getIP() + " start system update...\n");

				executor.execute(new FutureTask<Boolean>(new SystemUpdater(pi.getIP(), propertiesManager.getID(),
						propertiesManager.getPW(), selectedFile) {

					@Override
					public void succeed() {
						// TODO Auto-generated method stub
						Platform.runLater(() -> {
							synchronized (lock) {
								updatePi++;
								updateProgress.setProgress(updatePi / updatePiSum);
								promptText.setText(promptText.getText() + pi.getIP() + " update success!\n");
							}
						});
					}

				}));
			}

			executor.shutdown();
			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("完成更新");
				alert.setHeaderText("全部更新完成");
				alert.setContentText("全部樹梅派上的除濕機系統更新完成!");
				alert.showAndWait();

				dialogStage.close();
			});
		});
		updateThread.start();
	}

	@FXML
	private void cancel() {

		dialogStage.close();

	}

}
