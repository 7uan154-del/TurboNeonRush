package turboneon.ui;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel implements Pantalla {
    private final Timer timer;
    private double t;

    public MenuPanel(VentanaPrincipal v) {
        setLayout(new GridBagLayout());
        setBackground(Tema.FONDO);

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.setLayout(new BoxLayout(botones, BoxLayout.Y_AXIS));
        agregar(botones, "2 JUGADORES  ·  MISMO PC", Tema.CIAN, () -> v.menuLocal2());
        agregar(botones, "CONTRA LA COMPUTADORA", Tema.NARANJA, () -> v.menuCPU());
        agregar(botones, "CREAR PARTIDA EN RED  (ANFITRIÓN)", Tema.LIMA, () -> v.menuAnfitrion());
        agregar(botones, "UNIRSE A PARTIDA EN RED", Tema.LIMA, () -> v.menuCliente());
        agregar(botones, "CÓMO JUGAR", Tema.TEXTO, () -> v.mostrarManual());
        agregar(botones, "SALIR", Tema.ROJO, () -> System.exit(0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.weighty = 1; gc.anchor = GridBagConstraints.SOUTH; gc.insets = new Insets(0, 0, 28, 0);
        add(botones, gc);

        timer = new Timer(25, e -> { t += 0.025; repaint(); });
        timer.start();
    }

    private void agregar(JPanel p, String texto, Color color, Runnable accion) {
        BotonNeon b = new BotonNeon(texto, color);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addActionListener(e -> accion.run());
        p.add(b);
        p.add(Box.createVerticalStrut(9));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.calidad(g);
        int w = getWidth(), h = getHeight();
        Tema.fondoSynth(g, w, h, t);
        int ty = Math.max(90, (int) (h * 0.17));
        Tema.brillo(g, "TURBO", w / 2.0, ty, Tema.fuente(Font.BOLD | Font.ITALIC, 84f), Tema.CIAN, true);
        Tema.brillo(g, "NEÓN RUSH", w / 2.0, ty + 78, Tema.fuente(Font.BOLD | Font.ITALIC, 66f), Tema.MAGENTA, true);
        g.setFont(Tema.fuente(Font.PLAIN, 15f));
        g.setColor(Tema.TEXTO);
        Tema.centrado(g, "La carrera más loca, loca, reloca del salón  |  Esquiva, acelera y llega primero a la META", w / 2.0, ty + 112);
        g.dispose();
    }

    @Override public void detener() { timer.stop(); }
}
