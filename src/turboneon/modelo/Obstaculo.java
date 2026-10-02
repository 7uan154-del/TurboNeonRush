package turboneon.modelo;

import java.awt.Graphics2D;

public abstract class Obstaculo {
    public static final String[] TEXTOS = { "", "¡CONO!", "¡ACEITE!", "¡ROCA!", "¡TURBO!" };

    private final double x;
    private final int carril;

    protected Obstaculo(double x, int carril) { this.x = x; this.carril = carril; }

    public double getX() { return x; }
    public int getCarril() { return carril; }
    public boolean esBonus() { return false; }

    /** 1 cono, 2 aceite, 3 roca, 4 turbo. */
    public abstract int getTipo();
    /** Efecto sobre el corredor que lo toca. */
    public abstract void aplicar(Corredor c);
    /** Dibujo centrado en (cx,cy) con tamaño aproximado tam. */
    public abstract void dibujar(Graphics2D g, double cx, double cy, double tam, double t);
}
