import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatbotImobiliario extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    public ChatbotImobiliario() {
        // Configurações da Janela (Interface)
        setTitle("CrediFlow AI - Analista de Crédito Ético");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área de chat (Histórico)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setBackground(new Color(245, 245, 245));
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Painel Inferior (Entrada de texto)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Analisar");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Mensagem de Boas-vindas
        chatArea.append("CrediFlow AI: Olá! Sou seu analista de crédito. \nPor favor, informe a Renda, Valor do Imóvel, Entrada e Score.\n\n");

        // Evento do Botão
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processarMensagem();
            }
        });

        // Evento da tecla Enter
        inputField.addActionListener(e -> processarMensagem());
    }

    private void processarMensagem() {
        String userText = inputField.getText().trim();
        if (!userText.isEmpty()) {
            chatArea.append("Você: " + userText + "\n");

            // Simulação de resposta da IA baseada no Prompt de Sistema
            // Em um cenário real, aqui seria feita a chamada HTTP para a API da OpenAI
            String respostaIA = gerarRespostaSimulada(userText);

            chatArea.append("CrediFlow AI: " + respostaIA + "\n\n");
            inputField.setText("");
        }
    }

    private String gerarRespostaSimulada(String input) {
        // Lógica simplificada apenas para demonstração da Persona
        return "Recebi seus dados. Analisando sob as diretrizes do Sistema de Crédito Ético...\n" +
                "[Simulação]: De acordo com o System Prompt, estou validando apenas LTV e Renda.\n" +
                "Aguarde o parecer técnico final.";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatbotImobiliario chat = new ChatbotImobiliario();
            chat.setVisible(true);
        });
    }
}