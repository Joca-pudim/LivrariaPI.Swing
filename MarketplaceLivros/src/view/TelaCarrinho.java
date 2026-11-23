package view;

import dao.CarrinhoDAO;
import dao.PedidoDAO;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.ItemCarrinho;
import model.Usuario;

public class TelaCarrinho extends JFrame {

    private final Usuario cliente;
    private final CarrinhoDAO dao = new CarrinhoDAO();
    private List<ItemCarrinho> itens;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Livro", "Autor", "Loja", "Preço", "Qtd", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private final JLabel lblTotal = new JLabel("Total: R$ 0,00");

    public TelaCarrinho(Usuario cliente) {
        super("Meu carrinho");
        this.cliente = cliente;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 420);
        setLocationRelativeTo(null);

        JButton btnRemover = new JButton("Remover selecionado");
        JButton btnLimpar = new JButton("Esvaziar carrinho");
        JButton btnFinalizar = new JButton("Finalizar compra");
        JButton btnFechar = new JButton("Fechar");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));

        btnRemover.addActionListener(e -> remover());
        btnLimpar.addActionListener(e -> limpar());
        btnFinalizar.addActionListener(e -> finalizar());
        btnFechar.addActionListener(e -> dispose());

        JPanel rodape = new JPanel(new BorderLayout(10, 10));
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnRemover);
        botoes.add(btnLimpar);
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        direita.add(lblTotal);
        direita.add(btnFinalizar);
        direita.add(btnFechar);
        rodape.add(botoes, BorderLayout.WEST);
        rodape.add(direita, BorderLayout.EAST);

        setLayout(new BorderLayout(8, 8));
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
        carregar();
    }

    private void carregar() {
        try {
            itens = dao.listar(cliente.getCodigo());
            modelo.setRowCount(0);
            for (ItemCarrinho it : itens) {
                modelo.addRow(new Object[]{it.getCodProduto(), it.getTitulo(), it.getAutor(), it.getLoja(),
                    String.format("%.2f", it.getPreco()), it.getQuantidade(),
                    String.format("%.2f", it.getSubtotal())});
            }
            BigDecimal total = dao.total(cliente.getCodigo());
            lblTotal.setText(String.format("Total: R$ %.2f", total));
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }

    private void remover() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item.");
            return;
        }
        try {
            dao.remover(cliente.getCodigo(), itens.get(linha).getCodProduto());
            carregar();
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }

    private void limpar() {
        if (itens.isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Esvaziar o carrinho?", "Carrinho",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            dao.limpar(cliente.getCodigo());
            carregar();
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }

    private void finalizar() {
        if (itens.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio.");
            return;
        }
        JComboBox<String> cmbForma = new JComboBox<>(new String[]{"PIX", "Cartão de crédito", "Boleto"});
        JSpinner spnParcelas = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spnParcelas.setEnabled(false);
        cmbForma.addActionListener(e -> spnParcelas.setEnabled(cmbForma.getSelectedIndex() == 1));

        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Forma de pagamento:"));
        p.add(cmbForma);
        p.add(new JLabel("Parcelas (cartão):"));
        p.add(spnParcelas);
        p.add(new JLabel("Total:"));
        p.add(new JLabel(lblTotal.getText().replace("Total: ", "")));

        if (JOptionPane.showConfirmDialog(this, p, "Pagamento", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String[] formas = {PedidoDAO.PIX, PedidoDAO.CARTAO, PedidoDAO.BOLETO};
        String forma = formas[cmbForma.getSelectedIndex()];
        try {
            int cod = new PedidoDAO().finalizarCompra(cliente.getCodigo(), forma, (Integer) spnParcelas.getValue());
            String aviso = PedidoDAO.BOLETO.equals(forma)
                    ? "Pagamento pendente. Confirme em \"Meus pedidos e pagamentos\" após pagar o boleto."
                    : "Pagamento aprovado.";
            JOptionPane.showMessageDialog(this, "Pedido #" + cod + " realizado com sucesso!\n" + aviso);
            carregar();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Compra não concluída", JOptionPane.WARNING_MESSAGE);
            carregar();
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }
}
