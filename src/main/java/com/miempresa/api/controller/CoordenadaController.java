package com.miempresa.api.controller;

import  com.miempresa.api.model.Coordenada;
import com.miempresa.api.service.CoordenadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordenadas")
public class CoordenadaController {
    @Autowired
    private CoordenadaService coordenadaService;

    @GetMapping("/{username}")
    public List<Coordenada> obtenerPorUsuario(@PathVariable String username) {
        return coordenadaService.getCoordenadasPorUsuario(username);
    }

    @PostMapping("/{username}")
    public void guardar(@PathVariable String username, @RequestBody List<Coordenada> coordenadas) {
        coordenadaService.guardarCoordenadas(username, coordenadas);
    }

    @DeleteMapping("/{username}")
    public void eliminar(@PathVariable String username) {
        coordenadaService.eliminarCoordenadas(username);
    }
}
