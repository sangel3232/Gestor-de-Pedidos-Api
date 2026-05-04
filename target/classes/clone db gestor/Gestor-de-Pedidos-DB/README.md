# 📦 Gestor de Pedidos - Base de Datos

Solución completa de base de datos para el sistema de gestión de pedidos, incluyendo esquema, datos iniciales, funciones, triggers y migraciones controladas con **Liquibase**.

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Requisitos Previos](#-requisitos-previos)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Instalación](#-instalación)
- [Migraciones](#-migraciones)
- [Esquema de Base de Datos](#-esquema-de-base-de-datos)
- [Contribución](#-contribución)

## 🎯 Descripción

Este repositorio contiene todas las definiciones de base de datos para la aplicación **Gestor de Pedidos**, incluyendo:

- ✅ Esquema inicial de tablas
- ✅ Datos de precarga
- ✅ Sistema de auditoría
- ✅ Funciones y triggers
- ✅ Gestión de carrito y pagos
- ✅ Gestión de usuarios
- ✅ Reembolsos y devoluciones
- ✅ Metadata de pedidos

## 📚 Requisitos Previos

- **Java 8+** (para ejecutar Liquibase)
- **Maven 3.6+** (recomendado)
- **Base de Datos**: PostgreSQL 12+ (o compatible)
- **Liquibase 4.0+** (incluido en las dependencias del proyecto)

## 📁 Estructura del Proyecto

```
Gestor-de-Pedidos-DB/
├── README.md                          # Este archivo
├── db/
│   ├── changelog/
│   │   ├── db.changelog-master.xml   # Fichero maestro de cambios
│   │   └── changes/
│   │       ├── 001-schema-inicial.xml           # Tablas principales
│   │       ├── 002-inserts-datos.xml            # Datos iniciales
│   │       ├── 003-auditoria-tabla.xml          # Sistema de auditoría
│   │       ├── 004-funciones-triggers.xml       # Funciones y triggers
│   │       ├── 005-carrito-pago.xml             # Carrito y pagos
│   │       ├── 006-usuarios.xml                 # Gestión de usuarios
│   │       ├── 007-fix-enum-columns.xml         # Ajustes de enumeraciones
│   │       ├── 008-pedido-metadata.xml          # Metadata de pedidos
│   │       └── 008-reembolso.xml                # Sistema de reembolsos
│   └── migration/                     # Scripts SQL generados por Liquibase
└── ...
```

## 🚀 Instalación

### 1. Clonar el Repositorio

```bash
git clone <tu-repo-url>
cd Gestor-de-Pedidos-DB
```

### 2. Configurar Base de Datos

Crear base de datos PostgreSQL:

```sql
CREATE DATABASE gestor_pedidos;
```

### 3. Ejecutar Migraciones

Usando Maven (recomendado):

```bash
mvn liquibase:update -Dliquibase.url=jdbc:postgresql://localhost:5432/gestor_pedidos \
                    -Dliquibase.username=tu_usuario \
                    -Dliquibase.password=tu_password
```

O usando Liquibase CLI directamente:

```bash
liquibase --changeLogFile=db/changelog/db.changelog-master.xml \
          --url=jdbc:postgresql://localhost:5432/gestor_pedidos \
          --username=tu_usuario \
          --password=tu_password \
          update
```

## 📊 Migraciones

Las migraciones se ejecutan en orden secuencial:

| # | Archivo | Descripción |
|---|---------|-------------|
| 001 | `001-schema-inicial.xml` | Creación de tablas principales |
| 002 | `002-inserts-datos.xml` | Inserción de datos iniciales |
| 003 | `003-auditoria-tabla.xml` | Tabla de auditoría |
| 004 | `004-funciones-triggers.xml` | Funciones y triggers |
| 005 | `005-carrito-pago.xml` | Tablas de carrito y pagos |
| 006 | `006-usuarios.xml` | Gestión de usuarios |
| 007 | `007-fix-enum-columns.xml` | Ajustes de tipos enum |
| 008 | `008-pedido-metadata.xml` | Metadata de pedidos |
| 009 | `008-reembolso.xml` | Sistema de reembolsos |

### Ver Estado de Migraciones

```bash
mvn liquibase:status
```

### Deshacer Últimas Migraciones

```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

## 📈 Esquema de Base de Datos

El esquema incluye las siguientes entidades principales:

- **pedidos** - Órdenes de compra
- **usuarios** - Gestión de usuarios
- **productos** - Catálogo de productos
- **carrito** - Carrito de compras
- **pagos** - Transacciones de pago
- **reembolsos** - Gestión de devoluciones
- **auditoria** - Log de cambios en base de datos

Para ver el esquema completo, consulta los archivos XML en `db/changelog/changes/`.

## 🤝 Contribución

Para contribuir a este proyecto:

1. **Fork** el repositorio
2. Crea una **rama** para tu feature (`git checkout -b feature/nueva-migracion`)
3. Crea un nuevo archivo de migración en `db/changelog/changes/` con formato: `XXX-descripcion.xml`
4. Añade la referencia en `db.changelog-master.xml`
5. **Commit** tus cambios (`git commit -m 'Añadir nueva migración: ...'`)
6. **Push** a la rama (`git push origin feature/nueva-migracion`)
7. Abre un **Pull Request**

### Convenciones de Migraciones

- Usa números secuenciales para nombrar archivos: `001`, `002`, etc.
- Cada archivo debe contener un cambio lógico único
- Incluye rollback cuando sea posible
- Documenta cambios importantes en comentarios

## 📝 Licencia

Este proyecto es parte de **Gestor de Pedidos**. Consulta el fichero de licencia en la raíz del proyecto principal.