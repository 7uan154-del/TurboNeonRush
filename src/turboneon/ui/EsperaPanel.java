package turboneon.ui;

import javax.swing.*;
import java.awt.*;

public class EsperaPanel extends JPanel implements Pantalla {
    private final Timer timer;
    private double t;
    private volatile String titulo;
    private volatile String[] detalle = new String[0];

    public EsperaPanel(String titulo, Runnable alCancelar) {
        this.titulo = titulo;
        setLayout(new GridBagLayout());
        setBackground(Tema.FONDO);
        BotonNeon cancelar = new BotonNeon("CANCELAR", Tema.ROJO);
        cancelar.setPreferredSize(new Dimension(260, 46));
        cancelar.addActionListener(e -> alCancelar.run());
        GridBagConstraints gc = new GridBagConstraints();
        gc.weighty = 1; gc.anchor = GridBagConstraints.SOUTH; gc.insets = new Insets(0, 0, 60, 0);
        add(cancelar, gc);
        timer = new Timer(30, e -> { t += 0.03; repaint(); });
        timer.start();
    }

    public void setTitulo(String s) { titulo = s; }
    public void setDetalle(String... lineas) { detalle = lineas; }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.calidad(g);
        int w = getWidth(), h = getHeight();
        Tema.fondoSynth(g, w, h, t);
        g.setColor(new Color(5, 3, 15, 170));
        g.fillRect(0, 0, w, h);
        int puntos = (int) (t * 2) % 4;
        Tema.brillo(g, titulo + ".".repeat(puntos), w / 2.0, h * 0.28, Tema.fuente(Font.BOLD | Font.ITALIC, 40f), Tema.LIMA, true);
        g.setFont(Tema.fuente(Font.PLAIN, 19f));
        g.setColor(Tema.TEXTO);
        int y = (int) (h * 0.28) + 56;
        for (String l : detalle) { Tema.centrado(g, l, w / 2.0, y); y += 32; }
        g.dispose();
    }

    @Override public void detener() { timer.stop(); }
}
