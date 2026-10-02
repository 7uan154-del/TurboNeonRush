package turboneon.modelo;

import java.util.List;
import java.util.Random;

public class ControladorCPU implements Controlador {
    private final double precision;
    private final Random rnd;
    private double espera;

    public ControladorCPU(double precision, long semilla) {
        this.precision = precision;
        this.rnd = new Random(semilla);
    }

    @Override
    public void actualizar(Corredor c, double dt, Pista pista) {
        espera -= dt;
        if (espera > 0) return;
        espera = 0.10 + rnd.nextDouble() * 0.10;                  // tiempo de reacción
        if (c.getStun() > 0) return;

        double alcance = 130 + c.getVelocidad() * 0.5;
        double[] peligro = { 1e9, 1e9, 1e9 }, bonus = { 1e9, 1e9, 1e9 };
        List<Obstaculo> lista = pista.getObstaculos();
        for (int i = c.getIdxObst(); i < lista.size(); i++) {
            Obstaculo o = lista.get(i);
            double d = o.getX() - c.getX();
            if (d > alcance) break;
            if (d < -30) continue;
            double[] destino = o.esBonus() ? bonus : peligro;
            destino[o.getCarril()] = Math.min(destino[o.getCarril()], d);
        }

        int actual = c.getCarrilObjetivo();
        int[] vecinos = rnd.nextBoolean() ? new int[]{ actual - 1, actual + 1 } : new int[]{ actual + 1, actual - 1 };
        if (peligro[actual] < 1e9) {
            if (rnd.nextDouble() < precision) {
                int mejor = actual;
                double mejorD = peligro[actual];
                for (int v : vecinos) {
                    if (v < 0 || v >= Config.CARRILES) continue;
                    if (peligro[v] > mejorD) { mejor = v; mejorD = peligro[v]; }
                }
                c.setCarrilObjetivo(mejor);
            }
        } else {
            for (int v : vecinos) {
                if (v >= 0 && v < Config.CARRILES && bonus[v] < 1e9 && peligro[v] == 1e9 && rnd.nextDouble() < precision) {
                    c.setCarrilObjetivo(v);
                    break;
                }
            }
        }
        c.setNitroPedido(c.getNitro() > 45 && peligro[c.getCarrilObjetivo()] == 1e9);
    }
}
