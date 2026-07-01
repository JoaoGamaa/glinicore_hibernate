package viewer;

import controller.FuncoesUteis;
import controller.GerenciadorDominio;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Endereco;
import model.Paciente;

public class DlgCadPaciente extends JDialog {

    private final GerenciadorDominio gerenciador;
    private Paciente paciente;
    private boolean salvou;
    private final JTextField txtNome = new JTextField(28);
    private final JFormattedTextField txtCpf = new JFormattedTextField();
    private final JFormattedTextField txtDataNascimento = new JFormattedTextField();
    private final JFormattedTextField txtTelefone = new JFormattedTextField();
    private final JTextField txtEmail = new JTextField(28);
    private final JTextField txtRua = new JTextField(28);
    private final JTextField txtNumero = new JTextField(8);
    private final JTextField txtBairro = new JTextField(20);
    private final JTextField txtCidade = new JTextField(20);
    private final JTextField txtEstado = new JTextField(4);
    private final JTextField txtCep = new JTextField(12);
    private final JTextField txtComplemento = new JTextField(28);
    private final JCheckBox chkAtivo = new JCheckBox("Paciente ativo", true);
    private final JLabel lblFoto = new JLabel("Foto", SwingConstants.CENTER);
    private final JButton btnBuscarCep = new JButton("Buscar");

    public DlgCadPaciente(java.awt.Window owner, GerenciadorDominio gerenciador, Paciente paciente) {
        super(owner, paciente == null ? "Novo Paciente" : "Editar Paciente", ModalityType.APPLICATION_MODAL);
        this.gerenciador = gerenciador;
        this.paciente = paciente;
        configurarMascaras();
        montarTela();
        preencherCampos();
        pack();
        setLocationRelativeTo(owner);
    }

    private void configurarMascaras() {
        try {
            txtCpf.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
            txtDataNascimento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
            txtTelefone.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##) #####-####")));
        } catch (ParseException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(14, 14, 4, 14));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JPanel painelCep = new JPanel(new BorderLayout(4, 0));
        painelCep.add(txtCep, BorderLayout.CENTER);
        painelCep.add(btnBuscarCep, BorderLayout.EAST);
        btnBuscarCep.addActionListener(e -> buscarCep());
        txtCep.addActionListener(e -> buscarCep());

        int row = 0;
        addLinha(form, gbc, row++, "Nome *", txtNome, "CPF", txtCpf);
        addLinha(form, gbc, row++, "Nascimento", txtDataNascimento, "Telefone", txtTelefone);
        addLinha(form, gbc, row++, "E-mail", txtEmail, "CEP", painelCep);
        addLinha(form, gbc, row++, "Rua", txtRua, "Número", txtNumero);
        addLinha(form, gbc, row++, "Bairro", txtBairro, "Cidade", txtCidade);
        addLinha(form, gbc, row++, "Estado", txtEstado, "Complemento", txtComplemento);

        lblFoto.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        lblFoto.setPreferredSize(new java.awt.Dimension(120, 120));
        lblFoto.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { escolherFoto(); }
        });
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(chkAtivo, gbc);
        gbc.gridx = 2; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(lblFoto, gbc);

        JPanel botoes = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
        botoes.add(btnCancelar);
        botoes.add(btnSalvar);

        add(form, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void addLinha(JPanel form, GridBagConstraints gbc, int row, String label1, JComponent campo1, String label2, JComponent campo2) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel(label1), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        form.add(campo1, gbc);
        gbc.gridx = 2; gbc.gridy = row;
        form.add(new JLabel(label2), gbc);
        gbc.gridx = 3; gbc.gridy = row;
        form.add(campo2, gbc);
    }

    private void preencherCampos() {
        if (paciente == null) return;
        txtNome.setText(paciente.getNome());
        txtCpf.setText(paciente.getCpf());
        txtDataNascimento.setText(paciente.getDataNascimentoFormatada());
        txtTelefone.setText(paciente.getTelefone());
        txtEmail.setText(paciente.getEmail());
        chkAtivo.setSelected(Boolean.TRUE.equals(paciente.getAtivo()));
        if (paciente.getFotoIdentificacao() != null) {
            ImageIcon icon = FuncoesUteis.bytesToIcon(paciente.getFotoIdentificacao());
            if (icon != null) FuncoesUteis.mostrarFoto(lblFoto, icon);
        }
        Endereco e = paciente.getEndereco();
        if (e != null) {
            txtRua.setText(e.getRua());
            txtNumero.setText(e.getNumero());
            txtBairro.setText(e.getBairro());
            txtCidade.setText(e.getCidade());
            txtEstado.setText(e.getEstado());
            txtCep.setText(e.getCep());
            txtComplemento.setText(e.getComplemento());
        }
    }

    private void escolherFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Imagem", "png", "jpg", "jpeg", "gif", "bmp"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arq = chooser.getSelectedFile();
            FuncoesUteis.mostrarFoto(lblFoto, new ImageIcon(arq.getAbsolutePath()));
        }
    }

    private void buscarCep() {
        String cep = txtCep.getText();
        btnBuscarCep.setEnabled(false);
        btnBuscarCep.setText("...");

        SwingWorker<Endereco, Void> worker = new SwingWorker<>() {
            @Override
            protected Endereco doInBackground() {
                return gerenciador.buscarEnderecoPorCep(cep);
            }

            @Override
            protected void done() {
                try {
                    preencherEndereco(get());
                    JOptionPane.showMessageDialog(DlgCadPaciente.this, "Endereco preenchido pelo CEP.");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(DlgCadPaciente.this, "Consulta de CEP interrompida.", "CEP", JOptionPane.ERROR_MESSAGE);
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause() == null ? ex : ex.getCause();
                    JOptionPane.showMessageDialog(DlgCadPaciente.this, causa.getMessage(), "CEP", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnBuscarCep.setEnabled(true);
                    btnBuscarCep.setText("Buscar");
                }
            }
        };
        worker.execute();
    }

    private void preencherEndereco(Endereco endereco) {
        if (endereco == null) return;
        txtCep.setText(valor(endereco.getCep()));
        txtRua.setText(valor(endereco.getRua()));
        txtBairro.setText(valor(endereco.getBairro()));
        txtCidade.setText(valor(endereco.getCidade()));
        txtEstado.setText(valor(endereco.getEstado()));
        if (endereco.getComplemento() != null && !endereco.getComplemento().isBlank()) {
            txtComplemento.setText(endereco.getComplemento());
        }
        txtNumero.requestFocusInWindow();
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }

    private void salvar() {
        try {
            if (paciente == null) {
                paciente = gerenciador.inserirPaciente(
                        txtNome.getText(), txtCpf.getText(), FuncoesUteis.strToDate(txtDataNascimento.getText()),
                        txtTelefone.getText(), txtEmail.getText(), lblFoto.getIcon(), txtRua.getText(), txtNumero.getText(),
                        txtBairro.getText(), txtCidade.getText(), txtEstado.getText(), txtCep.getText(), txtComplemento.getText());
            } else {
                paciente = gerenciador.alterarPaciente(paciente, txtNome.getText(), txtCpf.getText(),
                        FuncoesUteis.strToDate(txtDataNascimento.getText()), txtTelefone.getText(), txtEmail.getText(),
                        lblFoto.getIcon(), txtRua.getText(), txtNumero.getText(), txtBairro.getText(), txtCidade.getText(),
                        txtEstado.getText(), txtCep.getText(), txtComplemento.getText(), chkAtivo.isSelected());
            }
            salvou = true;
            JOptionPane.showMessageDialog(this, "Paciente salvo com sucesso.");
            dispose();
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this, mensagemErroSalvar(ex), "Erro ao salvar paciente", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String mensagemErroSalvar(Throwable ex) {
        Throwable causa = causaRaiz(ex);
        String detalhe = causa.getMessage() == null ? ex.toString() : causa.getMessage();
        if (detalhe.toLowerCase().contains("connection")
                || detalhe.toLowerCase().contains("conex")
                || detalhe.toLowerCase().contains("connection refused")
                || detalhe.toLowerCase().contains("localhost:5432")
                || detalhe.toLowerCase().contains("postgresql")
                || detalhe.toLowerCase().contains("glinicore")
                || ex instanceof ExceptionInInitializerError) {
            return "Nao foi possivel conectar ao PostgreSQL.\n\n"
                    + "Verifique se o PostgreSQL esta aberto, se o banco 'glinicore' existe "
                    + "e se usuario/senha conferem no hibernate.cfg.xml.";
        }
        return detalhe;
    }

    private Throwable causaRaiz(Throwable ex) {
        if (ex instanceof ExceptionInInitializerError) {
            Throwable inicializacao = ((ExceptionInInitializerError) ex).getException();
            if (inicializacao != null) {
                return causaRaiz(inicializacao);
            }
        }
        Throwable causa = ex;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa;
    }

    public boolean isSalvou() { return salvou; }
    public Paciente getPaciente() { return paciente; }
}
