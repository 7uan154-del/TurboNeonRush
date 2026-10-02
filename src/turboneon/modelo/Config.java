package turboneon.modelo;

import java.awt.Color;

public final class Config {
    private Config() {}

    public static final double LONGITUD_PISTA = 22000;      // unidades de la meta
    public static final int CARRILES = 3;                    // sub-carriles por corredor
    public static final double CUENTA_ATRAS = 3.0;           // segundos
    public static final double ESPERA_TRAS_PRIMERO = 8.0;    // segundos para que lleguen los demás
    public static final int PUERTO_DEFECTO = 5555;

    public static final Color[] COLORES = {
        new Color(0x00E5FF), new Color(0xFF2BD6), new Color(0xB6FF3B), new Color(0xFF9A1F)
    };
}
