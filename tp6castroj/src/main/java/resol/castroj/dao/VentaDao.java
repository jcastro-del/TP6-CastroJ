package resol.castroj.dao;

import java.sql.SQLException;
import java.sql.SQLException;
import java.util.List;

import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VentaInvalidaException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;
import resol.castroj.modelo.Venta;

//nuestra interface 
//definimos q operaciones podemos hacer con las ventas
public interface VentaDao {

    //devuelve todas las ventas
    List<Venta> listarVentas() throws SQLException;

    // buscar venta por su id 
    Venta obtenerPorId(long id) throws SQLException;

    //registrar una nueva venta 
    //debe fallar por venta invalida, juego inexistente o reglas de negocio
    void registrarVenta(Venta venta)throws SQLException, VentaInvalidaException, VideojuegoNoEncontradoException, ReglaNegocioException;

    //devuelve todas las ventas correspondientes a un juego 
    List<Venta> listarPorVideojuego(long idVideojuego)throws SQLException;

    //devuelve todas las ventas del mes actual 
    List<Venta> listarVentasDelMes() throws SQLException;
}
