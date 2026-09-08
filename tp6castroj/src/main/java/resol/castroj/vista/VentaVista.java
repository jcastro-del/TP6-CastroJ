package resol.castroj.vista;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import resol.castroj.dto.VentaDto;

//vista de venta 
//su funcion es parecida a videojuegoVista, pero lo hae con las ventas 
public class VentaVista {

    Scanner scanner = new Scanner(System.in);


    ///solicitamos al usuario informacion, como el id  de la venta, id del videojuego, etc
    public int pedirIdVenta() {

        System.out.print("ID de venta: ");

        return scanner.nextInt();
    }


    public int pedirVideojuegoId() {

        System.out.print("ID del videojuego: ");

        return scanner.nextInt();
    }


    public int pedirCantidad() {

        System.out.print("Cantidad: ");

        return scanner.nextInt();
    }


    public Date pedirFecha() {

        System.out.print("Fecha (AAAA-MM-DD): ");

        String fecha = scanner.next();

        return Date.valueOf(fecha);
    }


    public void mostrarMensaje(String mensaje) {

        System.out.println(mensaje);
    }

    //mostrar una sola venta 
    //este metodo recibe un VentaDTO no recibe venta --> lo q nos pide el tp 7, trabajar la vista con el dto 
    public void mostrarVenta(VentaDto venta) {
        
        System.out.println("======================");

        // mostramos el id de la venta
        System.out.println( "ID: " + venta.getId() );

        // mostramos la fecha
        System.out.println( "Fecha: " + venta.getFecha() );

        // mostramos el nombre del videojuego
        System.out.println( "Videojuego: " + venta.getNombreVideojuego() );

        // mostramos la cantidad vendida
        System.out.println( "Cantidad: " + venta.getCantidad() );

        // mostramos el porcentaje de descuento
        System.out.println( "Descuento: " + venta.getPorcentajeDescuento() + "%" );

        // mostramos el total final
        System.out.println( "Total: $" + venta.getTotal() );

        System.out.println( "======================");
    }

    //mostrar varias ventas 
    //este metodo recibe una lista con varios dto de venta 
    public void mostrarVentas(List<VentaDto> ventas) {
        
        // recorremos la lista una venta por vez
        for (VentaDto venta : ventas) {
            mostrarVenta(venta);
        }
    }

}