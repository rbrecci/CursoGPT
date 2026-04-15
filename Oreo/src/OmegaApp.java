import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OMEGA BANKING v3.0
 *
 * Melhorias em relação à v2.0:
 *  - Login feito pelo nome do titular (sem necessidade de memorizar código)
 *  - Tela de login integrada ao painel principal (sem janela separada)
 *  - Botões desativados com visual claramente apagado (opacidade + cor rasa)
 *  - Botões ativos com ícone + texto e hover bem visível
 *  - Botão "Encerrar" destacado em vermelho
 *  - Saldo exibido em formato moeda (R$ 1.500,00)
 *  - Barra de status com indicador CONECTADO / DESCONECTADO
 *  - Separação visual entre painel de info e grade de ações
 *  - Layout mínimo aumentado para melhor legibilidade
 */
public class OmegaApp extends JFrame {

    private final Map<String, Conta> contas = new ConcurrentHashMap<>();
    private Conta contaAtiva = null;

    // ── Paleta ───────────────────────────────────────────────────────
    private static final Color C_BG        = new Color(13,  17,  23);
    private static final Color C_PAINEL    = new Color(10,  14,  26);
    private static final Color C_BORDA     = new Color(30,  38,  54);
    private static final Color C_ACENTO    = new Color(0,   220, 120);
    private static final Color C_TEXTO     = new Color(176, 190, 197);
    private static final Color C_MUTED     = new Color(74,  85,  104);
    private static final Color C_BTN_BG    = new Color(15,  30,  22);
    private static final Color C_BTN_HOV   = new Color(0,   220, 120);
    private static final Color C_OFF_BG    = new Color(13,  17,  23);
    private static final Color C_OFF_TEXT  = new Color(42,  51,  69);
    private static final Color C_DANGER    = new Color(197, 48,  48);
    private static final Color C_DANG_BG   = new Color(26,  15,  15);
    private static final Color C_DANG_HOV  = new Color(197, 48,  48);
    private static final Color C_INPUT_BG  = new Color(10,  14,  26);

    private static final Font F_MONO_BOLD  = new Font("Monospaced", Font.BOLD,  13);
    private static final Font F_MONO       = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font F_SALDO      = new Font("Monospaced", Font.BOLD,  28);
    private static final Font F_TITULO     = new Font("Monospaced", Font.BOLD,  18);
    private static final Font F_SMALL      = new Font("Monospaced", Font.PLAIN, 11);

    // ── Referências de componentes ───────────────────────────────────
    private JLabel lblNome, lblSubtitulo, lblSaldo, lblStatusBadge;
    private JPanel cardLogin, cardAcoes;
    private JTextField campoBuscaNome;
    private JList<String> listaNomes;
    private DefaultListModel<String> modeloLista;

    public OmegaApp() {
        setTitle("OMEGA BANKING");
        setSize(860, 580);
        setMinimumSize(new Dimension(740, 500));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_BG);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) { confirmarEncerramento(); }
        });

        // Contas de demonstração
        contas.put("joao silva",    new Conta("CC-1001", "João Silva",     new BigDecimal("1500.00"), hash("123")));
        contas.put("maria oliveira",new Conta("CC-1002", "Maria Oliveira", new BigDecimal("2500.00"), hash("456")));

        setLayout(new BorderLayout(0, 0));
        add(criarTopo(),    BorderLayout.NORTH);
        add(criarCorpo(),   BorderLayout.CENTER);
        add(criarStatus(),  BorderLayout.SOUTH);

        atualizarInterface();
    }

    // ═══════════════════════════════════════════════════════
    //  PAINEL SUPERIOR
    // ═══════════════════════════════════════════════════════

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(C_PAINEL);
        topo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDA),
                new EmptyBorder(18, 24, 16, 24)
        ));

        // Lado esquerdo: nome + subtítulo
        JPanel esquerda = new JPanel(new GridLayout(2, 1, 0, 4));
        esquerda.setOpaque(false);

        lblNome = new JLabel("OMEGA BANKING");
        lblNome.setForeground(C_ACENTO);
        lblNome.setFont(F_TITULO);

        lblSubtitulo = new JLabel("Faça login para continuar");
        lblSubtitulo.setForeground(C_MUTED);
        lblSubtitulo.setFont(F_MONO);

        esquerda.add(lblNome);
        esquerda.add(lblSubtitulo);

        // Lado direito: caixa de saldo
        JPanel caixaSaldo = new JPanel(new BorderLayout(0, 3));
        caixaSaldo.setBackground(new Color(8, 12, 20));
        caixaSaldo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDA, 1),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JLabel lblLabelSaldo = new JLabel("Saldo disponível");
        lblLabelSaldo.setForeground(C_MUTED);
        lblLabelSaldo.setFont(F_SMALL);
        lblLabelSaldo.setHorizontalAlignment(SwingConstants.RIGHT);

        lblSaldo = new JLabel("—");
        lblSaldo.setForeground(new Color(42, 51, 69));
        lblSaldo.setFont(F_SALDO);
        lblSaldo.setHorizontalAlignment(SwingConstants.RIGHT);

        caixaSaldo.add(lblLabelSaldo, BorderLayout.NORTH);
        caixaSaldo.add(lblSaldo,      BorderLayout.CENTER);

        topo.add(esquerda,   BorderLayout.WEST);
        topo.add(caixaSaldo, BorderLayout.EAST);
        return topo;
    }

    // ═══════════════════════════════════════════════════════
    //  CORPO: LOGIN ou AÇÕES (CardLayout)
    // ═══════════════════════════════════════════════════════

    private JPanel criarCorpo() {
        JPanel corpo = new JPanel(new CardLayout());
        corpo.setBackground(C_BG);

        cardLogin  = criarCardLogin();
        cardAcoes  = criarCardAcoes();

        corpo.add(cardLogin,  "login");
        corpo.add(cardAcoes,  "acoes");

        return corpo;
    }

    // ── Card de login ─────────────────────────────────────

    private JPanel criarCardLogin() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(C_BG);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(C_PAINEL);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDA, 1),
                new EmptyBorder(28, 32, 28, 32)
        ));
        box.setMaximumSize(new Dimension(380, 500));
        box.setPreferredSize(new Dimension(360, 460));

        JLabel titulo = new JLabel("Entrar na conta");
        titulo.setForeground(C_TEXTO);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 16));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Digite o nome do titular da conta");
        sub.setForeground(C_MUTED);
        sub.setFont(F_SMALL);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campo de busca
        campoBuscaNome = criarCampoTexto("ex: João Silva");
        campoBuscaNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Lista de sugestões
        modeloLista = new DefaultListModel<>();
        listaNomes = new JList<>(modeloLista);
        listaNomes.setBackground(new Color(8, 12, 20));
        listaNomes.setForeground(C_TEXTO);
        listaNomes.setFont(F_MONO_BOLD);
        listaNomes.setSelectionBackground(C_ACENTO);
        listaNomes.setSelectionForeground(new Color(6, 26, 15));
        listaNomes.setBorder(new EmptyBorder(4, 8, 4, 8));
        listaNomes.setFixedCellHeight(30);

        // Filtra ao digitar
        campoBuscaNome.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrarNomes(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrarNomes(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarNomes(); }
        });

        JScrollPane scrollLista = new JScrollPane(listaNomes);
        scrollLista.setBackground(new Color(8, 12, 20));
        scrollLista.setBorder(BorderFactory.createLineBorder(C_BORDA, 1));
        scrollLista.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        scrollLista.setMinimumSize(new Dimension(0, 90));
        scrollLista.setPreferredSize(new Dimension(0, 100));

        // Campo de senha
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setForeground(C_MUTED);
        lblSenha.setFont(F_SMALL);
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField campoSenha = new JPasswordField();
        estilizarCampo(campoSenha);
        campoSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Botão entrar
        JButton btnEntrar = criarBotaoPrimario("→  Entrar");
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnEntrar.addActionListener(e -> {
            String nomeSelecionado = listaNomes.getSelectedValue();
            if (nomeSelecionado == null) {
                String digitado = campoBuscaNome.getText().trim();
                if (!digitado.isEmpty()) {
                    // tenta encontrar exato
                    nomeSelecionado = contas.values().stream()
                            .filter(c -> c.titular.equalsIgnoreCase(digitado))
                            .map(c -> c.titular)
                            .findFirst().orElse(null);
                }
            }
            if (nomeSelecionado == null) { erro("Selecione ou digite um nome de titular válido."); return; }
            String senha = new String(campoSenha.getPassword());
            fazerLoginPorNome(nomeSelecionado, senha);
            campoSenha.setText("");
        });

        // Ação Enter no campo senha
        campoSenha.addActionListener(btnEntrar.getActionListeners()[0]);

        // Link criar conta
        JButton btnCriar = new JButton("Não tem conta? Criar nova conta");
        btnCriar.setFont(F_SMALL);
        btnCriar.setForeground(C_ACENTO);
        btnCriar.setBackground(C_BG);
        btnCriar.setBorder(new EmptyBorder(4, 0, 0, 0));
        btnCriar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCriar.setFocusPainted(false);
        btnCriar.setContentAreaFilled(false);
        btnCriar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCriar.addActionListener(e -> criarConta());

        // Monta o box
        box.add(titulo);
        box.add(Box.createVerticalStrut(4));
        box.add(sub);
        box.add(Box.createVerticalStrut(20));
        box.add(label("Nome do titular"));
        box.add(Box.createVerticalStrut(4));
        box.add(campoBuscaNome);
        box.add(Box.createVerticalStrut(4));
        box.add(scrollLista);
        box.add(Box.createVerticalStrut(14));
        box.add(label("Senha"));
        box.add(Box.createVerticalStrut(4));
        box.add(campoSenha);
        box.add(Box.createVerticalStrut(18));
        box.add(btnEntrar);
        box.add(Box.createVerticalStrut(10));
        box.add(btnCriar);

        filtrarNomes(); // preenche lista inicial

        outer.add(box);
        return outer;
    }

    private void filtrarNomes() {
        String filtro = campoBuscaNome.getText().trim().toLowerCase();
        modeloLista.clear();
        for (Conta c : contas.values()) {
            if (filtro.isEmpty() || c.titular.toLowerCase().contains(filtro)) {
                modeloLista.addElement(c.titular);
            }
        }
        if (modeloLista.getSize() > 0) listaNomes.setSelectedIndex(0);
    }

    // ── Card de ações ─────────────────────────────────────

    private JPanel criarCardAcoes() {
        JPanel painel = new JPanel(new GridLayout(2, 3, 12, 12));
        painel.setBackground(C_BG);
        painel.setBorder(new EmptyBorder(20, 24, 16, 24));

        painel.add(criarBotaoAcao("▼", "Depositar",     C_ACENTO, C_BTN_BG,  C_BTN_HOV, e -> depositar()));
        painel.add(criarBotaoAcao("▲", "Sacar",         C_ACENTO, C_BTN_BG,  C_BTN_HOV, e -> sacar()));
        painel.add(criarBotaoAcao("→", "Transferir",    C_ACENTO, C_BTN_BG,  C_BTN_HOV, e -> transferir()));
        painel.add(criarBotaoAcao("≡", "Extrato",       C_ACENTO, C_BTN_BG,  C_BTN_HOV, e -> verExtrato()));
        painel.add(criarBotaoAcao("✎", "Alterar Senha", C_ACENTO, C_BTN_BG,  C_BTN_HOV, e -> alterarSenha()));

        // Linha inferior direita: logout + encerrar
        JPanel rodapeBotoes = new JPanel(new GridLayout(2, 1, 0, 8));
        rodapeBotoes.setBackground(C_BG);
        rodapeBotoes.add(criarBotaoAcao("←", "Logout",   C_MUTED, C_PAINEL,  new Color(30, 38, 54), e -> fazerLogout()));
        rodapeBotoes.add(criarBotaoAcao("⏻", "Encerrar", C_DANGER, C_DANG_BG, C_DANG_HOV,           e -> confirmarEncerramento()));

        painel.add(rodapeBotoes);
        return painel;
    }

    // ── Fábrica de botão de ação ──────────────────────────

    private JButton criarBotaoAcao(String icone, String texto,
                                   Color corTexto, Color corBg, Color corHover,
                                   java.awt.event.ActionListener acao) {
        JButton btn = new JButton("<html><center><span style='font-size:18px'>" + icone + "</span><br>" + texto + "</center></html>");
        btn.setFont(F_MONO_BOLD);
        btn.setForeground(corTexto);
        btn.setBackground(corBg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDA, 1),
                new EmptyBorder(12, 8, 12, 8)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(acao);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(corHover);
                // texto fica escuro quando hover verde
                if (corHover.equals(C_BTN_HOV)) btn.setForeground(new Color(6, 26, 15));
                else if (corHover.equals(C_DANG_HOV)) btn.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(corBg);
                btn.setForeground(corTexto);
            }
        });
        return btn;
    }

    // ── Barra de status ───────────────────────────────────

    private JPanel criarStatus() {
        JPanel rod = new JPanel(new BorderLayout());
        rod.setBackground(C_PAINEL);
        rod.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDA),
                new EmptyBorder(6, 16, 6, 16)
        ));

        JLabel ver = new JLabel("OMEGA BANKING v3.0");
        ver.setForeground(C_MUTED);
        ver.setFont(F_SMALL);

        lblStatusBadge = new JLabel("● DESCONECTADO");
        lblStatusBadge.setForeground(C_MUTED);
        lblStatusBadge.setFont(F_SMALL);

        rod.add(ver,            BorderLayout.WEST);
        rod.add(lblStatusBadge, BorderLayout.EAST);
        return rod;
    }

    // ═══════════════════════════════════════════════════════
    //  ATUALIZAÇÃO DA INTERFACE
    // ═══════════════════════════════════════════════════════

    private void atualizarInterface() {
        boolean logado = contaAtiva != null;
        JPanel corpo = (JPanel) getContentPane().getComponent(1);
        CardLayout cl = (CardLayout) corpo.getLayout();

        if (logado) {
            lblNome.setText(contaAtiva.titular.toUpperCase());
            lblSubtitulo.setText("Conta: " + contaAtiva.numero);
            lblSaldo.setForeground(C_ACENTO);
            lblSaldo.setText(formatarMoeda(contaAtiva.saldo));
            lblStatusBadge.setText("● CONECTADO");
            lblStatusBadge.setForeground(C_ACENTO);
            cl.show(corpo, "acoes");
        } else {
            lblNome.setText("OMEGA BANKING");
            lblSubtitulo.setText("Faça login para continuar");
            lblSaldo.setForeground(new Color(42, 51, 69));
            lblSaldo.setText("—");
            lblStatusBadge.setText("● DESCONECTADO");
            lblStatusBadge.setForeground(C_MUTED);
            filtrarNomes();
            campoBuscaNome.setText("");
            cl.show(corpo, "login");
        }
    }

    // ═══════════════════════════════════════════════════════
    //  OPERAÇÕES BANCÁRIAS
    // ═══════════════════════════════════════════════════════

    private void fazerLoginPorNome(String nomeExibido, String senha) {
        // Busca pela chave (nome em lowercase)
        Conta encontrada = null;
        for (Conta c : contas.values()) {
            if (c.titular.equalsIgnoreCase(nomeExibido)) { encontrada = c; break; }
        }
        if (encontrada != null && encontrada.senhaHash.equals(hash(senha))) {
            contaAtiva = encontrada;
            atualizarInterface();
            sucesso("Bem-vindo(a), " + contaAtiva.titular + "!");
        } else {
            erro("Nome ou senha incorretos.");
        }
    }

    private void fazerLogout() {
        int opt = JOptionPane.showConfirmDialog(this, "Sair da conta atual?", "Logout", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            contaAtiva = null;
            atualizarInterface();
        }
    }

    private void criarConta() {
        String nome = JOptionPane.showInputDialog(this, "Nome completo do titular:");
        if (nome == null || nome.isBlank()) { erro("Nome não pode ser vazio."); return; }

        // Verifica duplicata
        String nomeKey = nome.trim().toLowerCase();
        for (Conta c : contas.values()) {
            if (c.titular.equalsIgnoreCase(nome.trim())) {
                erro("Já existe uma conta com esse nome.\nEscolha um nome diferente ou faça login."); return;
            }
        }

        String senha = lerSenha("Senha (mínimo 4 caracteres):");
        if (senha == null || senha.length() < 4) { erro("Senha muito curta."); return; }

        String saldoStr = JOptionPane.showInputDialog(this, "Saldo inicial (R$):");
        if (saldoStr == null || saldoStr.isBlank()) return;
        BigDecimal saldoInicial;
        try {
            saldoInicial = new BigDecimal(saldoStr.replace(",", "."));
            if (saldoInicial.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
        } catch (Exception e) { erro("Valor inválido para saldo inicial."); return; }

        String id = gerarIdUnico();
        Conta nova = new Conta(id, nome.trim(), saldoInicial, hash(senha));
        contas.put(nomeKey, nova);
        filtrarNomes();
        sucesso("Conta criada com sucesso!\nID: " + id + "\nTitular: " + nome.trim());
    }

    private void depositar() {
        String v = JOptionPane.showInputDialog(this, "Valor a depositar (R$):");
        if (v == null || v.isBlank()) return;
        try {
            BigDecimal valor = new BigDecimal(v.replace(",", "."));
            if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
            contaAtiva.saldo = contaAtiva.saldo.add(valor);
            contaAtiva.historico.add("+ " + formatarMoeda(valor) + "  [Depósito]");
            atualizarInterface();
            sucesso("Depósito de " + formatarMoeda(valor) + " realizado com sucesso.");
        } catch (Exception e) { erro("Valor inválido."); }
    }

    private void sacar() {
        String v = JOptionPane.showInputDialog(this, "Valor a sacar (R$):");
        if (v == null || v.isBlank()) return;
        try {
            BigDecimal valor = new BigDecimal(v.replace(",", "."));
            if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
            if (contaAtiva.saldo.compareTo(valor) < 0) {
                erro("Saldo insuficiente.\nSaldo atual: " + formatarMoeda(contaAtiva.saldo)); return;
            }
            contaAtiva.saldo = contaAtiva.saldo.subtract(valor);
            contaAtiva.historico.add("- " + formatarMoeda(valor) + "  [Saque]");
            atualizarInterface();
            sucesso("Saque de " + formatarMoeda(valor) + " realizado com sucesso.");
        } catch (Exception e) { erro("Valor inválido."); }
    }

    private void transferir() {
        // Monta lista de destinos possíveis (exceto conta ativa)
        java.util.List<String> nomes = new ArrayList<>();
        for (Conta c : contas.values()) {
            if (!c.numero.equals(contaAtiva.numero)) nomes.add(c.titular + " (" + c.numero + ")");
        }
        if (nomes.isEmpty()) { erro("Nenhuma outra conta cadastrada para receber transferência."); return; }

        String escolhido = (String) JOptionPane.showInputDialog(
                this, "Selecione a conta destino:", "Transferir",
                JOptionPane.PLAIN_MESSAGE, null,
                nomes.toArray(), nomes.get(0)
        );
        if (escolhido == null) return;

        // Extrai o número da conta do texto "(CC-XXXX)"
        String numDestino = escolhido.replaceAll(".*\\((.+)\\).*", "$1");
        Conta destino = contas.values().stream()
                .filter(c -> c.numero.equals(numDestino))
                .findFirst().orElse(null);
        if (destino == null) { erro("Conta destino não encontrada."); return; }

        String v = JOptionPane.showInputDialog(this, "Valor a transferir (R$):");
        if (v == null || v.isBlank()) return;
        try {
            BigDecimal valor = new BigDecimal(v.replace(",", "."));
            if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
            if (contaAtiva.saldo.compareTo(valor) < 0) { erro("Saldo insuficiente."); return; }

            contaAtiva.saldo = contaAtiva.saldo.subtract(valor);
            destino.saldo    = destino.saldo.add(valor);

            String reg = formatarMoeda(valor);
            contaAtiva.historico.add("→ " + reg + "  [Transferência para " + destino.titular + "]");
            destino.historico.add   ("← " + reg + "  [Transferência de "   + contaAtiva.titular + "]");

            atualizarInterface();
            sucesso("Transferência de " + reg + " para " + destino.titular + " realizada.");
        } catch (Exception e) { erro("Valor inválido."); }
    }

    private void verExtrato() {
        if (contaAtiva.historico.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma movimentação registrada.", "Extrato", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JTextArea area = new JTextArea(String.join("\n", contaAtiva.historico));
        area.setFont(F_MONO_BOLD);
        area.setEditable(false);
        area.setBackground(C_PAINEL);
        area.setForeground(C_ACENTO);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(440, 260));
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDA, 1));
        JOptionPane.showMessageDialog(this, scroll, "Extrato — " + contaAtiva.titular, JOptionPane.PLAIN_MESSAGE);
    }

    private void alterarSenha() {
        String atual = lerSenha("Senha atual:");
        if (atual == null) return;
        if (!contaAtiva.senhaHash.equals(hash(atual))) { erro("Senha atual incorreta."); return; }

        String nova = lerSenha("Nova senha (mínimo 4 caracteres):");
        if (nova == null || nova.length() < 4) { erro("Senha muito curta."); return; }

        String conf = lerSenha("Confirme a nova senha:");
        if (!nova.equals(conf)) { erro("As senhas não coincidem."); return; }

        contaAtiva.senhaHash = hash(nova);
        sucesso("Senha alterada com sucesso.");
    }

    private void confirmarEncerramento() {
        int opt = JOptionPane.showConfirmDialog(this, "Encerrar o sistema?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) System.exit(0);
    }

    // ═══════════════════════════════════════════════════════
    //  UTILITÁRIOS
    // ═══════════════════════════════════════════════════════

    private String gerarIdUnico() {
        SecureRandom rng = new SecureRandom();
        String id;
        Set<String> ids = new HashSet<>();
        for (Conta c : contas.values()) ids.add(c.numero);
        do { id = "CC-" + (1000 + rng.nextInt(8999)); } while (ids.contains(id));
        return id;
    }

    private String formatarMoeda(BigDecimal valor) {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return fmt.format(valor.setScale(2, RoundingMode.HALF_UP));
    }

    private String hash(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException("Erro ao calcular hash", e); }
    }

    private String lerSenha(String msg) {
        JPasswordField campo = new JPasswordField(20);
        estilizarCampo(campo);
        int r = JOptionPane.showConfirmDialog(this, campo, msg, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return r == JOptionPane.OK_OPTION ? new String(campo.getPassword()) : null;
    }

    // ── Helpers de UI ─────────────────────────────────────

    private JTextField criarCampoTexto(String placeholder) {
        JTextField f = new JTextField();
        estilizarCampo(f);
        // Placeholder via FocusListener
        f.setForeground(C_MUTED);
        f.setText(placeholder);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(C_TEXTO); }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(C_MUTED); }
            }
        });
        return f;
    }

    private void estilizarCampo(JTextField f) {
        f.setBackground(C_INPUT_BG);
        f.setForeground(C_TEXTO);
        f.setCaretColor(C_ACENTO);
        f.setFont(F_MONO_BOLD);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDA, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(F_MONO_BOLD);
        btn.setForeground(new Color(6, 26, 15));
        btn.setBackground(C_ACENTO);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 0, 10, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(0, 180, 100)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(C_ACENTO); }
        });
        return btn;
    }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(C_MUTED);
        l.setFont(F_SMALL);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void sucesso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "✓  Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void erro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "✗  Erro", JOptionPane.ERROR_MESSAGE);
    }

    // ═══════════════════════════════════════════════════════
    //  MODELO DE DADOS
    // ═══════════════════════════════════════════════════════

    private static class Conta {
        String numero, titular, senhaHash;
        BigDecimal saldo;
        final java.util.List<String> historico = new ArrayList<>();

        Conta(String numero, String titular, BigDecimal saldo, String senhaHash) {
            this.numero    = numero;
            this.titular   = titular;
            this.saldo     = saldo;
            this.senhaHash = senhaHash;
        }
    }

    // ═══════════════════════════════════════════════════════
    //  PONTO DE ENTRADA
    // ═══════════════════════════════════════════════════════

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new OmegaApp().setVisible(true);
        });
    }
}