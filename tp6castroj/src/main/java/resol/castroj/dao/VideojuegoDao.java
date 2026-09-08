package resol.castroj.dao;

import java.sql.SQLException;
import java.util.List;

import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;
import resol.castroj.modelo.Videojuego;

//nuestra contrato, no tiene logica, solo define q metodos deben existir 
//cualquiera q lo implemente, esta obligado a escribir codigo de estos metodos
//la clase VideojuegoDaoImpl va a implementar estos metodos con codigo jdbc/sql
public interface VideojuegoDao {

    //devuelve todos los videojuegos de la base, si hay un error en la consulta lanza un mensaje 
    List<Videojuego> listarVideojuegos() throws SQLException;

    //devuelve todos los disponibles 
    List<Videojuego> listarDisponibles() throws SQLException;

    //devuelve todos los q necesitan reposicion 
    List<Videojuego> listarQueNecesitanReposicion() throws SQLException;

    //busca un videojuego por su id, si no existe lanza un error
    Videojuego obtenerPorId(long id) throws SQLException, VideojuegoNoEncontradoException;

    //inserta un juego en la base --> lanza un error si no cumple alguna regla
    Videojuego agregarVideojuego(Videojuego videojuego) throws SQLException, ReglaNegocioException;

    //modifica un juego existente --> true / false segun si pudo actualizar 
    boolean actualizarVideojuego(Videojuego videojuego) throws SQLException;

    //borra un juego por id, true / false si logro eliminarlo 
    boolean eliminarVideojuego(long id) throws SQLException;
}