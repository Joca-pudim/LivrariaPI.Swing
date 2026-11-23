package view;

import java.awt.Component;
import javax.swing.JOptionPane;
import model.Usuario;
import util.Sessao;

/** Verificações de login/perfil usadas pelas telas novas. */
public class Acesso {

    /** Retorna o cliente logado, ou null (mostrando aviso) se não houver. */
    public static Usuario cliente(Component pai) {
        return exigir(pai, "Cliente", "Esta função é para clientes. Faça login como Cliente.");
    }

    public static Usuario loja(Component pai) {
        return exigir(pai, "Loja", "Esta função é para lojas. Faça login como Loja.");
    }

    private static Usuario exigir(Component pai, String tipo, String msg) {
        Usuario u = Sessao.usuario;
        if (u == null || !tipo.equals(u.getTipo())) {
            JOptionPane.showMessageDialog(pai, u == null ? "Você precisa estar logado." : msg,
                    "Acesso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return u;
    }

    public static void erroBanco(Component pai, Exception e) {
        JOptionPane.showMessageDialog(pai, "Erro ao acessar o banco: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
