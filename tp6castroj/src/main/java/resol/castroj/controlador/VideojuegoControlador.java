package resol.castroj.controlador;

import resol.castroj.dao.VideojuegoDao;
import resol.castroj.dao.VideojuegoDaoImpl;
import resol.castroj.dto.VideojuegoDto; //es el objeto q vamos a mndar a la vista, la misma ya no deberia recibir directamente de objeto 
import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;
import resol.castroj.modelo.Videojuego;
import resol.castroj.vista.VideojuegoVista;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class VideojuegoControlador {

    //gracias a este podemos hacer vista.pedirID() / vista.pedirNombre()
    private VideojuegoVista vista = new VideojuegoVista();

    private VideojuegoDao videojuegoDao = new VideojuegoDaoImpl();

    //listar todos los juegos 
    public void listar() {

        try {

            //pedimos al dao q lo busque en la bd, dao nos devuelve una lisya de objetos videojuego
            List<Videojuego> videojuegos = videojuegoDao.listarVideojuegos();

            //vista no debe recibir la lista, por eso creamos una nueva pero con videojuegodto
            List<VideojuegoDto> videojuegosDto = new ArrayList<>();

            //recorremos todo lo q nos devolvio dao
            for (Videojuego videojuego : videojuegos) {

                //creamos un dto con unicamente los datos q necesita la vista 
                VideojuegoDto dto = new VideojuegoDto(
                                videojuego.getId(),
                                videojuego.getNombre(),
                                videojuego.getPrecio(),
                                videojuego.necesitaReposicion()
                        );

                //agregamos el dto a nuestra lista 
                videojuegosDto.add(dto);
            }

            //lo mandamos a la vista, no estariamos mandando los juegos originales 
            vista.mostrarVideojuegos(videojuegosDto);


        } catch (SQLException e) {
            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }


    ///buscar videojuego por el id
    public void buscar() {

        try {

            //la vista pregunta que id se quiere buscar 
            int id = vista.pedirId();

            //le pedimos al dao q lo busque en la bd, el mismo dao nos devuelve ese dato 
            Videojuego videojuego = videojuegoDao.obtenerPorId(id);

            //ahora convertimos ese dato en uno dto para poder enviarlo a la vista 
            VideojuegoDto dto =
                    new VideojuegoDto(
                            videojuego.getId(),
                            videojuego.getNombre(),
                            videojuego.getPrecio(),
                            videojuego.necesitaReposicion()
                    );


            // lo mandamos a la vista
            vista.mostrarVideojuego(dto);


        } catch (
                SQLException
                | VideojuegoNoEncontradoException e
        ) {

            //puede fallar por problemas en la bd o q el id no exista 
            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }


    //agregar un videojuego
    public void agregar() {

        try {

            //pedimos a la vista los datos de ese juego q se quiere agregar 
            String nombre = vista.pedirNombre();
            String genero = vista.pedirGenero();
            double precio = vista.pedirPrecio();
            int unidades = vista.pedirUnidades();
            int nivel = vista.pedirNivelReposicion();
            int suspendido = vista.pedirSuspendido();

            //creamos el obj videojuego 
            Videojuego videojuego = new Videojuego( nombre, genero, precio, unidades, nivel, suspendido );

            //antes usabamos la clase madre videojuego.crearVideojuego()
            //pero ahora usamos el dao, el se encarga de geenerar el insert
            videojuegoDao.agregarVideojuego( videojuego );


            //mensaje de q fue agregado correctamente 
            vista.mostrarMensaje(
                    "Videojuego agregado."
            );


        } catch (
                SQLException
                | ReglaNegocioException e
        ) {

            //puede fallar por error de la bd / precio negativo o unidades negativas 
            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }


    //actualizar videojeugo 
    public void actualizar() {

        try {

            //pedimos el id del juego q queremos modificar
            int id = vista.pedirId();

            //verificamos q el mismo exista
            //si no existe, se lanza una excepcion 
            videojuegoDao.obtenerPorId(id);


            //pedimos lo datos 
            String nombre = vista.pedirNombre();
            String genero = vista.pedirGenero();
            double precio = vista.pedirPrecio();
            int unidades = vista.pedirUnidades();
            int nivel = vista.pedirNivelReposicion();
            int suspendido = vista.pedirSuspendido();

            //creamos un juego con id, usamos el constructor con id, por q estamos modificando algo ya existente 
            Videojuego videojuego =
                    new Videojuego( id, nombre, genero, precio, unidades, nivel, suspendido );


            //pedimos  al dao q haga el update 
            boolean actualizado = videojuegoDao.actualizarVideojuego( videojuego );

            //boolean nos devuelve true o false
            //si se conpleto true de lo contrario false
            if (actualizado) {

                vista.mostrarMensaje(
                        "Videojuego actualizado."
                );

            } else {

                vista.mostrarMensaje(
                        "No se pudo actualizar el videojuego."
                );
            }


        } catch (
                SQLException
                | VideojuegoNoEncontradoException e
        ) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }

    //eliminar un juego 
    public void eliminar() {

        try {

            //pedimos id 
            int id = vista.pedirId();

            //verificamos q exista 
            videojuegoDao.obtenerPorId(id);

            //pedimos al dao q haga el delete 
            boolean eliminado = videojuegoDao.eliminarVideojuego(id);

            //si devuelve true se elimino, de lo contrario no se elimino 
            if (eliminado) {

                vista.mostrarMensaje(
                        "Videojuego eliminado."
                );

            } else {

                vista.mostrarMensaje(
                        "No se pudo eliminar el videojuego."
                );
            }


        } catch (
                SQLException
                | VideojuegoNoEncontradoException e
        ) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }

    //listar los q necesitan reposicion 
    public void reposicion() {

        try {

            //pedimos al dao solamente los q necesitan reposicion 
            List<Videojuego> videojuegos = videojuegoDao.listarQueNecesitanReposicion();

            //creamos una lista vacia para dto
            List<VideojuegoDto> videojuegosDto = new ArrayList<>();

            //recorremos los jeugos
            for (Videojuego videojuego : videojuegos) {

                //convertimos cada juego en videojuegoDTO
                VideojuegoDto dto = new VideojuegoDto(
                                videojuego.getId(),
                                videojuego.getNombre(),
                                videojuego.getPrecio(),
                                videojuego.necesitaReposicion()
                        );


                //guardamos el dto en la lista
                videojuegosDto.add(dto);
            }


            // mandamos los dto a la vista 
            vista.mostrarVideojuegos( videojuegosDto );

        } catch (SQLException e) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }

    //listar los juego disponibles 
    public void disponibles() {

        try {

            //le pediumos al dao los disponibles
            List<Videojuego> videojuegos = videojuegoDao.listarDisponibles();

            //creamos una lista vacia para los dto  
            List<VideojuegoDto> videojuegosDto = new ArrayList<>();

            //recorremos 
            for (Videojuego videojuego : videojuegos) {

                VideojuegoDto dto = new VideojuegoDto(
                                videojuego.getId(),
                                videojuego.getNombre(),
                                videojuego.getPrecio(),
                                videojuego.necesitaReposicion()
                        );

                
                videojuegosDto.add(dto);
            }

            vista.mostrarVideojuegos( videojuegosDto );


        } catch (SQLException e) {

            vista.mostrarMensaje(
                    e.getMessage()
            );
        }
    }
}