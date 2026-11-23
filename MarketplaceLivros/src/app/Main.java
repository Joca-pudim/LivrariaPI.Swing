package app;

import java.awt.EventQueue;
import javax.swing.UIManager;
import view.MenuIntegrador;
import view.TelaLogin;

/** Ponto de entrada: instala os menus e abre a TelaLogin . */
public class Main {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            
        }
        EventQueue.invokeLater(() -> {
            MenuIntegrador.instalar();
            new TelaLogin().setVisible(true);
        });
    }
}
