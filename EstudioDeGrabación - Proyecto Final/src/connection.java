import java.sql.*;
import javax.swing.*;
import java.util.*;

public class connection {
    static final String URL = "jdbc:mysql://localhost:3306/estudio_de_grabacion?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASSWORD = "klmn5678";

    public static Connection getConection(){
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Established connection");
            //JOptionPane.showMessageDialog(null, "Established connection");

        }
        catch (ClassNotFoundException | SQLException e){
            System.out.println("Connection error");
            //JOptionPane.showMessageDialog(null, "Connection error "+ e);

        }
        return con;
    }

    public List<String[]> getClientsData(String sqlQuery, Object[] params) {
        List<String[]> data = new ArrayList<>();
        try (Connection conn = getConection();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {
            
            // Set the parameters for the PreparedStatement
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] row = new String[3]; //number of columns
                row[0] = rs.getString("idClientes");
                row[1] = rs.getString("nombre");
                row[2] = rs.getString("apellidos");
                data.add(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("ERROR");
        }
        return data;
    }

    public List<String[]> getEmpleadosData(String sqlQuery, Object[] params){
        List<String[]> data = new ArrayList<>();
        try (Connection conn = getConection();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            // Set the parameters for the PreparedStatement
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] row = new String[7]; // number of columns
                row[0] = rs.getString("idEmpleado");
                row[1] = rs.getString("nombre");
                row[2] = rs.getString("apellido");
                row[3] = rs.getString("autorizacion");
                row[4] = rs.getString("sueldo");
                row[5] = rs.getString("turno");
                data.add(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("ERROR");
        }
        return data;
    }


    public List<String[]> getSessionData(String sqlQuery, Object[] params) {
        List<String[]> data = new ArrayList<>();
        try (Connection conn = getConection();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            // Set the parameters for the PreparedStatement
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Assuming you know the number of columns and their order in the ResultSet
                String[] row = new String[] {
                        rs.getString("idSesiones"),
                        rs.getString("generoMusical"),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getString("duracion"),
                        rs.getString("Sala_idSala"),
                        rs.getString("costoTotal")
                };
                data.add(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("ERROR");
        }
        return data;
    }

    public void deleteSession(String sessionID){
        String deleteDependentSQL = "DELETE FROM SeisionesPorCliente WHERE Sesiones_idSesiones = ?";
        String deleteSessionSQL = "DELETE FROM Sesiones WHERE idSesiones = ?";
        try (Connection conn = getConection()) {
            // First, delete dependent rows in SesionesPorCliente
            try (PreparedStatement pstmt = conn.prepareStatement(deleteDependentSQL)) {
                pstmt.setString(1, sessionID);
                pstmt.executeUpdate();
            }

            // Next, delete the row in Sesiones
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSessionSQL)) {
                pstmt.setString(1, sessionID);
                pstmt.executeUpdate();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Fallo al eliminar sesión");
        }
    }

    public void insertDataToDatabase(String id, String genre, String date, String time, int duration, String roomId, String idCliente) {
        String insertQuery = "INSERT INTO Sesiones (idSesiones, generoMusical, fecha, hora, duracion, costoTotal, Sala_idSala) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String spcQuery = "INSERT INTO SeisionesPorCliente (Clientes_idClientes, Sesiones_idSesiones, Sesiones_Sala_idSala) VALUES (?, ?, ?)";
        try (Connection conn = getConection()) {
            // Construct the query to search for the cost per hour
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT costoPorHora FROM Sala WHERE idSala = ");
            queryBuilder.append(roomId);
            queryBuilder.append(";");
            String costSearchQuery = queryBuilder.toString();

            //Convert string roomId and clientId to int
            int intIDSesion = Integer.parseInt(id);
            int roomIdInt = Integer.parseInt(roomId);
            int clientIdInt = Integer.parseInt(idCliente);

            // Execute the search query to get the cost per hour
            try (PreparedStatement costStmt = conn.prepareStatement(costSearchQuery)) {
                ResultSet rs = costStmt.executeQuery();
                float costoPorHora = 0;
                if (rs.next()) {
                    costoPorHora = rs.getFloat("costoPorHora");
                }

                // Calculate the total cost
                float costoTotal = costoPorHora * duration;

                //Insert the data into Sesiones
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    pstmt.setInt(1, intIDSesion);
                    pstmt.setString(2, genre);
                    pstmt.setString(3, date);
                    pstmt.setString(4, time);
                    pstmt.setInt(5, duration);
                    pstmt.setFloat(6, costoTotal);
                    pstmt.setInt(7, roomIdInt);

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Datos insertados correctamente en la base de datos.");
                        String message ="Sesión agendada correctamente: " +
                                "\nID: " + intIDSesion + "\nGénero musical: " + genre +
                                "\nFecha: " + date +
                                "\nHora: " + time +
                                "\nDuración: " + duration + " horas" +
                                "\nID de sala: " + roomIdInt +
                                "\nID de cliente: " + clientIdInt;
                        JOptionPane.showMessageDialog(null, message);
                    } else {
                        System.out.println("Fallo al insertar datos en la base de datos.");
                    }
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(spcQuery)){
                pstmt.setInt(1, clientIdInt);
                pstmt.setInt(2, intIDSesion);
                pstmt.setInt(3, roomIdInt);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Datos insertados correctamente en SesionesPorCliente.");
                } else {
                    System.out.println("Fallo al insertar datos en SesionesPorCliente.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error de la base de datos: " + ex.getMessage());
        }
    }

    public void addNewClient(String idCliente, String nombre, String apellidos){
        String newClientQuery = "INSERT INTO Clientes (idClientes, nombre, apellidos) VALUES (?, ?, ?)";
        try (Connection conn = getConection()){
            try(PreparedStatement preparedStatement = conn.prepareStatement(newClientQuery)){
                //turn string idCliente to int
                int idClienteInt = Integer.parseInt(idCliente);
                preparedStatement.setInt(1, idClienteInt);
                preparedStatement.setString(2,nombre);
                preparedStatement.setString(3, apellidos);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Datos insertados correctamente en la tabla Cliente");
                } else {
                    JOptionPane.showMessageDialog(null, "Fallo al insertar los datos en la tabla Cliente");
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    public void addNewEmpleado(String idEmpleado, String nombreEmpleado, String apellidoEmpleado, String password, String autorizacion, String sueldo, String turno){
        String newEmployeeQuery = "INSERT INTO Empleado (idEmpleado, nombre, apellido, contraseña, autorizacion, sueldo, turno) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConection()){
            try(PreparedStatement preparedStatement = conn.prepareStatement(newEmployeeQuery)){
                //turn string idCliente to int
                int idEmpleadoInt = Integer.parseInt(idEmpleado);
                //turn string sueldo to float
                float floatSueldo = Float.parseFloat(sueldo);

                preparedStatement.setInt(1, idEmpleadoInt);
                preparedStatement.setString(2,nombreEmpleado);
                preparedStatement.setString(3, apellidoEmpleado);
                preparedStatement.setString(4, password);
                preparedStatement.setString(5, autorizacion);
                preparedStatement.setFloat(6, floatSueldo);
                preparedStatement.setString(7, turno);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Datos insertados correctamente en la tabla Empleado");
                } else {
                    JOptionPane.showMessageDialog(null, "Fallo al insertar los datos en la tabla Empleado");
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }



    public void updateSession(String sessionId, String genre, String date, String time, int duration, String roomId) {
        String costSearchQuery = "SELECT costoPorHora FROM Sala WHERE idSala = ?";
        String updateQuery = "UPDATE Sesiones SET generoMusical = ?, fecha = ?, hora = ?, duracion = ?, Sala_idSala = ?, costoTotal = ? WHERE idSesiones = ?";

        try (Connection conn = getConection();
             PreparedStatement costStmt = conn.prepareStatement(costSearchQuery);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            // Get costo por hora
            costStmt.setString(1, roomId);
            ResultSet rs = costStmt.executeQuery();
            float costoPorHora = 0;
            if (rs.next()) {
                costoPorHora = rs.getFloat("costoPorHora");
            }

            // Calcular el nuevo costo total
            float costoTotal = costoPorHora * duration;

            // Prepare the update statement
            pstmt.setString(1, genre); // 1st placeholder
            pstmt.setString(2, date); // 2nd placeholder
            pstmt.setString(3, time); // 3rd placeholder
            pstmt.setInt(4, duration); // 4th placeholder
            pstmt.setString(5, roomId); // 5th placeholder
            pstmt.setFloat(6, costoTotal); // 6th placeholder
            pstmt.setString(7, sessionId); // 7th placeholder

            // Execute the update
            pstmt.executeUpdate();
            String message = "La sesión ha sido modificada de manera exitosa";
            JOptionPane.showMessageDialog(null, message);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    public int logIn(String userId, String password) {
        String loginQuery = "SELECT autorizacion FROM Empleado WHERE idEmpleado = ? AND contraseña = ?";
        try (Connection conn = getConection();
             PreparedStatement pstmt = conn.prepareStatement(loginQuery)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("autorizacion"); // Return authorization level
            } else {
                return -1; // Login failed
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            return -1;
        }
    }

}
