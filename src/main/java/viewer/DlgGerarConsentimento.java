package viewer;

import controller.GerenciadorDominio;
import java.awt.BorderLayout;
import javax.swing.*;
import model.ConsentimentoTratamento;
import model.Orcamento;

public class DlgGerarConsentimento extends JDialog {

    private final GerenciadorDominio gerenciador;
    private boolean salvou;
    private ConsentimentoTratamento consentimento;
    private final JComboBox<Orcamento> cbOrcamento = new JComboBox<>();
    private final JTextField txtAssinatura = new JTextField(30);
    private final JTextArea txtTermo = new JTextArea(12, 60);

    public DlgGerarConsentimento(java.awt.Window owner, GerenciadorDominio gerenciador) {
        super(owner, "Novo Consentimento", ModalityType.APPLICATION_MODAL);
        this.gerenciador = gerenciador;
        montarTela();
        carregarOrcamentos();
        pack();
        setLocationRelativeTo(owner);
    }

    private void montarTela() {
        setLayout(new BorderLayout(8, 8));
        JPanel topo = new JPanel(new java.awt.GridLayout(2, 2, 8, 8));
        topo.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        topo.add(new JLabel("Orçamento"));
        topo.add(cbOrcamento);
        topo.add(new JLabel("Assinatura digital / nome do paciente"));
        topo.add(txtAssinatura);
        txtTermo.setText("Declaro que recebi explicações sobre o tratamento odontológico proposto, riscos, benefícios, alternativas, valores e condições do orçamento. Autorizo a execução do tratamento conforme o planejamento apresentado.");
        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(txtTermo), BorderLayout.CENTER);
        JPanel botoes = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnSalvar = new JButton("Gerar consentimento");
        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvar());
        botoes.add(btnCancelar);
        botoes.add(btnSalvar);
        add(botoes, BorderLayout.SOUTH);
    }

    private void carregarOrcamentos() {
        cbOrcamento.removeAllItems();
        for (Orcamento o : gerenciador.listarOrcamentosSeguro()) {
            cbOrcamento.addItem(o);
        }
        cbOrcamento.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Orcamento) {
                    Orcamento o = (Orcamento) value;
                    setText("#" + o.getId() + " - " + o.getPaciente() + " - " + o.getValorTotalFormatado());
                }
                return this;
            }
        });
    }

    private void salvar() {
        try {
            consentimento = gerenciador.gerarConsentimento((Orcamento) cbOrcamento.getSelectedItem(), txtAssinatura.getText(), txtTermo.getText());
            salvou = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao gerar consentimento", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSalvou() { return salvou; }
    public ConsentimentoTratamento getConsentimento() { return consentimento; }
}
