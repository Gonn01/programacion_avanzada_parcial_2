package dao;

import model.User;
import java.sql.SQLException;

public interface UsuarioDAO {
    User findByUsernameAndPassword(String username, String passwordHash) throws SQLException;
}
