package application;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import application.model.RaspberryPi;
import application.view.InitViewController;
import application.view.RaspberryStatusOverviewController;
import application.view.RootLayoutController;
import application.view.ScanProgressViewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private RootLayoutController rootLayoutController;

	private ObservableList<RaspberryPi> piData = FXCollections.observableArrayList();

	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	public MainApp() {

	}

	public void showScanProgressView() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ScanProgressView.fxml"));
			AnchorPane scanProgressView = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(scanProgressView);

			// Give the controller access to the main app.
			ScanProgressViewController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void ShowRaspberryStatusOverview() {

		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RaspberryStatusOverview.fxml"));
			AnchorPane RSO = (AnchorPane) loader.load();

			// Give the controller access to the main app.
			RaspberryStatusOverviewController controller = loader.getController();
			controller.setMainApp(this);

			// Set person overview into the center of root layout.
			rootLayout.setCenter(RSO);

			rootLayoutController.setUpUpdateSyetemMenuItem();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("除濕機系統控制平台");

			initRootLayout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the root layout.
	 */
	private void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Give the controller access to the main app.
			rootLayoutController = loader.getController();
			rootLayoutController.setMainApp(this);

			showInitView();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					System.exit(0);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showInitView() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/InitView.fxml"));
			AnchorPane scanProgressView = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(scanProgressView);

			// Give the controller access to the main app.
			InitViewController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	/**
	 * Returns the data as an observable list of RaspberryPis
	 * 
	 * @return
	 */
	public ObservableList<RaspberryPi> getPiData() {
		return piData;
	}

	public void addPiData(RaspberryPi pi) {
		piData.add(pi);
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
