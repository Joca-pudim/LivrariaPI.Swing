package view;

import dao.PedidoDAO;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import model.Usuario;
import util.Tabela;

public class TelaPedidos extends JFrame {

    private final Usuario cliente;
    private final PedidoDAO dao = new PedidoDAO();
    private final JTable tblPedidos = new JTable();
    private final JTable tblItens = new JTable();
    private Tabela pedidos;

    public TelaPedidos(Usuario cliente) {
        super("Meus pedidos e pagamentos");
        this.cliente = cliente;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 420);
        setLocationRelativeTo(null);

        JButton btnConfirmar = new JButton("Confirmar pagamento do pedido selecionado");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnFechar = new JButton("Fechar");
        btnConfirmar.addActionListener(e -> confirmar());
        btnAtualizar.addActionListener(e -> carregar());
        btnFechar.addActionListener(e -> dispose());

        JPanel abaPedidos = new JPanel(new BorderLayout(6, 6));
        abaPedidos.add(new JScrollPane(tblPedidos), BorderLayout.CENTER);
        JPanel bPedidos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bPedidos.add(btnConfirmar);
        abaPedidos.add(bPedidos, BorderLayout.SOUTH);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Pedidos e pagamentos", abaPedidos);
        abas.addTab("Livros comprados", new JScrollPane(tblItens));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(btnAtualizar);
        rodape.add(btnFechar);

        setLayout(new BorderLayout(8, 8));
        add(abas, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
        carregar();
    }

    private void carregar() {
        try {
            pedidos = dao.pedidosComPagamento(cliente.getCodigo());
            tblPedidos.setModel(pedidos.modelo());
            tblItens.setModel(dao.historicoItens(cliente.getCodigo()).modelo());
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }

    private void confirmar() {
        int linha = tblPedidos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido.");
            return;
        }
        int codPedido = Integer.parseInt(String.valueOf(pedidos.valor(linha, 0)));
        try {
            if (dao.confirmarPagamento(codPedido, cliente.getCodigo())) {
                JOptionPane.showMessageDialog(this, "Pagamento do pedido #" + codPedido + " confirmado.");
                carregar();
            } else {
                JOptionPane.showMessageDialog(this, "Este pedido não tem pagamento pendente.");
            }
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }
}
