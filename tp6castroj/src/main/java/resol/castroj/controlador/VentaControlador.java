package  resol.castroj.controlador;

import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VentaInvalidaException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;
import resol.castroj.modelo.Venta;
import resol.castroj.vista.VentaVista;

import java.sql.Date;
import java.sql.SQLException;

public class VentaControlador {

    //creamos el objeto de la vista --> gracias a este el controlador puede hacer cosas como 
    //vista.pedircantidad / vistapedirfecha
    private VentaVista vista =
            new VentaVista();

    //listar una venta 
    public void listar() {

        try {
            //le pedimos al modelo venta q liste todas las ventas --> controlador no hace select eso lo hace vemta.java
            Venta.listarVentas();

        } catch (SQLException e) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }

    //buscar por id
    public void buscar() {

        try {

            //le pedimos a la vista q solicite al usuario el id de la venta --> id de la venta: 3
            int id = vista.pedirIdVenta();

            //una vez obtenido el id le pedimos al modelo q busqeu esa venta 
            Venta.buscarVentaPorId(id);

        } catch (SQLException e) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }

    //registrar una nueva venta
    public void registrar() {

        try {

            //pedimos el id del videojuego q queremos vender
            int videojuegoId = vista.pedirVideojuegoId();

            //pedimos las unidades
            int cantidad = vista.pedirCantidad();

            //pedimos la fecha
            Date fecha = vista.pedirFecha();

            //creamos el objeto venta --> con los datos obtenidos de la vista, creamos una nueva venta
            //en este punto todavia no se encuentra cargado en nuestra base
            Venta venta = new Venta(fecha, cantidad, videojuegoId);

            //le pedimos al modelo q se registre la venta 
            //Venta.java se encarga de cosas como --> validar la cantidad, validar la fecha, buscar el juego, guardar la venta, etc
            venta.registrarVenta();


        } catch (
                SQLException
                | VentaInvalidaException    //ej: cantidad < 0 
                | VideojuegoNoEncontradoException //q el videojuego ingresado no exista
                | ReglaNegocioException e   //q no haya stock suficiente 
        ) {

            vista.mostrarMensaje( e.getMessage() );
        }
    }

    //buscar todas las ventas de un videojuego 
    public void buscarPorVideojuego() {

        try {

            //pedimos el id del videojuedo
            int id = vista.pedirVideojuegoId();

            //pedimso q busque todas las ventas relacionadas con ese videojuego 
            Venta.buscarVentasPorVideojuego(id);

        } catch (
                SQLException
                | VideojuegoNoEncontradoException e //tira error si el videojuego no existe 
        ) {

            vista.mostrarMensaje( e.getMessage());
        }
    }

    //reporte de ventas del mes actual
    public void reporteMes() {

        try {

            //pedimos al modelo q haga el reporte del mes actual 
            Venta.reporteMesActual();

        } catch (SQLException e) {

            vista.mostrarMensaje( e.getMessage() );
        }
    }
}