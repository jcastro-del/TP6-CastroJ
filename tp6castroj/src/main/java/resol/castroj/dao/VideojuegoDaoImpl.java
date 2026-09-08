package resol.castroj.dao;

//la consigna nos dice q esta clase debe implementar a videojuegoDao
// Connection representa la conexión con nuestra base de datos H2.
import java.sql.Connection;
import java.sql.PreparedStatement;

// ResultSet contiene las filas que devuelve un SELECT
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import resol.castroj.ConexionBD;
import resol.castroj.excepciones.ReglaNegocioException;
import resol.castroj.excepciones.VideojuegoNoEncontradoException;
import resol.castroj.modelo.Videojuego;


public class VideojuegoDaoImpl implements VideojuegoDao {

    //listar todos los videojuegos --> metodo de videojuegoDao
    @Override
    public List<Videojuego> listarVideojuegos()
            throws SQLException {

        // Creamos la consulta SQL.
        String sql = "SELECT * FROM videojuegos";


        // Creamos una lista vacía, para ir almacenando cada juego q encontramos en la bd
        List<Videojuego> videojuegos = new ArrayList<>();

        try (

                // abrimos la conexion con  la bd
                Connection conexion = ConexionBD.obtenerConexion();

                // preparamso nuestro select
                PreparedStatement statement = conexion.prepareStatement(sql);

                //ejecutamos el select 
                ResultSet resultado = statement.executeQuery()  //executeQuery --> cunado esperamos recibir filas como resultado

        ) {

            //resultado.next va por cada fila q devolvio la bd, el while se debe ejecutar hasta q no existan mas filas para recorer
            while (resultado.next()) {

                // objeto videojuego
                Videojuego videojuego = new Videojuego(

                                resultado.getInt("id"),
                                resultado.getString("nombre"),
                                resultado.getString("genero"),
                                resultado.getDouble("precio"),
                                resultado.getInt("unidades_disponibles"),
                                resultado.getInt("nivel_reposicion"),
                                resultado.getInt("suspendido")

                        );

                // agregamos ese obj videojuego a nuestra lista 
                videojuegos.add(videojuego);
            }
        }


        //al final devolvemos la lista si habia muchos videojuegos nos muestra todos, de caso contrarior no muestra nada 
        return videojuegos;
    }


    //lista unicamente los disponibles
    @Override
    public List<Videojuego> listarDisponibles()
            throws SQLException {

        //solaente queremos aquellos juegos donde suspendido = 1 
        String sql = "SELECT * FROM videojuegos " + "WHERE suspendido = 1";

        //creamos lista vacia para guardar unicamente juegos disponibles 
        List<Videojuego> videojuegos = new ArrayList<>();


        try (

                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()

        ) {

            // recorremos cada fila encontrada.
            while (resultado.next()) {

                //transformamos esa fila en un objeto videojuego
                Videojuego videojuego =
                        new Videojuego(
                                resultado.getInt("id"),
                                resultado.getString("nombre"),
                                resultado.getString("genero"),
                                resultado.getDouble("precio"),
                                resultado.getInt("unidades_disponibles"),
                                resultado.getInt("nivel_reposicion"),
                                resultado.getInt("suspendido")
                        );

                //agregamos ese videojuego a la lista
                videojuegos.add(videojuego);
            }
        }

        // devolvemos solamente los disponibles.
        return videojuegos;
    }

    //listar unicamente los q necesitan reposicion
    @Override
    public List<Videojuego> listarQueNecesitanReposicion()
            throws SQLException {

        //listamos unicamente los q necesitan reposicion
        String sql = "SELECT * FROM videojuegos " + "WHERE unidades_disponibles " + "< nivel_reposicion";


        // lista donde guardaremos los resultados.
        List<Videojuego> videojuegos = new ArrayList<>();


        try (

                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()

        ) {

            while (resultado.next()) {

                Videojuego videojuego =
                        new Videojuego(
                                resultado.getInt("id"),
                                resultado.getString("nombre"),
                                resultado.getString("genero"),
                                resultado.getDouble("precio"),
                                resultado.getInt("unidades_disponibles"),
                                resultado.getInt("nivel_reposicion"),
                                resultado.getInt("suspendido")
                            );


                //agregamos a la lista.
                videojuegos.add(videojuego);
            }
        }

        return videojuegos;
    }


    //obtener el videojuego pro el id 
    @Override
    public Videojuego obtenerPorId(long id)
            throws SQLException,
            VideojuegoNoEncontradoException {


        String sql = "SELECT * FROM videojuegos " + "WHERE id = ?";


        try (
                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            //reemplazamos el primer ? de nuestra consulta por el id recibido 
            //si por ej id = 5
            //where id = 5
            statement.setLong(1, id);


            //recien en este punto ejecutamos nuestra consulta 
            try (
                    ResultSet resultado = statement.executeQuery()
            ) {

                //como buscamos por id debe existir solo una fila, por lo q usamos if y no while 
                if (resultado.next()) {

                    Videojuego videojuego =
                            new Videojuego(
                                    resultado.getInt("id"),
                                    resultado.getString("nombre"),
                                    resultado.getString("genero"),
                                    resultado.getDouble("precio"),
                                    resultado.getInt("unidades_disponibles"),
                                    resultado.getInt("nivel_reposicion"),
                                    resultado.getInt("suspendido")
                            );


                    //devolvemos el result
                    return videojuego;
                }
            }
        }

        //si legamos hasta aca significa q el if anterior no encontro la fila, lanzamos un error
        throw new VideojuegoNoEncontradoException(
                "No existe un videojuego con ID " + id
        );
    }

    //agregamos un videojuego 
    @Override
    public Videojuego agregarVideojuego(Videojuego videojuego)throws SQLException,
            ReglaNegocioException {

        //validar el precio, el precio tiene q ser mayor a 0
        if (videojuego.getPrecio() <= 0) {

            throw new ReglaNegocioException(
                    "El precio debe ser mayor que cero."
            );
        }

        //validamos las unidades, las mismas no pueden ser negativas 
        if (videojuego.getUnidadesDisponibles() < 0) {

            throw new ReglaNegocioException(
                    "Las unidades disponibles "
                    + "no pueden ser negativas."
            );
        }

        //preparamos nuestro insert
        //no ponemos id, la bd tiene el auto-incremental
        // ? --> sera reemplazado por el dato del videojuego 
        String sql =
                "INSERT INTO videojuegos "
                + "(nombre, genero, precio, "
                + "unidades_disponibles, "
                + "nivel_reposicion, suspendido) "
                + "VALUES (?, ?, ?, ?, ?, ?)";


        try (
                Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement statement =conexion.prepareStatement(sql)
        ) {

            //el primer ? corresponde al nombre.
            statement.setString(1, videojuego.getNombre());


            // Segundo ?
            // corresponde al género.
            statement.setString(
                    2,
                    videojuego.getGenero()
            );


            // Tercer ?
            // corresponde al precio.
            statement.setDouble(
                    3,
                    videojuego.getPrecio()
            );


            // Cuarto ?
            // corresponde a unidades disponibles.
            statement.setInt(
                    4,
                    videojuego.getUnidadesDisponibles()
            );


            // Quinto ?
            // corresponde al nivel de reposición.
            statement.setInt(
                    5,
                    videojuego.getNivelReposicion()
            );


            // Sexto ?
            // corresponde a suspendido.
            statement.setInt(
                    6,
                    videojuego.getSuspendido()
            );


            // Ejecutamos el INSERT.
            //
            // executeUpdate() se usa para operaciones
            // que modifican datos:
            //
            // INSERT
            // UPDATE
            // DELETE
            statement.executeUpdate();
        }


        // Finalmente devolvemos el objeto
        // Videojuego que recibimos.
        return videojuego;
    }


    // ======================================================
    // 6. ACTUALIZAR VIDEOJUEGO
    // ======================================================

    @Override
    public boolean actualizarVideojuego(
            Videojuego videojuego)
            throws SQLException {


        // UPDATE modifica un videojuego existente.
        //
        // Los primeros 6 ? son los nuevos datos.
        //
        // El último ? corresponde al ID
        // del videojuego que queremos modificar.
        String sql =
                "UPDATE videojuegos SET "
                + "nombre = ?, "
                + "genero = ?, "
                + "precio = ?, "
                + "unidades_disponibles = ?, "
                + "nivel_reposicion = ?, "
                + "suspendido = ? "
                + "WHERE id = ?";


        try (

                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement statement =
                        conexion.prepareStatement(sql)

        ) {


            // Colocamos los nuevos valores.

            statement.setString(
                    1,
                    videojuego.getNombre()
            );

            statement.setString(
                    2,
                    videojuego.getGenero()
            );

            statement.setDouble(
                    3,
                    videojuego.getPrecio()
            );

            statement.setInt(
                    4,
                    videojuego.getUnidadesDisponibles()
            );

            statement.setInt(
                    5,
                    videojuego.getNivelReposicion()
            );

            statement.setInt(
                    6,
                    videojuego.getSuspendido()
            );


            // El séptimo ? corresponde al ID.
            //
            // Esto indica QUÉ videojuego
            // queremos actualizar.
            statement.setLong(
                    7,
                    videojuego.getId()
            );


            // executeUpdate() devuelve la cantidad
            // de filas que fueron modificadas.
            //
            // Ejemplo:
            //
            // 1 → encontró y actualizó el videojuego.
            // 0 → no encontró ese ID.
            int filasModificadas =
                    statement.executeUpdate();


            // Si modificamos alguna fila,
            // devolvemos true.
            //
            // Si fue 0, devolvemos false.
            return filasModificadas > 0;
        }
    }


    // ======================================================
    // 7. ELIMINAR VIDEOJUEGO
    // ======================================================

    @Override
    public boolean eliminarVideojuego(long id)
            throws SQLException {


        // Eliminamos solamente el videojuego
        // cuyo ID coincida con el recibido.
        String sql =
                "DELETE FROM videojuegos "
                + "WHERE id = ?";


        try (

                // Abrimos conexión.
                Connection conexion =
                        ConexionBD.obtenerConexion();


                // Preparamos el DELETE.
                PreparedStatement statement =
                        conexion.prepareStatement(sql)

        ) {


            // Reemplazamos el ? por el ID.
            statement.setLong(1, id);


            // Ejecutamos el DELETE.
            //
            // Nos devuelve cuántas filas
            // fueron eliminadas.
            int filasEliminadas =
                    statement.executeUpdate();


            // Si eliminó una fila:
            //
            // filasEliminadas = 1
            // 1 > 0 → true
            //
            // Si no encontró el ID:
            //
            // filasEliminadas = 0
            // 0 > 0 → false
            return filasEliminadas > 0;
        }
    }
}
