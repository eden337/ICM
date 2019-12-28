package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 * Start the first screen of the app.
 *
 * @author Idan
 *
 */
public class BypassedApp_Standalone extends Application {
/**
 * 		NO CONNECTION! - GUI ONLY
 */

	private static String DemoPage = "/client/views/Main.fxml";
	/**
	 * main method
	 */
	public static void main(String[] args) {

    	try {
    		launch(args);

		} catch (Exception e) {
			System.err.println("ByPassed App : GUI Launch Error. check 'DemoPage' source");
		}
	}

	/**
	 *
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Pane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(DemoPage));
			root = loader.load();
			Scene s1 = new Scene(root);
			//s1.setFill(Paint.valueOf("#264653"));
			primaryStage.setScene(s1);
			primaryStage.show();
			primaryStage.setResizable(false);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERR at App.Start");
		}
	}

}
