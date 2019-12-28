package server.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import server.AppServer;
import server.EchoServer;
import server.mysqlConnection;

public class ServerController implements Initializable {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextArea textBox;

	@FXML
	private RadioButton radio_mysqlWorkbench;

	@FXML
	private RadioButton radio_remoteSQL;

	@FXML
	private Button onBtn;

	@FXML
	private Button offBtn;

	@FXML
	private Circle server_light;

	@FXML
	private Circle db_light;

	@FXML
	private TextField host_field;

	@FXML
	private TextField scheme_field;

	@FXML
	private TextField password_field;

	@FXML
	private TextField username_field;

	@FXML
	private TextField port_field;

	@FXML
	private ToggleGroup radio_group;

    @FXML
    private TextField server_port_field;


	private DBDetails MySQLWorkbench = new DBDetails("localhost", "requirement", "root", "Aa123456", "");
	private DBDetails RemoteSQL = new DBDetails("remotemysql.com", "yRBHdnFuc9", "yRBHdnFuc9", "QOMMWb8Jo6", "5555");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				appendText(String.valueOf((char) b));
			}
		};
		System.setOut(new PrintStream(out, true));
		printFormFields(RemoteSQL);

	}

	private void appendText(String str) {
		Platform.runLater(() -> textBox.appendText(str));
	}

	@FXML
	void initialize() {

		assert textBox != null : "fx:id=\"textBox\" was not injected: check your FXML file 'serverGUI.fxml'.";
		assert radio_mysqlWorkbench != null : "fx:id=\"radio_mysqlWorkbench\" was not injected: check your FXML file 'serverGUI.fxml'.";
		assert radio_remoteSQL != null : "fx:id=\"radio_remoteSQL\" was not injected: check your FXML file 'serverGUI.fxml'.";
		assert onBtn != null : "fx:id=\"onBtn\" was not injected: check your FXML file 'serverGUI.fxml'.";
		assert offBtn != null : "fx:id=\"offBtn\" was not injected: check your FXML file 'serverGUI.fxml'.";
		assert server_light != null : "fx:id=\"server_light\" was not injected: check your FXML file 'serverGUI.fxml'.";

	}

	private void printFormFields(DBDetails dbDetails) {
		host_field.setText(dbDetails.getDB_HOST());
		scheme_field.setText(dbDetails.getDB_SCHEME());
		password_field.setText(dbDetails.getDB_PASSWORD());
		username_field.setText(dbDetails.getDB_USERNAME());
		port_field.setText(dbDetails.getDB_PORT());
	}

	private void setDBDetailsFromGUI() {
		DBDetails dbDetails = null;
		if (radio_remoteSQL.isSelected())
			dbDetails = RemoteSQL;
		else if (radio_mysqlWorkbench.isSelected())
			dbDetails = MySQLWorkbench;
		dbDetails.setDB_HOST(host_field.getText());
		dbDetails.setDB_PASSWORD(password_field.getText());
		dbDetails.setDB_PORT(port_field.getText());
		dbDetails.setDB_SCHEME(scheme_field.getText());
		dbDetails.setDB_USERNAME(username_field.getText());
	}

	@FXML
	void startServer() {
		// EchoServer.mainServer(args);
		setDBDetailsFromGUI();
		int port = 0; // Port to listen on

		try {
			port = Integer.parseInt(server_port_field.getText()); // Get port from command line
		} catch (Throwable t) {
			port = EchoServer.DEFAULT_PORT; // Set port to 5555
		}


		EchoServer sv = new EchoServer(port);
		AppServer.echoserver = sv;
		EchoServer.portNumber = Integer.parseInt(server_port_field.getText());
		if (radio_remoteSQL.isSelected())
			mysqlConnection.openConnection(RemoteSQL);
		else if (radio_mysqlWorkbench.isSelected())
			mysqlConnection.openConnection(MySQLWorkbench);

		if (!AppServer.echoserver.isListening()) {
			try {
				AppServer.echoserver.listen(); // Start listening for
												// connections
				if (AppServer.echoserver.isListening())
					server_light.setFill(Paint.valueOf("#1ffb1b"));
				else
					server_light.setFill(Paint.valueOf("#ff1717"));

			} catch (IOException e) {
				server_light.setFill(Paint.valueOf("#ff1717"));
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Failed to Start Server");
				alert.setHeaderText("error");
				alert.showAndWait();
			}

		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Server already Connected");
			alert.setHeaderText("Server already Connected"); // ADD MORE
																// INFORAMTION
			alert.show();
		}

		if (AppServer.echoserver.getDBStatus())
			db_light.setFill(Paint.valueOf("#1ffb1b"));
		else
			db_light.setFill(Paint.valueOf("#ff1717"));

	}

	@FXML
	void stopServer() {
		// EchoServer.mainServer(args);

		if (AppServer.echoserver.isListening()) {
			AppServer.echoserver.stopListening();
			; // Start listening for
				// connections
			if (!AppServer.echoserver.isListening())
				server_light.setFill(Paint.valueOf("#1ffb1b"));
			else
				server_light.setFill(Paint.valueOf("#ff1717"));

		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Server already Disconnected");
			alert.setHeaderText("Server already Disconnected"); // ADD MORE
																// INFORAMTION
			alert.show();
		}

		if (AppServer.echoserver.getDBStatus())
			db_light.setFill(Paint.valueOf("#1ffb1b"));
		else
			db_light.setFill(Paint.valueOf("#ff1717"));
	}

	@FXML
	void setFormFields(ActionEvent e) {
		DBDetails dbDetails = null;
		if (radio_remoteSQL.isSelected())
			dbDetails = RemoteSQL;
		else if (radio_mysqlWorkbench.isSelected())
			dbDetails = MySQLWorkbench;
		printFormFields(dbDetails);
	}

}
