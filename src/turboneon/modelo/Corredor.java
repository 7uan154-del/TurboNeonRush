package turboneon.modelo;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class Corredor {
    private final String nombre;
    private final int indice;
    private final Vehiculo vehiculo;
    private final Color color;
    private Controlador controlador;

    private double x, velocidad, carrilY = 1, nitro = 60, stun, tiempoFin;
    private int carrilObjetivo = 1, golpes, ultimoTipo, idxObst;
    private boolean nitroPedido, nitroActivo, terminado;
    private final Set<Obstaculo> golpeados = new HashSet<>();
    private String mensaje = "";
    private double mensajeT;

    public Corredor(String nombre, int indice, Vehiculo vehiculo, Color color, Controlador controlador) {
        this.nombre = nombre; this.indice = indice; this.vehiculo = vehiculo;
        this.color = color; this.controlador = controlador;
    }

    public void actualizar(double dt) {
        if (stun > 0) stun = Math.max(0, stun - dt);
        double objetivo = vehiculo.getVelocidadMax();
        double acel = vehiculo.getAceleracion();
        nitroActivo = false;
        if (terminado) {
            objetivo *= 0.3;
        } else if (nitroPedido && nitro > 0 && stun <= 0) {
            nitroActivo = true;
            objetivo *= 1.35;
            acel *= 1.6;
            nitro = Math.max(0, nitro - 38 * dt);
        } else if (!nitroPedido) {
            nitro = Math.min(100, nitro + 6 * dt);
        }
        if (stun > 0) objetivo *= 0.55;
        if (velocidad < objetivo) velocidad = Math.min(objetivo, velocidad + acel * dt);
        else velocidad = Math.max(objetivo, velocidad - 420 * dt);
        x += velocidad * dt;

        if (stun <= 0) {
            double d = carrilObjetivo - carrilY;
            double paso = vehiculo.getManejo() * dt;
            carrilY += Math.abs(d) <= paso ? d : Math.signum(d) * paso;
        }
    }

    public void reducirVelocidad(double fraccion) {
        velocidad *= Math.max(0.15, 1 - fraccion * vehiculo.getResistencia());
    }
    public void aturdir(double segundos) { stun = Math.max(stun, segundos * vehiculo.getResistencia()); }
    public void agregarNitro(double cantidad) { nitro = Math.min(100, nitro + cantidad); }
    public void impulso(double v) { velocidad += v; }
    public void registrarGolpe(int tipo) {
        golpes++;
        ultimoTipo = tipo;
        mensaje = Obstaculo.TEXTOS[tipo];
        mensajeT = 1.2;
    }
    public boolean yaGolpeo(Obstaculo o) { return golpeados.contains(o); }
    public void marcar(Obstaculo o) { golpeados.add(o); }

    /** Usado por el cliente de red para copiar el estado que manda el anfitrión. */
    public void sincronizar(double x, double carrilY, double vel, double stun, double nitro, int carrilObj,
                            boolean nitroAct, boolean term, double tFin, int golpes, int tipo) {
        this.x = x; this.carrilY = carrilY; this.velocidad = vel; this.stun = stun; this.nitro = nitro;
        this.carrilObjetivo = carrilObj; this.nitroActivo = nitroAct; this.terminado = term; this.tiempoFin = tFin;
        if (golpes != this.golpes && tipo > 0 && tipo < Obstaculo.TEXTOS.length) {
            this.mensaje = Obstaculo.TEXTOS[tipo];
            this.mensajeT = 1.2;
        }
        this.golpes = golpes; this.ultimoTipo = tipo;
    }

    public void tickVisual(double dt) { if (mensajeT > 0) mensajeT -= dt; }

    public void terminar(double tiempo) { terminado = true; tiempoFin = tiempo; }

    public String getNombre() { return nombre; }
    public int getIndice() { return indice; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public Color getColor() { return color; }
    public Controlador getControlador() { return controlador; }
    public void setControlador(Controlador c) { this.controlador = c; }
    public double getX() { return x; }
    public double getVelocidad() { return velocidad; }
    public double getCarrilY() { return carrilY; }
    public double getNitro() { return nitro; }
    public double getStun() { return stun; }
    public int getCarrilObjetivo() { return carrilObjetivo; }
    public void setCarrilObjetivo(int c) { carrilObjetivo = Math.max(0, Math.min(Config.CARRILES - 1, c)); }
    public void setNitroPedido(boolean n) { nitroPedido = n; }
    public boolean isNitroActivo() { return nitroActivo; }
    public boolean isTerminado() { return terminado; }
    public double getTiempoFin() { return tiempoFin; }
    public int getGolpes() { return golpes; }
    public int getUltimoTipo() { return ultimoTipo; }
    public int getIdxObst() { return idxObst; }
    public void setIdxObst(int i) { idxObst = i; }
    public String getMensaje() { return mensaje; }
    public double getMensajeT() { return mensajeT; }
}
