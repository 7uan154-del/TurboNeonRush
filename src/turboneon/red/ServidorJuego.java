package turboneon.red;

import turboneon.modelo.Carrera;
import turboneon.modelo.ControladorRemoto;
import turboneon.modelo.Corredor;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServidorJuego {
    public interface Oyente {
        default void clienteConectado(int conectados, int esperados, String nombre) {}
        default void todosListos() {}
        default void clienteDesconectado(ClienteRemoto c) {}
    }

    public static class ClienteRemoto {
        private final Socket socket;
        private final DataInputStream in;
        private final DataOutputStream out;
        private final String nombre;
        private final int vehiculo;
        private final ControladorRemoto controlador = new ControladorRemoto();
        private volatile boolean vivo = true;
        private int indice;

        ClienteRemoto(Socket s, DataInputStream in, DataOutputStream out, String nombre, int vehiculo) {
            this.socket = s; this.in = in; this.out = out; this.nombre = nombre; this.vehiculo = vehiculo;
        }
        public String getNombre() { return nombre; }
        public int getVehiculo() { return vehiculo; }
        public ControladorRemoto getControlador() { return controlador; }
        public int getIndice() { return indice; }
    }

    private final int esperados;
    private ServerSocket servidor;
    private final List<ClienteRemoto> clientes = new CopyOnWriteArrayList<>();
    private volatile Oyente oyente = new Oyente() {};
    private volatile boolean cerrado, iniciada;

    public ServidorJuego(int esperados) { this.esperados = esperados; }

    public void setOyente(Oyente o) { this.oyente = o; }
    public List<ClienteRemoto> getClientes() { return clientes; }

    public void iniciar(int puerto) throws IOException {
        servidor = new ServerSocket(puerto);
        Thread t = new Thread(this::aceptar, "servidor-aceptar");
        t.setDaemon(true);
        t.start();
    }

    private void aceptar() {
        try {
            while (!cerrado && clientes.size() < esperados) {
                Socket s = servidor.accept();
                try {
                    s.setTcpNoDelay(true);
                    DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
                    if (in.readByte() != Protocolo.HOLA) { s.close(); continue; }
                    String nombre = in.readUTF();
                    int veh = in.readInt();
                    ClienteRemoto c = new ClienteRemoto(s, in, out, nombre, veh);
                    clientes.add(c);
                    Thread lector = new Thread(() -> leer(c), "servidor-lector");
                    lector.setDaemon(true);
                    lector.start();
                    oyente.clienteConectado(clientes.size(), esperados, nombre);
                } catch (IOException e) {
                    try { s.close(); } catch (IOException ignorada) { /* nada que hacer */ }
                }
            }
            if (!cerrado) oyente.todosListos();
        } catch (IOException e) {
            // el ServerSocket se cerró (cancelaron la partida): fin normal
        }
    }

    private void leer(ClienteRemoto c) {
        try {
            while (c.vivo && !cerrado) {
                byte tipo = c.in.readByte();
                if (tipo == Protocolo.INPUT) {
                    int carril = c.in.readByte();
                    boolean nitro = c.in.readBoolean();
                    c.controlador.fijar(carril, nitro);
                }
            }
        } catch (IOException e) {
            // el jugador cerró el juego o se cayó la red
        } finally {
            desconectar(c);
        }
    }

    private synchronized void desconectar(ClienteRemoto c) {
        if (!c.vivo) return;
        c.vivo = false;
        try { c.socket.close(); } catch (IOException ignorada) { /* ya estaba cerrado */ }
        if (cerrado) return;
        if (!iniciada) {
            clientes.remove(c);
            oyente.clienteConectado(clientes.size(), esperados, null);
        } else {
            oyente.clienteDesconectado(c);
        }
    }

    public void enviarInicio(long semilla, Carrera carrera) {
        iniciada = true;
        List<Corredor> cs = carrera.getCorredores();
        for (int k = 0; k < clientes.size(); k++) {
            ClienteRemoto cl = clientes.get(k);
            cl.indice = k + 1;
            try {
                synchronized (cl.out) {
                    cl.out.writeByte(Protocolo.INICIO);
                    cl.out.writeLong(semilla);
                    cl.out.writeInt(cs.size());
                    cl.out.writeInt(cl.indice);
                    for (Corredor r : cs) {
                        cl.out.writeUTF(r.getNombre());
                        cl.out.writeInt(r.getVehiculo().getId());
                    }
                    cl.out.flush();
                }
            } catch (IOException e) {
                desconectar(cl);
            }
        }
    }

    public void enviarEstado(Carrera carrera) {
        if (clientes.isEmpty()) return;
        byte[] datos;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream(160);
            DataOutputStream d = new DataOutputStream(bo);
            Instantanea.escribir(d, carrera);
            d.flush();
            datos = bo.toByteArray();
        } catch (IOException e) {
            return;
        }
        for (ClienteRemoto cl : clientes) {
            if (!cl.vivo) continue;
            try {
                synchronized (cl.out) {
                    cl.out.write(datos);
                    cl.out.flush();
                }
            } catch (IOException e) {
                desconectar(cl);
            }
        }
    }

    public void cerrar() {
        cerrado = true;
        try { if (servidor != null) servidor.close(); } catch (IOException ignorada) { /* ya cerrado */ }
        for (ClienteRemoto c : clientes) {
            c.vivo = false;
            try { c.socket.close(); } catch (IOException ignorada) { /* ya cerrado */ }
        }
    }

    public static List<String> direccionesLocales() {
        List<String> ips = new ArrayList<>();
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback()) continue;
                for (InetAddress a : Collections.list(ni.getInetAddresses())) {
                    if (a instanceof Inet4Address && !a.isLoopbackAddress()) ips.add(a.getHostAddress());
                }
            }
        } catch (SocketException e) {
            // sin información de red: la lista queda vacía
        }
        return ips;
    }
}
