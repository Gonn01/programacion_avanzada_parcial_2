package dao.impl;

import dao.UsuarioDAO;
import model.User;
import model.TipoUsuario;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UsuarioDAO {
    private final Connection conn;

    // Constructor explícito que maneja el SQLException
    public UserDAOImpl() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    @Override
    public User findByUsernameAndHash(String username, String passwordHash) throws SQLException {
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

    @Override
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<User> findAllByType(TipoUsuario type) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_type=?";
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    users.add(map(rs));
            }
        }
        return users;
    }

    @Override
    public void create(User u) throws SQLException {
        String sql = "INSERT INTO users(username,password_hash,user_type) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getUserType().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    u.setId(keys.getInt(1));
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
