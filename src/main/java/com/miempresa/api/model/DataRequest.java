package com.miempresa.api.model;

public class DataRequest {
    private String host;
    private String port;
    private String database;
    private String user;
    private String password;
    private String table;

    // Getters y setters
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }
    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getTable() {
        return table;
    }
    public void setTable(String table) {
        this.table = table;
    }
}
