import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class AgendarSesionGUI {

    connection dbHandler = new connection();
    private JLabel title;

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Concert Details Entry");

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setSize(500, 400);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        title = new JLabel("Agendar sesión");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 5, 300,30);
        panel.add(title);


        JLabel idLabel = new JLabel("ID de sesión:");
        idLabel.setBounds(10, 40, 30, 25);
        panel.add(idLabel);

        JTextField idTextField = new JTextField(10);
        idTextField.setBounds(50, 40, 50, 25);
        idTextField.setEditable(false);
        panel.add(idTextField);

        JLabel genreLabel = new JLabel("Género musical:");
        genreLabel.setBounds(110, 40, 150, 25);
        panel.add(genreLabel);

        String[] generos = {"Rock", "Jazz", "Pop", "Clásica", "R&B", "Hip hop"};
        JComboBox<String> generosComboBox = new JComboBox<>(generos);
        generosComboBox.setBounds(260, 40, 100, 25);
        panel.add(generosComboBox);

        JLabel dateLabel = new JLabel("Fecha:");
        dateLabel.setBounds(10, 70, 150, 25);
        panel.add(dateLabel);

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(170, 70, 150, 25);
        panel.add(dateSpinner);

        JLabel timeLabel = new JLabel("Hora del sesion:");
        timeLabel.setBounds(10, 100, 150, 25);
        panel.add(timeLabel);

        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setBounds(170, 100, 80, 25);
        panel.add(timeSpinner);

        JLabel durationLabel = new JLabel("Duración (hrs):");
        durationLabel.setBounds(10, 130, 150, 25);
        panel.add(durationLabel);

        SpinnerModel durationModel = new SpinnerNumberModel(1, 1, 5, 1);
        JSpinner durationSpinner = new JSpinner(durationModel);
        durationSpinner.setBounds(170, 130, 50, 25);
        panel.add(durationSpinner);

        JLabel roomIdLabel = new JLabel("ID de sala:");
        roomIdLabel.setBounds(10, 160, 150, 25);
        panel.add(roomIdLabel);

        JTextField roomIdTextField = new JTextField(10);
        roomIdTextField.setBounds(170, 160, 50, 25);
        panel.add(roomIdTextField);

        JLabel userIdLabel = new JLabel("ID de cliente:");
        userIdLabel.setBounds(10, 190, 150, 25);
        panel.add(userIdLabel);

        JTextField userIdTextField = new JTextField(10);
        userIdTextField.setBounds(170, 190, 50, 25);
        panel.add(userIdTextField);

        JButton submitButton = new JButton("Agendar sesión");
        submitButton.setBounds(100, 220, 180, 25);
        panel.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) generosComboBox.getSelectedItem();
                Date selectedDate = (Date) dateSpinner.getValue();
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

                Date selectedTime = (Date) timeSpinner.getValue();
                String formattedTime = new SimpleDateFormat("HH:mm:ss").format(selectedTime);

                int duration = (int) durationSpinner.getValue();
                String roomId = roomIdTextField.getText();

                String id = generateRandomId();
                idTextField.setText(id);

                String idCliente = userIdTextField.getText();

                dbHandler.insertDataToDatabase(id, seleccion, formattedDate, formattedTime, duration, roomId, idCliente);

            }
        });
    }

    private String generateRandomId() {
        // Generar un ID aleatorio entre 0 y 999
        Random random = new Random();
        int id = random.nextInt(100000);
        return String.valueOf(id);
    }


}