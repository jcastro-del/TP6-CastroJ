package resol.castroj.modelo;

import resol.castroj.ConexionBD;
import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VentaInvalidaException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

//clase venta
public class Venta {

    private int id;
    private Date fecha;
    private int cantidad;
    private int videojuegoId;


    public Venta() {
    }


    public Venta( Date fecha, int cantidad, int videojuegoId ) {

        this.fecha = fecha;
        this.cantidad = cantidad;
        this.videojuegoId = videojuegoId;

    }


    
    //creamos la tabla si no existe 
    public static void crearTabla() {

        String sql = """
                CREATE TABLE IF NOT EXISTS ventas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    fecha DATE NOT NULL,
                    cantidad INT NOT NULL,
                    videojuego_id INT NOT NULL,

                    FOREIGN KEY (videojuego_id)
                    REFERENCES videojuegos(id)
                )
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                Statement stmt = conn.createStatement()
        ) {

            stmt.execute(sql);

            System.out.println( "Tabla ventas creada correctamente." );

        } catch (SQLException e) {

            System.out.println( "Error al crear tabla ventas: " + e.getMessage()
            );
        }
    }


    
    //calculamos el descuento 
    public static double calcularDescuento(int cantidad) {

        if (cantidad <= 1) {
            return 0;
        }

        if (cantidad <= 4) {
            return 0.05;
        }

        if (cantidad <= 9) {
            return 0.10;
        }

        return 0.15;
    }


    //resgistrar una venta 
    public void registrarVenta()
            throws SQLException,
            VentaInvalidaException,
            VideojuegoNoEncontradoException,
            ReglaNegocioException {

        //validamos q la cantidad sea mayor a 0
        if (cantidad <= 0) {

            throw new VentaInvalidaException(
                    "La cantidad vendida debe ser mayor que cero."
            );
        }


        //validar la fecha futura
        if (fecha.toLocalDate().isAfter(LocalDate.now())) {

            throw new VentaInvalidaException(
                    "La fecha de venta no puede ser futura."
            );
        }


        //buscamos el videojuego
        Videojuego videojuego = Videojuego.buscarPorId(videojuegoId);


        // verificar si se encuentra disponible
        if (!videojuego.estaDisponible()) {

            throw new ReglaNegocioException(
                    "El videojuego no está disponible para la venta."
            );
        }


        // Verificar stock.
        if (videojuego.getUnidadesDisponibles() < cantidad) {

            throw new ReglaNegocioException(
                    "No hay stock suficiente."
            );
        }


        // calculamos el subtotal
        double subtotal = videojuego.getPrecio() * cantidad;


        //obtener el porcentaje de descuento
        double descuento = calcularDescuento(cantidad);


        // calcular el total
        double total = subtotal - (subtotal * descuento);


        //guardar la venta
        String sql = """
                INSERT INTO ventas
                (fecha, cantidad, videojuego_id)
                VALUES (?, ?, ?)
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setDate(1, fecha);
            ps.setInt(2, cantidad);
            ps.setInt(3, videojuegoId);

            ps.executeUpdate();
        }


        // desoontar del stock 
        Videojuego.descontarStock( videojuegoId, cantidad );


        //moestrar resultado.
        System.out.println( "Venta registrada correctamente." );

        System.out.println(
                "Descuento aplicado: "
                + (descuento * 100)
                + "%"
        );

        System.out.println( "Total: $" + total );
    }


    // listar todas las ventas 
    public static void listarVentas()
            throws SQLException {

        String sql = """
                SELECT
                    v.id,
                    v.fecha,
                    v.cantidad,
                    v.videojuego_id,
                    j.nombre,
                    j.precio
                FROM ventas v, videojuegos j
                WHERE v.videojuego_id = j.id
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                int cantidad = rs.getInt("cantidad");

                double precio = rs.getDouble("precio");

                double descuento = calcularDescuento(cantidad);

                double subtotal =  precio * cantidad;

                double total = subtotal - (subtotal * descuento);


                System.out.println("--------------------------------");

                System.out.println( "ID: " + rs.getInt("id") );

                System.out.println( "Fecha: " + rs.getDate("fecha") );

                System.out.println( "Videojuego: " + rs.getString("nombre") );

                System.out.println( "Cantidad: " + cantidad );

                System.out.println( "Descuento: " + (descuento * 100) + "%" );

                System.out.println( "Total: $" + total );
            }
        }
    }


    //buscar venta por id
    public static void buscarVentaPorId(int id)
            throws SQLException {

        String sql =
                "SELECT * FROM ventas WHERE id = ?";

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println( "Venta N°" + rs.getInt("id") );

                System.out.println( "Fecha: " + rs.getDate("fecha") );

                System.out.println( "Cantidad: " + rs.getInt("cantidad") );

                System.out.println( "Videojuego ID: " + rs.getInt("videojuego_id") );

            } else {

                System.out.println( "No existe una venta con ese ID." );
            }
        }
    }


    //buscamos las ventas de un videojuego
    public static void buscarVentasPorVideojuego(
            int videojuegoId
    )
            throws SQLException,
            VideojuegoNoEncontradoException {

        Videojuego videojuego = Videojuego.buscarPorId(videojuegoId);

        System.out.println( videojuego.getNombre() );


        String sql = """
                SELECT *
                FROM ventas
                WHERE videojuego_id = ?
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, videojuegoId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                System.out.println("Venta N°"+ rs.getInt("id"));

                System.out.println("Fecha: "+ rs.getDate("fecha"));

                System.out.println( "Cantidad: "+ rs.getInt("cantidad"));

                System.out.println( "----------------------------");
            }
        }
    }



    //reporte del mes actual
    public static void reporteMesActual()
            throws SQLException {

        // MONTH(CURRENT_DATE) obtiene el mes actual.
        // YEAR(CURRENT_DATE) obtiene el año actual.
        String sql = """
                SELECT *
                FROM ventas
                WHERE MONTH(fecha) = MONTH(CURRENT_DATE)
                AND YEAR(fecha) = YEAR(CURRENT_DATE)
                """;

        try (
                Connection conn = ConexionBD.obtenerConexion();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            System.out.println("=== VENTAS DEL MES ACTUAL ===");

            while (rs.next()) {

                System.out.println( "Venta N°" + rs.getInt("id") );

                System.out.println( "Fecha: " + rs.getDate("fecha") );

                System.out.println("Cantidad: "+ rs.getInt("cantidad"));

                System.out.println("Videojuego ID: "+ rs.getInt("videojuego_id"));

                System.out.println("----------------------------");
            }
        }
    }


    //getters y setters 
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getVideojuegoId() {
        return videojuegoId;
    }

    public void setVideojuegoId(int videojuegoId) {
        this.videojuegoId = videojuegoId;
    }
}