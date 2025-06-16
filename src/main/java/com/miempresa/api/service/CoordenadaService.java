package com.miempresa.api.service;

import com.miempresa.api.model.Coordenada;
import com.miempresa.api.model.Usuario;
import com.miempresa.api.repository.CoordenadaRepository;
import com.miempresa.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoordenadaService {
    @Autowired
    private CoordenadaRepository coordenadaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Coordenada> getCoordenadasPorUsuario(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        return coordenadaRepository.findByUsuario(usuario);
    }

    public void guardarCoordenadas(String username, List<Coordenada> coordenadas) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        for (Coordenada c : coordenadas) {
            c.setUsuario(usuario);
        }
        coordenadaRepository.saveAll(coordenadas);
    }

    public void eliminarCoordenadas(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        List<Coordenada> coords = coordenadaRepository.findByUsuario(usuario);
        coordenadaRepository.deleteAll(coords);
    }
}
