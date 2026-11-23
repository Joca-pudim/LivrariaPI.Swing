package view;
import dao.ProdutoDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Categoria;
import model.Produto;
import util.Sessao;
/**
 *
 * @author Lucas
 */
public class TelaPrincipal extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaPrincipal.class.getName());

    /**
     * cria novo form TelaPrincipal
     */
    private DefaultTableModel modelo;
private java.util.List<Categoria> categorias;

public TelaPrincipal() {
    initComponents();
    setLocationRelativeTo(null);

    String nome = (Sessao.usuario != null) ? Sessao.usuario.getNome() : "visitante";
    lblBemVindo.setText("Olá, " + nome + "!");

    modelo = new DefaultTableModel(
            new Object[]{"Código", "Título", "Autor", "Categoria", "Loja",
                         "Condição", "Preço (R$)", "Estoque"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    tblLivros.setModel(modelo);

    carregarCategorias();
    pesquisar();
}

private void carregarCategorias() {
    try {
        categorias = new ProdutoDAO().listarCategorias();
        cmbCategoria.removeAllItems();
        cmbCategoria.addItem("Todas");
        for (Categoria c : categorias) {
            cmbCategoria.addItem(c.getNome());
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Erro ao carregar categorias: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

private BigDecimal lerValor(javax.swing.JTextField campo) {
    String texto = campo.getText().trim().replace(",", ".");
    return texto.isEmpty() ? null : new BigDecimal(texto);
}

private void pesquisar() {
    try {
        Integer codCategoria = null;
        int idx = cmbCategoria.getSelectedIndex();
        if (idx > 0) {
            codCategoria = categorias.get(idx - 1).getCodigo();
        }

        java.util.List<Produto> lista = new ProdutoDAO().buscar(
                txtBusca.getText().trim(), codCategoria,
                lerValor(txtPrecoMin), lerValor(txtPrecoMax));

        modelo.setRowCount(0);
        for (Produto p : lista) {
            modelo.addRow(new Object[]{
                p.getCodigo(), p.getTitulo(), p.getAutor(), p.getCategoria(),
                p.getLoja(), p.getCondicao(), String.format("%.2f", p.getPreco()),
                p.getEstoque()});
        }
        lblResultado.setText(lista.size() + " livro(s) encontrado(s)");
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Informe os preços apenas com números (ex.: 25,90).");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Erro ao buscar livros: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

    /**
     *
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {

        lblBemVindo = new javax.swing.JLabel();
        txtBusca = new javax.swing.JTextField();
        cmbCategoria = new javax.swing.JComboBox<>();
        txtPrecoMax = new javax.swing.JTextField();
        txtPrecoMin = new javax.swing.JTextField();
        btnPesquisar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        lblResultado = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLivros = new javax.swing.JTable();
        btnSair = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblBemVindo.setText("Pesquisar");

        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCategoria.addActionListener(this::cmbCategoriaActionPerformed);

        btnPesquisar.setText("Pesquisar");
        btnPesquisar.addActionListener(this::btnPesquisarActionPerformed);

        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(this::btnLimparActionPerformed);

        lblResultado.setText("Livros!");

        tblLivros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblLivros);
        if (tblLivros.getColumnModel().getColumnCount() > 0) {
            tblLivros.getColumnModel().getColumn(0).setMaxWidth(10);
        }

        btnSair.setText("Sair");
        btnSair.addActionListener(this::btnSairActionPerformed);

        jLabel1.setText("Preço minimo");

        jLabel2.setText("Preço Maximo");

        jLabel3.setText("Seja bem vindo!");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(373, 373, 373)
                        .addComponent(lblResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(245, 245, 245)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblBemVindo)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(txtBusca)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 709, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtPrecoMin, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(txtPrecoMax, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(btnPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)))
                .addGap(18, 18, 18)
                .addComponent(lblBemVindo)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecoMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecoMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisar)
                    .addComponent(btnLimpar))
                .addGap(18, 18, 18)
                .addComponent(lblResultado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        pack();
    }

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {
        Sessao.usuario = null;
        new TelaLogin().setVisible(true);
        this.dispose();        
    }

    private void btnPesquisarActionPerformed(java.awt.event.ActionEvent evt) {
        pesquisar();        
    }

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {
        txtBusca.setText("");
        txtPrecoMin.setText("");
        txtPrecoMax.setText("");
        cmbCategoria.setSelectedIndex(0);
        pesquisar();        
    }

    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {
        
    }


    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Cria o display do form */
        java.awt.EventQueue.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }


    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnPesquisar;
    private javax.swing.JButton btnSair;
    private javax.swing.JComboBox<String> cmbCategoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBemVindo;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JTable tblLivros;
    private javax.swing.JTextField txtBusca;
    private javax.swing.JTextField txtPrecoMax;
    private javax.swing.JTextField txtPrecoMin;

}
