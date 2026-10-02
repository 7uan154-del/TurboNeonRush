package turboneon.ui;

import javax.swing.JButton;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class BotonNeon extends JButton {
    private final Color color;
    private boolean encima;

    public BotonNeon(String texto, Color color) {
        super(texto);
        this.color = color;
        setFont(Tema.fuente(Font.BOLD, 16f));
        setForeground(Tema.TEXTO);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(400, 46));
        setMaximumSize(new Dimension(400, 46));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Tema.calidad(g);
        int w = getWidth(), h = getHeight();
        RoundRectangle2D forma = new RoundRectangle2D.Double(3, 3, w - 6, h - 6, 18, 18);
        g.setColor(Tema.alpha(color, encima ? 90 : 34));
        g.fill(forma);
        if (encima) {
            g.setColor(Tema.alpha(color, 50));
            g.setStroke(new BasicStroke(6f));
            g.draw(forma);
        }
        g.setColor(isEnabled() ? color : Tema.APAGADO);
        g.setStroke(new BasicStroke(2f));
        g.draw(forma);
        g.setFont(getFont());
        g.setColor(isEnabled() ? Color.WHITE : Tema.APAGADO);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(getText(), (w - fm.stringWidth(getText())) / 2f, (h + fm.getAscent() - fm.getDescent()) / 2f);
        g.dispose();
    }
}
