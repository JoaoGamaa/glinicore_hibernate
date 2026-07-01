package viewer;

import controller.FuncoesUteis;
import controller.GerenciadorDominio;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import model.ItemOrcamento;
import model.Orcamento;
import model.Paciente;
import model.StatusOrcamento;
import model.Usuario;

public class DlgCadOrcamento extends JDialog {

    private static final SimpleDateFormat FORMATO_CONSULTA = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    static {
        FORMATO_CONSULTA.setLenient(false);
    }

    private final GerenciadorDominio gerenciador;
    private Orcamento orcamento;
    private boolean salvou;

    private final JComboBox<Paciente> cbPaciente = new JComboBox<>();
    private final JComboBox<StatusOrcamento> cbStatus = new JComboBox<>(StatusOrcamento.values());
    private final JTextField txtDescricao = new JTextField(26);
    private final JSpinner spQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JTextField txtValorUnitario = new JTextField(10);
    private final JTextArea txtObservacoes = new JTextArea(4, 50);
    private final JTextField txtDataConsulta = criarCampoMascara("##/##/####");
    private final JTextField txtHoraConsulta = criarCampoMascara("##:##");
    private final JTextField txtProcedimentoConsulta = new JTextField(28);
    private final JTextArea txtObservacoesConsulta = new JTextArea(3, 50);
    private final DefaultTableModel modeloItens = new DefaultTableModel(
            new Object[]{"Descricao", "Qtd", "Valor unit.", "Total"}, 0);
    private final JTable tabelaItens = new JTable(modeloItens);
    private final List<ItemOrcamento> itens = new ArrayList<>();

    public DlgCadOrcamento(java.awt.Window owner, GerenciadorDominio gerenciador) {
        this(owner, gerenciador, null);
    }

    public DlgCadOrcamento(java.awt.Window owner, GerenciadorDominio gerenciador, Orcamento orcamento) {
        super(owner, orcamento == null ? "Novo Orcamento" : "Editar Orcamento", ModalityType.APPLICATION_MODAL);
        this.gerenciador = gerenciador;
        this.orcamento = orcamento;
        montarTela();
        carregarPacientes();
        preencherOrcamento();
        pack();
        setMinimumSize(new java.awt.Dimension(760, 580));
        setLocationRelativeTo(owner);
    }

    private void montarTela() {
        setLayout(new BorderLayout(8, 8));

        JPanel topo = new JPanel(new GridBagLayout());
        topo.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridx = 0; gbc.gridy = 0; topo.add(new JLabel("Paciente"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; topo.add(cbPaciente, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 4; topo.add(new JLabel("Status"), gbc);
        gbc.gridx = 5; topo.add(cbStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 1; topo.add(new JLabel("Procedimento"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; topo.add(txtDescricao, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 3; topo.add(new JLabel("Qtd"), gbc);
        gbc.gridx = 4; topo.add(spQuantidade, gbc);
        gbc.gridx = 5; topo.add(txtValorUnitario, gbc);

        JButton btnAdicionar = new JButton("Adicionar item");
        btnAdicionar.addActionListener(e -> adicionarItem());
        JButton btnRemover = new JButton("Remover item");
        btnRemover.addActionListener(e -> removerItemSelecionado());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; topo.add(btnAdicionar, gbc);
        gbc.gridx = 3; gbc.gridwidth = 3; topo.add(btnRemover, gbc);
        gbc.gridwidth = 1;

        JPanel agendamento = new JPanel(new GridBagLayout());
        agendamento.setBorder(BorderFactory.createTitledBorder("Consulta agendada"));
        GridBagConstraints ag = new GridBagConstraints();
        ag.insets = new Insets(4, 4, 4, 4);
        ag.fill = GridBagConstraints.HORIZONTAL;
        ag.weightx = 1;
        ag.gridx = 0; ag.gridy = 0; agendamento.add(new JLabel("Data"), ag);
        ag.gridx = 1; agendamento.add(txtDataConsulta, ag);
        ag.gridx = 2; agendamento.add(new JLabel("Hora"), ag);
        ag.gridx = 3; agendamento.add(txtHoraConsulta, ag);
        ag.gridx = 0; ag.gridy = 1; agendamento.add(new JLabel("Procedimento da consulta"), ag);
        ag.gridx = 1; ag.gridwidth = 3; agendamento.add(txtProcedimentoConsulta, ag);
        ag.gridx = 0; ag.gridy = 2; ag.gridwidth = 4;
        txtObservacoesConsulta.setBorder(BorderFactory.createTitledBorder("Observacoes da consulta"));
        agendamento.add(new JScrollPane(txtObservacoesConsulta), ag);

        JPanel centro = new JPanel(new BorderLayout(6, 6));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        centro.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);
        txtObservacoes.setBorder(BorderFactory.createTitledBorder("Observacoes do orcamento"));
        JPanel sulCentro = new JPanel(new BorderLayout(6, 6));
        sulCentro.add(new JScrollPane(txtObservacoes), BorderLayout.CENTER);
        sulCentro.add(agendamento, BorderLayout.SOUTH);
        centro.add(sulCentro, BorderLayout.SOUTH);

        JPanel botoes = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnSalvar = new JButton(orcamento == null ? "Salvar orcamento" : "Atualizar orcamento");
        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvar());
        botoes.add(btnCancelar);
        botoes.add(btnSalvar);

        add(topo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void carregarPacientes() {
        cbPaciente.removeAllItems();
        for (Paciente p : gerenciador.listarPacientesSeguro()) {
            cbPaciente.addItem(p);
        }
    }

    private void preencherOrcamento() {
        cbStatus.setSelectedItem(StatusOrcamento.PENDENTE);
        if (orcamento == null) {
            return;
        }
        selecionarPaciente(orcamento.getPaciente());
        cbStatus.setSelectedItem(orcamento.getStatus() == null ? StatusOrcamento.PENDENTE : orcamento.getStatus());
        txtObservacoes.setText(orcamento.getObservacoes() == null ? "" : orcamento.getObservacoes());
        txtProcedimentoConsulta.setText(orcamento.getProcedimentoConsulta() == null ? "" : orcamento.getProcedimentoConsulta());
        txtObservacoesConsulta.setText(orcamento.getObservacoesConsulta() == null ? "" : orcamento.getObservacoesConsulta());
        if (orcamento.getDataConsulta() != null) {
            String[] partes = FORMATO_CONSULTA.format(orcamento.getDataConsulta()).split(" ");
            txtDataConsulta.setText(partes[0]);
            txtHoraConsulta.setText(partes[1]);
        }
        itens.clear();
        modeloItens.setRowCount(0);
        for (ItemOrcamento item : orcamento.getItens()) {
            itens.add(item);
            adicionarLinhaItem(item);
        }
    }

    private void selecionarPaciente(Paciente paciente) {
        if (paciente == null || paciente.getId() == null) {
            return;
        }
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            Paciente item = cbPaciente.getItemAt(i);
            if (item != null && paciente.getId().equals(item.getId())) {
                cbPaciente.setSelectedIndex(i);
                return;
            }
        }
    }

    private void adicionarItem() {
        try {
            BigDecimal valor = FuncoesUteis.strToBigDecimal(txtValorUnitario.getText());
            ItemOrcamento item = new ItemOrcamento(txtDescricao.getText(), (Integer) spQuantidade.getValue(), valor);
            itens.add(item);
            adicionarLinhaItem(item);
            txtDescricao.setText("");
            txtValorUnitario.setText("");
            spQuantidade.setValue(1);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no item", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarLinhaItem(ItemOrcamento item) {
        modeloItens.addRow(new Object[]{
            item.getDescricaoProcedimento(),
            item.getQuantidade(),
            FuncoesUteis.moeda(item.getValorUnitario()),
            FuncoesUteis.moeda(item.getValorTotal())
        });
    }

    private void removerItemSelecionado() {
        int linha = tabelaItens.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.");
            return;
        }
        int modelo = tabelaItens.convertRowIndexToModel(linha);
        itens.remove(modelo);
        modeloItens.removeRow(modelo);
    }

    private void salvar() {
        try {
            boolean novo = orcamento == null;
            Usuario usuario = gerenciador.criarUsuarioPadraoSeNecessario();
            if (novo) {
                orcamento = new Orcamento();
            }

            orcamento.setPaciente((Paciente) cbPaciente.getSelectedItem());
            orcamento.setUsuario(usuario == null ? orcamento.getUsuario() : usuario);
            orcamento.setStatus((StatusOrcamento) cbStatus.getSelectedItem());
            orcamento.setObservacoes(textoOuNull(txtObservacoes.getText()));
            orcamento.setDataConsulta(parseDataConsulta());
            orcamento.setProcedimentoConsulta(textoOuNull(txtProcedimentoConsulta.getText()));
            orcamento.setObservacoesConsulta(textoOuNull(txtObservacoesConsulta.getText()));
            orcamento.getItens().clear();
            for (ItemOrcamento item : itens) {
                orcamento.adicionarItem(item);
            }

            if (novo) {
                gerenciador.inserirOrcamento(orcamento);
            } else {
                gerenciador.alterarOrcamento(orcamento);
            }
            salvou = true;
            JOptionPane.showMessageDialog(this, novo ? "Orcamento salvo com sucesso." : "Orcamento atualizado com sucesso.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao salvar orcamento", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Date parseDataConsulta() throws ParseException {
        String data = txtDataConsulta.getText();
        String hora = txtHoraConsulta.getText();
        boolean temData = data != null && !FuncoesUteis.somenteNumeros(data).isBlank();
        boolean temHora = hora != null && !FuncoesUteis.somenteNumeros(hora).isBlank();
        if (!temData && !temHora) {
            return null;
        }
        if (!temData || !temHora) {
            throw new IllegalArgumentException("Informe data e hora para marcar a consulta.");
        }
        return FORMATO_CONSULTA.parse(data + " " + hora);
    }

    private JTextField criarCampoMascara(String mascara) {
        try {
            return new javax.swing.JFormattedTextField(new MaskFormatter(mascara));
        } catch (ParseException ex) {
            return new JTextField(10);
        }
    }

    private String textoOuNull(String texto) {
        return texto == null || texto.trim().isBlank() ? null : texto.trim();
    }

    public boolean isSalvou() { return salvou; }
    public Orcamento getOrcamento() { return orcamento; }
}
