import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CancelacionesGUI extends JFrame {
    connection dbHandler = new connection();
    JLabel selectionLabel, titleLabel;
    JButton cancelButton;
    JTable displayTable;
    JScrollPane scrollPane;
    public CancelacionesGUI(){
        //Frame set up
        Container container = getContentPane();
        container.setLayout(new FlowLayout());

        //Swing elements set up
        titleLabel = new JLabel("Cancelación de sesiones");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        container.add(titleLabel);

        //Table Model setup
        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable (int row, int column){
                return false; // Non-editable cells
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
        container.add(scrollPane);

        selectionLabel = new JLabel("");
        container.add(selectionLabel);

        cancelButton = new JButton("Cancelar sesión");
        cancelButton.addActionListener(event -> deleteSelectedSession());
        container.add(cancelButton); // Moved this line up

        displayTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) { //handle only the final selection event
                int selectedSessionRow = displayTable.getSelectedRow();
                if (selectedSessionRow != -1){
                    selectionLabel.setText("¿Desea eliminar la sesión seleccionada?");
                    container.add(cancelButton);
                } else {
                    selectionLabel.setText(" ");
                }
            }
        });

        // Now load the default session data
        loadDefaultSessionData();

        setVisible(true);
        setSize(500, 600);
    }


    private void deleteSelectedSession(){
        int selectedRow = displayTable.getSelectedRow();
        if (selectedRow != -1){
            String sessionId = displayTable.getModel().getValueAt(selectedRow, 0).toString();
            // Execute delete query
            dbHandler.deleteSession(sessionId);

            // Refresh the table
            refreshTable();
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

    private void loadDefaultSessionData() {
        String sqlQuery = "SELECT * FROM Sesiones ORDER BY fecha DESC, hora DESC;";
        List<String[]> results = dbHandler.getSessionData(sqlQuery, new Object[]{});
        DefaultTableModel model = (DefaultTableModel) displayTable.getModel();
        model.setRowCount(0);
        for (String[] row : results) {
            model.addRow(row);
        }
    }

}
