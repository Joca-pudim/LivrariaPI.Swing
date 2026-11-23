package view;

import dao.CarrinhoDAO;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import javax.swing.*;
import model.Usuario;

/**
 * Acrescenta uma barra de menus à TelaPrincipal (catálogo)
 */
public class MenuIntegrador {

    public static void instalar() {
        Toolkit.getDefaultToolkit().addAWTEventListener(ev -> {
            if (ev.getID() == WindowEvent.WINDOW_OPENED && ev.getSource() instanceof TelaPrincipal) {
                TelaPrincipal t = (TelaPrincipal) ev.getSource();
                if (t.getJMenuBar() == null) {
                    JMenuBar barra = criarBarra(t);
                    t.setJMenuBar(barra);
                    t.setSize(t.getWidth(), t.getHeight() + barra.getPreferredSize().height);
                    t.revalidate();
                }
            }
        }, AWTEvent.WINDOW_EVENT_MASK);
    }

    private static JMenuBar criarBarra(TelaPrincipal tela) {
        JMenu compras = new JMenu("Compras");
        compras.add(item("Adicionar livro selecionado ao carrinho", () -> adicionarSelecionado(tela)));
        compras.add(item("Meu carrinho", () -> {
            Usuario u = Acesso.cliente(tela);
            if (u != null) new TelaCarrinho(u).setVisible(true);
        }));
        compras.add(item("Meus pedidos e pagamentos", () -> {
            Usuario u = Acesso.cliente(tela);
            if (u != null) new TelaPedidos(u).setVisible(true);
        }));

        JMenu minhaLoja = new JMenu("Minha loja");
        minhaLoja.add(item("Meus livros (cadastrar / alterar)", () -> {
            Usuario u = Acesso.loja(tela);
            if (u != null) new TelaMeusLivros(u).setVisible(true);
        }));
        minhaLoja.add(item("Relatórios", () -> {
            Usuario u = Acesso.loja(tela);
            if (u != null) new TelaRelatorios(u).setVisible(true);
        }));

        JMenuBar barra = new JMenuBar();
        barra.add(compras);
        barra.add(minhaLoja);
        return barra;
    }

    private static JMenuItem item(String texto, Runnable acao) {
        JMenuItem mi = new JMenuItem(texto);
        mi.addActionListener(e -> acao.run());
        return mi;
    }

    private static void adicionarSelecionado(TelaPrincipal tela) {
        Usuario u = Acesso.cliente(tela);
        if (u == null) return;
        JTable tabela = procurarTabela(tela.getContentPane());
        if (tabela == null || tabela.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(tela, "Selecione um livro na lista do catálogo.");
            return;
        }
        int linha = tabela.getSelectedRow();
        int codProduto = Integer.parseInt(String.valueOf(tabela.getValueAt(linha, 0)));
        String titulo = String.valueOf(tabela.getValueAt(linha, 1));

        JSpinner spn = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        JPanel p = new JPanel();
        p.add(new JLabel("Quantidade de \"" + titulo + "\":"));
        p.add(spn);
        if (JOptionPane.showConfirmDialog(tela, p, "Adicionar ao carrinho",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            new CarrinhoDAO().adicionar(u.getCodigo(), codProduto, (Integer) spn.getValue());
            JOptionPane.showMessageDialog(tela, "Livro adicionado ao carrinho.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(tela, e.getMessage(), "Carrinho", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            Acesso.erroBanco(tela, e);
        }
    }

    private static JTable procurarTabela(Container c) {
        for (Component filho : c.getComponents()) {
            if (filho instanceof JTable) return (JTable) filho;
            if (filho instanceof Container) {
                JTable t = procurarTabela((Container) filho);
                if (t != null) return t;
            }
        }
        return null;
    }
}
