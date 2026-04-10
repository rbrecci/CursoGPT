import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class Atv1 extends JFrame {
    private JTextField txtNome;
    private JTextField txtEmail;
    private JFormattedTextField txtTelefone;
    private JFormattedTextField txtCpf;
    private JButton btnSalvar;
    private JButton btnLimpar;

    public Atv1() {
        this.setTitle("Cadastro de Cliente");
        this.setSize(450, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Campos da Interface ---
        gbc.gridx = 0; gbc.gridy = 0;
        this.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        this.txtNome = new JTextField(20);
        this.add(this.txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        this.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1;
        this.txtEmail = new JTextField(20);
        this.add(this.txtEmail, gbc);

        try {
            gbc.gridx = 0; gbc.gridy = 2;
            this.add(new JLabel("Telefone:"), gbc);
            MaskFormatter telefoneMask = new MaskFormatter("(##) #####-####");
            telefoneMask.setPlaceholderCharacter('_');
            this.txtTelefone = new JFormattedTextField(telefoneMask);
            gbc.gridx = 1;
            this.add(this.txtTelefone, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            this.add(new JLabel("CPF:"), gbc);
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            this.txtCpf = new JFormattedTextField(cpfMask);
            gbc.gridx = 1;
            this.add(this.txtCpf, gbc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- Botões ---
        JPanel painelBotoes = new JPanel();
        this.btnSalvar = new JButton("Salvar");
        this.btnLimpar = new JButton("Limpar");
        painelBotoes.add(this.btnSalvar);
        painelBotoes.add(this.btnLimpar);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        this.add(painelBotoes, gbc);

        // --- Eventos ---
        this.btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText();
            String email = txtEmail.getText();
            String telefone = txtTelefone.getText();
            String cpf = txtCpf.getText();
            String cpfNumerico = cpf.replaceAll("[^0-9]", "");

            if (!validarCPF(cpfNumerico)) {
                JOptionPane.showMessageDialog(null, "CPF inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            } else if (salvarNoBanco(nome, email, telefone, cpf)) {
                JOptionPane.showMessageDialog(null, "Cliente salvo com sucesso!");
                limparCampos();
            }
        });

        this.btnLimpar.addActionListener(e -> limparCampos());
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEmail.setText("");
        txtTelefone.setValue(null);
        txtCpf.setValue(null);
    }

    private boolean validarCPF(String cpf) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;
        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - 48) * (10 - i);
            int dig1 = 11 - (soma % 11);
            if (dig1 >= 10) dig1 = 0;

            soma = 0;
            for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - 48) * (11 - i);
            int dig2 = 11 - (soma % 11);
            if (dig2 >= 10) dig2 = 0;

            return dig1 == (cpf.charAt(9) - 48) && dig2 == (cpf.charAt(10) - 48);
        } catch (Exception e) { return false; }
    }

    private boolean salvarNoBanco(String nome, String email, String telefone, String cpf) {
        // AJUSTE AQUI: Se usar XAMPP, a senha costuma ser "" (vazio)
        String url = "jdbc:mysql://localhost:3306/sistema_clientes?useSSL=false&serverTimezone=UTC";
        String usuario = "root";
        String senha = "Senai@118"; // <--- MUDE PARA A SUA SENHA REAL OU DEIXE VAZIO SE FOR XAMPP

        String sql = "INSERT INTO clientes (nome, email, telefone, cpf) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, telefone);
            stmt.setString(4, cpf);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Atv1().setVisible(true));
    }
}