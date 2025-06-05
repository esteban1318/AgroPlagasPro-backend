package com.miempresa.api.controller;

import com.miempresa.api.model.TableRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TableController {

    @PostMapping("/tables")
    public ResponseEntity<?> listarTablas(@RequestBody TableRequest request) {
        // ✅ Corrección: uso de String.format para construir la URL
        String url = String.format("jdbc:postgresql://%s:%d/%s",
                request.host, request.port, request.database);

        try (Connection conn = DriverManager.getConnection(url, request.user, request.password)) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name"
            );
            ResultSet rs = stmt.executeQuery();

            List<String> tablas = new ArrayList<>();
            while (rs.next()) {
                tablas.add(rs.getString("table_name"));
            }

            return ResponseEntity.ok(tablas);

        } catch (SQLException e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Error al listar tablas: " + e.getMessage()));
        }
    }
}
