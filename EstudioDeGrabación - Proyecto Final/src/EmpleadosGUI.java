import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

public class EmpleadosGUI {
    connection dbHandler = new connection();
    private JLabel title;
    private JTextField iduserTextField, sueldoTextField;
    private JTextField nombreTextField;
    private JTextField apellidoTextField;
    private JPasswordField passwordField;
    private JComboBox<String> autorizacionCombo, turnoCombo;
    private JTable clientsDisplayTable = new JTable();
    private JScrollPane scrollPane;

    public void EmpleadosGUI() {
        // Panel set up
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setSize(435, 600);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        //"Layout" set up
        panel.setLayout(null);

        title = new JLabel("Visualizar y agregar empleados");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 5, 300,30);
        panel.add(title);

        // Swing elements set up
        JLabel iduserLabel = new JLabel("IdEmpleado:");
        iduserLabel.setBounds(10, 40, 80, 25);
        panel.add(iduserLabel);

        iduserTextField = new JTextField(20);
        // Auto generate the client's ID
        iduserTextField.setText(String.valueOf(new Random().nextInt(10000)));
        iduserTextField.setBounds(100, 40, 165, 25);
        panel.add(iduserTextField);

        JLabel nombreLabel = new JLabel("Nombre:");
        nombreLabel.setBounds(10, 70, 80, 25);
        panel.add(nombreLabel);

        nombreTextField = new JTextField(20);
        nombreTextField.setBounds(100, 70, 165, 25);
        panel.add(nombreTextField);

        JLabel apellidoLabel = new JLabel("Apellido:");
        apellidoLabel.setBounds(10, 100, 80, 25);
        panel.add(apellidoLabel);

        apellidoTextField = new JTextField(20);
        apellidoTextField.setBounds(100, 100, 165, 25);
        panel.add(apellidoTextField);

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setBounds(10, 130, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(100, 130, 165, 25);
        panel.add(passwordField);

        JLabel autorizacionLabel = new JLabel("Autorización:");
        autorizacionLabel.setBounds(10, 160, 80, 25);
        panel.add(autorizacionLabel);

        String[] autorizacionOptions = {"0", "1"};
        autorizacionCombo = new JComboBox<>(autorizacionOptions);
        autorizacionCombo.setBounds(100, 160, 165, 25);
        panel.add(autorizacionCombo);

        JLabel sueldoLabel = new JLabel("Sueldo:");
        sueldoLabel.setBounds(10, 190, 80, 25);
        panel.add(sueldoLabel);

        sueldoTextField = new JTextField(20);
        sueldoTextField.setBounds(100, 190, 165, 25);
        panel.add(sueldoTextField);

        JLabel turnoLabel = new JLabel("Turno:");
        turnoLabel.setBounds(10, 220, 80, 25);
        panel.add(turnoLabel);

        String[] turnoOptions = {"Vespertino", "Matutino"};
        turnoCombo = new JComboBox<>(turnoOptions);
        turnoCombo.setBounds(100, 220, 165, 25);
        panel.add(turnoCombo);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(100, 255, 80, 25);
        panel.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idEmpleado = iduserTextField.getText();
                String nombreEmpleado = nombreTextField.getText();
                String apellidoEmpleado = apellidoTextField.getText();
                String password = new String(passwordField.getPassword());
                String autorizacion = (String) autorizacionCombo.getSelectedItem();
                String sueldo = sueldoTextField.getText();
                String turno = (String) turnoCombo.getSelectedItem();

                dbHandler.addNewEmpleado(idEmpleado, nombreEmpleado, apellidoEmpleado, password, autorizacion, sueldo, turno);
                refreshEmpleadoTable();
                //Clear text fields
                iduserTextField.setText("");
                nombreTextField.setText("");
                apellidoTextField.setText("");
                passwordField.setText("");
                autorizacionCombo.setSelectedIndex(0);
                sueldoTextField.setText("");
                turnoCombo.setSelectedIndex(0);
            }
        });

        // Table Model setup
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells
            }
        };

        tableModel.addColumn("ID");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Apellido");
        tableModel.addColumn("Autorización");
        tableModel.addColumn("Sueldo");
        tableModel.addColumn("Turno");

        clientsDisplayTable = new JTable(tableModel);
        scrollPane = new JScrollPane(clientsDisplayTable);
        clientsDisplayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setBounds(10, 290, 400, 300);
        panel.add(scrollPane);

        loadDefaultEmpleadoData();
    }

    private void loadDefaultEmpleadoData() {
        String sqlQuery = "SELECT * FROM Empleado;";
        List<String[]> results = dbHandler.getEmpleadosData(sqlQuery, new Object[]{});
        DefaultTableModel model = (DefaultTableModel) clientsDisplayTable.getModel();
        model.setRowCount(0);
        for (String[] row : results) {
            model.addRow(row);
        }
    }

    private void refreshEmpleadoTable() {
        String displayQuery = "SELECT * FROM Empleado";
        List<String[]> results = dbHandler.getEmpleadosData(displayQuery, new Object[]{});
        DefaultTableModel model = (DefaultTableModel) clientsDisplayTable.getModel();
        model.setRowCount(0);
        for (String[] row : results) {
            model.addRow(row);
        }
    }
}