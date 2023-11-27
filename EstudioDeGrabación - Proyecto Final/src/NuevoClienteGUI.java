import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

public class NuevoClienteGUI {
    connection dbHandler = new connection();
    private JLabel title;
    private JTextField iduserTextField;
    private JTextField nombreTextField;
    private JTextField apellidoTextField;
    JTable clientsDisplayTable = new JTable();
    JScrollPane scrollPane;

    public void createGUI() {
        //Panel set up
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

        title = new JLabel("Visualizar y agregar clientes");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 5, 300,30);
        panel.add(title);

        //Swing elements set up
        JLabel iduserLabel = new JLabel("IdCliente:");
        iduserLabel.setBounds(10, 40, 80, 25);
        panel.add(iduserLabel);

        iduserTextField = new JTextField(20);
        // Auto generate the client's ID
        iduserTextField.setText(String.valueOf(new Random().nextInt(1000)));
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

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(100, 130, 80, 25);
        panel.add(submitButton);


        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idCliente = iduserTextField.getText();
                String nombreCliente = nombreTextField.getText();
                String apellidoCliente = apellidoTextField.getText();
                dbHandler.addNewClient(idCliente, nombreCliente, apellidoCliente);
                refreshClientTable();
                //Clear text fields
                iduserTextField.setText("");
                nombreTextField.setText("");
                apellidoTextField.setText("");
            }
        });

        //Table Model setup
        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable (int row, int column){
                return false; // Non-editable cells
            }
        };

        tableModel.addColumn("ID");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Apellido");

        clientsDisplayTable = new JTable(tableModel);
        scrollPane = new JScrollPane(clientsDisplayTable);
        clientsDisplayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setBounds(10, 170, 400, 500);
        panel.add(scrollPane);

        loadDefaultClientData();
    }
    private void loadDefaultClientData() {
        String sqlQuery = "SELECT * FROM Clientes;";
        List<String[]> results = dbHandler.getClientsData(sqlQuery, new Object[]{});
        DefaultTableModel model = (DefaultTableModel) clientsDisplayTable.getModel();
        model.setRowCount(0);
        for (String[] row : results) {
            model.addRow(row);
        }
    }

    private void refreshClientTable() {
        String displayQuery = "SELECT * FROM Clientes";
        List<String[]> results = dbHandler.getClientsData(displayQuery, new Object[]{});
        DefaultTableModel model = (DefaultTableModel) clientsDisplayTable.getModel();
        model.setRowCount(0);
        for (String[] row : results) {
            model.addRow(row);
        }
    }


}
