package turboneon.modelo;

public class ControladorRemoto implements Controlador {
    private volatile int carril = 1;
    private volatile boolean nitro;
    public void fijar(int carril, boolean nitro) {
        this.carril = Math.max(0, Math.min(Config.CARRILES - 1, carril));
        this.nitro = nitro;
    }

    @Override
    public void actualizar(Corredor c, double dt, Pista pista) {
        if (c.getStun() <= 0) c.setCarrilObjetivo(carril);
        c.setNitroPedido(nitro);
    }
}
