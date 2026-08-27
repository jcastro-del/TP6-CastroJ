package resol.castroj.controlador;

import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;
import resol.castroj.modelo.Videojuego;
import resol.castroj.vista.VideojuegoVista;

import java.sql.SQLException;

public class VideojuegoControlador {

        //cremoa un objeto de la vista, gracias a esta controlador puede hacer: vista.pedirID // vista.pedirNombre
    private VideojuegoVista vista =
            new VideojuegoVista();


        //listamos los videojuegos
    public void listar() {

        try {

                //le pedimso al modelo videojuegos q liste todos los juegos 
            Videojuego.listarVideojuegos();

        } catch (SQLException e) {

            vista.mostrarMensaje(
                    e.getMessage()      //mostramos el mensaje usando la vista 
            );
        }
    }


    //buscar videojuego por id 
    public void buscar() {

        try {

                //pedir el id a la vista --> vista le pregunta al usuario: ingrese id
            int id = vista.pedirId();

            //le pedimos al modelo q busqeu el videojuego con ese id 
            Videojuego videojuego = Videojuego.buscarPorId(id);

            //si lo encontro, mostramos los datos 
            System.out.println(
                    videojuego.getNombre()
            );

            System.out.println(
                    videojuego.getGenero()
            );

            System.out.println(
                    videojuego.getPrecio()
            );

        } catch (
                SQLException
                | VideojuegoNoEncontradoException e     //id no existe 
        ) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }


    //agregar un videojuego 
    public void agregar() {

        try {

                //para poder agregar un videojeugo debemos solicitar al usuario algunos datos
            String nombre = vista.pedirNombre();

            String genero = vista.pedirGenero();

            double precio = vista.pedirPrecio();

            int unidades = vista.pedirUnidades();

            int nivel = vista.pedirNivelReposicion();

            int suspendido = vista.pedirSuspendido();

                //creamos el objeto videojeugo con los datos obtenidos de la vista, en este punto todavia no fueron guardados en la bd  
            Videojuego videojuego = new Videojuego( nombre, genero, precio, unidades, nivel, suspendido);

                //aca le pedimos la modelo q lo inserte en la base 
            videojuego.crearVideojuego();

                //mostramos un mensaje si se inserto correctamente 
            vista.mostrarMensaje( "Videojuego agregado." );


        } catch (
                SQLException
                | ReglaNegocioException e       //puede fallar por alguna regla de negocio 
        ) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }


    //actualizar un videojuego
    public void actualizar() {

        try {

                //pedimos el id al cual queremos actualizar 
            int id = vista.pedirId();

            //pedimos los datos nuevos q queramos actualizar 
            String nombre = vista.pedirNombre();

            String genero = vista.pedirGenero();

            double precio = vista.pedirPrecio();

            int unidades = vista.pedirUnidades();

            int nivel = vista.pedirNivelReposicion();

            int suspendido = vista.pedirSuspendido();


            //creamos el objeto con el id --> aca deberiamos usar nuestro segundo contructor con ID, ya q estamos actualizando un registro q ya existe 
            Videojuego videojuego = new Videojuego( id, nombre, genero, precio, unidades, nivel, suspendido );

            //confirmamso el update 
            videojuego.actualizarVideojuego();

            //mostramos un mensjaej una vez actualizado 
            vista.mostrarMensaje("Videojuego actualizado.");


        } catch (
                SQLException
                | ReglaNegocioException
                | VideojuegoNoEncontradoException e
        ) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }


    //eliminar un videojuego 
    //en este caso vamos a borrar por el ID por q es lo mas seguro, pero tmb podriamos eleminar, actualizar 
    //en base a un nombre o otro q elijamos
    public void eliminar() {

        try {

                //le pedimos a la vista el id del videojuego q queremos eliminar 
            int id = vista.pedirId();

            //una vez q tengamos el id del juego, le pedimos Videojeugo q lo elimine
            Videojuego.eliminarVideojuego(id);


            vista.mostrarMensaje( "Videojuego eliminado." );


        } catch (
                SQLException
                | VideojuegoNoEncontradoException e
        ) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }


    //listar videojuegos q necesitan reposicion 
    public void reposicion() {

        try {

                //Videojeugo es quien hace el select, el controlador simplemente le dice hace esta operacion
            Videojuego.listarReposicion();

        } catch (SQLException e) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }


    //listar los videojuegos disponibles 
    public void disponibles() {

        try {

                //el modelo solo busca el juego con suspendido = 1 
            Videojuego.listarDisponibles();

        } catch (SQLException e) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }
}