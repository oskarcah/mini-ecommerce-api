package com.ecommerce.api;

import com.ecommerce.api.entity.Cliente;
import com.ecommerce.api.entity.EstatusFactura;
import com.ecommerce.api.entity.Factura;
import com.ecommerce.api.entity.Producto;
import com.ecommerce.api.entity.RenglonFactura;
import com.ecommerce.api.repository.ClienteRepository;
import com.ecommerce.api.repository.FacturaRepository;
import com.ecommerce.api.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class RunnerCargaDataInicialDB implements CommandLineRunner {
  
  private static final Logger log = LoggerFactory.getLogger(RunnerCargaDataInicialDB.class);
  
  private final ClienteRepository clienteRepository;
  private final FacturaRepository facturaRepository;
  private final ProductoRepository productoRepository;
  
  
  public RunnerCargaDataInicialDB(
      ClienteRepository clienteRepository,
      FacturaRepository facturaRepository,
      ProductoRepository productoRepository) {
    this.clienteRepository = clienteRepository;
    this.facturaRepository = facturaRepository;
    this.productoRepository = productoRepository;
  }
  
  /**
   * Carga de datos iniciales para pruebas
   *
   * @param args no se usan
   * @throws Exception si hay falla en la BD
   */
  @Override
  @Transactional()
  public void run(String... args) throws Exception {
    log.info("Inicio carga datos de prueba");
    // carga clientes de prueba
    log.info("Creación de clientes");
    clienteRepository.save(Cliente
        .builder()
        .documento("123")
        .nombre("pedro perez")
        .direccion("calle 1")
        .telefono("2111234567")
        .build());
    
    clienteRepository.save(Cliente
        .builder()
        .documento("546")
        .nombre("maria fernandez")
        .direccion("Edificio dragon")
        .telefono("21133399922")
        .build());
    
    clienteRepository.save(Cliente
        .builder()
        .documento("999")
        .nombre("Fernando Alvarez")
        .direccion("Esquina 33")
        .telefono("21199999999")
        .build());
    
    clienteRepository.save(Cliente
        .builder()
        .documento("777")
        .nombre("Margarita Perez")
        .direccion("Esquina 27")
        .telefono("2115734545")
        .build());
    
    // carga productos de prueba
    log.info("Creación de productos");
    productoRepository.save(
        Producto
            .builder()
            .sku("P001")
            .descripcion("Pantalón negro casual dama")
            .precioUnitario(50.55D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P002")
            .descripcion("Pantalón azul casual dama")
            .precioUnitario(50.55D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P003")
            .descripcion("Pantalón verde casual dama")
            .precioUnitario(50.55D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P004")
            .descripcion("Jean azul dama")
            .precioUnitario(47.25D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P005")
            .descripcion("Jean gris dama")
            .precioUnitario(47.25D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P006")
            .descripcion("Pantalón negro casual caballero")
            .precioUnitario(55.25D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P007")
            .descripcion("Pantalón azul casual caballero")
            .precioUnitario(55.25D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P008")
            .descripcion("Pantalón verde casual caballero")
            .precioUnitario(55.25D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P009")
            .descripcion("Jean azul caballero")
            .precioUnitario(49.90D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P010")
            .descripcion("Jean gris caballero")
            .precioUnitario(49.90D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P011")
            .descripcion("Camiseta negra manga corta unisex")
            .precioUnitario(36D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P012")
            .descripcion("Camiseta negra manga larga unisex")
            .precioUnitario(39D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P013")
            .descripcion("Camisa estampada cuadros dama")
            .precioUnitario(48.25D)
            .disponible(true)
            .build());
    
    productoRepository.save(
        Producto
            .builder()
            .sku("P014")
            .descripcion("Camisa estampada cuadros caballero")
            .precioUnitario(48.25D)
            .disponible(true)
            .build());
  
    log.info("Creación de factura de ejemplo");
    Producto p1 = productoRepository.findBySku("P001").get();
    Producto p2 = productoRepository.findBySku("P002").get();
    Cliente c = clienteRepository.findByDocumento("123").get();
 
    facturaRepository.save(Factura
        .builder()
        .cliente(c)
        .fecha(LocalDate.now())
        .estatus(EstatusFactura.PENDIENTE_PAGO)
        .renglones(List.of(
            RenglonFactura
                .builder()
                .producto(p1)
                .cantidad(2)
                .precioUnitario(p1.getPrecioUnitario())
                .build(),
            RenglonFactura
                .builder()
                .producto(p2)
                .cantidad(1)
                .precioUnitario(p2.getPrecioUnitario())
                .build()))
        .build());
  
    log.info("Fin de carga de datos");
  }
}
