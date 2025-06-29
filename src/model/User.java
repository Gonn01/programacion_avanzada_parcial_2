package model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private TipoUsuario tipoUsuario;

    public User(int id, String username, String passwordHash, TipoUsuario userType) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.tipoUsuario = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}