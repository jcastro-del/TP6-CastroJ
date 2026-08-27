package resol.castroj;

import java.sql.SQLException;

import resol.castroj.controlador.VentaControlador;
import resol.castroj.controlador.VideojuegoControlador;
import resol.castroj.modelo.Venta;
import resol.castroj.modelo.Videojuego;
import resol.castroj.vista.VistaGeneral;

public class Main {

    public static void main(String[] args) {

        // Crear las tablas si todavía no existen.
        Videojuego.crearTabla();
        Venta.crearTabla();


        // Crear vista general y controladores.
        VistaGeneral vista =
                new VistaGeneral();

        VideojuegoControlador videojuegoControlador =
                new VideojuegoControlador();

        VentaControlador ventaControlador =
                new VentaControlador();


        int opcion;

        do {

            opcion =
                    vista.mostrarMenuPrincipal();

            switch (opcion) {

                case 1:

                    menuVideojuegos(
                            vista,
                            videojuegoControlador
                    );

                    break;


                case 2:

                    menuVentas(
                            vista,
                            ventaControlador
                    );

                    break;


                case 0:

                    System.out.println(
                            "Programa finalizado..."
                    );

                    break;


                default:

                    System.out.println(
                            "Opcion Incorrecta"
                    );
            }

        } while (opcion != 0);
    }

    //menu videojuegos 
    public static void menuVideojuegos( VistaGeneral vista, VideojuegoControlador controlador ) {

        int opcion;


        do {

            opcion = vista.mostrarMenuVideojuegos();

            switch (opcion) {

                case 1:
                    controlador.listar();
                    break;

                case 2:
                    controlador.buscar();
                    break;

                case 3:
                    controlador.agregar();
                    break;

                case 4:
                    controlador.actualizar();
                    break;

                case 5:
                    controlador.eliminar();
                    break;

                case 6:
                    controlador.reposicion();
                    break;

                case 7:
                    controlador.disponibles();
                    break;
            }

        } while (opcion != 0);
    }


    // menu ventas 
    public static void menuVentas( VistaGeneral vista, VentaControlador controlador) {

        int opcion;

        do {

            opcion = vista.mostrarMenuVentas();


            switch (opcion) {

                case 1:
                    controlador.listar();
                    break;

                case 2:
                    controlador.buscar();
                    break;

                case 3:
                    controlador.registrar();
                    break;

                case 4:
                    controlador.buscarPorVideojuego();
                    break;

                case 5:
                    controlador.reporteMes();
                    break;
            }

        } while (opcion != 0);
    }
}