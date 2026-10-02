package turboneon.red;

public final class Protocolo {
    private Protocolo() {}
    public static final byte HOLA = 1;     // cliente -> anfitrión: nombre, vehículo
    public static final byte INICIO = 2;   // anfitrión -> cliente: semilla, jugadores
    public static final byte INPUT = 3;    // cliente -> anfitrión: carril, nitro
    public static final byte ESTADO = 4;   // anfitrión -> cliente: estado de la carrera
}
