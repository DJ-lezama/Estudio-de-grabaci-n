import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Main GUI
public class GUI extends JFrame {
    connection dbHandler = new connection();
    private int authorizationLevel;
    JPanel queriesPanel, optionsPanel, mainPanel;

    JLabel sesionesLabel, diaLabel, mesLabel, yearLabel, horaLabel, nombreClienteLabel, apellidoClienteLabel, IDClienteLabel, duracionLabel, costoLabel, salasLabel, generoMusicalLabel, titleLabel, titleLabel2;
    JTextField costoTotalTF, nombreClienteTF, IDClienteTF, apellidoClienteTF;
    JButton searchButton, cancelButton, modifyButton, newClientButton, newSessionButton, empleadosButton;
    JTable queryResultsTable;
    JScrollPane scrollPane;

    public GUI(int authorizationLevel){
        this.authorizationLevel = authorizationLevel;
        //mainPanel set up
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        //Panel creation
        CreateQueriesPanel();
        createOptionsPanel();

        loadDefaultSessionData();

        //create a splitter pane
        JSplitPane splitPaneV = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, queriesPanel, optionsPanel);
        mainPanel.add(splitPaneV, BorderLayout.EAST);
        splitPaneV.setOneTouchExpandable(true);

        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700); // Set the size of the frame
        setVisible(true);
    }
    public void CreateQueriesPanel(){
        //Panel set up
        queriesPanel = new JPanel();
        GroupLayout layout = new GroupLayout(queriesPanel);
        queriesPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        //Swing elements set up
        titleLabel = new JLabel("Sistema de Consultas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        diaLabel = new JLabel("Día: ");
        JComboBox<Object> dayComboBox = new JComboBox<>();
        dayComboBox.addItem("Cualquiera");
        for (int i = 1; i <= 31; i++) {
            dayComboBox.addItem(i);
        }

        mesLabel = new JLabel("Mes: ");
        JComboBox<Object> monthComboBox = new JComboBox<>();
        monthComboBox.addItem("Cualquiera");
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(i);
        }

        horaLabel = new JLabel("Hora: ");
        JComboBox<Object> hoursComboBox = new JComboBox<>();
        hoursComboBox.addItem("Cualquiera");
        for (int i = 8; i <= 22; i++) {
            hoursComboBox.addItem(i);
        }

        yearLabel = new JLabel("Año: ");
        JComboBox<Object> yearsComboBox = new JComboBox<>();
        yearsComboBox.addItem("Cualquiera");
        for (int i = 2023; i >= 2010; i--) {
            yearsComboBox.addItem(i);
        }

        salasLabel = new JLabel("Sala: ");
        JComboBox<Object> salasComboBox = new JComboBox<>();
        salasComboBox.addItem("Cualquiera");
        for (int i = 1; i <= 7; i++) {
            salasComboBox.addItem(i);
        }

        duracionLabel = new JLabel("Duración de la sesión (hrs): ");
        JComboBox<Object> duracionComboBox = new JComboBox<>();
        duracionComboBox.addItem("Cualquiera");
        for (int i = 1; i <= 6; i++) {
            duracionComboBox.addItem(i);
        }

        generoMusicalLabel = new JLabel("Género musical: ");
        JComboBox<String> generosComboBox = new JComboBox<>();
        generosComboBox.addItem("Cualquiera");
        generosComboBox.addItem("Rock");
        generosComboBox.addItem("Clásica");
        generosComboBox.addItem("Pop");
        generosComboBox.addItem("Jazz");
        generosComboBox.addItem("R&B");
        generosComboBox.addItem("Hip hop");

        costoLabel = new JLabel("Costo de la sesión: ");
        costoTotalTF = new JTextField(20);

        nombreClienteLabel = new JLabel("Nombre del cliente: ");
        nombreClienteTF = new JTextField(20);

        IDClienteLabel = new JLabel("ID de Cliente: ");
        IDClienteTF = new JTextField(20);

        apellidoClienteLabel = new JLabel("Apellido del cliente: ");
        apellidoClienteTF = new JTextField(20);

        sesionesLabel = new JLabel("Sesiones agendadas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        searchButton = new JButton("Buscar y actualizar");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               //Build the SQL query
                StringBuilder sqlQueryBuilder = new StringBuilder("FROM Sesiones s ");
                StringBuilder subqueryBuilder = new StringBuilder();
                //ArrayList to store the parameters for the PreparedStatement
                ArrayList<Object> parameters = new ArrayList<>();

                //Check if the client-related (id, name and last name) fields are being used
                if(!nombreClienteTF.getText().trim().isEmpty() || !IDClienteTF.getText().trim().isEmpty() || !apellidoClienteTF.getText().trim().isEmpty()) {
                    subqueryBuilder.append("INNER JOIN SeisionesPorCliente spc ON s.idSesiones = spc.Sesiones_idSesiones ");
                    subqueryBuilder.append("INNER JOIN Clientes c ON spc.Clientes_idClientes = c.idClientes ");
                }

                //Flag to check for WHERE conditions
                boolean whereAdded = false;

                // Check each input field and append to the query as necessary
                if (!Objects.equals(dayComboBox.getSelectedItem(), "Cualquiera")) {
                    sqlQueryBuilder.append(parameters.isEmpty() ? "WHERE " : "AND ").append("DAY(s.fecha) = ? ");
                    parameters.add(dayComboBox.getSelectedItem());
                    whereAdded = true;
                }
                if (!Objects.equals(monthComboBox.getSelectedItem(), "Cualquiera")) {
                    sqlQueryBuilder.append(parameters.isEmpty() ? "WHERE " : "AND ").append("MONTH(s.fecha) = ? ");
                    parameters.add(monthComboBox.getSelectedItem());
                    whereAdded = true;
                }
                if (!Objects.equals(yearsComboBox.getSelectedItem(), "Cualquiera")) {
                    sqlQueryBuilder.append(parameters.isEmpty() ? "WHERE " : "AND ").append("YEAR(s.fecha) = ? ");
                    parameters.add(yearsComboBox.getSelectedItem());
                    whereAdded = true;
                }
                if (!Objects.equals(hoursComboBox.getSelectedItem(), "Cualquiera")) {
                    sqlQueryBuilder.append(parameters.isEmpty() ? "WHERE " : "AND ").append("HOUR(s.hora) = ? ");
                    parameters.add(hoursComboBox.getSelectedItem());
                    whereAdded = true;
                }
                if (!Objects.equals(salasComboBox.getSelectedItem(), "Cualquiera")) {
                    sqlQueryBuilder.append(parameters.isEmpty() ? "WHERE " : "AND ").append("s.Sala_idSala = ? ");
                    parameters.add(salasComboBox.getSelectedItem());
                    whereAdded = true;
                }
                if (!Objects.equals(generosComboBox.getSelectedItem(), "Cualquiera")) {
                    sqlQueryBuilder.append(parameters.isEmpty() ? "WHERE " : "AND ").append("s.generoMusical = ? ");
                    System.out.println(salasComboBox.getSelectedItem());
                    parameters.add(generosComboBox.getSelectedItem());
                    whereAdded = true;
                }
                if (!Objects.equals(duracionComboBox.getSelectedItem(), "Cualquiera")) {
                    sqlQueryBuilder.append(parameters.isEmpty() ? "WHERE " : "AND ").append("s.duracion = ? ");
                    parameters.add(duracionComboBox.getSelectedItem());
                    whereAdded = true;
                }
                if (!costoTotalTF.getText().trim().isEmpty()) {
                    sqlQueryBuilder.append(parameters.isEmpty() ? "WHERE " : "AND ").append("s.costoTotal = ? ");
                    parameters.add(Float.parseFloat(costoTotalTF.getText().trim()));
                    whereAdded = true;
                }

                // Insert JOIN clauses right after "FROM Sesiones s "
                sqlQueryBuilder.insert(sqlQueryBuilder.indexOf("FROM Sesiones s ") + "FROM Sesiones s ".length(), subqueryBuilder.toString());

                //Add client related conditions
                if (!nombreClienteTF.getText().trim().isEmpty()) {
                    sqlQueryBuilder.append(whereAdded ? "AND " : "WHERE ").append("c.nombre = ? ");
                    parameters.add(nombreClienteTF.getText().trim());
                    whereAdded = true; // Update the flag after adding a condition
                }
                if (!apellidoClienteTF.getText().trim().isEmpty()) {
                    sqlQueryBuilder.append(whereAdded ? "AND " : "WHERE ").append("c.apellidos = ? ");
                    parameters.add(apellidoClienteTF.getText().trim());
                    whereAdded = true; // Update the flag after adding a condition
                }
                if (!IDClienteTF.getText().trim().isEmpty()) {
                    sqlQueryBuilder.append(whereAdded ? "AND " : "WHERE ").append("c.idClientes = ? ");
                    parameters.add(IDClienteTF.getText().trim());
                }


                sqlQueryBuilder.insert(0, "SELECT s.* ");
                //Add part of query that will order the results from most to least recent
                sqlQueryBuilder.append(" ORDER BY s.fecha DESC, s.hora DESC");

                // Convert StringBuilder to String to get the completed query
                String sqlQuery = sqlQueryBuilder.toString();
                System.out.println(sqlQuery);

                //Call dbHandler to get the data from the query
                List<String[]> results = dbHandler.getSessionData(sqlQuery, parameters.toArray());
                //Update the table model with the results
                DefaultTableModel model = (DefaultTableModel) queryResultsTable.getModel();
                model.setRowCount(0);
                for (String[] row: results){
                    model.addRow(row);
                }

            }
        });
        //Table Model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Género musical");
        model.addColumn("Fecha");
        model.addColumn("Hora");
        model.addColumn("Duración");
        model.addColumn("ID de Sala");
        model.addColumn("Costo total");
        queryResultsTable = new JTable(model);
        scrollPane = new JScrollPane(queryResultsTable);

        //Layout set up
        // Horizontal group
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(titleLabel)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(diaLabel)
                                        .addComponent(mesLabel)
                                        .addComponent(horaLabel)
                                        .addComponent(yearLabel)
                                        .addComponent(salasLabel)
                                        .addComponent(duracionLabel)
                                        .addComponent(generoMusicalLabel)
                                        .addComponent(costoLabel)
                                        .addComponent(nombreClienteLabel)
                                        .addComponent(apellidoClienteLabel)
                                        .addComponent(IDClienteLabel))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(dayComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(monthComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(hoursComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(yearsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(salasComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(duracionComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(costoTotalTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(generosComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(nombreClienteTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(apellidoClienteTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(IDClienteTF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(searchButton)))
                        .addComponent(sesionesLabel) //Add "title" to the scrollPane
                        .addComponent(scrollPane)); // Add scrollPane to the horizontal group

// Vertical group
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup()
                .addComponent(titleLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(diaLabel)
                        .addComponent(dayComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(mesLabel)
                        .addComponent(monthComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(horaLabel)
                        .addComponent(hoursComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(yearLabel)
                        .addComponent(yearsComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(salasLabel)
                        .addComponent(salasComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(duracionLabel)
                        .addComponent(duracionComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(generoMusicalLabel)
                        .addComponent(generosComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(costoLabel)
                        .addComponent(costoTotalTF))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nombreClienteLabel)
                        .addComponent(nombreClienteTF))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(apellidoClienteLabel)
                        .addComponent(apellidoClienteTF))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(IDClienteLabel)
                        .addComponent(IDClienteTF))
                .addComponent(searchButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // To ensure there is some space between the button and the table
                .addComponent(sesionesLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE); // Set a preferred height for the scrollPane

        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);
    }

    public void createOptionsPanel(){
        //Panel set up
        optionsPanel = new JPanel();
        GroupLayout layout2 = new GroupLayout(optionsPanel);
        optionsPanel.setLayout(layout2);

        layout2.setAutoCreateGaps(true);
        layout2.setAutoCreateContainerGaps(true);

        //Swing elements set up
        titleLabel2 = new JLabel("Menú de operaciones");
        titleLabel2.setFont(new Font("Arial", Font.BOLD, 16));

        newClientButton = new JButton("Visualizar y agregar clientes");
        newSessionButton = new JButton("Agendar nueva sesión");
        cancelButton = new JButton("Cancelar sesión");
        modifyButton = new JButton("Modificar sesión existente");
        empleadosButton = new JButton("Visualizar y agregar empleados");


        //Layout set up
        // Horizontal group
        GroupLayout.ParallelGroup hGroup = layout2.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(titleLabel2)
                .addComponent(newClientButton)
                .addComponent(newSessionButton)
                .addComponent(cancelButton)
                .addComponent(modifyButton)
                .addComponent(empleadosButton);

        // Vertical group
        GroupLayout.SequentialGroup vGroup = layout2.createSequentialGroup()
                .addComponent(titleLabel2)
                .addComponent(newClientButton)
                .addComponent(newSessionButton)
                .addComponent(cancelButton)
                .addComponent(modifyButton)
                .addComponent(empleadosButton);

        layout2.setHorizontalGroup(hGroup);
        layout2.setVerticalGroup(vGroup);

        //Adding the button handler
        ButtonHandler optionsHandler = new ButtonHandler();
        newClientButton.addActionListener(optionsHandler);
        newSessionButton.addActionListener(optionsHandler);
        cancelButton.addActionListener(optionsHandler);
        modifyButton.addActionListener(optionsHandler);
        empleadosButton.addActionListener(optionsHandler);

        //setting ActionCommands
        newClientButton.setActionCommand("newClient");
        newSessionButton.setActionCommand("newSession");
        cancelButton.setActionCommand("cancel");
        modifyButton.setActionCommand("modify");
        empleadosButton.setActionCommand("empleados");
    }

    private class ButtonHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent event){
            String command = event.getActionCommand();
            switch (command){
                case "newClient":
                    if (authorizationLevel == 1){
                        SwingUtilities.invokeLater(()->{
                            new NuevoClienteGUI().createGUI();
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "No tienes autorización para realizar esta operación");
                    }
                    break;
                case "newSession":
                    if (authorizationLevel == 1){
                        SwingUtilities.invokeLater(() -> {
                            new AgendarSesionGUI().createAndShowGUI();
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "No tienes autorización para realizar esta operación");
                    }
                    break;
                case "cancel":
                    if (authorizationLevel == 1){
                        CancelacionesGUI newCancellation = new CancelacionesGUI();
                        newCancellation.setVisible(true);
                    } else{
                        JOptionPane.showMessageDialog(null, "No tienes autorización para realizar esta operación");
                    }
                    break;
                case "modify":
                    if (authorizationLevel == 1){
                        ModificacionesGUI modify = new ModificacionesGUI();
                        modify.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "No tienes autorización para realizar esta operación");
                    }
                    break;
                case "empleados":
                    if (authorizationLevel == 1){
                        SwingUtilities.invokeLater(() -> {
                            new EmpleadosGUI().EmpleadosGUI();
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "No tienes autorización para realizar esta operación");

                    }
            }
        }
    }

    private void loadDefaultSessionData() {
        String sqlQuery = "SELECT * FROM Sesiones ORDER BY fecha DESC, hora DESC;";
        List<String[]> results = dbHandler.getSessionData(sqlQuery, new Object[]{});
        DefaultTableModel model = (DefaultTableModel) queryResultsTable.getModel();
        model.setRowCount(0);
        for (String[] row : results) {
            model.addRow(row);
        }
    }
}
