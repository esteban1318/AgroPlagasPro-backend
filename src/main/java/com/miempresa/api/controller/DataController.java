package com.miempresa.api.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miempresa.api.model.LoginApp;
import com.miempresa.api.model.User;
import com.miempresa.api.model.DataRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataController {

    public String usuario;
    public String password;
    public  String role;

    @PostMapping("/data")
    public ResponseEntity<?> obtenerDatos(@RequestBody DataRequest request) {
        if (!request.getTable().matches("[a-zA-Z0-9_]+")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Nombre de tabla inválido"));
        }

        String url = "jdbc:postgresql://" + request.getHost() + ":" + request.getPort() + "/" + request.getDatabase();
        System.out.println("Intentando conectar a: " + url);

        try (Connection conn = DriverManager.getConnection(url, request.getUser(), request.getPassword())) {

            if (!tableExists(conn, request.getTable())) {
                return ResponseEntity.status(404).body(Map.of("error", "La tabla no existe"));
            }

            // Solo columnas necesarias + filtrado de coordenadas válidas
            String sql = "SELECT id, marcado, fecha, envio, linea, arbol, evaluar, tercio, zona, estado, " +
                    "cantidad, obs, empleado_id, lote_id, plaga_id, cod_moni_id, x, y " +
                    "FROM public.\"" + request.getTable() + "\" " +
                    "WHERE x IS NOT NULL AND y IS NOT NULL";

            System.out.println("Ejecutando consulta: " + sql);

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> features = new ArrayList<>();

            while (rs.next()) {
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");

                // Crear geometry
                Map<String, Object> geometry = Map.of(
                        "type", "Point",
                        "coordinates", List.of(x, y)
                );

                // Crear properties
                Map<String, Object> properties = new HashMap<>();
                properties.put("id", rs.getObject("id"));
                properties.put("marcado", rs.getObject("marcado"));
                properties.put("fecha", rs.getObject("fecha"));
                properties.put("envio", rs.getObject("envio"));
                properties.put("linea", rs.getObject("linea"));
                properties.put("arbol", rs.getObject("arbol"));
                properties.put("evaluar", rs.getObject("evaluar"));
                properties.put("tercio", rs.getObject("tercio"));
                properties.put("zona", rs.getObject("zona"));
                properties.put("estado", rs.getObject("estado"));
                properties.put("cantidad", rs.getObject("cantidad"));
                properties.put("obs", rs.getObject("obs"));
                properties.put("empleado_id", rs.getObject("empleado_id"));
                properties.put("lote_id", rs.getObject("lote_id"));
                properties.put("plaga_id", rs.getObject("plaga_id"));
                properties.put("cod_moni_id", rs.getObject("cod_moni_id"));

                // Crear Feature
                Map<String, Object> feature = Map.of(
                        "type", "Feature",
                        "geometry", geometry,
                        "properties", properties
                );

                features.add(feature);
            }

            Map<String, Object> featureCollection = Map.of(
                    "type", "FeatureCollection",
                    "features", features
            );

            return ResponseEntity.ok(featureCollection);

        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Error al obtener datos",
                    "details", e.getMessage()
            ));
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet tables = meta.getTables(null, "public", tableName, new String[]{"TABLE"});
        return tables.next();
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginApp request) {

        List<User> usuarios;

        // Leer usuarios desde users.json
        try (InputStream is = getClass().getResourceAsStream("/users.json")) {
            if (is == null) {
                return ResponseEntity.status(500).body(Map.of("error", "Archivo users.json no encontrado"));
            }
            ObjectMapper mapper = new ObjectMapper();
            usuarios = mapper.readValue(is, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error leyendo users.json", "details", e.getMessage()));
        }

        // Buscar usuario por username
        User user = usuarios.stream()
                .filter(u -> u.getUsername().equals(request.getUsername()))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado"));
        }

        // Verificar contraseña con BCrypt
        boolean passwordOk = request.getPassword().equals(user.getPasswordHash());


        if (!passwordOk) {
            return ResponseEntity.status(401).body(Map.of("error", "Contraseña incorrecta"));
        }

        // Verificar rol
        if (!user.getRole().equals(request.getRole())) {
            return ResponseEntity.status(403).body(Map.of("error", "Rol incorrecto"));
        }

        // Login exitoso
        return ResponseEntity.ok(Map.of(
                "message", "Login exitoso",
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }
}
