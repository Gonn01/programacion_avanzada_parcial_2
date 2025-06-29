package dao.impl;

import dao.UsuarioDAO;
import model.User;
import model.TipoUsuario;
import util.ConexionDB;

import java.sql.*;

public class UsuarioDAOImpl implements UsuarioDAO {
    private final Connection conn;

    public UsuarioDAOImpl() throws SQLException {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    @Override
    public User findByUsernameAndPassword(String username, String passwordHash) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password_hash=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
                return null;
            }
        }
    }

    private User map(ResultSet data) throws SQLException {
        int id = data.getInt("id");

        String username = data.getString("username");

        String passwordHash = data.getString("password_hash");

        TipoUsuario tipoUsuario = TipoUsuario.valueOf(data.getString("user_type"));

        return new User(id, username, passwordHash, tipoUsuario);
    }
}
