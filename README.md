# API Ecommerce

Just another Ecommerce API


# Directorios


## Directorio del proyecto

```
api/
```

## Directorio con archivo Postman

```
postman/
```

# Construcción 

##  Comando para compilar y generar jar

Se debe abrir una consola de línea de comandos sobre el directorio `api/`

###  Windows
```
mvnw clean package
```

### Linux
```
./mvnw clean package
```

La compilación del `jar` queda en el directorio `target`.


# Ejecución Aplicación


## Por línea de comando 

### Windows
```
mvnw spring-boot:run
```

### Linux
```
./mvnw spring-boot:run
```

Se mantiene la salida del log en la consola donde se arrancó el programa.

Por defecto la app abre en http://localhost:8080

El puerto se modifica a través de la propiedad `server.port`


# Base de datos


Como viene la configuración  en `application.properties`
por defecto funciona en mySQL 8. Debe tener un usuario con permiso de creación de Bases de datos y reemplazar nombre de BD y credenciales por los valores actuales del servidor. La BD se debe se destruye si existe y  crea en el arranque  de la aplicación.

 INICIO Configuración de BD MySQL
 Descomentar si se va a usar mySQL
```
# URL + nombre BD
spring.datasource.url= jdbc:mysql://localhost:3306/sistema_facturacion?createDatabaseIfNotExist=true
# Credenciales
spring.datasource.username=root
spring.datasource.password=Root.123
# Dialecto mysql
# Si tiene mySQL 5 sería org.hibernate.dialect.MySQL5Dialect
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL8Dialect
# FIN Configuración de BD MySQL

# Como se crea la BD
# - none = no se crea, se asume que existe
# - update = se actualiza si hay cambios en las tablas respecto a los objetos entity
# - drop = Borra la BD si existe y la crea nueva
# - create = crea BD nueva
# en el caso de usar H2 se puede comentar, pues la BD en memoria se crea siempre
spring.jpa.hibernate.ddl-auto=create
```

Una vez creada la BD si desea que esta no se borre y se regenere durante el  arranque de la aplicación debe cambiar el valor de la propiedad 
`spring.jpa.hibernate.ddl-auto` a `none`

Si se desea usar la BD embebida en memora _H2_, se deben comentar todas las líneas  anteriores que involucran config de mysql. 


# Datos de prueba

Para generar datos de prueba iniciales (seed) debe usarse el método run de  la clase  `com.ecommerce.api.RunnerCargaDataInicialDB`. En el código fuente de la clase hay ejemplo de gneeración de algunos clientes y productos.

# Documentación API

Se suministra un archivo de Postman (`postman/Ecommerce.postman_collection.json`) con un espcio de trabajo que contiene request de ejemplo para el consumo de api. 

En todos los endpoints debe ir el encabezado `Content-Type: Application/json`

En los endpoints que tienen data de entrada en el cuerpo de la request debe ir el encabezado `Accept: Application/json`

# Descripción endpoints

Descripción de clientes  a través de ejemplos. El valor `${URL_BASE}` indica la dirección base del servidor donde será desplegado el API (Por ejemplo `URL_BASE = http://localhost:8080/ecommerce`  el directorio `ecommerce` es la base de todos los endpoints tal como se define en el código de los controladores.

## Clientes
  
### Lista de todos los clientes

```
GET ${URL_BASE}/clientes
```

###  Busca cliente por documento de identidad

```
GET http://localhost:8080/ecommerce/clientes/{id_cliente}
```

Ejemplo: 

Busca datos del cliente con documento `2333`
```
GET http://localhost:8080/ecommerce/clientes/2333
```

### Crea cliente nuevo

```
POST GET http://localhost:8080/ecommerce/clientes
```
La información del cliente a crear va en el cuerpo de la petición.

Ejemplo de cuerpo de la petición

```
{
  "numeroDocumento": "342",
  "nombre": "pablo jimenez",
  "direccion": "calle principal 01",
  "numeroTelefono": "322000000"
}
```

Registra nuevo cliente con documento `342`

### Actualiza cliente existente

```
PUT  http://localhost:8080/ecommerce/clientes
```

Los datos a actualizar del cliente van en el cuerpo de la petición. Se aplica actualización de acuerdo al valor del atributo  `numeroDocumento` del objeto en JSON.

Ejemplo de cuerpo de la petición:
```
{
  "numeroDocumento": "342",
  "nombre": "nombre nuevo",
  "direccion": "direccion nueva",
  "numeroTelefono": "telefono nuevo"
}
```

Actualiza en BD los atributos del cliente existente con documento `342`  a los valores en el objeto.


### Elimina cliente
```
DELETE GET http://localhost:8080/ecommerce/clientes/{idCliente}
```

Ejemplo

```
DELETE http://localhost:8080/ecommerce/clientes/2333
```

Elimina el cliente existente con documento `2333` a base de datos.

## Productos

### Lista de todos los clientes


```
GET http://localhost:8080/ecommerce/productos
```

### Busca producto por codigo sku

```
GET http://localhost:8080/ecommerce/productos/{sku}
```

Ejemplo: 

Busca datos del producto con código SKU `P001`

```
GET http://localhost:8080/ecommerce/productos/P001
```

###  Crea producto nuevo
```
POST http://localhost:8080/ecommerce/productos
```

La información del producto a crear va en el cuerpo de la petición.

Ejemplo de cuerpo de la petición

```
{
  "codigo": "P0001",
  "descripcion": "Pantalón negro caballero",
  "precio": 125.00,
  "disponible": true
}
```


Registra nuevo producto con código SKU `P0001`


###  Actualiza producto existente

```
PUT GET http://localhost:8080/ecommerce/productos
```

Los datos a actualizar del cliente van en el cuerpo de la petición. Se aplica actualización de acuerdo al valor del atributo  `codigo` del objeto en JSON.

Ejemplo de cuerpo de la petición

```
{
  "codigo": "P0001",
  "descripcion": "Pantalón negro caballero",
  "precio": 125.00,
  "disponible": true
}
```

Actualiza en BD los atributos del producto existente con documento `P0001`  a los valores en el objeto.


###  Elimina producto

```
DELETE GET http://localhost:8080/ecommerce/productos/{sku}
```

Ejemplo
```
DELETE http://localhost:8080/ecommerce/productos/P0001
```

Elimina el cliente existente con código SKU `P0001` a base de datos.

### Activación producto 

Setea el atributo `activo` a `true`, lo cual hace que este producto esté disponible para nuevas facturas.

```
PUT http://localhost:8080/ecommerce/productos/{sku}/activar
```

Ejemplo
```
http://localhost:8080/ecommerce/productos/P001/activar
```


El producto existente con código SKU `P001` queda en estado  _activo_.


###  Desactivación producto 

Setea el atributo `activo` a `false`, lo cual hace que este producto no esté disponible para nuevas facturas.

```
PUT http://localhost:8080/ecommerce/productos/{sku}/desactivar
```

Ejemplo

```
http://localhost:8080/ecommerce/productos/P001/desactivar
```

El producto existente con código SKU `P001` queda en estado _inactivo_.


# Facturas
  
##  Crear factura

Crea un nuevo registro de factura para un cliente, con sus productos y detalle de cada producto.  

```
POST http://localhost:8080/ecommerce/facturas
```

Ejemplo de cuerpo de la petición.

El cliente debe existir en BD. Todos los productos deben existir en BD y estar en estado _activo_

```
{
  "fecha": "2023-03-25",
  "documentoCliente": "123",
  "detalleProductos": [
    {
      "codigoProducto": "P012",
      "cantidad": 2
    },
    {
      "codigoProducto": "P014",
      "cantidad": 3
    },
    {
      "codigoProducto": "P001",
      "cantidad": 5
    }
  ]
}
```

Ejemplo de respuesta:

```
{
  "message": "Creada factura número 2",
  "factura": {
    "numero": 2,
    "fecha": "2023-03-25",
    "estatus": "PENDIENTE_PAGO",
    "cliente": {
      "numeroDocumento": "123",
      "nombre": "pedro perez",
      "direccion": "calle 1",
      "numeroTelefono": "2111234567"
    },
    "detalle": [
      {
        "producto": {
          "codigo": "P012",
          "descripcion": "Camiseta negra manga larga unisex",
          "precio": 39.0,
          "disponible": null
        },
        "cantidad": 2,
        "precioUnitario": 39.0,
        "precio": 78.0
      },
      {
        "producto": {
          "codigo": "P014",
          "descripcion": "Camisa estampada cuadros caballero",
          "precio": 48.25,
          "disponible": null
        },
        "cantidad": 3,
        "precioUnitario": 48.25,
        "precio": 144.75
      },
      {
        "producto": {
          "codigo": "P001",
          "descripcion": "Pantalón negro casual dama",
          "precio": 50.55,
          "disponible": null
        },
        "cantidad": 5,
        "precioUnitario": 50.55,
        "precio": 252.75
      }
    ],
    "total": 475.5
  }
}
```

## Buscar factura por número

```
GET http://localhost:8080/ecommerce/facturas/{numero}
```

Ejemplo
```
GET http://localhost:8080/ecommerce/facturas/1
```

Devuelve objeto JSON con la información de la factura No 1.


## Cambiar estatus factura

Posibles estatus (clase EstatusFactura):
   - `PENDIENTE_PAGO`
   - `ANULADA`
   - `PAGADA`
   

```  
PUT http://localhost:8080/ecommerce/facturas/{numero}/estatus/{estatus}
```

Ejemplo:
```
PUT http://localhost:8080/ecommerce/facturas/1/estatus/PAGADA
```

Actualiza la factura No 1 existente a estatus PAGADA.






