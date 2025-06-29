package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static ConexionDB instancia;

    private Connection conexion;

    private final String URLDB = "jdbc:mysql://localhost:3306/bank";

    private final String USUARIODB = "root";

    private final String PASSWORDDB = "";

    private ConexionDB() throws SQLException {
        this.conexion = DriverManager.getConnection(URLDB, USUARIODB, PASSWORDDB);
    }

    /// synchronized es para evitar problemas de concurrencia en aplicaciones
    /// multihilo
    public static synchronized ConexionDB getInstancia() throws SQLException {
        if (instancia == null || instancia.getConexion().isClosed()) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }
}
