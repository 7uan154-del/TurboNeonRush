package turboneon.modelo;

import java.awt.event.KeyEvent;

public class TeclasJugador {
    public final int[] arriba, abajo, nitro;
    public final String etiqueta;

    private TeclasJugador(int[] arriba, int[] abajo, int[] nitro, String etiqueta) {
        this.arriba = arriba; this.abajo = abajo; this.nitro = nitro; this.etiqueta = etiqueta;
    }

    public static final TeclasJugador P1 = new TeclasJugador(
        new int[]{KeyEvent.VK_W}, new int[]{KeyEvent.VK_S}, new int[]{KeyEvent.VK_D}, "W / S  ·  Nitro: D");
    public static final TeclasJugador P2 = new TeclasJugador(
        new int[]{KeyEvent.VK_UP}, new int[]{KeyEvent.VK_DOWN}, new int[]{KeyEvent.VK_RIGHT}, "↑ / ↓  ·  Nitro: →");
    public static final TeclasJugador SOLO = new TeclasJugador(
        new int[]{KeyEvent.VK_W, KeyEvent.VK_UP}, new int[]{KeyEvent.VK_S, KeyEvent.VK_DOWN},
        new int[]{KeyEvent.VK_D, KeyEvent.VK_RIGHT, KeyEvent.VK_SPACE}, "W/S o ↑/↓  ·  Nitro: D, → o ESPACIO");

    public static boolean contiene(int[] teclas, int k) {
        for (int t : teclas) if (t == k) return true;
        return false;
    }
}
