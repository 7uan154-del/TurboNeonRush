package turboneon.red;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;

public class ClienteJuego {
    public interface Oyente {
        void inicio(long semilla, int miIndice, String[] nombres, int[] vehiculos);
        void desconectado(String motivo);
    }

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private volatile boolean cerrado;
    private volatile Consumer<Instantanea> alEstado;
    private volatile Consumer<String> alDesconectar;
    private int n;

    public void setAlEstado(Consumer<Instantanea> c) { this.alEstado = c; }
    public void setAlDesconectar(Consumer<String> c) { this.alDesconectar = c; }

    public void conectar(String host, int puerto, String nombre, int vehiculo, Oyente oyente) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, puerto), 5000);
        socket.setTcpNoDelay(true);
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        out.writeByte(Protocolo.HOLA);
        out.writeUTF(nombre);
        out.writeInt(vehiculo);
        out.flush();
        Thread t = new Thread(() -> leer(oyente), "cliente-lector");
        t.setDaemon(true);
        t.start();
    }

    private void leer(Oyente oyente) {
        try {
            while (!cerrado) {
                byte tipo = in.readByte();
                if (tipo == Protocolo.INICIO) {
                    long semilla = in.readLong();
                    n = in.readInt();
                    int yo = in.readInt();
                    String[] nombres = new String[n];
                    int[] veh = new int[n];
                    for (int i = 0; i < n; i++) { nombres[i] = in.readUTF(); veh[i] = in.readInt(); }
                    oyente.inicio(semilla, yo, nombres, veh);
                } else if (tipo == Protocolo.ESTADO) {
                    Instantanea s = Instantanea.leer(in, n);
                    Consumer<Instantanea> c = alEstado;
                    if (c != null) c.accept(s);
                }
            }
        } catch (IOException e) {
            if (!cerrado) {
                Consumer<String> c = alDesconectar;
                if (c != null) c.accept("Se perdió la conexión con el anfitrión.");
                else oyente.desconectado("Se perdió la conexión con el anfitrión.");
            }
        }
    }

    public void enviarInput(int carril, boolean nitro) {
        if (cerrado || out == null) return;
        try {
            synchronized (out) {
                out.writeByte(Protocolo.INPUT);
                out.writeByte(carril);
                out.writeBoolean(nitro);
                out.flush();
            }
        } catch (IOException e) {
            // el hilo lector detectará la caída y avisará
        }
    }

    public void cerrar() {
        cerrado = true;
        try { if (socket != null) socket.close(); } catch (IOException ignorada) { /* ya cerrado */ }
    }
}
