package client.controllers;


import client.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class homepageController extends AppController implements Initializable {
    public static homepageController instance;
    @FXML
    private ResourceBundle resources;

    @FXML
    private Text greeting;

    @FXML
    private URL location;

    @FXML
    private Text userRequestsInTreatment;

    @FXML
    private Text userTotalRequest;

    @FXML
    private Text UserNeedToTreat;

    @FXML
    private TableView<?> table5last;

    @FXML
    private Pane paneForIT;

    @FXML
    void gotoMyRequests(MouseEvent event) {
        mainController.instance.goToViewRequest(null);
    }

    @FXML
    void gotoRequestTreatment(MouseEvent event) {
        mainController.instance.goToViewRequest(null);
    }

    @FXML
    void initialize() {
        assert userRequestsInTreatment != null : "fx:id=\"userRequestsInTreatment\" was not injected: check your FXML file 'Homepage.fxml'.";
        assert userTotalRequest != null : "fx:id=\"userTotalRequest\" was not injected: check your FXML file 'Homepage.fxml'.";
        assert UserNeedToTreat != null : "fx:id=\"UserNeedToTreat\" was not injected: check your FXML file 'Homepage.fxml'.";
        assert table5last != null : "fx:id=\"table5last\" was not injected: check your FXML file 'Homepage.fxml'.";

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        paneForIT.setVisible(false);
        setGreeting();

        if(App.user.isEngineer())
            paneForIT.setVisible(true);

    }

    private void setGreeting() {
        String greetingText = "";
        GregorianCalendar time = new GregorianCalendar();
        int hour = time.get(Calendar.HOUR_OF_DAY);
//        int min = time.get(Calendar.MINUTE);
        int day = time.get(Calendar.DAY_OF_MONTH);
//        int month = time.get(Calendar.MONTH) + 1;
//        int year = time.get(Calendar.YEAR);

        if(hour < 4 )
            greetingText = "Good Night";
        else if (hour < 12)
            greetingText = "Good Morning";
        else if (hour < 17 && !(hour == 12))
            greetingText = "Good Afternoon";
        else if (hour == 12)
            greetingText = "Good Noon";
        else
            greetingText = "Good Evening";


        greeting.setText(greetingText + ", " + App.user.getFirstName());
    }

}
