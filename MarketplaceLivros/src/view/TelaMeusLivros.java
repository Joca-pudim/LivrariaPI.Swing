package view;

import dao.LivroDAO;
import dao.ProdutoDAO;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import model.Categoria;
import model.Usuario;
import util.Tabela;

/** Cadastro e manutenção dos livros da loja logada. */
public class TelaMeusLivros extends JFrame {

    private final Usuario loja;
    private final LivroDAO dao = new LivroDAO();
    private List<Categoria> categorias;
    private Tabela livros;

    private final JTextField txtTitulo = new JTextField(20);
    private final JTextField txtAutor = new JTextField(20);
    private final JTextField txtPreco = new JTextField(8);
    private final JSpinner spnEstoque = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
    private final JComboBox<String> cmbCondicao = new JComboBox<>(new String[]{"Novo", "Usado"});
    private final JComboBox<String> cmbCategoria = new JComboBox<>();
    private final JTable tabela = new JTable();

    public TelaMeusLivros(Usuario loja) {
        super("Meus livros - " + loja.getNome());
        this.loja = loja;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(860, 520);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Cadastrar novo livro"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        addCampo(form, g, 0, 0, "Título:", txtTitulo);
        addCampo(form, g, 0, 2, "Autor:", txtAutor);
        addCampo(form, g, 1, 0, "Preço (R$):", txtPreco);
        addCampo(form, g, 1, 2, "Estoque:", spnEstoque);
        addCampo(form, g, 2, 0, "Condição:", cmbCondicao);
        addCampo(form, g, 2, 2, "Categoria:", cmbCategoria);
        JButton btnCadastrar = new JButton("Cadastrar livro");
        g.gridx = 3; g.gridy = 3;
        form.add(btnCadastrar, g);
        btnCadastrar.addActionListener(e -> cadastrar());

        JButton btnAtualizar = new JButton("Alterar preço/estoque do selecionado");
        JButton btnRecarregar = new JButton("Atualizar lista");
        JButton btnFechar = new JButton("Fechar");
        btnAtualizar.addActionListener(e -> alterar());
        btnRecarregar.addActionListener(e -> carregarLista());
        btnFechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodape.add(btnAtualizar);
        rodape.add(btnRecarregar);
        rodape.add(btnFechar);

        setLayout(new BorderLayout(8, 8));
        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        carregarCategorias();
        carregarLista();
    }

    private void addCampo(JPanel p, GridBagConstraints g, int linha, int col, String rotulo, JComponent c) {
        g.gridy = linha; g.gridx = col;
        p.add(new JLabel(rotulo), g);
        g.gridx = col + 1;
        p.add(c, g);
    }

    private void carregarCategorias() {
        try {
            categorias = new ProdutoDAO().listarCategorias();
            for (Categoria c : categorias) cmbCategoria.addItem(c.getNome());
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }

    private void carregarLista() {
        try {
            livros = dao.listarDaLoja(loja.getCodigo());
            tabela.setModel(livros.modelo());
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }

    private void cadastrar() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        if (titulo.isEmpty() || autor.isEmpty() || txtPreco.getText().isBlank() || categorias == null
                || cmbCategoria.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Preencha título, autor, preço e categoria.");
            return;
        }
        BigDecimal preco;
        try {
            preco = new BigDecimal(txtPreco.getText().trim().replace(",", "."));
            if (preco.signum() < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe o preço apenas com números (ex.: 25,90).");
            return;
        }
        try {
            dao.inserir(loja.getCodigo(), titulo, autor, preco, (Integer) spnEstoque.getValue(),
                    (String) cmbCondicao.getSelectedItem(),
                    categorias.get(cmbCategoria.getSelectedIndex()).getCodigo());
            JOptionPane.showMessageDialog(this, "Livro cadastrado!");
            txtTitulo.setText("");
            txtAutor.setText("");
            txtPreco.setText("");
            spnEstoque.setValue(1);
            carregarLista();
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }

    private void alterar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na lista.");
            return;
        }
        JTextField preco = new JTextField(String.valueOf(livros.valor(linha, 5)));
        JSpinner estoque = new JSpinner(new SpinnerNumberModel(
                Integer.parseInt(String.valueOf(livros.valor(linha, 6))), 0, 9999, 1));
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.add(new JLabel("Novo preço (R$):"));
        p.add(preco);
        p.add(new JLabel("Novo estoque:"));
        p.add(estoque);
        if (JOptionPane.showConfirmDialog(this, p, String.valueOf(livros.valor(linha, 1)),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            BigDecimal novoPreco = new BigDecimal(preco.getText().trim().replace(",", "."));
            if (novoPreco.signum() < 0) throw new NumberFormatException();
            int cod = Integer.parseInt(String.valueOf(livros.valor(linha, 0)));
            dao.atualizar(loja.getCodigo(), cod, novoPreco, (Integer) estoque.getValue());
            carregarLista();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço inválido.");
        } catch (SQLException e) {
            Acesso.erroBanco(this, e);
        }
    }
}
