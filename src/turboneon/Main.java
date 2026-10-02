package turboneon;

import turboneon.ui.Tema;
import turboneon.ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.Font;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        UIManager.put("OptionPane.background", new ColorUIResource(Tema.PANEL));
        UIManager.put("Panel.background", new ColorUIResource(Tema.PANEL));
        UIManager.put("OptionPane.messageForeground", new ColorUIResource(Tema.TEXTO));
        UIManager.put("Label.foreground", new ColorUIResource(Tema.TEXTO));
        UIManager.put("OptionPane.messageFont", Tema.fuente(Font.PLAIN, 15f));
        UIManager.put("Button.font", Tema.fuente(Font.BOLD, 13f));
        UIManager.put("TextField.background", new ColorUIResource(0x1A2140));
        UIManager.put("TextField.foreground", new ColorUIResource(0xFFFFFF));
        UIManager.put("TextField.caretForeground", new ColorUIResource(Tema.CIAN));
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
