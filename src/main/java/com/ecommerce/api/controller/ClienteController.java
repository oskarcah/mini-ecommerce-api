package com.ecommerce.api.controller;

import com.ecommerce.api.dto.ClienteDto;
import com.ecommerce.api.dto.ListaClientesDto;
import com.ecommerce.api.dto.ResultDto;
import com.ecommerce.api.entity.Cliente;
import com.ecommerce.api.exception.RegistroExisteExcepcion;
import com.ecommerce.api.exception.RegistroNoExisteExcepcion;
import com.ecommerce.api.exception.ValidacionExcepcion;
import com.ecommerce.api.repository.ClienteRepository;
import com.ecommerce.api.repository.FacturaRepository;
import jakarta.transaction.Transactional;
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
@RequestMapping("/ecommerce/clientes")
public class ClienteController {
  private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
  
  private ClienteRepository clienteRepository;
  private final FacturaRepository facturaRepository;
  
  public ClienteController(ClienteRepository clienteRepository,
                           FacturaRepository facturaRepository) {
    this.clienteRepository = clienteRepository;
    this.facturaRepository = facturaRepository;
  }
  
  /**
   * Endpoint que devuelve todos los clientes
   *
   * @return Response con Objeto que contiene lista de clientes
   */
  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ListaClientesDto> clientes() {
    log.info("Inicio carga todos los clientes");
    // se convierten todos los clientes a ClienteDto desde el
    // resultado de la query
    final List<Cliente> clientes =
        clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "documento"));
    log.info("Fin carga todos los clientes, se devuelve Ok");
    // crear response
    return ResponseEntity.ok(
        ListaClientesDto
            .builder()
            .clientes(clientes
                .stream()
                .map(this::crearClienteDto)
                .collect(Collectors.toList()))
            .build());
  }
  
  /**
   * Endpoint que devuelve un objeto cliente por Número de documento
   *
   * @param documento String con el número de documento
   * @return Response con instancia de objeto cliente
   */
  @GetMapping(
      value = "/{documento}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClienteDto> clientePorDocumento(
      @PathVariable("documento") String documento) {
    log.info("Inicio carga cliente para documento =" + documento);
    // buscar el cliente
    final var optClient = clienteRepository.findByDocumento(documento);
    if (optClient.isEmpty()) {
      log.info("No existe cliente para documento =" + documento + ", se devuelve Not found");
      throw new RegistroNoExisteExcepcion("Cliente con documento " + documento + " no existe",
          null);
    }
    
    log.info("Fin carga carga cliente para documento =" + documento + ", se devuelve Ok");
    // crear response
    return ResponseEntity.ok(crearClienteDto(optClient.get()));
  }
  
  /**
   * Crea un nuevo cliente en BD con los datos contenidos en el objeto,
   * de acuerdo al número de documento del campo NumeroDocumento
   *
   * @param clienteDto Instancia con los datos del cliente a crear
   * @return Response con el resultado de la inserción
   */
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResultDto> crearCliente(@RequestBody ClienteDto clienteDto) {
    log.info("Inicio crear cliente para request =" + clienteDto);
    // validar datos del request
    validarCliente(clienteDto);
    // validar si existe
    validarNoExisteDocumento(clienteDto.getNumeroDocumento());
    
    // actualiar el cliente
    final Cliente c = clienteRepository.save(Cliente
        .builder()
        .documento(clienteDto.getNumeroDocumento())
        .nombre(clienteDto.getNombre())
        .direccion(clienteDto.getDireccion())
        .telefono(clienteDto.getNumeroTelefono())
        .build());
    log.info("Fin crear cliente se creó cliente con id =" + c.getId() + ", se devuelve Ok");
    // crear response
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResultDto
            .builder()
            .message("Cliente con documento " + clienteDto.getNumeroDocumento() + " registrado")
            .build());
  }
  
  /**
   * Actualiza datos de un  cliente existente en BD
   * con los datos contenidos en el objeto,
   * de acuerdo al número de documento del campo NumeroDocumento
   *
   * @param clienteDto Instancia con los datos del cliente a actualizar
   * @return Response con el resultado de la actualzación
   */
  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  public ResponseEntity<ResultDto> actualizarCliente(@RequestBody ClienteDto clienteDto) {
    log.info("Inicio actualizar cliente para request =" + clienteDto);
    // validar datos del request
    validarCliente(clienteDto);
    // validar que el documento no exista
    validarExisteDocumento(clienteDto.getNumeroDocumento());
    // actualizar
    clienteRepository.actualizarCliente(clienteDto);
    log.info("Fin actualzar cliente, se actualizó cliente con documento =" +
        clienteDto.getNumeroDocumento() + ", se devuelve Ok");
    // crear respuesta
    return ResponseEntity.ok(ResultDto
        .builder()
        .message("cliente actualizado")
        .build());
  }
  
  /**
   * Elimina de BD el cliente con el documento dado
   *
   * @param documento String con el número de documento
   * @return Response con el resultado de la eliminación
   */
  @DeleteMapping(
      value = "/{documento}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  public ResponseEntity<ResultDto> borrarCliente(@PathVariable("documento") String documento) {
    log.info("Inicio eliminar cliente con documento =" + documento);
    // validar que exista
    validarExisteDocumento(documento);
    // validar que el cliente no tenga facturas
    if (facturaRepository.clienteTieneFacturas(documento)) {
      throw new RegistroExisteExcepcion("El cliente con documento " + documento +
          " tiene facturas registradas. No se puede eliminar", null);
    }
    // borrar el registro
    clienteRepository.deleteByDocumento(documento);
    log.info("Fin eliminar cliente con documento =" + documento + ", se devuelve Ok");
    // crear respuesta
    return ResponseEntity.ok(ResultDto
        .builder()
        .message("cliente eliminado")
        .build());
  }
  
  /**
   * Validador de la existencia de un documento en BD, lanza excepción si
   * no es válido
   *
   * @param documento número de documento a validar
   */
  private void validarExisteDocumento(String documento) {
    if (!clienteRepository.existsByDocumento(documento)) {
      throw new RegistroExisteExcepcion(
          "No existe ningún cliente con documento " + documento, null);
    }
  }
  
  /**
   * Validador de que  un documento en BD no exista, lanza excepción si
   * no es válido
   *
   * @param documento número de documento a validar
   */
  private void validarNoExisteDocumento(String documento) {
    if (clienteRepository.existsByDocumento(documento)) {
      throw new RegistroExisteExcepcion(
          "Ya existe un cliente con documento " + documento, null);
    }
  }
  
  /**
   * Convierte de Cliente a Cliente Dto
   *
   * @param c objeto Cliente a convertir
   * @return objeto ClienteDto
   */
  private ClienteDto crearClienteDto(Cliente c) {
    return ClienteDto
        .builder()
        .numeroDocumento(c.getDocumento())
        .nombre(c.getNombre())
        .numeroTelefono(c.getTelefono())
        .direccion(c.getDireccion())
        .build();
  }
  
  /**
   * Valida que todos los campos del cliente sean correctos y lanza
   * excepción si alguno de los campos es incorrecto.
   *
   * @param clienteDto objeto a validar
   */
  private void validarCliente(ClienteDto clienteDto) {
    if (clienteDto == null) {
      throw new ValidacionExcepcion("Datos del cliente no puede ser nulo", null);
    }
    // validar documento no sea vacio
    if (clienteDto.getNumeroDocumento() == null ||
        clienteDto.getNumeroDocumento().trim().isEmpty()) {
      throw new ValidacionExcepcion("El numero de documento no puede ser vacio", null);
    }
    // validar nombre que no sea vacio
    if (clienteDto.getNombre() == null || clienteDto.getNombre().trim().isEmpty()) {
      throw new ValidacionExcepcion("El nombre no puede ser vacio", null);
    }
    // validar direccion que no sea vacia
    if (clienteDto.getNombre() == null || clienteDto.getNombre().trim().isEmpty()) {
      throw new ValidacionExcepcion("La direccion no puede ser vacia", null);
    }
    // validar telefono que sea solo dígitos
    if (clienteDto.getNumeroTelefono() == null ||
        !clienteDto.getNumeroTelefono().trim().matches("^\\d+$")) {
      throw new ValidacionExcepcion("La direccion no puede ser vacío y consta de solo dígitos",
          null);
    }
  }
}
