package resol.castroj.vista;

import java.util.Scanner;

//vista de videojuego
//esta clase solo va a preguntar info para dsp devolverle esos datos al controlador 
public class VideojuegoVista {

    Scanner scanner = new Scanner(System.in);


    //pedimso al usuario id, nombre, genero, precio, etc
    public int pedirId() {

        System.out.print("Ingrese ID: ");

        return scanner.nextInt();
    }


    public String pedirNombre() {

        scanner.nextLine();

        System.out.print("Nombre: ");

        return scanner.nextLine();
    }


    public String pedirGenero() {

        System.out.print("Género: ");

        return scanner.nextLine();
    }


    public double pedirPrecio() {

        System.out.print("Precio: ");

        return scanner.nextDouble();
    }


    public int pedirUnidades() {

        System.out.print("Unidades disponibles: ");

        return scanner.nextInt();
    }


    public int pedirNivelReposicion() {

        System.out.print("Nivel de reposición: ");

        return scanner.nextInt();
    }


    public int pedirSuspendido() {

        System.out.print(
                "Disponible (1 = sí, 0 = no): "
        );

        return scanner.nextInt();
    }


    //neustro controlador puede usar este metodo par mostrar errores o confirmaciones
    public void mostrarMensaje(String mensaje) {

        System.out.println(mensaje);
    }
}