
package view;
import dao.ClienteDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.swing.JOptionPane;
/**
 *
 * @author Lucas
 */
public class TelaCadastro extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaCadastro.class.getName());

    /**
     * Cria novo form TelaCadastro
     */
    public TelaCadastro() {
        initComponents();
    }


    @SuppressWarnings("unchecked")

    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        txtCpf = new javax.swing.JTextField();
        txtNascimento = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtTelefone = new javax.swing.JTextField();
        txtCep = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtEndereco = new javax.swing.JTextField();
        txtNumero = new javax.swing.JTextField();
        txtComplemento = new javax.swing.JTextField();
        txtBairro = new javax.swing.JTextField();
        txtCidade = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnSalvar = new javax.swing.JButton();
        btnVoltar = new javax.swing.JButton();
        txtSenha = new javax.swing.JPasswordField();
        txtConfirma = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Dados pessoais");

        txtNome.setText("Nome Completo");
        txtNome.addActionListener(this::txtNomeActionPerformed);

        txtCpf.setText("CPF");

        txtNascimento.setText("Data de nascimento");

        jLabel2.setText("Dados de contato");

        txtEmail.setText("E-mail");

        txtTelefone.setText("Telefone");

        txtCep.setText("CEP");

        jLabel3.setText("Endereço de entrega");

        txtEndereco.setText("Rua / Avenida");

        txtNumero.setText("Numero");

        txtComplemento.setText("Complemento");

        txtBairro.setText("Bairro");

        txtCidade.setText("Cidade / Estado");

        jLabel4.setText("Acesse à conta");

        btnSalvar.setText("Criar Conta");
        btnSalvar.addActionListener(this::btnSalvarActionPerformed);

        btnVoltar.setText("Cancelar");
        btnVoltar.addActionListener(this::btnVoltarActionPerformed);

        txtSenha.addActionListener(this::txtSenhaActionPerformed);

        jLabel5.setText("Senha");

        jLabel6.setText("Confirma");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(txtConfirma, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50))))
                    .addComponent(jLabel4)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(txtComplemento))
                        .addComponent(jLabel3)
                        .addComponent(jLabel2)
                        .addComponent(jLabel1)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(txtNascimento))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(txtTelefone))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtCep, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(txtEndereco))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(txtCidade))))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNascimento, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(txtCpf))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTelefone, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(txtEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtEndereco, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(txtCep))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtComplemento, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(txtNumero))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCidade, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(txtBairro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtConfirma))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {
        
    }

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {
            String nome = txtNome.getText().trim();
String cpf = txtCpf.getText().replaceAll("\\D", "");
String email = txtEmail.getText().trim();
String telefone = txtTelefone.getText().replaceAll("\\D", "");
String cep = txtCep.getText().replaceAll("\\D", "");
String endereco = txtEndereco.getText().trim();
String numero = txtNumero.getText().trim();
String bairro = txtBairro.getText().trim();
String cidade = txtCidade.getText().trim();
String senha = new String(txtSenha.getPassword());
String confirma = new String(txtConfirma.getPassword());

// Validações
if (nome.isEmpty() || email.isEmpty() || endereco.isEmpty() || numero.isEmpty()
        || bairro.isEmpty() || cidade.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.");
    return;
}
if (cpf.length() != 11) {
    JOptionPane.showMessageDialog(this, "O CPF deve ter 11 dígitos.");
    return;
}
if (telefone.length() < 10 || telefone.length() > 11) {
    JOptionPane.showMessageDialog(this, "Telefone inválido (use DDD + número).");
    return;
}
if (cep.length() != 8) {
    JOptionPane.showMessageDialog(this, "O CEP deve ter 8 dígitos.");
    return;
}
if (!email.contains("@")) {
    JOptionPane.showMessageDialog(this, "E-mail inválido.");
    return;
}
if (senha.length() < 6) {
    JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 6 caracteres.");
    return;
}
if (!senha.equals(confirma)) {
    JOptionPane.showMessageDialog(this, "As senhas não conferem.");
    return;
}

java.sql.Date nascimento;
try {
    LocalDate data = LocalDate.parse(txtNascimento.getText().trim(),
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT));
    nascimento = java.sql.Date.valueOf(data);
} catch (DateTimeParseException e) {
    JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/mm/aaaa.");
    return;
}

try {
    ClienteDAO dao = new ClienteDAO();
    if (dao.existe(email, cpf)) {
        JOptionPane.showMessageDialog(this, "Já existe um cadastro com esse e-mail ou CPF.");
        return;
    }
    dao.inserir(nome, cpf, nascimento, email, telefone, cep, endereco, numero,
            txtComplemento.getText(), bairro, cidade, senha);
    JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!");
    new TelaLogin().setVisible(true);
    this.dispose();
} catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(),
            "Erro", JOptionPane.ERROR_MESSAGE);
}    
    }

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {
        new TelaLogin().setVisible(true);
        this.dispose();        
    }

    private void txtSenhaActionPerformed(java.awt.event.ActionEvent evt) {
        
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


        java.awt.EventQueue.invokeLater(() -> new TelaCadastro().setVisible(true));
    }

    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JTextField txtCep;
    private javax.swing.JTextField txtCidade;
    private javax.swing.JTextField txtComplemento;
    private javax.swing.JPasswordField txtConfirma;
    private javax.swing.JTextField txtCpf;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEndereco;
    private javax.swing.JTextField txtNascimento;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JPasswordField txtSenha;
    private javax.swing.JTextField txtTelefone;
}
