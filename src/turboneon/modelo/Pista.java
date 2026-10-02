package turboneon.modelo;

import java.util.*;

public class Pista {
    private final long semilla;
    private final double longitud;
    private final List<Obstaculo> obstaculos = new ArrayList<>();

    public Pista(long semilla, double longitud) {
        this.semilla = semilla;
        this.longitud = longitud;
        Random rnd = new Random(semilla);
        double x = 1600;
        while (x < longitud - 1200) {
            int cantidad = rnd.nextInt(100) < 35 ? 2 : 1;           // nunca se bloquean los 3 carriles
            List<Integer> carriles = new ArrayList<>(Arrays.asList(0, 1, 2));
            Collections.shuffle(carriles, rnd);
            for (int i = 0; i < cantidad; i++) {
                obstaculos.add(crear(rnd, x, carriles.get(i)));
            }
            x += 260 + rnd.nextInt(220);
        }
        obstaculos.sort(Comparator.comparingDouble(Obstaculo::getX));
    }

    private Obstaculo crear(Random rnd, double x, int carril) {
        int p = rnd.nextInt(100);
        if (p < 38) return new Cono(x, carril);
        if (p < 66) return new Aceite(x, carril);
        if (p < 88) return new Roca(x, carril);
        return new Turbo(x, carril);
    }

    public long getSemilla() { return semilla; }
    public double getLongitud() { return longitud; }
    public List<Obstaculo> getObstaculos() { return obstaculos; }
}
