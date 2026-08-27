package resol.castroj.modelo;

import resol.castroj.ConexionBD;
import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//clase videojuego --> molde 
public class Videojuego {

    private int id;
    private String nombre;
    private String genero;
    private double precio;

    //atributos implementados en el tp6 
    private int unidadesDisponibles;
    private int nivelReposicion;
    private int suspendido;


    //contructor vacio, nos sirve para crear un videojeugo pero sin cargarle datos

    public Videojuego() {
    }


    //constructor, pero sin id, lo vamos a utilizar cunado queramos crear un videojuego de 0 (hacer un insert)
    public Videojuego(String nombre, String genero, double precio, int unidadesDisponibles, int nivelReposicion, int suspendido) {

        this.nombre = nombre;
        this.genero = genero;
        this.precio = precio;
        this.unidadesDisponibles = unidadesDisponibles;
        this.nivelReposicion = nivelReposicion;
        this.suspendido = suspendido;

    }


    // Constructor con id, lo vamos a usar para trabajar con videojuegos q ya tiene id, osea q ya fueron insertamos
    //como puede ser una modificacion, una consulta, un delete, etc
    public Videojuego( int id, String nombre, String genero, double precio, int unidadesDisponibles, int nivelReposicion, int suspendido) {

        this.id = id;
        this.nombre = nombre;
        this.genero = genero;
        this.precio = precio;
        this.unidadesDisponibles = unidadesDisponibles;
        this.nivelReposicion = nivelReposicion;
        this.suspendido = suspendido;

    }


    // creamos la tabla si no existe
    public static void crearTabla() {

        String sql = """
                CREATE TABLE IF NOT EXISTS videojuegos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    genero VARCHAR(50) NOT NULL,
                    precio DECIMAL(10,2) NOT NULL,
                    unidades_disponibles INT NOT NULL,
                    nivel_reposicion INT NOT NULL,
                    suspendido INT NOT NULL
                )
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                Statement stmt = conn.createStatement()
        ) {

            stmt.execute(sql);

            System.out.println(
                    "Tabla videojuegos creada correctamente."
            );

        } catch (SQLException e) {

            System.out.println(
                    "Error al crear tabla videojuegos: "
                    + e.getMessage()
            );
        }
    }


    //validar precio, stockl
    private void validar()
            throws ReglaNegocioException {

        // El precio debe ser mayor que cero.
        if (precio <= 0) {

            throw new ReglaNegocioException(
                    "El precio debe ser mayor que cero."
            );
        }

        // El stock no puede ser negativo.
        if (unidadesDisponibles < 0) {

            throw new ReglaNegocioException(
                    "Las unidades disponibles no pueden ser negativas."
            );
        }

        // Suspendido solamente puede valer 0 o 1.
        if (suspendido != 0 && suspendido != 1) {

            throw new ReglaNegocioException(
                    "Suspendido debe ser 0 o 1."
            );
        }
    }


    //regla de reposicion 
    public boolean necesitaReposicion() {

        return unidadesDisponibles < nivelReposicion;
    }


    // 1 = disponible
    // 0 = no disponible
    public boolean estaDisponible() {

        return suspendido == 1;
    }


    //creamos videojuego
    public void crearVideojuego()
            throws SQLException,
            ReglaNegocioException {

        validar();

        String sql = """
                INSERT INTO videojuegos
                (
                    nombre,
                    genero,
                    precio,
                    unidades_disponibles,
                    nivel_reposicion,
                    suspendido
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            // cada uno de los set completa uno de los ?.
            ps.setString(1, nombre);
            ps.setString(2, genero);
            ps.setDouble(3, precio);
            ps.setInt(4, unidadesDisponibles);
            ps.setInt(5, nivelReposicion);
            ps.setInt(6, suspendido);

            ps.executeUpdate();
        }
    }


    //listar todos los videojuegos
    public static void listarVideojuegos()
            throws SQLException {

        String sql =
                "SELECT * FROM videojuegos";

        try (
                Connection conn = ConexionBD.obtenerConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                System.out.println("--------------------------------");

                System.out.println( "ID: " + rs.getInt("id") );

                System.out.println( "Nombre: " + rs.getString("nombre") );

                System.out.println( "Género: " + rs.getString("genero") );

                System.out.println( "Precio: $" + rs.getDouble("precio") );

                System.out.println( "Stock: " + rs.getInt("unidades_disponibles") );

                System.out.println( "Nivel reposición: " + rs.getInt("nivel_reposicion") );

                System.out.println( "Disponible: " + rs.getInt("suspendido") );
            }
        }
    }


    // buscar videojuegos por id
    public static Videojuego buscarPorId(int id)
            throws SQLException,
            VideojuegoNoEncontradoException {

        String sql = "SELECT * FROM videojuegos WHERE id = ?";

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Videojuego(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("genero"),
                        rs.getDouble("precio"),
                        rs.getInt("unidades_disponibles"),
                        rs.getInt("nivel_reposicion"),
                        rs.getInt("suspendido")
                );
            }
        }

        //lanzar un mensaje si no existe ese id
        throw new VideojuegoNoEncontradoException(
                "No existe un videojuego con el ID indicado."
        );
    }


    // actualizar un videojuego
    public void actualizarVideojuego()
            throws SQLException,
            VideojuegoNoEncontradoException,
            ReglaNegocioException {

        validar();

        String sql = """
                UPDATE videojuegos
                SET nombre = ?,
                    genero = ?,
                    precio = ?,
                    unidades_disponibles = ?,
                    nivel_reposicion = ?,
                    suspendido = ?
                WHERE id = ?
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, nombre);
            ps.setString(2, genero);
            ps.setDouble(3, precio);
            ps.setInt(4, unidadesDisponibles);
            ps.setInt(5, nivelReposicion);
            ps.setInt(6, suspendido);
            ps.setInt(7, id);

            int filas = ps.executeUpdate();

            if (filas == 0) {

                throw new VideojuegoNoEncontradoException(
                        "No existe un videojuego con el ID indicado."
                );
            }
        }
    }


    // eliminar un videojuego
    public static void eliminarVideojuego(int id)
            throws SQLException,
            VideojuegoNoEncontradoException {

        String sql =
                "DELETE FROM videojuegos WHERE id = ?";

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            int filas = ps.executeUpdate();

            if (filas == 0) {

                throw new VideojuegoNoEncontradoException(
                        "No existe un videojuego con el ID indicado."
                );
            }
        }
    }


    // listar todos los videojuegos disponibles
    public static void listarDisponibles()
            throws SQLException {

        String sql = """
                SELECT *
                FROM videojuegos
                WHERE suspendido = 1
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                System.out.println(
                        rs.getInt("id")
                        + " - "
                        + rs.getString("nombre")
                );
            }
        }
    }


    //listar reposicion, debemos mostrar cuales se estan quedando sin stocl y deberiamos reponerlos 
    public static void listarReposicion()
            throws SQLException {

        String sql = """
                SELECT *
                FROM videojuegos
                WHERE unidades_disponibles < nivel_reposicion
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                System.out.println(
                        rs.getString("nombre")
                        + " - Stock: "
                        + rs.getInt("unidades_disponibles")
                        + " - Nivel reposición: "
                        + rs.getInt("nivel_reposicion")
                );
            }
        }
    }


    // metodo q nos ayduara a descontar del stock
    public static void descontarStock( int id, int cantidad ) throws SQLException {

        String sql = """
                UPDATE videojuegos
                SET unidades_disponibles =
                    unidades_disponibles - ?
                WHERE id = ?
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, cantidad);
            ps.setInt(2, id);

            ps.executeUpdate();
        }
    }


    //getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getUnidadesDisponibles() {
        return unidadesDisponibles;
    }

    public void setUnidadesDisponibles(int unidadesDisponibles) {
        this.unidadesDisponibles = unidadesDisponibles;
    }

    public int getNivelReposicion() {
        return nivelReposicion;
    }

    public void setNivelReposicion(int nivelReposicion) {
        this.nivelReposicion = nivelReposicion;
    }

    public int getSuspendido() {
        return suspendido;
    }

    public void setSuspendido(int suspendido) {
        this.suspendido = suspendido;
    }
}