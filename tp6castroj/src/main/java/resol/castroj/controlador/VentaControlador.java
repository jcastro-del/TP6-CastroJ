package  resol.castroj.controlador;

import resol.castroj.dao.VentaDao;
import resol.castroj.dao.VentaDaoImpl;
import resol.castroj.dao.VideojuegoDao;
import resol.castroj.dao.VideojuegoDaoImpl;

//dto
import resol.castroj.dto.VentaDto;

//excepciones 
import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VentaInvalidaException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;

//modelos
import resol.castroj.modelo.Venta;
import resol.castroj.modelo.Videojuego;

//vista
import resol.castroj.vista.VentaVista;

//java 
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentaControlador {

    private VentaVista vista = new VentaVista();

    //dao de venta 
    private VentaDao ventaDao = new VentaDaoImpl(); 

    //dao de videojuego, lo necesitamos por q venta tiene videojuego id, pero vantaDTO necesita nombrevideojuego 
    private VideojuegoDao videojuegoDao = new VideojuegoDaoImpl();

    //listar todas las ventas 
    public void listar() {

        try {

            //le pedimos al dao de venta q nos busque todas las ventas 
            List<Venta> ventas = ventaDao.listarVentas();

            //creamos la lista dto para la vista 
            List<VentaDto> ventasDto = new ArrayList<>();

            //recorremos las ventas una por una 
            for (Venta venta : ventas) {

                //buscar el videojuego de esa venta, venta solo tiene videojuegoID entonces usamos ese id para buscar el juego completo 
                Videojuego videojuego = videojuegoDao.obtenerPorId( venta.getVideojuegoId());

                //calcular el descuento, la clase venta ya tiene esa regla 
                double descuento = Venta.calcularDescuento(venta.getCantidad());

                //subtotal / precio * cantidad 
                double subtotal = videojuego.getPrecio() * venta.getCantidad();

                //total , sub total - descuento
                double total = subtotal - (subtotal * descuento);

                //crear el dto, ahora tenemos toda la info q necesita la vista 
                VentaDto dto = new VentaDto(

                                //id de la venta 
                                venta.getId(),

                                // fecha
                                venta.getFecha(),

                                // nombre del videojuego
                                videojuego.getNombre(),

                                // cantidad vendida
                                venta.getCantidad(),

                                //dto pide porcentaje
                                descuento * 100,

                                // total final
                                total
                        );


                // agregamos el dto a nuestra lista
                ventasDto.add(dto);
            }


            //mandamos los dto a la vista 
            vista.mostrarVentas(ventasDto);


        } catch (
                SQLException | VideojuegoNoEncontradoException e
        ) {
            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }

    //buscar venta por el id 
    public void buscar() {

        try {

            //vista pide el id de la venta 
            int id = vista.pedirIdVenta();

            //pedimos al dao q busqeu la venta 
            Venta venta = ventaDao.obtenerPorId(id);

            //dao devuelve null si no encuentra la venta 
            if (venta == null) {
                vista.mostrarMensaje(
                        "No existe una venta con ese ID."
                );

                //return termina este metodo, no seguimos ejecutando lo q viene abajo 
                return;
            }

            // buscamos el videojuego relacionado con esta venta
            Videojuego videojuego = videojuegoDao.obtenerPorId( venta.getVideojuegoId());

            // calculamos el descuento
            double descuento = Venta.calcularDescuento(venta.getCantidad());

            // calculamos subtotal
            double subtotal = videojuego.getPrecio() * venta.getCantidad();

            // calculamos total
            double total = subtotal - (subtotal * descuento);

            // creamos el dto
            VentaDto dto =
                    new VentaDto(

                            venta.getId(),
                            venta.getFecha(),
                            videojuego.getNombre(),
                            venta.getCantidad(),
                            descuento * 100,
                            total
                    );

            //vista recibe el dto
            vista.mostrarVenta(dto);


        } catch (
                SQLException | VideojuegoNoEncontradoException e
        ) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }

    // registra nueva venta 
    public void registrar() {

        try {

            // pedimos el id del jeugo q queremso vender
            int videojuegoId = vista.pedirVideojuegoId();

            // pedimos la cantidad
            int cantidad = vista.pedirCantidad();

            // pedimos la fecha
            Date fecha = vista.pedirFecha();

            // creamos el objeto Venta
            Venta venta = new Venta( fecha, cantidad, videojuegoId );

            //antes usabamos venta para registrrar la venta, ahora usamos el dao, ventadaoimpl se encarga de lo demas 
            ventaDao.registrarVenta(venta);

            vista.mostrarMensaje( "Venta registrada correctamente.");


        } catch (
                SQLException | VentaInvalidaException | VideojuegoNoEncontradoException | ReglaNegocioException e
        ) {
            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }

    //buscar ventas de un videojuego 
    public void buscarPorVideojuego() {

        try {

            // pedimos el id del videojuego
            int id = vista.pedirVideojuegoId();

            //primero buscamos el videojuego, nos sirve para verificar q exista y obtener su nombre y precio
            Videojuego videojuego = videojuegoDao.obtenerPorId(id);

            //pedimos al dao todas las ventas de ese videojuego 
            List<Venta> ventas = ventaDao.listarPorVideojuego(id);

            // lista vacia de dto
            List<VentaDto> ventasDto = new ArrayList<>();

            // recorremos las ventas
            for (Venta venta : ventas) {

                //calcular el descuento segun la cantidad 
                double descuento = Venta.calcularDescuento( venta.getCantidad());

                double subtotal = videojuego.getPrecio() * venta.getCantidad();

                double total = subtotal - (subtotal * descuento);

                VentaDto dto =
                        new VentaDto(
                                venta.getId(),
                                venta.getFecha(),
                                videojuego.getNombre(),
                                venta.getCantidad(),
                                descuento * 100,
                                total
                        );
                ventasDto.add(dto);
            }

            //mandamos la lista de dto a la vista
            vista.mostrarVentas( ventasDto);


        } catch (
                SQLException | VideojuegoNoEncontradoException e
        ) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }

    //reporte del mes actual 
    public void reporteMes() {

        try {

            //pedimos al dao las ventas del mes actual 
            List<Venta> ventas = ventaDao.listarVentasDelMes();

            //lista vacia del dto
            List<VentaDto> ventasDto = new ArrayList<>();

            // recorremos las ventas encontradas 
            for (Venta venta : ventas) {

                // como Venta solamente tiene videojuegoId, buscamos el Videojuego
                Videojuego videojuego = videojuegoDao.obtenerPorId( venta.getVideojuegoId());

                //calculamos descuento 
                double descuento = Venta.calcularDescuento(venta.getCantidad());

                //subtotal
                double subtotal = videojuego.getPrecio() * venta.getCantidad();

                //total
                double total = subtotal - (subtotal * descuento);

                VentaDto dto =
                        new VentaDto(
                                venta.getId(),
                                venta.getFecha(),
                                videojuego.getNombre(),
                                venta.getCantidad(),
                                descuento * 100,
                                total
                        );
                ventasDto.add(dto);
            }

            vista.mostrarVentas( ventasDto );


        } catch (
                SQLException | VideojuegoNoEncontradoException e
        ) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }
}