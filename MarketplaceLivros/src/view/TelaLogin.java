
package view;
import dao.LoginDAO;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import model.Usuario;
import util.Sessao;
/**
 *
 * @author Lucas
 */
public class TelaLogin extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaLogin.class.getName());

    /**
     * novo form para TelaLogin
     */
    public TelaLogin() {
        initComponents();
    }


    @SuppressWarnings("unchecked")

    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtSenha = new javax.swing.JPasswordField();
        cmbTipo = new javax.swing.JComboBox<>();
        btnEntrar = new javax.swing.JButton();
        btnCadastrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Login");

        jLabel2.setText("Senha");

        txtEmail.setText("Digite Email");

        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cliente", "Loja" }));
        cmbTipo.addActionListener(this::cmbTipoActionPerformed);

        btnEntrar.setText("Entrar");
        btnEntrar.addActionListener(this::btnEntrarActionPerformed);

        btnCadastrar.setText("Cadastrar");
        btnCadastrar.addActionListener(this::btnCadastrarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtEmail)
                        .addComponent(txtSenha, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnEntrar)
                        .addGap(18, 18, 18)
                        .addComponent(btnCadastrar)
                        .addGap(18, 18, 18)
                        .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap(183, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(13, 13, 13)
                .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEntrar)
                    .addComponent(btnCadastrar)
                    .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        pack();
    }

    private void cmbTipoActionPerformed(java.awt.event.ActionEvent evt) {
       
    }

    private void btnEntrarActionPerformed(java.awt.event.ActionEvent evt) {
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword());
            String tipo = (String) cmbTipo.getSelectedItem();
            new TelaPrincipal().setVisible(true);
            this.dispose();

if (email.isEmpty() || senha.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Preencha e-mail e senha.");
    return;
}

try {
    Usuario u = new LoginDAO().autenticar(email, senha, tipo);
    if (u == null) {
        JOptionPane.showMessageDialog(this, "E-mail ou senha incorretos.",
                "Login", JOptionPane.ERROR_MESSAGE);
        return;
    }
    Sessao.usuario = u;
    JOptionPane.showMessageDialog(this, "Bem-vindo(a), " + u.getNome() + "!");
    
} 
catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Erro ao acessar o banco: " + e.getMessage(),
            "Erro", JOptionPane.ERROR_MESSAGE);
}  
    }

    private void btnCadastrarActionPerformed(java.awt.event.ActionEvent evt) {
        new TelaCadastro().setVisible(true);
        this.dispose();       
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
        
        java.awt.EventQueue.invokeLater(() -> new TelaLogin().setVisible(true));
    }


    private javax.swing.JButton btnCadastrar;
    private javax.swing.JButton btnEntrar;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtSenha;

}
