import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;

class Cliente {
    String nome;
    String nascimento;
    String cpf;
    String email;
    String celular;

    public Cliente(String nome, String nascimento, String cpf, String email, String celular) {
        this.nome = nome;
        this.nascimento = nascimento;
        this.cpf = cpf;
        this.email = email;
        this.celular = celular;
    }
}

public class CadastroClientes extends JFrame {

    private JTextField txtNome, txtEmail;
    private JFormattedTextField txtNascimento, txtCpf, txtCelular;
    private JButton btnSalvar, btnListar;
    private JTable tabela;
    private DefaultTableModel model;

    private ArrayList<Cliente> listaClientes = new ArrayList<>();

    public CadastroClientes() {
        setTitle("Cadastro de Clientes");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtNome = new JTextField();
        txtEmail = new JTextField();

        try {
            txtNascimento = new JFormattedTextField(new MaskFormatter("##/##/####"));
            txtCpf = new JFormattedTextField(new MaskFormatter("###.###.###-##"));
            txtCelular = new JFormattedTextField(new MaskFormatter("(##) #####-####"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        panelForm.add(new JLabel("Nome:"));
        panelForm.add(txtNome);

        panelForm.add(new JLabel("Data de Nascimento:"));
        panelForm.add(txtNascimento);

        panelForm.add(new JLabel("CPF:"));
        panelForm.add(txtCpf);

        panelForm.add(new JLabel("Email:"));
        panelForm.add(txtEmail);

        panelForm.add(new JLabel("Celular:"));
        panelForm.add(txtCelular);

        btnSalvar = new JButton("Salvar");
        btnListar = new JButton("Listar");

        panelForm.add(btnSalvar);
        panelForm.add(btnListar);

        add(panelForm, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Nome", "Nascimento", "CPF", "Email", "Celular"}, 0);
        tabela = new JTable(model);

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Evento salvar
        btnSalvar.addActionListener(e -> salvarCliente());

        // Evento listar
        btnListar.addActionListener(e -> listarClientes());
    }

    private void salvarCliente() {
        String nome = txtNome.getText().trim();
        String nascimento = txtNascimento.getText();
        String cpf = txtCpf.getText();
        String email = txtEmail.getText().trim();
        String celular = txtCelular.getText();

        if (nome.isEmpty() || nascimento.contains(" ") || cpf.contains(" ") || email.isEmpty() || celular.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente!");
            return;
        }

        if (!validarCPF(cpf)) {
            JOptionPane.showMessageDialog(this, "CPF inválido!");
            return;
        }

        Cliente cliente = new Cliente(nome, nascimento, cpf, email, celular);
        listaClientes.add(cliente);

        JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!");
        limparCampos();
    }

    private void listarClientes() {
        model.setRowCount(0);

        for (Cliente c : listaClientes) {
            model.addRow(new Object[]{c.nome, c.nascimento, c.cpf, c.email, c.celular});
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtNascimento.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        txtCelular.setText("");
    }

    // Validação de CPF
    public static boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0, peso = 10;
            for (int i = 0; i < 9; i++)
                soma += (cpf.charAt(i) - '0') * peso--;

            int dig1 = 11 - (soma % 11);
            dig1 = (dig1 >= 10) ? 0 : dig1;

            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++)
                soma += (cpf.charAt(i) - '0') * peso--;

            int dig2 = 11 - (soma % 11);
            dig2 = (dig2 >= 10) ? 0 : dig2;

            return dig1 == (cpf.charAt(9) - '0') && dig2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Falha ao aplicar FlatLaf");
        }

        SwingUtilities.invokeLater(() -> new CadastroClientes().setVisible(true));
    }
}
