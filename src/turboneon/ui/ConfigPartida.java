package turboneon.ui;

import turboneon.modelo.*;

import java.util.*;

public class ConfigPartida {
    public enum Modo { LOCAL, CPU, ANFITRION, CLIENTE }
    public enum Tipo { HUMANO, CPU, REMOTO }

    public static class Slot {
        public Tipo tipo;
        public String nombre;
        public int vehiculo;
        public TeclasJugador teclas;
        public ControladorRemoto remoto;

        Slot(Tipo tipo, String nombre) { this.tipo = tipo; this.nombre = nombre; }
    }

    private static final String[] NOMBRES_CPU = { "CPU Rayo", "CPU Turbo" };

    public final Modo modo;
    public final List<Slot> slots = new ArrayList<>();
    public String ip = "127.0.0.1";
    public int puerto = Config.PUERTO_DEFECTO;

    private ConfigPartida(Modo modo) { this.modo = modo; }

    public static ConfigPartida local(int humanos, int cpus) {
        ConfigPartida c = new ConfigPartida(cpus > 0 ? Modo.CPU : Modo.LOCAL);
        TeclasJugador[] mapas = humanos == 1 ? new TeclasJugador[]{ TeclasJugador.SOLO }
                : new TeclasJugador[]{ TeclasJugador.P1, TeclasJugador.P2 };
        for (int i = 0; i < humanos; i++) {
            Slot s = new Slot(Tipo.HUMANO, "Jugador " + (i + 1));
            s.teclas = mapas[i];
            s.vehiculo = i % Vehiculo.TOTAL;
            c.slots.add(s);
        }
        for (int i = 0; i < cpus; i++) c.slots.add(new Slot(Tipo.CPU, NOMBRES_CPU[i % NOMBRES_CPU.length]));
        return c;
    }

    public static ConfigPartida anfitrion(int total) {
        ConfigPartida c = new ConfigPartida(Modo.ANFITRION);
        Slot yo = new Slot(Tipo.HUMANO, "Anfitrión");
        yo.teclas = TeclasJugador.SOLO;
        c.slots.add(yo);
        for (int i = 1; i < total; i++) c.slots.add(new Slot(Tipo.REMOTO, "Jugador " + (i + 1)));
        return c;
    }

    public static ConfigPartida cliente(String ip, int puerto, String nombre) {
        ConfigPartida c = new ConfigPartida(Modo.CLIENTE);
        c.ip = ip;
        c.puerto = puerto;
        Slot yo = new Slot(Tipo.HUMANO, nombre);
        yo.teclas = TeclasJugador.SOLO;
        c.slots.add(yo);
        return c;
    }

    public boolean esRed() { return modo == Modo.ANFITRION || modo == Modo.CLIENTE; }

    public void completarCPU() {
        Random r = new Random();
        for (Slot s : slots) if (s.tipo == Tipo.CPU) s.vehiculo = r.nextInt(Vehiculo.TOTAL);
    }

    public Carrera crearCarrera(long semilla) {
        Random rnd = new Random(semilla);
        List<Corredor> lista = new ArrayList<>();
        int cpu = 0;
        for (int i = 0; i < slots.size(); i++) {
            Slot s = slots.get(i);
            Controlador ctl;
            switch (s.tipo) {
                case HUMANO: ctl = new ControladorHumano(); break;
                case CPU:    ctl = new ControladorCPU(0.90 - 0.08 * cpu++, rnd.nextLong()); break;
                default:     ctl = s.remoto != null ? s.remoto : new ControladorCPU(0.8, rnd.nextLong());
            }
            lista.add(new Corredor(s.nombre, i, Vehiculo.crear(s.vehiculo), Config.COLORES[i], ctl));
        }
        return new Carrera(lista, new Pista(semilla, Config.LONGITUD_PISTA), false);
    }

    public static Carrera crearCarreraCliente(long semilla, String[] nombres, int[] vehiculos) {
        List<Corredor> lista = new ArrayList<>();
        for (int i = 0; i < nombres.length; i++) {
            lista.add(new Corredor(nombres[i], i, Vehiculo.crear(vehiculos[i]), Config.COLORES[i], null));
        }
        return new Carrera(lista, new Pista(semilla, Config.LONGITUD_PISTA), true);
    }
}
