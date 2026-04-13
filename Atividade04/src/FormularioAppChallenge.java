import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

public class FormularioAppChallenge extends JFrame {

    private JFormattedTextField txtData;
    private JComboBox<String> cbOperador, cbFornecedor, cbProduto, cbTipoMov;
    private JTextField txtQuantidade;
    private final String ARQUIVO_NOME = "dados_movimentacao.txt";

    public FormularioAppChallenge() {
        setTitle("Sistema de Controle de Movimentação - Versão Beta");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new GridLayout(8, 2, 10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campo DATA
        painelPrincipal.add(new JLabel("Data (dd/mm/aaaa):"));
        try {
            // '#' define que apenas números são aceitos
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            txtData = new JFormattedTextField();
        }
        painelPrincipal.add(txtData); // Adicionado (estava faltando!)

        painelPrincipal.add(new JLabel("Operador:"));
        cbOperador = new JComboBox<>(new String[]{"José", "Maria", "Joana", "Lucas"});
        painelPrincipal.add(cbOperador);

        painelPrincipal.add(new JLabel("Fornecedor:"));
        cbFornecedor = new JComboBox<>(new String[]{"ABC", "WYZ", "XPTO"});
        painelPrincipal.add(cbFornecedor);

        painelPrincipal.add(new JLabel("Produto:"));
        cbProduto = new JComboBox<>(new String[]{"Prod A", "Prod B", "Prod C", "Prod D", "Prod E"});
        painelPrincipal.add(cbProduto);

        painelPrincipal.add(new JLabel("Quantidade:"));
        txtQuantidade = new JTextField(); // Inicializado (estava nulo!)
        painelPrincipal.add(txtQuantidade);

        painelPrincipal.add(new JLabel("Tipo de Movimentação:"));
        cbTipoMov = new JComboBox<>(new String[]{"Entrada", "Saída"});
        painelPrincipal.add(cbTipoMov);

        JButton btnSalvar = new JButton("Registrar");
        JButton btnListar = new JButton("Ver Registros");
        JButton btnSair = new JButton("Sair");

        painelPrincipal.add(btnSalvar);
        painelPrincipal.add(btnListar);
        painelPrincipal.add(new JLabel(""));
        painelPrincipal.add(btnSair);

        add(painelPrincipal);

        // Listeners
        btnSalvar.addActionListener(e -> salvarDados());
        btnListar.addActionListener(e -> exibirRegistros());
        btnSair.addActionListener(e -> System.exit(0));
    }

    private void salvarDados() {
        // Validação simples de quantidade
        if (txtQuantidade.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha a quantidade!");
            return;
        }

        // 'true' no FileWriter permite adicionar linhas sem apagar as anteriores
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_NOME, true))) {
            String registro = String.format("Data: %s | Op: %s | Forn: %s | Prod: %s | Qtd: %s | Tipo: %s",
                    txtData.getText(),
                    cbOperador.getSelectedItem(),
                    cbFornecedor.getSelectedItem(),
                    cbProduto.getSelectedItem(),
                    txtQuantidade.getText(),
                    cbTipoMov.getSelectedItem());

            writer.write(registro);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Movimentação registrada com sucesso!");

            // Limpa o campo de quantidade após salvar
            txtQuantidade.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }

    private void exibirRegistros() {
        try {
            File arquivo = new File(ARQUIVO_NOME);
            if (!arquivo.exists()) {
                JOptionPane.showMessageDialog(this, "Nenhum registro encontrado.");
                return;
            }
            String conteudo = new String(Files.readAllBytes(Paths.get(ARQUIVO_NOME)));

            // JTextArea para suportar múltiplas linhas no scroll se necessário
            JTextArea textArea = new JTextArea(conteudo);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Registros Salvos", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao ler arquivo.");
        }
    }

    public static void main(String[] args) {
        // Look and Feel do sistema para ficar mais moderno
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            FormularioAppChallenge app = new FormularioAppChallenge();
            app.setVisible(true); // Essencial para a janela aparecer!
        });
    }
}