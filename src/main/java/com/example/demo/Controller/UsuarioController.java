package com.example.demo.Controller;

import com.example.demo.Models.Usuario;
import com.example.demo.Repository.UsuarioRepository;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UsuarioController {

    UsuarioRepository repository;

    public UsuarioController(UsuarioRepository r) {
        this.repository = r;
    }

    //Si la DB esta vacía, ir a /api/crearUsuarios genera 2 por defecto
    @GetMapping("/api/crearUsuarios")
    public void crearUsuarios() {
        if(repository.count() == 0) {
            Usuario usuario1 = new Usuario("rodrigo@mail.com", "rodrigo1");
            Usuario usuario2 = new Usuario("erika@mail.com", "erika1");
            repository.save(usuario1);
            repository.save(usuario2);
        }

    }

    @GetMapping("/api/usuarios")
    public List<Usuario> obtenerUsuarios() {
        return repository.findAll();
    }

    @GetMapping("/api/usuario/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        Optional<Usuario> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(opt.get());
        }
    }

    //El crossOrigin es para evitar errores de CORS al probar localmente
    @CrossOrigin
    @PostMapping("/api/registrarUsuario")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioExistente = repository.findByMail(usuario.getMail());

        if (usuarioExistente != null) {
            // Correo electrónico ya en uso, retornar un error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: El correo electrónico ya está en uso.");
        }

        // Si el correo no existe, guardar el usuario
        repository.save(usuario);
        return ResponseEntity.ok("Usuario registrado correctamente.");
    }

    //El crossOrigin es para evitar errores de CORS al probar localmente
    @CrossOrigin
    @PostMapping("/api/iniciarSesion")
    public ResponseEntity<String> iniciarSesion(@RequestBody Usuario usuario) {
        Usuario usuarioExistente = repository.findByMail(usuario.getMail());

        //Si el usuario no está registrado
        if (usuarioExistente == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: El usuario no está registrado.");

        //Si no coincide el mail con la contraseña
        if (!usuario.getContrasena().equals(usuarioExistente.getContrasena()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: El mail no coincide con la contraseña.");

        return ResponseEntity.ok("Inicio de sesión correcto.");
    }
}
