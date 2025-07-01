package dao;

import model.Usuario;
import java.sql.SQLException;

public interface UsuarioDAO {
    Usuario findByUsernameAndPassword(String username, String passwordHash) throws SQLException;
}
