package view;

import dao.RelatorioDAO;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import model.Usuario;
import util.Tabela;

/** Relatórios da loja: operações de agregação, condicionais e junções. */
public class TelaRelatorios extends JFrame {

    private static final String[] NOMES = {
        "Vendas da minha loja (por livro e comprador)",
        "Total vendido por loja (ranking)",
        "Pedidos por status",
        "Ticket médio dos pedidos",
        "Meus livros com estoque baixo",
        "Livro mais caro/mais barato por categoria"
    };
    private static final String[] TECNICAS = {
        "JOIN de 4 tabelas: item_pedido, pedido, produto, cliente",
        "SUM + COUNT + GROUP BY com JOIN",
        "COUNT + SUM + GROUP BY",
        "AVG + MIN + MAX",
        "Condicional WHERE estoque <= limite",
        "MIN + MAX + AVG + GROUP BY"
    };

    private final Usuario loja;
    private final RelatorioDAO dao = new RelatorioDAO();
    private final JComboBox<String> cmbRelatorio = new JComboBox<>(NOMES);
    private final JLabel lblTecnica = new JLabel(TECNICAS[0]);
    private final JTable tabela = new JTable();
    private final JLabel lblLinhas = new JLabel(" ");

    public TelaRelatorios(Usuario loja) {
        super("Relatórios - " + loja.getNome());
        this.loja = loja;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 460);
        setLocationRelativeTo(null);

        JButton btnGerar = new JButton("Gerar");
        JButton btnFechar = new JButton("Fechar");
        btnGerar.addActionListener(e -> gerar());
        btnFechar.addActionListener(e -> dispose());
        cmbRelatorio.addActionListener(e -> lblTecnica.setText(TECNICAS[cmbRelatorio.getSelectedIndex()]));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Relatório:"));
        topo.add(cmbRelatorio);
        topo.add(btnGerar);
        topo.add(lblTecnica);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(lblLinhas, BorderLayout.WEST);
        JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        d.add(btnFechar);
        rodape.add(d, BorderLayout.EAST);

        setLayout(new BorderLayout(8, 8));
        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
        gerar();
    }

    private void gerar() {
        try {
            Tabela t;
            switch (cmbRelatorio.getSelectedIndex()) {
                case 0: t = dao.vendasDaLoja(loja.getCodigo()); break;
                case 1: t = dao.totalVendidoPorLoja(); break;
                case 2: t = dao.pedidosPorStatus(); break;
                case 3: t = dao.ticketMedio(); break;
                case 4:
                    String s = JOptionPane.showInputDialog(this, "Mostrar livros com estoque até:", "5");
                    if (s == null) return;
                    t = dao.estoqueBaixo(loja.getCodigo(), Integer.parseInt(s.trim()));
                    break;
                default: t = dao.extremosPorCategoria();
            }
            tabela.setModel(t.modelo());
            lblLinhas.setText("  " + t.tamanho() + " linha(s)");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um número inteiro.");
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }
}
