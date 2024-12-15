package dev.bkrk;

import javax.swing.*;
import java.awt.*;

public class WeatherAppHomePage extends JFrame {

    public WeatherAppHomePage() {
        super("Welcome to How About Weather!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        setIconImage(new ImageIcon("src/assets/icon.png").getImage());


        // create a background panel.
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // load image.
                ImageIcon bg = new ImageIcon("src/assets/background.png");
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);

        // title
        JLabel title = new JLabel("How About Weather?");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.decode("#001F3F"));
        title.setBounds(50, 25, 350, 50);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(title);

        // description.
        JLabel description = new JLabel("<html><div style='text-align: center;'>" +
                "You are in the <b>right place</b> to <br> find out the <i>weather forecast!</i></div></html>");
        description.setFont(new Font("Arial", Font.PLAIN, 24));
        description.setForeground(Color.BLACK);
        description.setBounds(50, 400, 350, 70);
        description.setHorizontalAlignment(SwingConstants.CENTER);
        description.setVerticalAlignment(SwingConstants.TOP);
        backgroundPanel.add(description);

        //button.
        JButton startButton = new JButton("Time to Forecast !");
        startButton.setBounds(50, 500, 350, 50);
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBackground(new Color(30, 144, 255));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> {
            new WeatherAppGUI().setVisible(true);
            dispose();
        });
        backgroundPanel.add(startButton);
        add(backgroundPanel);
    }
}