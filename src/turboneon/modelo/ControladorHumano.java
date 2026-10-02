package turboneon.modelo;

public class ControladorHumano implements Controlador {
    private volatile int deseado = 1;
    private volatile boolean nitro;
    public void arriba() { deseado = Math.max(0, deseado - 1); }
    public void abajo()  { deseado = Math.min(Config.CARRILES - 1, deseado + 1); }
    public void setNitro(boolean n) { nitro = n; }
    public int getDeseado() { return deseado; }
    public boolean isNitro() { return nitro; }

    @Override
    public void actualizar(Corredor c, double dt, Pista pista) {
        if (c.getStun() > 0) deseado = c.getCarrilObjetivo();   // sin control mientras patina
        else c.setCarrilObjetivo(deseado);
        c.setNitroPedido(nitro);
    }
}
