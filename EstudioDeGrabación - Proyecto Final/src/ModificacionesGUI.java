import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModificacionesGUI extends JFrame {
    connection dbHandler = new connection();
    JLabel titleLabel;
    JButton modifyButton;
    JTable displayTable;
    JScrollPane scrollPane;
    JPanel displayPanel, fieldsDisplay, mainPanel;
    JSpinner timeSpinner, durationSpinner, dateSpinner;
    JComboBox<String> generosComboBox;
    JTextField roomIdTextField;

    public ModificacionesGUI(){
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        createDisplayPanel();
        loadDefaultSessionData();
        createFieldsDisplay();

        //create a splitter pane
        JSplitPane splitPaneV = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, displayPanel, fieldsDisplay);
        mainPanel.add(splitPaneV);
        splitPaneV.setOneTouchExpandable(true);

        splitPaneV.setDividerLocation(500);
        add(mainPanel);

        setVisible(true);
        setSize(800, 600);
    }

    public void createDisplayPanel(){
        displayPanel = new JPanel();
        displayPanel.setLayout(new FlowLayout());
        titleLabel = new JLabel("Modificar cita");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        displayPanel.add(titleLabel);

        //Table Model
        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable (int row, int column){
                //method to make cells non-editable
                return false;
            }
        };

        tableModel.addColumn("ID");
        tableModel.addColumn("Género musical");
        tableModel.addColumn("Fecha");
        tableModel.addColumn("Hora");
        tableModel.addColumn("Duración");
        tableModel.addColumn("ID de Sala");
        tableModel.addColumn("Costo total");
        displayTable = new JTable(tableModel);
        scrollPane = new JScrollPane(displayTable);
        displayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        displayPanel.add(scrollPane);
        String displayQry = "SELECT * FROM Sesiones";
        ArrayList<Object> parameters = new ArrayList<>();

        //Call dbHandler to get the data from the query
        List<String[]> results = dbHandler.getSessionData(displayQry, parameters.toArray());
        //Update the table model with the results
        DefaultTableModel model = (DefaultTableModel) displayTable.getModel();
        model.setRowCount(0);
        for (String[] row: results){
            model.addRow(row);
        }

        displayTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) { //handle only the final selection event
                int selectedSessionRow = displayTable.getSelectedRow();
                if (selectedSessionRow != -1){
                    // Assuming column order: ID, Genre, Date, Time, Duration, Room ID, Total Cost
                    String genre = (String) displayTable.getValueAt(selectedSessionRow, 1);
                    String dateString = (String) displayTable.getValueAt(selectedSessionRow, 2);
                    String timeString = (String) displayTable.getValueAt(selectedSessionRow, 3);
                    String durationString = (String) displayTable.getValueAt(selectedSessionRow, 4);
                    String roomId = (String) displayTable.getValueAt(selectedSessionRow, 5);

                    generosComboBox.setSelectedItem(genre);

                    // Parse date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    try {
                        Date date = dateFormat.parse(dateString);
                        Date time = timeFormat.parse(timeString);
                        dateSpinner.setValue(date);
                        timeSpinner.setValue(time);
                    } catch (ParseException e) {
                        e.printStackTrace(); // Handle parse exception
                    }

                    // Parse duration
                    try {
                        int duration = Integer.parseInt(durationString);
                        durationSpinner.setValue(duration);
                    } catch (NumberFormatException e) {
                        e.printStackTrace(); // Handle number format exception
                    }

                    roomIdTextField.setText(roomId);
                }
            }
        });



    }

    public void createFieldsDisplay(){
        fieldsDisplay = new JPanel();
        GroupLayout layout = new GroupLayout(fieldsDisplay);
        fieldsDisplay.setLayout(layout);

        JLabel genreLabel = new JLabel("Género musical:");

        String[] generos = {"Rock", "Jazz", "Pop", "Clásica", "R&B", "Hip hop"};
        generosComboBox = new JComboBox<>(generos);

        JLabel dateLabel = new JLabel("Fecha:");

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);

        JLabel timeLabel = new JLabel("Hora del sesion:");

        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);

        JLabel durationLabel = new JLabel("Duración (hrs):");

        SpinnerModel durationModel = new SpinnerNumberModel(1, 1, 5, 1);
        durationSpinner = new JSpinner(durationModel);

        JLabel roomIdLabel = new JLabel("ID de sala:");

        roomIdTextField = new JTextField(10);

        modifyButton = new JButton("Modificar sesión");
        fieldsDisplay.add(modifyButton);


        // Horizontal group
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(genreLabel)
                                        .addComponent(dateLabel)
                                        .addComponent(timeLabel)
                                        .addComponent(durationLabel)
                                        .addComponent(roomIdLabel))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(generosComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(dateSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(timeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(durationSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(roomIdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addComponent(modifyButton)
                );

// Vertical group
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(genreLabel)
                        .addComponent(generosComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(dateLabel)
                        .addComponent(dateSpinner))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(timeLabel)
                        .addComponent(timeSpinner))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(durationLabel)
                        .addComponent(durationSpinner))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(roomIdLabel)
                        .addComponent(roomIdTextField))
                .addComponent(modifyButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE); // To ensure there is some space between the button and the table

        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);

        modifyButton.addActionListener(event -> modifySelectedSession());
    }

    private void modifySelectedSession(){
        int selectedRow = displayTable.getSelectedRow();
        if (selectedRow != -1){
            String sessionId = displayTable.getModel().getValueAt(selectedRow, 0).toString();
            String genre = generosComboBox.getSelectedItem().toString();
            Date date = (Date) dateSpinner.getValue();
            Date time = (Date) timeSpinner.getValue();
            int duration = (Integer) durationSpinner.getValue();
            String roomId = roomIdTextField.getText();

            // Format date and time to string
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            String formattedTime = new SimpleDateFormat("HH:mm:ss").format(time);

            dbHandler.updateSession(sessionId, genre, formattedDate, formattedTime, duration, roomId);

            // Refresh the table
            refreshTable();
        }
    }

    private void loadDefaultSessionData() {
        String sqlQuery = "SELECT * FROM Sesiones ORDER BY fecha DESC, hora DESC;";
        List<String[]> results = dbHandler.getSessionData(sqlQuery, new Object[]{});
        DefaultTableModel model = (DefaultTableModel) displayTable.getModel();
        model.setRowCount(0);
        for (String[] row : results) {
            model.addRow(row);
        }
    }


    private void refreshTable(){
        String displayQuery = "SELECT * FROM Sesiones";
        ArrayList<Object> parameters = new ArrayList<>();
        List<String[]> results = dbHandler.getSessionData(displayQuery, parameters.toArray());

        DefaultTableModel model = (DefaultTableModel) displayTable.getModel();
        model.setRowCount(0);
        for (String[] row : results) {
            model.addRow(row);
        }
    }

}

