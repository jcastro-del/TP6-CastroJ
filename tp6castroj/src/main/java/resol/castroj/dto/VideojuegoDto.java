package resol.castroj.dto;

//la vista necesita solo id, nombre, precio, necesitaReposicion
// DTO esta clase sirve para transportar solamente, los datos de Videojuego que necesita la vista
//no tiene SQL, no se conecta a la base de datos, no tiene reglas de negocio.
// Simplemente guarda datos.
public class VideojuegoDto {

    private long id;
    private String nombre;
    private double precio;
    private boolean necesitaReposicion;

    //constructor vacio, dto debe contener un constructor vacio, para crear el objeto sin cargarle datos todavia 
    public VideojuegoDto() {

    }

    //constructor con los atributos, permite crear el dto con todos sus datos
    public VideojuegoDto( long id, String nombre, double precio, boolean necesitaReposicion) {

        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.necesitaReposicion = necesitaReposicion;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    //en boolean usamos is en vez de get
    public boolean isNecesitaReposicion() {
        return necesitaReposicion;
    }


    public void setNecesitaReposicion(boolean necesitaReposicion) {

        this.necesitaReposicion = necesitaReposicion;
        
    }
}