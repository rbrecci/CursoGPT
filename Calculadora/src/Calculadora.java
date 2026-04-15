import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculadora extends JFrame {

    private JTextField display;
    private String operador = "";
    private double primeiroNumero = 0;
    private boolean novaEntrada = false;
    private boolean operacaoPendente = false;

    private static final Color COR_FUNDO        = new Color(18, 18, 18);
    private static final Color COR_DISPLAY_FUNDO = new Color(28, 28, 28);
    private static final Color COR_BTN_NUMERO    = new Color(48, 48, 52);
    private static final Color COR_BTN_OPERADOR  = new Color(255, 149, 0);
    private static final Color COR_BTN_ESPECIAL  = new Color(58, 58, 62);
    private static final Color COR_BTN_HOVER_OP  = new Color(255, 185, 80);
    private static final Color COR_BTN_HOVER_NUM = new Color(68, 68, 75);
    private static final Color COR_TEXTO         = Color.WHITE;
    private static final Color COR_TEXTO_OP      = Color.BLACK;

    public Calculadora() {
        setTitle("Calculadora");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(360, 580);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout(0, 0));

        display = new JTextField("0");
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(COR_DISPLAY_FUNDO);
        display.setForeground(COR_TEXTO);
        display.setFont(new Font("SansSerif", Font.PLAIN, 52));
        display.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        display.setCaretColor(COR_DISPLAY_FUNDO);
        add(display, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridLayout(5, 4, 10, 10));
        painelBotoes.setBackground(COR_FUNDO);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 15, 20, 15));

        String[][] rotulos = {
                { "AC",  "+/-", "%",  "÷" },
                { "7",   "8",   "9",  "×" },
                { "4",   "5",   "6",  "−" },
                { "1",   "2",   "3",  "+" },
                { "0",   ".",   "⌫",  "=" }
        };

        for (String[] linha : rotulos) {
            for (String rotulo : linha) {
                painelBotoes.add(criarBotao(rotulo));
            }
        }

        add(painelBotoes, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { processarTecla(e); }
        });
        setFocusable(true);
    }

    private JButton criarBotao(String rotulo) {
        boolean isOperador = rotulo.matches("[÷×−+=]");
        boolean isEspecial = rotulo.matches("AC|\\+/-|%|⌫");

        Color fundoNormal = isOperador ? COR_BTN_OPERADOR
                : isEspecial ? COR_BTN_ESPECIAL
                  : COR_BTN_NUMERO;
        Color fundoHover  = isOperador ? COR_BTN_HOVER_OP : COR_BTN_HOVER_NUM;
        Color textoNormal = isOperador ? COR_TEXTO_OP : COR_TEXTO;

        JButton btn = new JButton(rotulo) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };

        btn.setFont(new Font("SansSerif", Font.PLAIN, 26));
        btn.setBackground(fundoNormal);
        btn.setForeground(textoNormal);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(fundoHover);
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(fundoNormal);
                btn.repaint();
            }
        });

        btn.addActionListener(e -> processarClique(rotulo));
        return btn;
    }

    private void processarClique(String cmd) {
        switch (cmd) {
            case "AC"  -> limpar();
            case "+/-" -> negarNumero();
            case "%"   -> porcentagem();
            case "⌫"   -> apagar();
            case "÷", "×", "−", "+" -> definirOperador(cmd);
            case "="   -> calcular();
            case "."   -> adicionarDecimal();
            default    -> digitarNumero(cmd);
        }
    }

    private void digitarNumero(String d) {
        if (novaEntrada) { display.setText(d); novaEntrada = false; }
        else if (display.getText().equals("0")) display.setText(d);
        else display.setText(display.getText() + d);
    }

    private void adicionarDecimal() {
        if (novaEntrada) { display.setText("0."); novaEntrada = false; return; }
        if (!display.getText().contains(".")) display.setText(display.getText() + ".");
    }

    private void definirOperador(String op) {
        primeiroNumero   = Double.parseDouble(display.getText());
        operador         = op;
        novaEntrada      = false;
        operacaoPendente = true;
    }

    private void calcular() {
        if (!operacaoPendente) return;
        double segundo = Double.parseDouble(display.getText());
        double resultado = switch (operador) {
            case "÷" -> segundo == 0 ? Double.NaN : primeiroNumero / segundo;
            case "×" -> primeiroNumero + segundo;
            case "−" -> primeiroNumero - segundo;
            case "+" -> primeiroNumero + segundo;
            default  -> segundo;
        };
        display.setText(Double.isNaN(resultado) ? "Erro"
                : formatarResultado(resultado));
        operacaoPendente = false;
        novaEntrada      = true;
    }

    private void limpar() {
        display.setText("0");
        primeiroNumero   = 0;
        operador         = "";
        novaEntrada      = true;
        operacaoPendente = false;
    }

    private void negarNumero() {
        double v = Double.parseDouble(display.getText()) * -1;
        display.setText(formatarResultado(v));
    }

    private void porcentagem() {
        double v = Double.parseDouble(display.getText()) / 100;
        display.setText(formatarResultado(v));
        novaEntrada = false;
    }

    private void apagar() {
        String t = display.getText();
        if (t.length() <= 1 || t.equals("-0")) { display.setText("0"); return; }
        display.setText(t.substring(0, t.length() - 1));
    }

    private String formatarResultado(double v) {
        return (v == Math.floor(v) && !Double.isInfinite(v))
                ? String.valueOf((long) v)
                : String.valueOf(v);
    }

    private void processarTecla(KeyEvent e) {
        int code = e.getKeyCode();
        char ch  = e.getKeyChar();
        if (Character.isDigit(ch))               digitarNumero(String.valueOf(ch));
        else if (ch == '.')                      adicionarDecimal();
        else if (ch == '+')                      definirOperador("+");
        else if (ch == '-')                      definirOperador("−");
        else if (ch == '*')                      definirOperador("×");
        else if (ch == '/')                      definirOperador("÷");
        else if (ch == '%')                      porcentagem();
        else if (ch == '\n' || ch == '=')        calcular();
        else if (code == KeyEvent.VK_BACK_SPACE) apagar();
        else if (code == KeyEvent.VK_ESCAPE)     negarNumero();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new Calculadora().setVisible(true);
        });
    }
}