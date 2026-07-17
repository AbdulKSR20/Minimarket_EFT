package com.minimarket.config;

import com.minimarket.entity.*;
import com.minimarket.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.Date;
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
    @Autowired
    private SucursalRepository sucursalRepository;
    @Autowired
    private StockSucursalRepository stockSucursalRepository;
    @Autowired
    private PromocionRepository promocionRepository;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Sucursal sucursal = null;
        if (sucursalRepository.findAll().isEmpty()) {
            sucursal = new Sucursal();
            sucursal.setNombre("MiniMarket Central");
            sucursal.setDireccion("Avenida Principal 123");
            sucursal = sucursalRepository.save(sucursal);
        } else {
            sucursal = sucursalRepository.findAll().get(0);
        }

        Proveedor proveedor = null;
        if (proveedorRepository.findAll().isEmpty()) {
            proveedor = new Proveedor();
            proveedor.setNombre("Distribuidora Nacional S.A.");
            proveedor.setContacto("contacto@distribuidora.com");
            proveedor = proveedorRepository.save(proveedor);
        }

        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(rolRepository.findByNombre("ADMIN").get()));
            usuarioRepository.save(admin);
        }

        Usuario cliente = null;
        if (usuarioRepository.findByUsername("cliente").isEmpty()) {
            cliente = new Usuario();
            cliente.setUsername("cliente");
            cliente.setPassword(passwordEncoder.encode("cliente123"));
            cliente.setRoles(Set.of(rolRepository.findByNombre("CLIENTE").get()));
            cliente = usuarioRepository.save(cliente);
        } else {
            cliente = usuarioRepository.findByUsername("cliente").get();
        }

        if (productoRepository.findAll().isEmpty()) {
            Categoria catLacteos = new Categoria();
            catLacteos.setNombre("Lácteos");
            catLacteos = categoriaRepository.save(catLacteos);

            Categoria catBebidas = new Categoria();
            catBebidas.setNombre("Bebidas");
            catBebidas = categoriaRepository.save(catBebidas);

            Promocion promoVerano = new Promocion();
            promoVerano.setNombre("Descuento Verano 20%");
            promoVerano.setDescripcion("Promoción por temporada de verano");
            promoVerano.setPorcentajeDescuento(20.0);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -5);
            promoVerano.setFechaInicio(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 15);
            promoVerano.setFechaFin(cal.getTime());
            promoVerano = promocionRepository.save(promoVerano);

            Producto leche = new Producto();
            leche.setNombre("Leche Descremada 1L");
            leche.setPrecio(1500.0);
            leche.setCategoria(catLacteos);
            leche = productoRepository.save(leche);

            Producto cocaCola = new Producto();
            cocaCola.setNombre("Coca Cola 2L");
            cocaCola.setPrecio(2000.0);
            cocaCola.setCategoria(catBebidas);
            cocaCola.setPromocion(promoVerano);
            cocaCola = productoRepository.save(cocaCola);

            StockSucursal stockLeche = new StockSucursal();
            stockLeche.setSucursal(sucursal);
            stockLeche.setProducto(leche);
            stockLeche.setCantidad(50);
            stockLeche.setStockMinimo(10);
            stockSucursalRepository.save(stockLeche);

            StockSucursal stockCola = new StockSucursal();
            stockCola.setSucursal(sucursal);
            stockCola.setProducto(cocaCola);
            stockCola.setCantidad(20);
            stockCola.setStockMinimo(5);
            stockSucursalRepository.save(stockCola);

            if (carritoRepository.findByUsuarioId(cliente.getId()).isEmpty()) {
                Carrito item1 = new Carrito();
                item1.setUsuario(cliente);
                item1.setProducto(leche);
                item1.setCantidad(2);
                carritoRepository.save(item1);

                Carrito item2 = new Carrito();
                item2.setUsuario(cliente);
                item2.setProducto(cocaCola);
                item2.setCantidad(1);
                carritoRepository.save(item2);
            }
        }

    }
}