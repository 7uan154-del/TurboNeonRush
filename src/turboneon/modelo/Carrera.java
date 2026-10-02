package turboneon.modelo;

import java.util.ArrayList;
import java.util.List;

public class Carrera {
    public enum Estado { CUENTA_ATRAS, EN_CURSO, TERMINADA }

    private final List<Corredor> corredores;
    private final Pista pista;
    private final boolean remota;
    private Estado estado = Estado.CUENTA_ATRAS;
    private double cuenta = Config.CUENTA_ATRAS;
    private double tiempo;
    private double tiempoPrimero = -1;

    public Carrera(List<Corredor> corredores, Pista pista, boolean remota) {
        this.corredores = corredores; this.pista = pista; this.remota = remota;
    }

    public void actualizar(double dt) {
        if (remota) return;
        for (Corredor r : corredores) if (r.getControlador() != null && !r.isTerminado()) {
            r.getControlador().actualizar(r, dt, pista);
        }
        switch (estado) {
            case CUENTA_ATRAS:
                cuenta -= dt;
                if (cuenta <= 0) { estado = Estado.EN_CURSO; tiempo = 0; }
                break;
            case EN_CURSO:
                tiempo += dt;
                boolean todos = true;
                for (Corredor r : corredores) {
                    r.actualizar(dt);
                    if (!r.isTerminado()) {
                        colisiones(r);
                        if (r.getX() >= pista.getLongitud()) {
                            r.terminar(tiempo);
                            if (tiempoPrimero < 0) tiempoPrimero = tiempo;
                        } else todos = false;
                    }
                }
                if (todos || (tiempoPrimero >= 0 && tiempo - tiempoPrimero > Config.ESPERA_TRAS_PRIMERO)) {
                    estado = Estado.TERMINADA;
                }
                break;
            case TERMINADA:
                for (Corredor r : corredores) r.actualizar(dt);   // los carros siguen rodando suave
                break;
        }
        for (Corredor r : corredores) r.tickVisual(dt);
    }

    private void colisiones(Corredor r) {
        List<Obstaculo> lista = pista.getObstaculos();
        int i = r.getIdxObst();
        while (i < lista.size() && lista.get(i).getX() < r.getX() - 80) i++;
        r.setIdxObst(i);
        for (int j = i; j < lista.size(); j++) {
            Obstaculo o = lista.get(j);
            if (o.getX() > r.getX() + 55) break;
            if (r.yaGolpeo(o)) continue;
            if (Math.abs(o.getX() - r.getX()) < 55 && Math.abs(r.getCarrilY() - o.getCarril()) < 0.5) {
                r.marcar(o);
                o.aplicar(r);          // POLIMORFISMO: cada obstáculo hace lo suyo
            }
        }
    }

    public List<Corredor> clasificacion() {
        List<Corredor> l = new ArrayList<>(corredores);
        l.sort((a, b) -> {
            if (a.isTerminado() && b.isTerminado()) return Double.compare(a.getTiempoFin(), b.getTiempoFin());
            if (a.isTerminado()) return -1;
            if (b.isTerminado()) return 1;
            return Double.compare(b.getX(), a.getX());
        });
        return l;
    }

    public void sincronizar(Estado e, double cuenta, double tiempo) {
        this.estado = e; this.cuenta = cuenta; this.tiempo = tiempo;
    }

    public List<Corredor> getCorredores() { return corredores; }
    public Pista getPista() { return pista; }
    public Estado getEstado() { return estado; }
    public double getCuenta() { return cuenta; }
    public double getTiempo() { return tiempo; }
    public boolean isRemota() { return remota; }
}
