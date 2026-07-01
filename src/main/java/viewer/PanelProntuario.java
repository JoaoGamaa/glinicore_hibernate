package viewer;

import controller.GerenciadorDominio;
import controller.GerInterGrafica;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Anamnese;
import model.Atendimento;
import model.Odontograma;
import model.Orcamento;
import model.Paciente;
import model.Prontuario;

public class PanelProntuario extends JPanel {

    private final GerenciadorDominio gerenciador;
    private final Paciente pacienteInicial;
    private final JComboBox<Paciente> cbPaciente = new JComboBox<>();
    private final JLabel lblData = new JLabel("-");
    private final JTextArea txtObservacoes = new JTextArea(6, 40);
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"Tipo", "Data", "Descrição"}, 0);
    private final JTable tabela = new JTable(modelo);

    public PanelProntuario() {
        this(GerInterGrafica.getInstance().getGerenciadorDominio());
    }

    public PanelProntuario(GerenciadorDominio gerenciador) {
        this(gerenciador, null);
    }

    public PanelProntuario(GerenciadorDominio gerenciador, Paciente pacienteInicial) {
        this.gerenciador = gerenciador;
        this.pacienteInicial = pacienteInicial;
        setBackground(java.awt.Color.WHITE);
        setPreferredSize(new java.awt.Dimension(760, 690));
        montarTela();
        carregarPacientes();
    }

    private void montarTela() {
        setLayout(new BorderLayout(12, 12));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(java.awt.Color.WHITE);
        JLabel titulo = new JLabel("Prontuários");
        titulo.setFont(new java.awt.Font("Yu Gothic UI Semibold", java.awt.Font.BOLD, 36));
        header.setBorder(BorderFactory.createEmptyBorder(18, 40, 0, 40));
        header.add(titulo, BorderLayout.WEST);

        JPanel topo = new JPanel(new GridLayout(2, 2, 8, 8));
        topo.setBackground(java.awt.Color.WHITE);
        topo.setBorder(BorderFactory.createEmptyBorder(10, 40, 0, 40));
        topo.add(new JLabel("Selecionar paciente"));
        topo.add(cbPaciente);
        topo.add(new JLabel("Data de abertura"));
        topo.add(lblData);
        cbPaciente.addActionListener(e -> carregarProntuarioSelecionado());

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        txtObservacoes.setBorder(BorderFactory.createTitledBorder("Observações gerais"));
        centro.add(new JScrollPane(txtObservacoes), BorderLayout.NORTH);
        centro.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel botoes = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        botoes.setBackground(java.awt.Color.WHITE);
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));
        JButton btnRelatorio = new JButton("Gerar Relatorio do Prontuario");
        btnRelatorio.addActionListener(e -> gerarRelatorioProntuario());
        JButton btnSalvarObs = new JButton("Salvar observações");
        btnSalvarObs.addActionListener(e -> salvarObservacoes());
        botoes.add(btnRelatorio);
        botoes.add(btnSalvarObs);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(java.awt.Color.WHITE);
        norte.add(header, BorderLayout.NORTH);
        norte.add(topo, BorderLayout.CENTER);

        add(norte, BorderLayout.PAGE_START);
        add(centro, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void carregarPacientes() {
        cbPaciente.removeAllItems();
        List<Paciente> pacientes = gerenciador.listarPacientesSeguro();
        for (Paciente p : pacientes) {
            cbPaciente.addItem(p);
        }
        selecionarPacienteInicial();
        carregarProntuarioSelecionado();
    }

    private void selecionarPacienteInicial() {
        if (pacienteInicial == null || pacienteInicial.getId() == null) {
            return;
        }
        for (int i = 0; i < cbPaciente.getItemCount(); i++) {
            Paciente item = cbPaciente.getItemAt(i);
            if (item != null && pacienteInicial.getId().equals(item.getId())) {
                cbPaciente.setSelectedIndex(i);
                return;
            }
        }
    }

    private void carregarProntuarioSelecionado() {
        modelo.setRowCount(0);
        Paciente paciente = (Paciente) cbPaciente.getSelectedItem();
        if (paciente == null) {
            lblData.setText("-");
            txtObservacoes.setText("");
            return;
        }
        try {
            Prontuario prontuario = gerenciador.obterOuCriarProntuario(paciente);
            lblData.setText(prontuario.getDataAberturaFormatada());
            txtObservacoes.setText(prontuario.getObservacoesGerais());
            for (Anamnese anamnese : gerenciador.listarAnamnesesPaciente(paciente)) {
                modelo.addRow(new Object[]{"Anamnese", anamnese.getDataRegistroFormatada(), resumoAnamnese(anamnese)});
            }
            for (Odontograma odontograma : gerenciador.listarOdontogramasPaciente(paciente)) {
                modelo.addRow(new Object[]{"Odontograma", odontograma.getDataRegistroFormatada(), odontograma.getObservacoes()});
            }
            for (Atendimento atendimento : gerenciador.listarAtendimentosPaciente(paciente)) {
                modelo.addRow(new Object[]{"Atendimento", atendimento.getDataAtendimentoFormatada(), atendimento.getProcedimentoRealizado()});
            }
            for (Orcamento orcamento : gerenciador.listarOrcamentosPaciente(paciente)) {
                modelo.addRow(new Object[]{"Orcamento", orcamento.getDataCriacaoFormatada(), orcamento.getValorTotalFormatado() + " - " + orcamento.getStatus()});
            }
        } catch (Throwable ex) {
            lblData.setText("Banco não conectado");
            txtObservacoes.setText("Configure o banco no hibernate.cfg.xml e rode o projeto novamente.");
        }
    }

    private void salvarObservacoes() {
        try {
            Paciente paciente = (Paciente) cbPaciente.getSelectedItem();
            if (paciente == null) return;
            Prontuario prontuario = gerenciador.obterOuCriarProntuario(paciente);
            prontuario.setObservacoesGerais(txtObservacoes.getText());
            gerenciador.salvarOuAlterar(prontuario);
            JOptionPane.showMessageDialog(this, "Observações salvas.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarRelatorioProntuario() {
        Paciente paciente = (Paciente) cbPaciente.getSelectedItem();
        if (paciente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um paciente para gerar o relatorio do prontuario.");
            return;
        }
        try {
            GerInterGrafica.getInstance().getGerRelatorios().relComListaParametros(
                    gerenciador.montarProntuarioParaRelatorio(paciente),
                    "relProntuarioPaciente",
                    gerenciador.parametrosProntuarioPaciente(paciente));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao gerar relatorio", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String resumoAnamnese(Anamnese anamnese) {
        if (anamnese.getHistoriaDoencaAtual() != null && !anamnese.getHistoriaDoencaAtual().isBlank()) {
            return anamnese.getHistoriaDoencaAtual();
        }
        return anamnese.getObservacoes();
    }
}
