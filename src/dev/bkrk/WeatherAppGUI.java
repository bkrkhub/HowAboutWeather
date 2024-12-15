package dev.bkrk;

import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;

    // Constructor and title statement.
    public WeatherAppGUI() {
        super("How About Weather ?");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(new ImageIcon("src/assets/icon.png").getImage());


        // set the gui size.
        setSize(450,650);

        // load our gui at the center of the screen
        setLocationRelativeTo(null);
        setLayout(null);
        // Users can't resize the gui.
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        //search bar.
        JTextField searchTextField = new JTextField();
        // set the location and size.
        searchTextField.setBounds(15,15,351,45);
        // change the font style.
        searchTextField.setFont(new Font("Dialog", Font.BOLD + Font.ITALIC, 24));
        // Adding KeyListener.
        searchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                // Allow only English characters and spaces (A-Z, a-z, space).
                if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                    e.consume(); // Cancel invalid character
                }
            }
        });
        add(searchTextField);

        //weather img.
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD + Font.ITALIC, 48));
        // center
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.ITALIC, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity2.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        //humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> %100</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.ITALIC, 16));
        add(humidityText);

        //wind speed image
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windsock.png"));
        windSpeedImage.setBounds(225,500,74,74);
        add(windSpeedImage);

        //wind speed text
        JLabel windSpeedText = new JLabel("<html><b>WindSpeed</b> 15km/h</html>");
        windSpeedText.setBounds(310, 500,90,55);
        windSpeedText.setFont(new Font("Dialog", Font.ITALIC, 16));
        add(windSpeedText);

        // search button.
        JButton searchButton = new JButton(loadImageforSatellite("src/assets/sattelite-dish.png"));
        // change the cursor to hand cursor when hovering the button.
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the location input from the user.
                String userInput = searchTextField.getText();

                // Validate the input: check if it's empty or contains only spaces.
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Please enter a valid location name.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // Fetch weather data for the given location.
                weatherData = WeatherApp.getWeatherData(userInput);

                // Check if weather data is null (invalid location or API error).
                if (weatherData == null) {
                    JOptionPane.showMessageDialog(
                            null,
                            "No valid location found or failed to fetch data from the API. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // Update the GUI components with the retrieved weather data.

                // Update weather condition image.
                String weatherCondition = (String) weatherData.get("weather_condition");
                switch (weatherCondition) {
                    case "Clear" :
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                // Update temperature text.
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "°C");

                // Update weather condition description.
                weatherConditionDesc.setText(weatherCondition);

                // Update humidity text.
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // Update wind speed text.
                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>WindSpeed</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);

    }

    private ImageIcon loadImageforSatellite(String resourcePath) {
        try {
            // read the image file from the given path.
            BufferedImage originalImage = ImageIO.read(new File(resourcePath));
            // Resize the image to 40x40 pixels
            Image resizedImage = originalImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            // Create an ImageIcon from the resized image
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Couldn't find resource !");
        return null;
    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            // Read the image file from the given path.
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Couldn't find resource !");
        return null;
    }
}
