package com.ecommerce.api.controller;

import static com.ecommerce.api.entity.EstatusFactura.PENDIENTE_PAGO;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import com.ecommerce.api.dto.ClienteDto;
import com.ecommerce.api.dto.CrearFacturaResultDto;
import com.ecommerce.api.dto.FacturaRequestDto;
import com.ecommerce.api.dto.FacturaResponseDto;
import com.ecommerce.api.dto.ProductoDto;
import com.ecommerce.api.dto.RenglonFacturaRequestDto;
import com.ecommerce.api.dto.RenglonFacturaResponseDto;
import com.ecommerce.api.dto.ResultDto;
import com.ecommerce.api.entity.Cliente;
import com.ecommerce.api.entity.EstatusFactura;
import com.ecommerce.api.entity.Factura;
import com.ecommerce.api.entity.Producto;
import com.ecommerce.api.entity.RenglonFactura;
import com.ecommerce.api.exception.RegistroNoExisteExcepcion;
import com.ecommerce.api.exception.ValidacionExcepcion;
import com.ecommerce.api.repository.ClienteRepository;
import com.ecommerce.api.repository.FacturaRepository;
import com.ecommerce.api.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/ecommerce/facturas")
@Transactional
public class FacturaController {
  private static final Logger log = LoggerFactory.getLogger(FacturaController.class);
  private final ClienteRepository clienteRepository;
  private final FacturaRepository facturaRepository;
  private final ProductoRepository productoRepository;
  
  public FacturaController(ClienteRepository clienteRepository, FacturaRepository facturaRepository,
                           ProductoRepository productoRepository) {
    this.clienteRepository = clienteRepository;
    this.facturaRepository = facturaRepository;
    this.productoRepository = productoRepository;
  }
  
  /**
   * Endpoint que devuelve una factura por número de factura
   *
   * @param numero Entero con numero de factura
   * @return Response con instancia de objeto factura
   */
  @GetMapping(
      value = "/{numero}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FacturaResponseDto> facturaPorNumero(
      @PathVariable("numero") Long numero) {
    log.info("Inicio factura por numero  =" + numero);
    // buscar la factura
    final var optFactura = facturaRepository.findById(numero);
    if (optFactura.isEmpty()) {
      throw new RegistroNoExisteExcepcion("Factura Nro " + numero + " no existe", null);
    }
    log.info("Inicio factura por numero  =" + numero + ", se devolvió Ok");
    // crear response
    return ResponseEntity.ok(crearInstanciaFacturaResponseDto(optFactura.get()));
  }
  
  /**
   * Crea una nueva factura  en BD con los datos contenidos en el objeto,
   *
   * @param facturaDto Instancia con los datos de la factura a crear
   * @return Response con el resultado de la inserción
   */
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrearFacturaResultDto> crearFactura(
      @RequestBody FacturaRequestDto facturaDto) {
    log.info("Inicio crear factura con request  =" + facturaDto);
    // validar el dato de factura
    validarFacturaRequest(facturaDto);
    // crear la instancia de factura
    Factura factura = facturaRepository.save(crearInstanciaFactura(facturaDto));
    log.info("Fin crear factura con numero  =" + factura.getId() + ", se devuelve Ok");
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CrearFacturaResultDto
            .builder()
            .message("Creada factura número " + factura.getId())
            .factura(crearInstanciaFacturaResponseDto(factura))
            .build());
  }
  
  @PutMapping(
      value = "/{numero}/estatus/{estatus}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResultDto> actualizarEstatusFactura(
      @PathVariable("numero") Long numeroFactura,
      @PathVariable("estatus") EstatusFactura estatus) {
  
    log.info("Inicio actualizar estatus de factura numero =" + numeroFactura + " a estatus " + estatus);
    // validar que la factura exista
    if (!facturaRepository.existsById(numeroFactura)) {
      throw new RegistroNoExisteExcepcion(
          "No existe factura  número " + numeroFactura, null);
    }
    // actualizar estatus
    facturaRepository.updateEstatusByNumero(numeroFactura, estatus);
    log.info("Fin actualizar estatus de factura numero =" + numeroFactura + " , se devuelve Ok");
    return ResponseEntity.status(HttpStatus.OK)
        .body(ResultDto
            .builder()
            .message("Estatus de factura " + numeroFactura + " actualizado a " + estatus)
            .build());
  }
  
  
  /**
   * Convierte de Cliente a Cliente Dto
   *
   * @param c objeto Cliente a convertir
   * @return objeto ClienteDto
   */
  private static ClienteDto crearInstanciaClienteDto(Cliente c) {
    return ClienteDto
        .builder()
        .numeroDocumento(c.getDocumento())
        .nombre(c.getNombre())
        .numeroTelefono(c.getTelefono())
        .direccion(c.getDireccion())
        .build();
  }
  
  /**
   * Convierte de Producto a ProductoDto
   *
   * @param p objeto Producto a convertir
   * @return objeto ProductoDto
   */
  private static ProductoDto crearInstanciaProductoDto(Producto p) {
    return ProductoDto
        .builder()
        .codigo(p.getSku())
        .descripcion(p.getDescripcion())
        .precio(p.getPrecioUnitario())
        .build();
  }
  
  /**
   * Convierte de RenglonFactura a RenglonFacturaResponseDto
   *
   * @param r objeto RenglonFactura a convertir
   * @return objeto RenglonFacturaResponseDto
   */
  private RenglonFacturaResponseDto crearInstanciaRenglonFacturaDto(RenglonFactura r) {
    return RenglonFacturaResponseDto
        .builder()
        .producto(crearInstanciaProductoDto(r.getProducto()))
        .precioUnitario(redondearMoneda(r.getPrecioUnitario()))
        .cantidad(r.getCantidad())
        .precio(redondearMoneda(r.getPrecioUnitario() * r.getCantidad()))
        .build();
  }
  
  /**
   * Convierte de RenglonFacturaRequestDto a RenglonFactura
   *
   * @param r objeto RenglonFacturaRequestDto a convertir
   * @return objeto RenglonFactura
   */
  private RenglonFactura crearInstanciaRenglonFactura(RenglonFacturaRequestDto r) {
    Producto p = productoRepository.findBySku(r.getCodigoProducto()).get();
    return RenglonFactura
        .builder()
        .producto(p)
        .precioUnitario(p.getPrecioUnitario())
        .cantidad(r.getCantidad())
        .build();
  }
  
  /**
   * Convierte de Factura FacturaResponseDto
   *
   * @param factura objeto FacturaResponseDto a convertir
   * @return instancia de FacturaResponseDto
   */
  private FacturaResponseDto crearInstanciaFacturaResponseDto(Factura factura) {
    return
        FacturaResponseDto
            .builder()
            .numero(factura.getId())
            .fecha(factura.getFecha().format(ISO_LOCAL_DATE))
            .estatus(factura.getEstatus())
            .cliente(crearInstanciaClienteDto(factura.getCliente()))
            .total(redondearMoneda(factura.getTotal()))
            .detalle(factura.getRenglones()
                .stream()
                .map(this::crearInstanciaRenglonFacturaDto)
                .collect(Collectors.toList()))
            .build();
  }
  
  /**
   * Convierte de FacturaRequestDto a Factura
   *
   * @param facturaDto objeto FacturaRequestDto a convertir
   * @return instancia de Factura
   */
  private Factura crearInstanciaFactura(FacturaRequestDto facturaDto) {
    return Factura
        .builder()
        .fecha(facturaDto.getFecha())
        .estatus(PENDIENTE_PAGO)
        .cliente(clienteRepository.findByDocumento(facturaDto.getDocumentoCliente()).get())
        .renglones(facturaDto.getDetalleProductos()
            .stream()
            .map(this::crearInstanciaRenglonFactura)
            .collect(Collectors.toList()))
        .build();
  }
  
  /**
   * Valida que todos los campos del dto de factura sean correctos y lanza
   * excepción si alguno de los campos es incorrecto.
   *
   * @param facturaDto objeto a validar
   */
  private void validarFacturaRequest(FacturaRequestDto facturaDto) {
    // validar si es nulo
    if (facturaDto == null) {
      throw new ValidacionExcepcion("Datos de factura no puede ser nulo", null);
    }
    // validar el cliente
    if (facturaDto.getDocumentoCliente() == null ||
        facturaDto.getDocumentoCliente().trim().isEmpty()) {
      throw new ValidacionExcepcion("El número de documento del cliente no puede ser nulo", null);
    }
    if (!clienteRepository.existsByDocumento(facturaDto.getDocumentoCliente())) {
      throw new RegistroNoExisteExcepcion(
          "No existe cliente con documento " + facturaDto.getDocumentoCliente(), null);
    }
    //validar fecha
    if (facturaDto.getFecha() == null) {
      throw new ValidacionExcepcion("La fecha de factura no peude ser nula", null);
    }
    // validar detalles
    if (facturaDto.getDetalleProductos() == null || facturaDto.getDetalleProductos().isEmpty()) {
      throw new ValidacionExcepcion("La factura debe tener al menos un producto", null);
    }
    final List<String> productosNoEncontrados = new ArrayList<>();
    facturaDto.getDetalleProductos()
        .forEach(r -> {
          validarRenglonFacturaRequestDto(r);
          final String sku = r.getCodigoProducto();
          if (sku != null &&
              (!productoRepository.existsBySku(sku) ||
                  !productoRepository.estaDisponiblePorSku(sku))) {
            productosNoEncontrados.add(sku);
          }
        });
    
    // si no se encontró algún producto o hay algún productio inactivo se da excepción
    if (!productosNoEncontrados.isEmpty()) {
      throw new RegistroNoExisteExcepcion("Los productos " + productosNoEncontrados + " no existen",
          null);
    }
  }
  
  private void validarRenglonFacturaRequestDto(RenglonFacturaRequestDto renglonDto) {
    // validar no nulo
    if (renglonDto == null) {
      throw new ValidacionExcepcion("El renglon de producto de la factura no puede ser nulo", null);
    }
    
    // validar código sku producto no nulo
    if (renglonDto.getCodigoProducto() == null || renglonDto.getCodigoProducto().trim().isEmpty()) {
      throw new ValidacionExcepcion("El código sku del producto no puede ser vacío", null);
    }
    
    // validar cantidad > 0
    if (renglonDto.getCantidad() <= 0) {
      throw new ValidacionExcepcion(
          "La cantidad de producto para el producto " + renglonDto.getCodigoProducto() +
              " debe ser mayor a 0", null);
    }
  }
  
  /**
   * Hace el redondeo de la moneda a dos dígitos
   *
   * @param x doble a redondear
   * @return valor redondeado a dos dígitos
   */
  private double redondearMoneda(double x) {
    return Math.round(x * 100.0D) / 100.0D;
  }
}
