package resol.castroj.vista;

import java.util.Scanner;

//vista general --> esta muestra los 3 menus principales
//los creamos como metodos, asi luego podemos llemarlos desde otras clases 
public class VistaGeneral {

    Scanner scanner = new Scanner(System.in);


    //menu principal
    public int mostrarMenuPrincipal() {

        System.out.println();
        System.out.println("=== MENÚ PRINCIPAL ===");
        System.out.println("1. Gestión de Videojuegos");
        System.out.println("2. Gestión de Ventas");
        System.out.println("0. Salir");

        System.out.print("Seleccione una opción: ");

        //retorna la opcion elegida
        return scanner.nextInt();
    }


    //menu videojuegos 
    public int mostrarMenuVideojuegos() {

        System.out.println();
        System.out.println("=== GESTIÓN DE VIDEOJUEGOS ===");

        System.out.println("1. Listar videojuegos");
        System.out.println("2. Buscar videojuego por ID");
        System.out.println("3. Agregar videojuego");
        System.out.println("4. Actualizar videojuego");
        System.out.println("5. Eliminar videojuego");
        System.out.println("6. Videojuegos que necesitan reposición");
        System.out.println("7. Videojuegos disponibles para la venta");
        System.out.println("0. Volver");

        System.out.print("Seleccione una opción: ");

        //retona la opcion elegida
        return scanner.nextInt();
    }


    //menu ventas 
    public int mostrarMenuVentas() {

        System.out.println();
        System.out.println("=== GESTIÓN DE VENTAS ===");

        System.out.println("1. Listar ventas");
        System.out.println("2. Buscar venta por ID");
        System.out.println("3. Registrar venta");
        System.out.println("4. Buscar ventas de un videojuego");
        System.out.println("5. Reporte de ventas del mes actual");
        System.out.println("0. Volver");

        System.out.print("Seleccione una opción: ");

        //retorna la opcion elegida
        return scanner.nextInt();
    }
}