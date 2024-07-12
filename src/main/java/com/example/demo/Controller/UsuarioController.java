package com.example.demo.Controller;

import com.example.demo.Models.Usuario;
import com.example.demo.Repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UsuarioController {

    UsuarioRepository repository;

    //Si la DB esta vacía, ir a /api/crearUsuarios genera 2 por defecto
    @PostMapping("/crearUsuarios")
    public ResponseEntity<Void> crearUsuarios() {
        if(repository.count() == 0) {
            Usuario usuario1 = new Usuario();
            usuario1.setMail("erika@mail.com");
            usuario1.setPass("erika1");
            Usuario usuario2 = new Usuario();
            usuario2.setMail("rodrigo@mail.com");
            usuario2.setPass("rodrigo1");
            repository.save(usuario1);
            repository.save(usuario2);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuarios")
    public List<Usuario> obtenerUsuarios() {
        return repository.findAll();
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        Optional<Usuario> opt = repository.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    //El crossOrigin es para evitar errores de CORS al probar localmente
    @CrossOrigin
    @PostMapping("/registrarUsuario")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario usuario) {
        System.out.println("user: " + usuario.toString());
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
    @PostMapping("/iniciarSesion")
    public ResponseEntity<String> iniciarSesion(@RequestBody Usuario usuario) {
        Usuario usuarioExistente = repository.findByMail(usuario.getMail());

        //Si el usuario no está registrado
        if (usuarioExistente == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: El usuario no está registrado.");

        //Si no coincide el mail con la contraseña
        if (!usuario.getPass().equals(usuarioExistente.getPass()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: El mail no coincide con la contraseña.");

        return ResponseEntity.ok("Inicio de sesión correcto.");
    }
}
