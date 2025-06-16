package com.miempresa.api.repository;

import com.miempresa.api.model.Coordenada;
import com.miempresa.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface  CoordenadaRepository extends JpaRepository<Coordenada, Long> {
    List<Coordenada> findByUsuario(Usuario usuario );
}
