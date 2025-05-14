import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AplicativoReservaViagens extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel painelPrincipal = new JPanel(cardLayout);

    // Dados da compra
    private String destinoSelecionado = null;
    private String nomeCompleto = null;
    private String cpf = null;
    private String dataNascimento = null;
    private int idade = -1;
    private String email = null;
    private String assentoSelecionado = null;
    private String metodoPagamento = null;

    // Controle dos assentos disponíveis (cadeiras)
    private boolean[] assentosOcupados = new boolean[30]; // 30 assentos para exemplo

    // Componentes usados em várias telas para atualizar dinamicamente
    // Tela cadastro passageiro
    private JTextField txtNome;
    private JTextField txtCPF;
    private JTextField txtDataNascimento;
    private JLabel lblIdade;
    private JTextField txtEmail;

    // Tela assentos
    private JPanel painelAssentos;
    private JButton[] botoesAssentos;

    // Tela pagamento
    private ButtonGroup grupoPagamentos;
    private JRadioButton rbBoleto;
    private JRadioButton rbCartao;
    private JRadioButton rbPix;

    // Tela cartão de crédito (nova tela)
    private JPanel painelCartaoCreditoDetalhes;
    private JTextField txtNumeroCartao;
    private JTextField txtValidadeCartao;
    private JTextField txtCVV;
    private JLabel lblErroCVV;

    // Tela confirmação
    private JTextArea textoConfirmacao;

    // Tela seleção voos
    private JPanel painelVooOpcoes;
    private ButtonGroup grupoVoos;

    // Para destacar o destino selecionado
    private JPanel[] paineisDestinoBoxes;
    private Color corSelecionado = new Color(30, 144, 255);
    private Color corPadraoFundo = Color.WHITE;
    private Color corPadraoFonte = Color.BLACK;

    // Dados da seleção de voo
    // Inclui preço como o quinto elemento em cada voo
    private String[][] voos; // Cada voo: [codigo, data, horario, companhia, preco, conexoes]
    private String vooSelecionadoCodigo = null;
    private double precoVooSelecionado = 0.0; // Armazena o preço do voo selecionado

    public AplicativoReservaViagens() {
        setTitle("Aplicativo de Reserva de Viagens");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        painelPrincipal.add(criarTelaSelecaoDestino(), "Destino");
        painelPrincipal.add(criarTelaCadastroPassageiro(), "Cadastro");
        painelPrincipal.add(criarTelaSelecaoVoo(), "SelecaoVoo");
        painelPrincipal.add(criarTelaAssentos(), "Assentos");
        painelPrincipal.add(criarTelaPagamento(), "Pagamento");
        painelPrincipal.add(criarTelaCartaoCredito(), "CartaoCredito");
        painelPrincipal.add(criarTelaConfirmacao(), "Confirmacao");

        add(painelPrincipal);
        mostrarTela("Destino");
    }

    private JPanel criarTelaSelecaoDestino() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Selecione o Destino Desejado");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel opcoesDestinos = new JPanel(new GridLayout(2, 2, 20, 20));

        String[] destinos = {"Fortaleza (CE)", "Rio de Janeiro (RJ)", "Guarulhos (SP)", "Recife (PE)"};
        Color[] coresDeFundo = {
            new Color(255, 228, 181),
            new Color(135, 206, 250),
            new Color(152, 251, 152),
            new Color(255, 182, 193)
        };
        Color[] coresDaFonte = {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};

        paineisDestinoBoxes = new JPanel[destinos.length];

        for (int i = 0; i < destinos.length; i++) {
            String destino = destinos[i];
            Color corFundo = coresDeFundo[i];
            Color corFonte = coresDaFonte[i];

            JPanel box = new JPanel();
            box.setBackground(corFundo);
            box.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            box.setLayout(new BorderLayout());

            JLabel labelDestino = new JLabel(destino, SwingConstants.CENTER);
            labelDestino.setForeground(corFonte);
            labelDestino.setFont(new Font("Arial", Font.BOLD, 20));
            labelDestino.setOpaque(false);

            box.add(labelDestino, BorderLayout.CENTER);

            box.setCursor(new Cursor(Cursor.HAND_CURSOR));

            int index = i;
            box.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    destinoSelecionado = destino;
                    for (int j = 0; j < paineisDestinoBoxes.length; j++) {
                        if (j == index) {
                            paineisDestinoBoxes[j].setBorder(BorderFactory.createLineBorder(corSelecionado, 4));
                            paineisDestinoBoxes[j].setBackground(corSelecionado);
                            JLabel lbl = (JLabel) paineisDestinoBoxes[j].getComponent(0);
                            lbl.setForeground(Color.WHITE);
                        } else {
                            paineisDestinoBoxes[j].setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                            paineisDestinoBoxes[j].setBackground(coresDeFundo[j]);
                            JLabel lbl = (JLabel) paineisDestinoBoxes[j].getComponent(0);
                            lbl.setForeground(coresDaFonte[j]);
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!destino.equals(destinoSelecionado)) {
                        box.setBackground(corSelecionado.brighter());
                        JLabel lbl = (JLabel) box.getComponent(0);
                        lbl.setForeground(Color.DARK_GRAY);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!destino.equals(destinoSelecionado)) {
                        box.setBackground(corFundo);
                        JLabel lbl = (JLabel) box.getComponent(0);
                        lbl.setForeground(corFonte);
                    }
                }
            });

            paineisDestinoBoxes[i] = box;
            opcoesDestinos.add(box);
        }

        panel.add(opcoesDestinos, BorderLayout.CENTER);

        JButton botaoProximo = new JButton("Próximo");
        botaoProximo.setFont(new Font("Arial", Font.BOLD, 18));
        botaoProximo.addActionListener(e -> {
            if (destinoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um destino.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mostrarTela("Cadastro");
        });

        JPanel painelBotao = new JPanel();
        painelBotao.add(botaoProximo);
        panel.add(painelBotao, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarTelaCadastroPassageiro() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titulo = new JLabel("Cadastro de Passageiro");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome Completo
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblNome = new JLabel("Nome Completo:");
        lblNome.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(lblNome, gbc);

        gbc.gridx = 1;
        txtNome = new JTextField(20);
        txtNome.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(txtNome, gbc);

        // CPF
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblCPF = new JLabel("CPF:");
        lblCPF.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(lblCPF, gbc);

        gbc.gridx = 1;
        txtCPF = new JTextField(20);
        txtCPF.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(txtCPF, gbc);

        // Data de nascimento
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblDataNasc = new JLabel("Data de nascimento (dd.MM.yyyy):");
        lblDataNasc.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(lblDataNasc, gbc);

        gbc.gridx = 1;
        JPanel painelDataIdade = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        txtDataNascimento = new JTextField(15);
        txtDataNascimento.setFont(new Font("Arial", Font.PLAIN, 16));
        lblIdade = new JLabel("");
        lblIdade.setFont(new Font("Arial", Font.BOLD, 16));
        lblIdade.setForeground(Color.BLUE);
        painelDataIdade.add(txtDataNascimento);
        painelDataIdade.add(lblIdade);
        formPanel.add(painelDataIdade, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(txtEmail, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Ações
        JPanel painelBotoes = new JPanel();

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 16));
        btnVoltar.addActionListener(e -> mostrarTela("Destino"));
        painelBotoes.add(btnVoltar);

        JButton btnProximo = new JButton("Próximo");
        btnProximo.setFont(new Font("Arial", Font.BOLD, 16));
        btnProximo.addActionListener(e -> {
            if (validarCadastro()) {
                salvarDadosCadastro();
                gerarVoosAleatorios();
                mostrarTela("SelecaoVoo");
            }
        });
        painelBotoes.add(btnProximo);

        panel.add(painelBotoes, BorderLayout.SOUTH);

        // Listener para atualizar idade ao digitar data nascimento
        txtDataNascimento.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                atualizarIdade();
            }
            public void removeUpdate(DocumentEvent e) {
                atualizarIdade();
            }
            public void changedUpdate(DocumentEvent e) {
                atualizarIdade();
            }
        });

        return panel;
    }

    private void atualizarIdade() {
        String texto = txtDataNascimento.getText().trim();
        if (texto.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate dataNasc = LocalDate.parse(texto, fmt);
                LocalDate hoje = LocalDate.now();
                if (dataNasc.isAfter(hoje)) {
                    lblIdade.setText("Data futura inválida");
                    idade = -1;
                    return;
                }
                Period periodo = Period.between(dataNasc, hoje);
                idade = periodo.getYears();
                lblIdade.setText("Idade: " + idade);
            } catch (Exception ex) {
                lblIdade.setText("");
                idade = -1;
            }
        } else {
            lblIdade.setText("");
            idade = -1;
        }
    }

    private boolean validarCadastro() {
        String nome = txtNome.getText().trim();
        String cpfStr = txtCPF.getText().trim();
        String dataNascStr = txtDataNascimento.getText().trim();
        String emailStr = txtEmail.getText().trim();

        // Valida nome (apenas letras e espaços)
        if (!nome.matches("[A-Za-zÀ-ú ]+")) {
            JOptionPane.showMessageDialog(this, "Nome inválido. Apenas letras e espaços são permitidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome não pode ser vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Valida CPF (apenas dígitos, até 11 chars)
        if (!cpfStr.matches("\\d{1,11}")) {
            JOptionPane.showMessageDialog(this, "CPF inválido. Digite apenas números (máximo 11 dígitos).", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Valida data nascimento
        if (!dataNascStr.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Data de nascimento inválida. Use o formato dd.MM.aaaa.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (idade < 0) {
            JOptionPane.showMessageDialog(this, "Data de nascimento inválida ou futura.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Valida e-mail (permitido letras, números e os caracteres "@" e ".")
        if (!emailStr.matches("[A-Za-z0-9@.]+")) {
            JOptionPane.showMessageDialog(this, "Email inválido. Use letras, números e os caracteres '@' e '.'.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!emailStr.contains("@") || !emailStr.contains(".")) {
            JOptionPane.showMessageDialog(this, "Email deve conter '@' e '.'.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void salvarDadosCadastro() {
        nomeCompleto = txtNome.getText().trim();
        cpf = txtCPF.getText().trim();
        dataNascimento = txtDataNascimento.getText().trim();
        email = txtEmail.getText().trim();
    }

    private void gerarVoosAleatorios() {
        voos = new String[3][6]; // Altere para 6 colunas (inclui preço e conexões)
        Random random = new Random();
        String[] companhias = {"Azul", "Latam", "Gol"};
        DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // Definindo destinos para conexões
        String[] destinosConexoes = {"São Paulo (SP)", "Belo Horizonte (MG)", "Salvador (BA)", "Curitiba (PR)", "Manaus (AM)"};

        for (int i = 0; i < 3; i++) {
            voos[i][0] = gerarCodigoVoo(5);
            int anoAtual = LocalDate.now().getYear();
            int anoVoo = anoAtual + random.nextInt(2027 - anoAtual + 1);
            int mes = 1 + random.nextInt(12);
            int dia;
            switch (mes) {
                case 2:
                    boolean bissexto = (anoVoo % 4 == 0 && anoVoo % 100 != 0) || (anoVoo % 400 == 0);
                    dia = 1 + random.nextInt(bissexto ? 29 : 28);
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    dia = 1 + random.nextInt(30);
                    break;
                default:
                    dia = 1 + random.nextInt(31);
            }
            LocalDate dataVoo = LocalDate.of(anoVoo, mes, dia);
            voos[i][1] = dataVoo.format(formatterData);

            int hora = random.nextInt(24);
            int minuto = random.nextInt(60);
            voos[i][2] = String.format("%02d:%02d", hora, minuto);

            voos[i][3] = companhias[i]; // associação fixa: i=0 Azul, i=1 Latam, i=2 Gol

            // Definir preço conforme companhia
            double preco;
            String conexoes = "";
            switch (voos[i][3]) {
                case "Azul":
                    preco = 1300 + random.nextDouble() * 200; // R$1300 a 1500 (mais caro)
                    conexoes = "Voo Direto";
                    break;
                case "Latam":
                    preco = 1050 + random.nextDouble() * 150; // R$1050 a 1200 (médio)
                    conexoes = "1 Conexão: " + destinosConexoes[random.nextInt(destinosConexoes.length)];
                    break;
                case "Gol":
                    preco = 900 + random.nextDouble() * 100; // R$900 a 1000 (mais barato)
                    // Garantir que as duas conexões sejam distintas
                    int primeiroIndice = random.nextInt(destinosConexoes.length);
                    int segundoIndice;
                    do {
                        segundoIndice = random.nextInt(destinosConexoes.length);
                    } while (segundoIndice == primeiroIndice);
                    conexoes = "2 Conexões: " + destinosConexoes[primeiroIndice] + " e " + destinosConexoes[segundoIndice];
                    break;
                default:
                    preco = 1000;
            }
            voos[i][4] = String.format("%.2f", preco);
            voos[i][5] = conexoes; // Adiciona as conexões
        }
    }

    private String gerarCodigoVoo(int tamanho) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return codigo.toString();
    }

    private JPanel criarTelaSelecaoVoo() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titulo = new JLabel("Selecione o Voo");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo, BorderLayout.NORTH);

        painelVooOpcoes = new JPanel(new GridLayout(0, 1, 10, 10)); // Ajustado para número variável de voos
        grupoVoos = new ButtonGroup();

        panel.add(painelVooOpcoes, BorderLayout.CENTER);

        JPanel botoes = new JPanel();

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 16));
        btnVoltar.addActionListener(e -> mostrarTela("Cadastro"));
        botoes.add(btnVoltar);

        JButton btnProximo = new JButton("Próximo");
        btnProximo.setFont(new Font("Arial", Font.BOLD, 16));
        btnProximo.addActionListener(e -> {
            if (vooSelecionadoCodigo == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um voo.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mostrarTela("Assentos");
        });
        botoes.add(btnProximo);

        panel.add(botoes, BorderLayout.SOUTH);

        return panel;
    }

    private void atualizarTelaSelecaoVoo() {
        painelVooOpcoes.removeAll();
        grupoVoos = new ButtonGroup();
        vooSelecionadoCodigo = null;
        precoVooSelecionado = 0.0;

        for (int i = 0; i < voos.length; i++) {
            String codigo = voos[i][0];
            String data = voos[i][1];
            String horario = voos[i][2];
            String companhia = voos[i][3];
            String preco = voos[i][4];
            String conexoes = voos[i][5];

            String texto = String.format(
                    "<html><div style='padding: 10px; border: 2px solid #1e90ff; border-radius: 8px; background-color: #f0f8ff;'>"
                            + "<b>Voo %s</b><br>Data: %s<br>Horário: %s<br>Companhia: %s<br><b>Preço: R$ %s</b><br><i>%s</i></div></html>",
                    codigo, data, horario, companhia, preco, conexoes);
            JRadioButton rb = new JRadioButton(texto);
            rb.setFont(new Font("Arial", Font.PLAIN, 16));
            rb.setOpaque(false);
            rb.setContentAreaFilled(false);
            rb.setBorderPainted(false);
            grupoVoos.add(rb);

            int idx = i;
            rb.addActionListener(e -> {
                vooSelecionadoCodigo = voos[idx][0];
                // Atualizando o preço diretamente do array para garantir precisão
                precoVooSelecionado = Double.parseDouble(voos[idx][4]);
            });

            painelVooOpcoes.add(rb);
        }
        painelVooOpcoes.revalidate();
        painelVooOpcoes.repaint();
    }

    private JPanel criarTelaAssentos() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Escolha o Assento");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo, BorderLayout.NORTH);

        painelAssentos = new JPanel(new GridLayout(5, 6, 10, 10)); // 30 assentos
        botoesAssentos = new JButton[30];

        atualizarAssentos();

        panel.add(painelAssentos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 16));
        btnVoltar.addActionListener(e -> mostrarTela("SelecaoVoo"));
        painelBotoes.add(btnVoltar);

        JButton btnProximo = new JButton("Próximo");
        btnProximo.setFont(new Font("Arial", Font.BOLD, 16));
        btnProximo.addActionListener(e -> {
            if (assentoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um assento.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mostrarTela("Pagamento");
        });
        painelBotoes.add(btnProximo);

        panel.add(painelBotoes, BorderLayout.SOUTH);

        return panel;
    }

    private void atualizarAssentos() {
        painelAssentos.removeAll();
        for (int i = 0; i < 30; i++) {
            JButton botao = new JButton(String.valueOf(i + 1));
            botao.setFont(new Font("Arial", Font.BOLD, 14));
            botao.setOpaque(true);
            botao.setBorderPainted(false);
            if (assentosOcupados[i]) {
                botao.setBackground(Color.RED);
                botao.setEnabled(false);
            } else {
                botao.setBackground(Color.CYAN);
                botao.setEnabled(true);
            }
            int indice = i;
            botao.addActionListener(e -> {
                for (JButton b : botoesAssentos) {
                    if (!assentosOcupados[Integer.parseInt(b.getText()) - 1])
                        b.setBackground(Color.CYAN);
                }
                botao.setBackground(Color.BLUE);
                assentoSelecionado = botao.getText();
            });
            botoesAssentos[i] = botao;
            painelAssentos.add(botao);
        }
        painelAssentos.revalidate();
        painelAssentos.repaint();
    }

    private JPanel criarTelaPagamento() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titulo = new JLabel("Escolha o Método de Pagamento");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel opcoesPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        grupoPagamentos = new ButtonGroup();

        rbBoleto = new JRadioButton("Boleto");
        rbBoleto.setFont(new Font("Arial", Font.PLAIN, 18));
        rbCartao = new JRadioButton("Cartão de Crédito");
        rbCartao.setFont(new Font("Arial", Font.PLAIN, 18));
        rbPix = new JRadioButton("PIX");
        rbPix.setFont(new Font("Arial", Font.PLAIN, 18));

        grupoPagamentos.add(rbBoleto);
        grupoPagamentos.add(rbCartao);
        grupoPagamentos.add(rbPix);

        opcoesPanel.add(rbBoleto);
        opcoesPanel.add(rbCartao);
        opcoesPanel.add(rbPix);

        panel.add(opcoesPanel, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 16));
        btnVoltar.addActionListener(e -> mostrarTela("Assentos"));
        painelBotoes.add(btnVoltar);

        JButton btnProximo = new JButton("Próximo");
        btnProximo.setFont(new Font("Arial", Font.BOLD, 16));
        btnProximo.addActionListener(e -> {
            if (rbBoleto.isSelected()) {
                metodoPagamento = "Boleto";
                JOptionPane.showMessageDialog(this, "Boleto enviado para o email " + email, "Boleto enviado", JOptionPane.INFORMATION_MESSAGE);
                assentosOcupados[Integer.parseInt(assentoSelecionado) - 1] = true;
                mostrarTela("Confirmacao");
            } else if (rbCartao.isSelected()) {
                mostrarTela("CartaoCredito");
            } else if (rbPix.isSelected()) {
                metodoPagamento = "PIX";
                String chavePix = gerarChavePix();
                JOptionPane.showMessageDialog(this, "Chave PIX gerada: " + chavePix + "\nPagamento via PIX confirmado!", "PIX", JOptionPane.INFORMATION_MESSAGE);
                assentosOcupados[Integer.parseInt(assentoSelecionado) - 1] = true;
                mostrarTela("Confirmacao");
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um método de pagamento.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        painelBotoes.add(btnProximo);

        panel.add(painelBotoes, BorderLayout.PAGE_END);

        return panel;
    }

    private JPanel criarTelaCartaoCredito() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JLabel titulo = new JLabel("Pagamento com Cartão de Crédito");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo, BorderLayout.NORTH);

        painelCartaoCreditoDetalhes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblNumeroCartaoLabel = new JLabel("Número do Cartão (16 dígitos):");
        lblNumeroCartaoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        painelCartaoCreditoDetalhes.add(lblNumeroCartaoLabel, gbc);

        gbc.gridx = 1;
        txtNumeroCartao = new JTextField(20);
        painelCartaoCreditoDetalhes.add(txtNumeroCartao, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblValidadeCartaoLabel = new JLabel("Data de Validade (MM.AAAA):");
        lblValidadeCartaoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        painelCartaoCreditoDetalhes.add(lblValidadeCartaoLabel, gbc);

        gbc.gridx = 1;
        txtValidadeCartao = new JTextField(10);
        painelCartaoCreditoDetalhes.add(txtValidadeCartao, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblCVVLabel = new JLabel("CVV (3 dígitos):");
        lblCVVLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        painelCartaoCreditoDetalhes.add(lblCVVLabel, gbc);

        gbc.gridx = 1;
        txtCVV = new JTextField(5);
        painelCartaoCreditoDetalhes.add(txtCVV, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        lblErroCVV = new JLabel("");
        lblErroCVV.setForeground(Color.RED);
        painelCartaoCreditoDetalhes.add(lblErroCVV, gbc);

        txtCVV.getDocument().addDocumentListener(new DocumentListener() {
            private void validarCVV() {
                String cvv = txtCVV.getText();
                if (!cvv.matches("\\d*")) {
                    lblErroCVV.setText("CVV inválido! Apenas números.");
                } else if (cvv.length() > 3) {
                    lblErroCVV.setText("CVV deve ter exatamente 3 dígitos.");
                } else if (cvv.length() < 3) {
                    lblErroCVV.setText("CVV deve ter 3 dígitos.");
                } else {
                    lblErroCVV.setText("");
                }
            }
            public void insertUpdate(DocumentEvent e) { validarCVV(); }
            public void removeUpdate(DocumentEvent e) { validarCVV(); }
            public void changedUpdate(DocumentEvent e) { validarCVV(); }
        });

        panel.add(painelCartaoCreditoDetalhes, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 16));
        btnVoltar.addActionListener(e -> mostrarTela("Pagamento"));
        painelBotoes.add(btnVoltar);

        JButton btnConfirmar = new JButton("Confirmar Compra");
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 16));
        btnConfirmar.addActionListener(e -> {
            if (validarPagamentoCartao()) {
                metodoPagamento = "Cartão de Crédito";
                JOptionPane.showMessageDialog(this, "Pagamento com cartão aprovado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                assentosOcupados[Integer.parseInt(assentoSelecionado) - 1] = true;
                mostrarTela("Confirmacao");
            }
        });
        painelBotoes.add(btnConfirmar);

        panel.add(painelBotoes, BorderLayout.SOUTH);

        return panel;
    }

    private boolean validarPagamentoCartao() {
        String numCartao = txtNumeroCartao.getText().trim();
        String validade = txtValidadeCartao.getText().trim();
        String cvv = txtCVV.getText().trim();

        if (!numCartao.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this, "Número do cartão inválido. Deve conter exatamente 16 dígitos numéricos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!validade.matches("\\d{2}\\.\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Data de validade inválida. Use o formato MM.AAAA.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!cvv.matches("\\d{3}")) {
            JOptionPane.showMessageDialog(this, "CVV inválido. Deve ter exatamente 3 dígitos numéricos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!lblErroCVV.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Corrija o campo CVV.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            String[] partes = validade.split("\\.");
            int mes = Integer.parseInt(partes[0]);
            int ano = Integer.parseInt(partes[1]);
            if (mes < 1 || mes > 12) {
                JOptionPane.showMessageDialog(this, "Mês da validade inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data de validade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private String gerarChavePix() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder chave = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            chave.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return chave.toString();
    }

    private JPanel criarTelaConfirmacao() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Confirmação da Reserva");
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titulo, BorderLayout.NORTH);

        textoConfirmacao = new JTextArea();
        textoConfirmacao.setFont(new Font("Arial", Font.PLAIN, 16));
        textoConfirmacao.setEditable(false);
        textoConfirmacao.setLineWrap(true);
        textoConfirmacao.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(textoConfirmacao);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();

        JButton btnFazerMaisUmaCompra = new JButton("Fazer mais uma compra");
        btnFazerMaisUmaCompra.setFont(new Font("Arial", Font.BOLD, 18));
        btnFazerMaisUmaCompra.addActionListener(e -> {
            resetarDados();
            mostrarTela("Destino");
        });
        painelBotoes.add(btnFazerMaisUmaCompra);

        JButton btnFinalizar = new JButton("Finalizar");
        btnFinalizar.setFont(new Font("Arial", Font.BOLD, 18));
        btnFinalizar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Obrigado por usar nosso aplicativo!\nAté a próxima viagem!", "Finalizado", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
        painelBotoes.add(btnFinalizar);

        panel.add(painelBotoes, BorderLayout.SOUTH);

        return panel;
    }

    private void resetarDados() {
        destinoSelecionado = null;
        nomeCompleto = null;
        cpf = null;
        dataNascimento = null;
        idade = -1;
        email = null;
        assentoSelecionado = null;
        metodoPagamento = null;
        vooSelecionadoCodigo = null;
        precoVooSelecionado = 0.0;

        // Limpar os campos de entrada
        if (txtNome != null) txtNome.setText("");
        if (txtCPF != null) txtCPF.setText("");
        if (txtDataNascimento != null) txtDataNascimento.setText("");
        if (lblIdade != null) lblIdade.setText("");
        if (txtEmail != null) txtEmail.setText("");

        grupoPagamentos.clearSelection();

        for (int i = 0; i < assentosOcupados.length; i++) {
            assentosOcupados[i] = false;
        }
    }

    private void mostrarTela(String nome) {
        if (nome.equals("Confirmacao")) {
            montarTextoConfirmacao();
        }
        if (nome.equals("Assentos")) {
            atualizarAssentos();
        }
        if (nome.equals("SelecaoVoo")) {
            atualizarTelaSelecaoVoo();
        }
        cardLayout.show(painelPrincipal, nome);
    }

    private void montarTextoConfirmacao() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reserva confirmada!\n\n");
        sb.append("Destino: ").append(destinoSelecionado).append("\n");
        sb.append("Nome Completo: ").append(nomeCompleto).append("\n");
        sb.append("CPF: ").append(cpf).append("\n");
        sb.append("Data de Nascimento: ").append(dataNascimento).append(" (Idade: ").append(idade).append(" anos)\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Voo: ").append(vooSelecionadoCodigo != null ? vooSelecionadoCodigo : "N/A").append("\n");

        // Buscar conexões e preço do voo selecionado do array para garantir exibição correta
        String conexoes = "N/A";
        String precoStr = "0.00";
        if (voos != null && vooSelecionadoCodigo != null) {
            for (String[] voo : voos) {
                if (voo[0].equals(vooSelecionadoCodigo)) {
                    conexoes = voo.length > 5 ? voo[5] : "N/A";
                    precoStr = voo.length > 4 ? voo[4] : "0.00";
                    break;
                }
            }
        }
        sb.append("Conexões: ").append(conexoes).append("\n");

        sb.append(String.format("Preço do Voo: R$ %s\n", precoStr));
        sb.append("Assento: ").append(assentoSelecionado).append("\n");
        sb.append("Método de Pagamento: ").append(metodoPagamento).append("\n\n");
        sb.append("Boa viagem!");

        textoConfirmacao.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AplicativoReservaViagens app = new AplicativoReservaViagens();
            app.setVisible(true);
        });
    }
}