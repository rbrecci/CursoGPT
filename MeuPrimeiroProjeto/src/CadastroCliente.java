import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Classe principal que representa o formulário de cadastro de clientes.
 * Tudo foi colocado em um único arquivo para facilitar o ensino inicial.
 */
public class CadastroCliente extends JFrame {

    // Declaração dos componentes do formulário
    private JTextField txtNome;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JTextField txtCpf;

    private JButton btnSalvar;
    private JButton btnLimpar;

    /**
     * Construtor da classe: responsável por montar a interface gráfica
     */
    public CadastroCliente() {
        setTitle("Cadastro de Cliente");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela na tela

        // Define o layout principal como GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Define espaçamento padrão entre os componentes
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ====== COMPONENTES ======

        // Linha 0 - Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        txtNome = new JTextField(20);
        add(txtNome, gbc);

        // Linha 1 - Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("E-mail:"), gbc);

        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        add(txtEmail, gbc);

        // Linha 2 - Telefone
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Telefone:"), gbc);

        gbc.gridx = 1;
        txtTelefone = new JTextField(20);
        add(txtTelefone, gbc);

        // Linha 3 - CPF
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("CPF:"), gbc);

        gbc.gridx = 1;
        txtCpf = new JTextField(20);
        add(txtCpf, gbc);

        // ====== BOTÕES ======

        // Painel para agrupar os botões
        JPanel painelBotoes = new JPanel();

        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);

        // Linha 4 - adiciona o painel de botões
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // ocupa duas colunas
        add(painelBotoes, gbc);

        // ====== EVENTOS ======

        // Evento do botão Salvar
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Captura os valores digitados
                String nome = txtNome.getText();
                String email = txtEmail.getText();
                String telefone = txtTelefone.getText();
                String cpf = txtCpf.getText();

                // Exibe os dados em uma caixa de diálogo
                JOptionPane.showMessageDialog(null,
                        "Dados cadastrados:\n\n" +
                                "Nome: " + nome + "\n" +
                                "E-mail: " + email + "\n" +
                                "Telefone: " + telefone + "\n" +
                                "CPF: " + cpf
                );
            }
        });

        // Evento do botão Limpar
        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Limpa todos os campos do formulário
                txtNome.setText("");
                txtEmail.setText("");
                txtTelefone.setText("");
                txtCpf.setText("");
            }
        });
    }

    /**
     * Método principal (ponto de entrada da aplicação)
     */
    public static void main(String[] args) {

        // Boa prática: garantir que a interface rode na thread gráfica (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CadastroCliente().setVisible(true);
            }
        });
    }
}