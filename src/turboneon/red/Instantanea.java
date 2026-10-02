package turboneon.red;

import turboneon.modelo.Carrera;
import turboneon.modelo.Corredor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Instantanea {
    private int estado;
    private float cuenta, tiempo;
    private float[] x, carrilY, vel, stun, nitro, tFin;
    private int[] carrilObj, golpes, tipo;
    private boolean[] nitroAct, term;

    public static void escribir(DataOutputStream o, Carrera c) throws IOException {
        o.writeByte(Protocolo.ESTADO);
        o.writeByte(c.getEstado().ordinal());
        o.writeFloat((float) c.getCuenta());
        o.writeFloat((float) c.getTiempo());
        for (Corredor r : c.getCorredores()) {
            o.writeFloat((float) r.getX());
            o.writeFloat((float) r.getCarrilY());
            o.writeFloat((float) r.getVelocidad());
            o.writeFloat((float) r.getStun());
            o.writeFloat((float) r.getNitro());
            o.writeByte(r.getCarrilObjetivo());
            o.writeBoolean(r.isNitroActivo());
            o.writeBoolean(r.isTerminado());
            o.writeFloat((float) r.getTiempoFin());
            o.writeInt(r.getGolpes());
            o.writeByte(r.getUltimoTipo());
        }
    }

    public static Instantanea leer(DataInputStream in, int n) throws IOException {
        Instantanea s = new Instantanea();
        s.estado = in.readByte();
        s.cuenta = in.readFloat();
        s.tiempo = in.readFloat();
        s.x = new float[n]; s.carrilY = new float[n]; s.vel = new float[n]; s.stun = new float[n];
        s.nitro = new float[n]; s.tFin = new float[n]; s.carrilObj = new int[n]; s.golpes = new int[n];
        s.tipo = new int[n]; s.nitroAct = new boolean[n]; s.term = new boolean[n];
        for (int i = 0; i < n; i++) {
            s.x[i] = in.readFloat(); s.carrilY[i] = in.readFloat(); s.vel[i] = in.readFloat();
            s.stun[i] = in.readFloat(); s.nitro[i] = in.readFloat(); s.carrilObj[i] = in.readByte();
            s.nitroAct[i] = in.readBoolean(); s.term[i] = in.readBoolean(); s.tFin[i] = in.readFloat();
            s.golpes[i] = in.readInt(); s.tipo[i] = in.readByte();
        }
        return s;
    }

    public void aplicar(Carrera c) {
        Carrera.Estado[] valores = Carrera.Estado.values();
        if (estado >= 0 && estado < valores.length) c.sincronizar(valores[estado], cuenta, tiempo);
        int n = Math.min(x.length, c.getCorredores().size());
        for (int i = 0; i < n; i++) {
            c.getCorredores().get(i).sincronizar(x[i], carrilY[i], vel[i], stun[i], nitro[i], carrilObj[i],
                    nitroAct[i], term[i], tFin[i], golpes[i], tipo[i]);
        }
    }
}
