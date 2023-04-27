package com.ecommerce.api.controller;

import com.ecommerce.api.dto.ListaProductosDto;
import com.ecommerce.api.dto.ProductoDto;
import com.ecommerce.api.dto.ResultDto;
import com.ecommerce.api.entity.Producto;
import com.ecommerce.api.exception.RegistroExisteExcepcion;
import com.ecommerce.api.exception.RegistroNoExisteExcepcion;
import com.ecommerce.api.exception.ValidacionExcepcion;
import com.ecommerce.api.repository.FacturaRepository;
import com.ecommerce.api.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/ecommerce/productos")
public class ProductoController {
  private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
  
  private final ProductoRepository productoRepository;
  private final FacturaRepository facturaRepository;
  
  public ProductoController(ProductoRepository productoRepository,
                            FacturaRepository facturaRepository) {
    this.productoRepository = productoRepository;
    this.facturaRepository = facturaRepository;
  }
  
  /**
   * Endpoint que devuelve todos los productos
   *
   * @return Response con Objeto que contiene lista de productos
   */
  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ListaProductosDto> productos() {
    log.info("Inicio carga todos los productos");
    final List<ProductoDto> productosDto = new ArrayList<>();
    
    // se convierten todos los productos a ProductoDto desde el
    // resultado de la query
    final List<Producto> productos = productoRepository.findAll(Sort.by(Sort.Direction.ASC, "sku"));
    
    log.info("Fin carga todos los productos, se devuelve Ok");
    // crear response
    return ResponseEntity.ok(
        ListaProductosDto
            .builder()
            .productos(productos
                .stream()
                .map(this::crearProductoDto)
                .collect(Collectors.toList()))
            .build());
  }
  
  /**
   * Endpoint que devuelve un objeto producto por código Sku de inventario
   *
   * @param sku String con el código de producto
   * @return Response con instancia de objeto Producto
   */
  @GetMapping(
      value = "/{sku}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProductoDto> productoPorSku(
      @PathVariable("sku") String sku) {
    log.info("Inicio carga producto para código sku =" + sku);
    // buscar el cliente
    final var optionalProducto = productoRepository.findBySku(sku);
    if (optionalProducto.isEmpty()) {
      throw new RegistroNoExisteExcepcion("Producto con código sku " + sku + " no existe",
          null);
    }
    log.info("Fin carga carga producto para código sku =" + sku + ", se devuelve Ok");
    // crear response
    return ResponseEntity.ok(crearProductoDto(optionalProducto.get()));
  }
  
  /**
   * Crea un nuevo producto en BD con los datos contenidos en el objeto,
   * de acuerdo al código sku del campo codigoProducto
   *
   * @param productoDto Instancia con los datos del cliente a crear
   * @return Response con el resultado de la inserción
   */
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResultDto> crearProducto(@RequestBody ProductoDto productoDto) {
    // validar datos del request
    validarProducto(productoDto);
    // validar si existe
    validarNoExisteSku(productoDto.getCodigo());
    
    // actualiar el cliente
    productoRepository.save(Producto
        .builder()
        .sku(productoDto.getCodigo())
        .descripcion(productoDto.getDescripcion())
        .precioUnitario(productoDto.getPrecio())
        .disponible(productoDto.getDisponible())
        .build());
    
    // crear response
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResultDto
            .builder()
            .message("Producto con código sku " + productoDto.getCodigo() + " registrado")
            .build());
  }
  
  /**
   * Actualiza datos de un producto existente en BD
   * con los datos contenidos en el objeto,
   * de acuerdo al código sky de producto del campo Codigo
   *
   * @param productoDto Instancia con los datos del producto a actualizar
   * @return Response con el resultado de la actualzación
   */
  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  public ResponseEntity<ResultDto> actualizarProducto(@RequestBody ProductoDto productoDto) {
    log.info("Inicio actualizar producto para request =" + productoDto);
    // validar datos del request
    validarProducto(productoDto);
    // validar que el sku exista
    validarExisteSku(productoDto.getCodigo());
    // actualizar
    productoRepository.actualizarProducto(productoDto);
    // crear respuesta
    log.info("Fin actualizar producto para código sku =" + productoDto.getCodigo());
    return ResponseEntity.ok(ResultDto
        .builder()
        .message("producto actualizado")
        .build());
  }
  
  /**
   * Elimina de BD el producto con el código sku dado
   *
   * @param sku String con el código sku del producto
   * @return Response con el resultado de la eliminación
   */
  @DeleteMapping(
      value = "/{sku}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  public ResponseEntity<ResultDto> borrarProducto(@PathVariable("sku") String sku) {
    log.info("Inicio eliminar producto con código sku =" + sku);
    // validar que exista
    validarExisteSku(sku);
    if (facturaRepository.productoTieneFacturas(sku)) {
      throw new RegistroExisteExcepcion("El producto código sku " + sku +
          " tiene facturas registradas. No se puede eliminar", null);
    }
    // borrar el registro
    productoRepository.deleteBySku(sku);
    // crear respuesta
    log.info("Fin eliminar producto con código sku =" + sku + ", se devuelve Ok");
    return ResponseEntity.ok(ResultDto
        .builder()
        .message("producto eliminado")
        .build());
  }
  
  /**
   * Setea el producto como disponible para factura, cambiando el campo "disponible" a true
   *
   * @param sku Código sku del producto a activar
   * @return Response con el resultado de la actualización
   */
  @PutMapping(
      value = "/{sku}/activar",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResultDto> activarProducto(@PathVariable("sku") String sku) {
    // validar que el sku exista
    validarExisteSku(sku);
    
    // actualizar
    productoRepository.activarProducto(sku);
    
    // crear respuesta
    return ResponseEntity.ok(ResultDto
        .builder()
        .message("producto activado")
        .build());
  }
  
  /**
   * Setea el producto como no disponible para factura, cambiando el campo "disponible" a false
   *
   * @param sku Código sku del producto a desactivar
   * @return Response con el resultado de la actualización
   */
  @PutMapping(
      value = "/{sku}/desactivar",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResultDto> desactivarProducto(@PathVariable("sku") String sku) {
    // validar que el sku  exista
    validarExisteSku(sku);
    
    // actualizar
    productoRepository.desactivarProducto(sku);
    
    // crear respuesta
    return ResponseEntity.ok(ResultDto
        .builder()
        .message("producto desactivado")
        .build());
  }
  
  
  /**
   * Validador de la existencia de un código sku de producto en BD,
   * lanza excepción si no es válido
   *
   * @param sku código sku a validar
   */
  private void validarExisteSku(String sku) {
    if (!productoRepository.existsBySku(sku)) {
      throw new RegistroNoExisteExcepcion(
          "No existe ningún producto con código sku " + sku, null);
    }
  }
  
  /**
   * Validador de que un código sku de producto en BD no exista, lanza excepción si
   * no es válido
   *
   * @param sku código sku a validar
   */
  private void validarNoExisteSku(String sku) {
    if (productoRepository.existsBySku(sku)) {
      throw new RegistroExisteExcepcion(
          "Ya existe un producto con código sku " + sku, null);
    }
  }
  
  /**
   * Convierte de Producto a Producto Dto
   *
   * @param p objeto Producto a convertir
   * @return objeto ProductoDto
   */
  private ProductoDto crearProductoDto(Producto p) {
    return ProductoDto
        .builder()
        .codigo(p.getSku())
        .descripcion(p.getDescripcion())
        .precio(p.getPrecioUnitario())
        .disponible(p.getDisponible())
        .build();
  }
  
  /**
   * Valida que todos los campos del producto sean correctos y lanza
   * excepción si alguno de los campos es incorrecto.
   *
   * @param productoDto objeto a validar
   */
  private void validarProducto(ProductoDto productoDto) {
    if (productoDto == null) {
      throw new ValidacionExcepcion("Datos del producto no puede ser nulo", null);
    }
    // validar sku no sea vacío
    if (productoDto.getCodigo() == null ||
        productoDto.getCodigo().trim().isEmpty()) {
      throw new ValidacionExcepcion("El código sku no puede ser vacío", null);
    }
    // validar descripción que no sea vacía
    if (productoDto.getDescripcion() == null || productoDto.getDescripcion().trim().isEmpty()) {
      throw new ValidacionExcepcion("La descripción no puede ser vacía", null);
    }
    // validar precio sea válido
    if (productoDto.getPrecio() == null ||
        productoDto.getPrecio() <= 0) {
      throw new ValidacionExcepcion("El precio del producto debe ser mayor a 0", null);
    }
    
    // validar campo disponible no nulo
    if (productoDto.getDisponible() == null) {
      throw new ValidacionExcepcion("El valor del campo disponible debe ser 'true' o 'false'",
          null);
    }
  }
}
