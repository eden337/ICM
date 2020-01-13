package server;

import common.ocsf.server.ConnectionToClient;
import javafx.collections.FXCollections;
import server.controllers.ServerController;

import java.util.Timer;
import java.util.TimerTask;

public class ICM_Scheduler {

    public static void scheduler(){
        Timer timer = new Timer(); // Instantiate Timer Object
        TimerTask checkConnectedUsers_Task = new checkConnectedUsers();
        timer.schedule(checkConnectedUsers_Task,0, 30000); // each 30 seconds
        System.out.println("ICM_Scheduler - Started");

    }


    static class checkConnectedUsers extends TimerTask {
        @Override
        public synchronized void run() {
            System.out.println(EchoServer.connectedUsers);
            if(EchoServer.connectedUsers == null) return;
            try {
                EchoServer.connectedUsers.forEach((k, v) -> {
                    if (!v.isAlive()) {
                        System.out.println("remove " + k);
                        EchoServer.connectedUsers.remove(k);
                    }
                });
                ServerController.instance.usersList.setItems(FXCollections.observableArrayList(EchoServer.connectedUsers.keySet()));
            }
            catch (Exception e){
                System.out.println("!! Timer Exception !!");
            }
        }
    } // checkConnectedUsers class
}//ICM_Scheduler

