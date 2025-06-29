package dao.impl;

import dao.UsuarioDAO;
import model.User;
import model.TipoUsuario;
import util.ConexionDB;

import java.sql.*;

public class UsuarioDAOImpl implements UsuarioDAO {
    private final Connection conn;

    // Constructor explícito que maneja el SQLException
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

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setUserType(TipoUsuario.valueOf(rs.getString("user_type")));
        return u;
    }
}
