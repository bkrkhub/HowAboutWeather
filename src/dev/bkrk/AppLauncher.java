package dev.bkrk;

import javax.swing.*;

public class AppLauncher {
        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new WeatherAppHomePage().setVisible(true);
                    /*
                    System.out.println(WeatherApp.getLocationData("Ankara"));
                    System.out.println(WeatherApp.getCurrentTime());
                    */
                }
            });
        }

}
