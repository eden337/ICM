package client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppController {

	protected Alert alert = new Alert(null);

	/**
	 *
	 * @param at
	 * @param title
	 * @param content
	 * @param header
	 */
	protected void showAlert(AlertType at, String title, String content, String header){
		Platform.runLater(() -> {
			alert.setAlertType(at);
			alert.setTitle(title);
			alert.setContentText(content);
			alert.setHeaderText(header);
			alert.show();
		});
	}

 /*
 * @param window : Current window
 * @param fxml : Full path
 */
	protected static void changeWindow(Window window,String fxml) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				window.hide();
				try {
					Pane  root = FXMLLoader.load(getClass().getResource(fxml));
					Scene scene=new Scene(root);
					Stage primaryStage= new Stage();
					primaryStage.setScene(scene);
					primaryStage.show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	} // changeWindow

	/**
	 *
	 * @return : String the time and  data zone
	 */
	protected String getToday() {
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		String date_time;
		date_time = formatter.format(date);
		return date_time;
	}

	/**
	 * Load FXML page file as a child of main window. Loaded page resolution
	 * should be 1000*710.
	 *
	 * @param fxmlName : String (Without file type name)
	 */
	protected void loadPage(String fxmlName) {
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/client/views/"+fxmlName + ".fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainController.instance.getBorderPane().setCenter(root);
	}
}
