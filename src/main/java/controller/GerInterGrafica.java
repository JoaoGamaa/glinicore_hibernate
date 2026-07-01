package controller;

import java.awt.Window;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.Paciente;
import viewer.Dashboard;
import viewer.DlgCadOrcamento;
import viewer.DlgCadPaciente;
import viewer.DlgRelatoriosGerenciais;
import viewer.DlgGerarConsentimento;
import viewer.PanelAnamnese;
import viewer.PanelConsentimento;
import viewer.PanelOdontograma;
import viewer.PanelOrcamento;
import viewer.PanelPacientes;
import viewer.PanelProntuario;
import viewer.Panelinicio;

public final class GerInterGrafica {

    private static final GerInterGrafica INSTANCE = new GerInterGrafica();

    private Dashboard dashboard;
    private final GerenciadorDominio gerenciadorDominio;
    private final GerenciadorRelatorios gerenciadorRelatorios;

    private GerInterGrafica() {
        gerenciadorDominio = new GerenciadorDominio();
        gerenciadorRelatorios = new GerenciadorRelatorios();
    }

    public static GerInterGrafica getInstance() {
        return INSTANCE;
    }

    public static GerInterGrafica getMyInstance() {
        return INSTANCE;
    }

    public GerenciadorDominio getGerenciadorDominio() {
        return gerenciadorDominio;
    }

    public GerenciadorRelatorios getGerenciadorRelatorios() {
        return gerenciadorRelatorios;
    }

    public GerenciadorRelatorios getGerRelatorios() {
        return gerenciadorRelatorios;
    }

    public void iniciar() {
        configurarInterface();
        prepararBanco();
        SwingUtilities.invokeLater(this::abrirDashboard);
    }

    public void abrirDashboard() {
        if (dashboard == null) {
            dashboard = new Dashboard(this);
            dashboard.setLocationRelativeTo(null);
        }
        dashboard.setVisible(true);
        dashboard.toFront();
    }

    public void abrirInicio() {
        trocarPainel(new Panelinicio(gerenciadorDominio));
    }

    public void abrirPacientes() {
        trocarPainel(new PanelPacientes(gerenciadorDominio));
    }

    public void abrirAnamnese() {
        trocarPainel(new PanelAnamnese(gerenciadorDominio));
    }

    public void abrirAnamnese(Paciente paciente) {
        trocarPainel(new PanelAnamnese(gerenciadorDominio, paciente));
    }

    public void abrirProntuario() {
        trocarPainel(new PanelProntuario(gerenciadorDominio));
    }

    public void abrirProntuario(Paciente paciente) {
        trocarPainel(new PanelProntuario(gerenciadorDominio, paciente));
    }

    public void abrirOdontograma() {
        trocarPainel(new PanelOdontograma(gerenciadorDominio));
    }

    public void abrirOdontograma(Paciente paciente) {
        trocarPainel(new PanelOdontograma(gerenciadorDominio, paciente));
    }

    public void abrirOrcamento() {
        trocarPainel(new PanelOrcamento(gerenciadorDominio));
    }

    public void abrirConsentimento() {
        trocarPainel(new PanelConsentimento(gerenciadorDominio));
    }

    public DlgCadPaciente abrirCadastroPaciente(Window owner, Paciente paciente) {
        DlgCadPaciente dlg = new DlgCadPaciente(owner, gerenciadorDominio, paciente);
        dlg.setVisible(true);
        return dlg;
    }

    public DlgCadOrcamento abrirCadastroOrcamento(Window owner) {
        DlgCadOrcamento dlg = new DlgCadOrcamento(owner, gerenciadorDominio);
        dlg.setVisible(true);
        return dlg;
    }

    public DlgCadOrcamento abrirCadastroOrcamento(Window owner, model.Orcamento orcamento) {
        DlgCadOrcamento dlg = new DlgCadOrcamento(owner, gerenciadorDominio, orcamento);
        dlg.setVisible(true);
        return dlg;
    }

    public DlgGerarConsentimento abrirCadastroConsentimento(Window owner) {
        DlgGerarConsentimento dlg = new DlgGerarConsentimento(owner, gerenciadorDominio);
        dlg.setVisible(true);
        return dlg;
    }

    public void abrirRelatoriosGerenciais(Window owner) {
        DlgRelatoriosGerenciais dlg = new DlgRelatoriosGerenciais(owner, gerenciadorDominio, gerenciadorRelatorios);
        dlg.setVisible(true);
    }

    public void trocarPainel(JPanel painel) {
        if (dashboard == null) {
            abrirDashboard();
        }
        dashboard.exibirPainel(painel);
    }

    private void configurarInterface() {
        try {
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("OptionPane.yesButtonText", "Sim");
            UIManager.put("OptionPane.noButtonText", "Nao");
            UIManager.put("OptionPane.cancelButtonText", "Cancelar");
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (RuntimeException ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception fallbackEx) {
                JOptionPane.showMessageDialog(null, fallbackEx.getMessage(), "Interface", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void prepararBanco() {
        try {
            DatabaseSeeder.executarSeNecessario(gerenciadorDominio);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(null,
                    "Nao foi possivel preparar o banco de dados.\n"
                    + "Verifique se o PostgreSQL esta aberto e se o banco configurado em ConexaoHibernate existe.\n\n"
                    + ex.getMessage(),
                    "Banco de dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        GerInterGrafica.getInstance().iniciar();
    }
}
