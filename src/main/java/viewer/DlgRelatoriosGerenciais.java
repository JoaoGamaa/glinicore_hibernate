package viewer;

import controller.GerenciadorDominio;
import controller.GerenciadorRelatorios;
import controller.relatorio.RelatorioGerencialDTO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DlgRelatoriosGerenciais extends JDialog {

    private final GerenciadorDominio gerenciadorDominio;
    private final GerenciadorRelatorios gerenciadorRelatorios;
    private final JComboBox<String> cbRelatorio = new JComboBox<>(new String[]{
        "Pacientes por cidade",
        "Pacientes por bairro",
        "Orcamentos por status",
        "Valor total de orcamentos por paciente",
        "Consentimentos por mes",
        "Anamneses por paciente"
    });
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Descricao", "Quantidade", "Valor total"}, 0);
    private final JTable tabela = new JTable(modelo);
    private List<RelatorioGerencialDTO> dadosAtuais = new ArrayList<>();

    public DlgRelatoriosGerenciais(Window owner, GerenciadorDominio gerenciadorDominio,
            GerenciadorRelatorios gerenciadorRelatorios) {
        super(owner, "Relatorios Gerenciais", ModalityType.APPLICATION_MODAL);
        this.gerenciadorDominio = gerenciadorDominio;
        this.gerenciadorRelatorios = gerenciadorRelatorios;
        montarTela();
        setSize(720, 460);
        setLocationRelativeTo(owner);
        carregarDados();
    }

    private void montarTela() {
        setLayout(new BorderLayout(10, 10));
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Relatorio"));
        topo.add(cbRelatorio);

        JButton btnExecutar = new JButton("Executar");
        btnExecutar.addActionListener(e -> carregarDados());
        topo.add(btnExecutar);

        JButton btnGerar = new JButton("Gerar Relatorio");
        btnGerar.addActionListener(e -> gerarRelatorio());
        topo.add(btnGerar);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    private void carregarDados() {
        try {
            List<Object[]> linhas;
            switch (cbRelatorio.getSelectedIndex()) {
                case 1:
                    linhas = gerenciadorDominio.contarPacientesPorBairro();
                    break;
                case 2:
                    linhas = gerenciadorDominio.contarOrcamentosPorStatus();
                    break;
                case 3:
                    linhas = gerenciadorDominio.somarOrcamentosPorPaciente();
                    break;
                case 4:
                    linhas = gerenciadorDominio.contarConsentimentosPorMes();
                    break;
                case 5:
                    linhas = gerenciadorDominio.contarAnamnesesPorPaciente();
                    break;
                default:
                    linhas = gerenciadorDominio.contarPacientesPorCidade();
                    break;
            }
            preencherTabela(linhas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao carregar relatorio", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherTabela(List<Object[]> linhas) {
        modelo.setRowCount(0);
        dadosAtuais = new ArrayList<>();
        for (Object[] linha : linhas) {
            String descricao = linha[0] == null ? "" : String.valueOf(linha[0]);
            Long quantidade = linha[1] instanceof Number ? ((Number) linha[1]).longValue() : null;
            BigDecimal valor = linha[2] instanceof BigDecimal ? (BigDecimal) linha[2] : null;
            RelatorioGerencialDTO dto = new RelatorioGerencialDTO(descricao, quantidade, valor);
            dadosAtuais.add(dto);
            modelo.addRow(new Object[]{dto.getDescricao(), dto.getQuantidade(), dto.getValorTotalFormatado()});
        }
    }

    private void gerarRelatorio() {
        if (dadosAtuais == null || dadosAtuais.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nao ha dados para gerar o relatorio.");
            return;
        }
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO", cbRelatorio.getSelectedItem().toString());
        gerenciadorRelatorios.relComListaParametros(dadosAtuais, "relGerencial", parametros);
    }
}
