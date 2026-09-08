package resol.castroj.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import resol.castroj.ConexionBD;
import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VentaInvalidaException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;
import resol.castroj.modelo.Venta;
import resol.castroj.modelo.Videojuego;


//implementamos el dao de ventas 
//ventadao nos decia q metodos tenian q existir y ventadaoimpl nos dice como implementarlo 
public class VentaDaoImpl implements VentaDao {

    //necesitamos buscar juegos cuando registramos una venta, usamos la interfaz de videojuegodao 
    private VideojuegoDao videojuegoDao = new VideojuegoDaoImpl();

    //listar todas las ventas
    @Override
    public List<Venta> listarVentas() throws SQLException {

        String sql = "SELECT * FROM ventas";

        //creamos lista vacia donde vamos guardando las ventas encontradas
        List<Venta> ventas = new ArrayList<>();

        try (

                // abrimos conexión 
                Connection conexion = ConexionBD.obtenerConexion();

                // preparamos el select 
                PreparedStatement statement = conexion.prepareStatement(sql);

                //ejecutamos la consulta 
                //resulset contiene todas las filas encontradas
                ResultSet resultado = statement.executeQuery()

        ) {

            //recorremos las filas, una por una
            while (resultado.next()) {

                //creamos una venta vacia, usamos constructor vacio por q mi clase venta no tiene un constructor q reciba id 
                Venta venta = new Venta();


                // tomamos el id de la fila y lo guardamos en el objeto.
                venta.setId( resultado.getInt("id"));

                // tomamos la fecha
                venta.setFecha( resultado.getDate("fecha"));

                //tomamos cantidad vendida 
                venta.setCantidad( resultado.getInt("cantidad"));

                // tomamos el id del videojuego vendido
                venta.setVideojuegoId( resultado.getInt( "videojuego_id"));

                // agregamos la venta a nuestra lista.
                ventas.add(venta);
            }
        }


        // devolvemos todas las ventas
        return ventas;
    }

    //buscar una venta por el id 
    @Override
    public Venta obtenerPorId(long id) throws SQLException {

        //buscamos solamente la venta q coincida con el id 
        String sql = "SELECT * FROM ventas " + "WHERE id = ?";

        try (

                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql)

        ) {

            // reemplazamos el ? por el ID recibido.
            statement.setLong(1, id);

            try (
                    ResultSet resultado = statement.executeQuery()
            ) {


                // si encontramos una fila
                if (resultado.next()) {

                    // creamos una venta vacia 
                    Venta venta = new Venta();

                    //le cargamos sus datos 
                    venta.setId(resultado.getInt("id"));
                    venta.setFecha(resultado.getDate("fecha"));
                    venta.setCantidad(resultado.getInt("cantidad"));
                    venta.setVideojuegoId(resultado.getInt("videojuego_id"));

                    // devolvemos la venta encontrada 
                    return venta;
                }
            }
        }

        //si no encontramos esa venta devolvemos null
        return null;
    }

    //registrar una venta 
    @Override
    public void registrarVenta(Venta venta)throws SQLException, VentaInvalidaException, VideojuegoNoEncontradoException, ReglaNegocioException {

        //validamos la cantidad, una venta no puede tener cantidad negativa o ser = 0
        if (venta.getCantidad() <= 0) {
            throw new VentaInvalidaException("La cantidad vendida " + "debe ser mayor que cero.");
        }

        //validar la fecha, la misma no puede estar en futuro
        if (venta.getFecha().toLocalDate().isAfter(LocalDate.now())) {
            throw new VentaInvalidaException("La fecha de venta " + "no puede ser futura.");
        }

        //buscar el videojuego por el id, antes usabamos la clase Venta para buscar por id, ahora usamos el dao
        Videojuego videojuego = videojuegoDao.obtenerPorId(venta.getVideojuegoId());

        //verificar si esta disponible 
        if (!videojuego.estaDisponible()) {
            throw new ReglaNegocioException("El videojuego no está " + "disponible para la venta.");
        }

        //verificar el stock, la cantidad de unidades en stock tiene q ser mayor a la cantidad q se quiere vender 
        if (
                videojuego.getUnidadesDisponibles() < venta.getCantidad()
        ) {
            throw new ReglaNegocioException(
                    "No hay stock suficiente."
            );
        }

        // calcular el descuento, nuestra clase venta tiene ese metodo calcularDescuento(cantidad), entonces seguimos usando esa regla q ya teniamos 
        double descuento = Venta.calcularDescuento(venta.getCantidad());

        //calcular el subtotal, eso lo hacemos precio * cantidad 
        double subtotal = videojuego.getPrecio() * venta.getCantidad();

        //calculamos el total
        //la logica seria algo asi ejemplo -->
        // subtotal = 10000 y descuento = 0.10
        //descuento en dinero 10000 * 0.10 = 1000
        //total quedaria 10000 - 1000 = 9000
        double total = subtotal - (subtotal * descuento);

        //guardamos la venta 
        String sql = "INSERT INTO ventas " + "(fecha, cantidad, videojuego_id) " + "VALUES (?, ?, ?)";

        try (

                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql)

        ) {

            //primer ? --> fecha 
            statement.setDate(1, venta.getFecha());

            // segundo ?, cantidad
            statement.setInt( 2, venta.getCantidad() );

            //tercer ?, videojuego_id
            statement.setInt( 3, venta.getVideojuegoId() );

            // ejecutamso el insert
            statement.executeUpdate();
        }


        //descontar el stock 
        //si stock actual es 5 y cantidad vendida es 2 
        //el nuevo stock queda en 3
        int nuevoStock = videojuego.getUnidadesDisponibles() - venta.getCantidad();

        //modificamos el obj videojuego con el nuevo stock 
        videojuego.setUnidadesDisponibles( nuevoStock );

        //le pedimos al dao de videojuegos q haga el update 
        videojuegoDao.actualizarVideojuego( videojuego );
    }

    //listar las ventas de un videojuego
    @Override
    public List<Venta> listarPorVideojuego( long idVideojuego) throws SQLException {

        //buscamos las ventas cuyo videojuegoID sea igual al id recibido 
        String sql = "SELECT * FROM ventas " + "WHERE videojuego_id = ?";

        List<Venta> ventas = new ArrayList<>();

        try (
                Connection conexion =ConexionBD.obtenerConexion();
                PreparedStatement statement =conexion.prepareStatement(sql)
        ) {

            //reemplazamos el ? por el id del juego
            statement.setLong( 1, idVideojuego);

            try (
                    ResultSet resultado =statement.executeQuery()
            ) {

                //recorremos las ventas encontrdas 
                while (resultado.next()) {

                    // creamos venta vacia 
                    Venta venta = new Venta();

                    //cargamos los datos
                    venta.setId(resultado.getInt("id"));
                    venta.setFecha(resultado.getDate("fecha"));
                    venta.setCantidad(resultado.getInt("cantidad"));
                    venta.setVideojuegoId(resultado.getInt("videojuego_id"));

                    //agregamos la venta a nuestra lista 
                    ventas.add(venta);
                }
            }
        }

        //devolvemos ventas encontradas
        return ventas;
    }

    //listar ventas del mes actual 
    @Override
    public List<Venta> listarVentasDelMes() throws SQLException {
        String sql =
                "SELECT * FROM ventas "
                + "WHERE MONTH(fecha) = "
                + "MONTH(CURRENT_DATE) "
                + "AND YEAR(fecha) = "
                + "YEAR(CURRENT_DATE)";

        //lista donde guardamso las ventas del mes 
        List<Venta> ventas = new ArrayList<>();

        try (
                Connection conexion =ConexionBD.obtenerConexion();
                PreparedStatement statement =conexion.prepareStatement(sql);
                ResultSet resultado =statement.executeQuery()
        ) {

            //recorremos ventas encontradas 
            while (resultado.next()) {

                //creamos venta 
                Venta venta = new Venta();

                venta.setId(resultado.getInt("id"));
                venta.setFecha(resultado.getDate("fecha"));
                venta.setCantidad(resultado.getInt("cantidad"));
                venta.setVideojuegoId(resultado.getInt("videojuego_id"));

                ventas.add(venta);
            }
        }
        
        return ventas;
    }
}