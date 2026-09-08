package resol.castroj.dto;

import java.sql.Date;

public class VentaDto {

    private long id;
    private Date fecha;
    private String nombreVideojuego;
    private int cantidad;
    private double porcentajeDescuento;
    private double total;

    // constructor vacio
    public VentaDto() {

    }

    //constructor con todos los datos
    public VentaDto( long id, Date fecha, String nombreVideojuego, int cantidad, double porcentajeDescuento, double total) {
        this.id = id;
        this.fecha = fecha;
        this.nombreVideojuego = nombreVideojuego;
        this.cantidad = cantidad;
        this.porcentajeDescuento = porcentajeDescuento;
        this.total = total;
    }

    //getters y setters 
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getNombreVideojuego() { return nombreVideojuego; }
    public void setNombreVideojuego(String nombreVideojuego) { this.nombreVideojuego = nombreVideojuego; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {this.cantidad = cantidad;}

    public double getPorcentajeDescuento() { return porcentajeDescuento;}
    public void setPorcentajeDescuento(double porcentajeDescuento) {this.porcentajeDescuento = porcentajeDescuento;}

    public double getTotal() {return total;}
    public void setTotal(double total) { this.total = total;}
}