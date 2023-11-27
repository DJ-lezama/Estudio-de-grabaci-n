import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogInGUI {

    connection dbHandler = new connection();
    JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LogInGUI().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setSize(330, 150);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("ID de Encargado:");
        userLabel.setBounds(10, 20, 120, 25);
        panel.add(userLabel);

        JTextField userTextField = new JTextField(20);
        userTextField.setBounds(140, 20, 150, 25);
        panel.add(userTextField);

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setBounds(10, 50, 120, 25);
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(140, 50, 150, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.setBounds(100, 80, 120, 25);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userTextField.getText();
                String password = new String(passwordField.getPassword());

                int authorizationLevel = dbHandler.logIn(userId, password);

                if (authorizationLevel != -1) {
                    JOptionPane.showMessageDialog(panel, "Inicio de sesión exitoso");
                    frame.dispose();
                    // Open the main GUI and pass the authorization level
                    GUI newMainGUI = new GUI(authorizationLevel);
                    newMainGUI.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(panel, "Fallo en el incio de sesión");
                    userTextField.setText("");
                    passwordField.setText("");
                }
            }
        });


    }
}
