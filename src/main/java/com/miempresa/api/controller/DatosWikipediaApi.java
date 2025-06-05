package com.miempresa.api.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.*;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class DatosWikipediaApi {

    @GetMapping("/wikipedia-descripcion")
    public ResponseEntity<?> obtenerDescripcionWikipedia(@RequestParam String nombre) {
        try {
            System.out.println("Nombre recibido: " + nombre);

            // Construir URL
            String url = "https://es.wikipedia.org/wiki/" + URLEncoder.encode(nombre, StandardCharsets.UTF_8);
            System.out.println("URL de consulta: " + url);

            // Cliente HTTP
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            // Solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            // Obtener respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("URL final tras redirección: " + response.uri());

            String html = response.body();

            // Parsear HTML
            Document doc = Jsoup.parse(html);

            // Seleccionar el primer párrafo significativo dentro del contenido
            // En Wikipedia el texto está dentro de <div id="mw-content-text">
            Element content = doc.selectFirst("div#mw-content-text");
            if (content == null) {
                return ResponseEntity.status(404).body("❌ No se encontró el contenido de la página.");
            }

            // Seleccionar el primer párrafo <p> que contenga texto visible y no esté vacío
            Element primerParrafo = null;
            for (Element p : content.select("p")) {
                if (!p.text().isBlank()) {
                    primerParrafo = p;
                    break;
                }
            }

            if (primerParrafo == null) {
                return ResponseEntity.status(404).body("❌ No se encontró descripción en la página.");
            }

            // Devolver solo el texto del primer párrafo
            String descripcion = primerParrafo.text();

            return ResponseEntity.ok(descripcion);

        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(500).body("❌ Error al obtener datos de Wikipedia: " + e.getMessage());
        }
    }
}
