import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FormularioAppChallenge extends JFrame {

    private JFormattedTextField txtData;
    private JComboBox<String> cbOperador, cbFornecedor, cbProduto, cbTipoMov;
    private JTextField txtQuantidade;
    private final String ARQUIVO_NOME = "dados_movimentacao.txt";
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FormularioAppChallenge() {
        setTitle("Sistema de Controle de Movimentação - Secure v1.0");
        setSize(450, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new GridLayout(8, 2, 10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campo DATA com Máscara
        painelPrincipal.add(new JLabel("Data (dd/mm/aaaa):"));
        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            txtData = new JFormattedTextField();
        }
        painelPrincipal.add(txtData);

        // Combos
        painelPrincipal.add(new JLabel("Operador:"));
        cbOperador = new JComboBox<>(new String[]{"Selecione...", "José", "Maria", "Joana", "Lucas"});
        painelPrincipal.add(cbOperador);

        painelPrincipal.add(new JLabel("Fornecedor:"));
        cbFornecedor = new JComboBox<>(new String[]{"Selecione...", "ABC", "WYZ", "XPTO"});
        painelPrincipal.add(cbFornecedor);

        painelPrincipal.add(new JLabel("Produto:"));
        cbProduto = new JComboBox<>(new String[]{"Selecione...", "Prod A", "Prod B", "Prod C", "Prod D", "Prod E"});
        painelPrincipal.add(cbProduto);

        painelPrincipal.add(new JLabel("Quantidade:"));
        txtQuantidade = new JTextField();
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

        btnSalvar.addActionListener(e -> validarESalvar());
        btnListar.addActionListener(e -> exibirRegistros());
        btnSair.addActionListener(e -> System.exit(0));
    }

    /**
     * Centraliza todas as validações antes de permitir a escrita no arquivo.
     */
    private void validarESalvar() {
        StringBuilder erros = new StringBuilder();

        // 1. Validação de Data Lógica (Existe no calendário?)
        String dataStr = txtData.getText().trim();
        try {
            LocalDate dataValida = LocalDate.parse(dataStr, formatadorData);
            if (dataValida.isAfter(LocalDate.now())) {
                erros.append("- A data não pode ser futura.\n");
            }
        } catch (DateTimeParseException e) {
            erros.append("- Data inválida ou inexistente (ex: 31/02).\n");
        }

        // 2. Validação de Seleção de ComboBoxes
        if (cbOperador.getSelectedIndex() == 0) erros.append("- Selecione um Operador.\n");
        if (cbFornecedor.getSelectedIndex() == 0) erros.append("- Selecione um Fornecedor.\n");
        if (cbProduto.getSelectedIndex() == 0) erros.append("- Selecione um Produto.\n");

        // 3. Validação de Quantidade (Número Positivo)
        String qtdStr = txtQuantidade.getText().trim();
        try {
            double qtd = Double.parseDouble(qtdStr.replace(",", "."));
            if (qtd <= 0) {
                erros.append("- A quantidade deve ser maior que zero.\n");
            }
        } catch (NumberFormatException e) {
            erros.append("- Quantidade deve ser um número válido.\n");
        }

        // Verificação final
        if (erros.length() > 0) {
            JOptionPane.showMessageDialog(this, "Erros encontrados:\n" + erros, "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        salvarDados();
    }

    private void salvarDados() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_NOME, true))) {
            // Sanitização simples: remove caracteres que podem quebrar o log (como o separador '|')
            String quantSani = txtQuantidade.getText().replace("|", "");

            String registro = String.format("%s | %s | %s | %s | %s | %s",
                    txtData.getText(),
                    cbOperador.getSelectedItem(),
                    cbFornecedor.getSelectedItem(),
                    cbProduto.getSelectedItem(),
                    quantSani,
                    cbTipoMov.getSelectedItem());

            writer.write(registro);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Movimentação registrada com sucesso!");
            limparCampos();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro crítico de E/S: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtData.setValue(null);
        cbOperador.setSelectedIndex(0);
        cbFornecedor.setSelectedIndex(0);
        cbProduto.setSelectedIndex(0);
        txtQuantidade.setText("");
    }

    private void exibirRegistros() {
        try {
            File arquivo = new File(ARQUIVO_NOME);
            if (!arquivo.exists() || arquivo.length() == 0) {
                JOptionPane.showMessageDialog(this, "Nenhum registro encontrado.");
                return;
            }
            String conteudo = new String(Files.readAllBytes(Paths.get(ARQUIVO_NOME)));
            JTextArea textArea = new JTextArea(conteudo);
            textArea.setEditable(false);
            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(this, scroll, "Histórico de Movimentações", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao ler arquivo.");
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new FormularioAppChallenge().setVisible(true));
    }
}