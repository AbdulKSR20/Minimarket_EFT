package com.minimarket.config;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializerConfig implements ApplicationRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");

            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(rolRepository.findByNombre("ADMIN").get()));

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario por defecto creado -> Username: admin | Password: admin123");
        }

        for (int i = 1; i <= 5; i++) {
            Categoria c = new Categoria();
            c.setNombre("Categoria " + i);
            categoriaRepository.save(c);

            Producto p = new Producto();
            p.setNombre("Producto " + i);
            p.setPrecio(i * 10.0);
            p.setCategoria(c);
            productoRepository.save(p);

        }
    }

}