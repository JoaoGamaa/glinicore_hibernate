package viewer;

import controller.GerenciadorDominio;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import model.Paciente;

public class DlgCadAgendamento extends JDialog {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final GerenciadorDominio gerenciador;
    private final JComboBox<Paciente> cmbPaciente = new JComboBox<>();
    private final JTextField txtData = new JTextField(10);
    private final JTextField txtHora = new JTextField(8);
    private final JTextField txtProcedimento = new JTextField(25);
    private final JTextField txtProfissional = new JTextField(20);
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Agendado", "Confirmado"});
    private final JTextArea txtObservacoes = new JTextArea(4, 25);
    private boolean salvo;

    public DlgCadAgendamento(Window owner, GerenciadorDominio gerenciador) {
        super(owner, "Novo Agendamento", ModalityType.APPLICATION_MODAL);
        this.gerenciador = gerenciador;
        montarTela();
        carregarPacientes();
        pack();
        setMinimumSize(new Dimension(520, 360));
        setLocationRelativeTo(owner);
    }

    public boolean isSalvo() {
        return salvo;
    }

    private void montarTela() {
        JPanel conteudo = new JPanel(new GridBagLayout());
        conteudo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        adicionarCampo(conteudo, c, 0, "Paciente", cmbPaciente);
        adicionarCampo(conteudo, c, 1, "Data", txtData);
        adicionarCampo(conteudo, c, 2, "Hora", txtHora);
        adicionarCampo(conteudo, c, 3, "Procedimento", txtProcedimento);
        adicionarCampo(conteudo, c, 4, "Profissional", txtProfissional);
        adicionarCampo(conteudo, c, 5, "Status", cmbStatus);

        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0;
        conteudo.add(new JLabel("Observacoes"), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        conteudo.add(new JScrollPane(txtObservacoes), c);

        txtData.setText(LocalDate.now().plusDays(1).format(FORMATO_DATA));
        txtHora.setText("08:00");

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel();
        botoes.add(btnSalvar);
        botoes.add(btnCancelar);

        add(conteudo, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints c, int linha, String rotulo,
            java.awt.Component campo) {
        c.gridx = 0;
        c.gridy = linha;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        painel.add(new JLabel(rotulo), c);
        c.gridx = 1;
        c.weightx = 1;
        painel.add(campo, c);
    }

    private void carregarPacientes() {
        List<Paciente> pacientes = gerenciador.listarPacientesSeguro();
        for (Paciente paciente : pacientes) {
            cmbPaciente.addItem(paciente);
        }
    }

    private void salvar() {
        try {
            Paciente paciente = (Paciente) cmbPaciente.getSelectedItem();
            LocalDate data = LocalDate.parse(txtData.getText().trim(), FORMATO_DATA);
            LocalTime hora = LocalTime.parse(txtHora.getText().trim(), FORMATO_HORA);
            gerenciador.inserirAgendamento(paciente, data, hora, txtProcedimento.getText(),
                    txtProfissional.getText(), (String) cmbStatus.getSelectedItem(), txtObservacoes.getText());
            salvo = true;
            JOptionPane.showMessageDialog(this, "Agendamento salvo com sucesso.");
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Informe data no formato dd/MM/yyyy e hora no formato HH:mm.",
                    "Agendamento", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Agendamento", JOptionPane.ERROR_MESSAGE);
        }
    }
}
