package dao;

import model.User;
import model.TipoUsuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {
    User findByUsernameAndHash(String username, String passwordHash) throws SQLException;

    User findById(int id) throws SQLException;

    List<User> findAllByType(TipoUsuario type) throws SQLException;

    void create(User u) throws SQLException;
}
