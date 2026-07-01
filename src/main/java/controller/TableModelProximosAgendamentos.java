package controller;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.Agendamento;
import model.Paciente;

public class TableModelProximosAgendamentos extends AbstractTableModel {

    private List<Agendamento> lista = new ArrayList<>();
    private final String[] colunas = {"Data", "Hora", "Paciente", "Procedimento", "Profissional", "Status"};

    @Override
    public int getRowCount() { return lista.size(); }

    @Override
    public int getColumnCount() { return colunas.length; }

    @Override
    public String getColumnName(int column) { return colunas[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Agendamento agendamento = lista.get(rowIndex);
        Paciente paciente = agendamento.getPaciente();
        switch (columnIndex) {
            case 0: return agendamento.getDataFormatada();
            case 1: return agendamento.getHoraFormatada();
            case 2: return paciente == null ? "" : paciente.getNome();
            case 3: return agendamento.getProcedimento();
            case 4: return agendamento.getProfissional();
            case 5: return agendamento.getStatus();
            default: return null;
        }
    }

    public Agendamento getItem(int rowIndex) {
        return lista.get(rowIndex);
    }

    public void setLista(List<Agendamento> novaLista) {
        lista = novaLista == null ? new ArrayList<>() : new ArrayList<>(novaLista);
        fireTableDataChanged();
    }
}
