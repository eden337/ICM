package client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Start the first screen of the app.
 *
 * @author Idan
 * 
 *
 */
public class BypassedApp extends Application {


	private static String DemoPage = "/client/views/Login.fxml";
	public static Stage stage;
	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;
	public static Client client;
	public static String server_ip = "";
	public static String server_port = "";

	/**
	 * main method
	 */
	public static void main(String[] args) {
	   	App.server_ip = "localhost";
    	App.server_port = "5454";
    	try {
        	App.startClient();

		} catch (Exception e) {
			System.err.println("ByPassed App : Clint Connection Error.");
		}
    	try {
    		launch(args);

		} catch (Exception e) {
			System.err.println("ByPassed App : GUI Launch Error. check 'DemoPage' source");
		}
	}

	public static boolean startClient() {
		String host = "";
		int port = 0; // The port number
			if(server_port == "")
				port = DEFAULT_PORT;
			else
				port = Integer.parseInt(server_port);

			host = server_ip;


		try {
			client = new Client(host, port);
			System.out.println("Client setup connection! " + host + ":" + port);
			return true;
		} catch (IOException exception) {
			System.out.println("Error: Can't setup connection!" + " Terminating client." + exception);
			System.exit(1);
			return false;
		}

	}

	/**
	 *
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Pane root = null;
		stage = primaryStage;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(DemoPage));
			root = loader.load();
			Scene s1 = new Scene(root);
			primaryStage.setScene(s1);
			primaryStage.show();
			primaryStage.setResizable(false);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERR at App.Start");
		}
	}

	
}
