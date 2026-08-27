package resol.castroj.vista;

import java.sql.Date;
import java.util.Scanner;

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
}