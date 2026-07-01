package controller;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.Orcamento;

public class TableModelOrcamento extends AbstractTableModel {

    private List<Orcamento> lista = new ArrayList<>();
    private final String[] colunas = {"ID", "Paciente", "Data", "Consulta", "Valor", "Status"};

    @Override
    public int getRowCount() { return lista.size(); }

    @Override
    public int getColumnCount() { return colunas.length; }

    @Override
    public String getColumnName(int column) { return colunas[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Orcamento o = lista.get(rowIndex);
        switch (columnIndex) {
            case 0: return o.getId();
            case 1: return o.getPaciente();
            case 2: return o.getDataCriacaoFormatada();
            case 3: return o.getDataConsultaFormatada();
            case 4: return o.getValorTotalFormatado();
            case 5: return o.getStatus();
            default: return null;
        }
    }

    public Orcamento getItem(int rowIndex) { return lista.get(rowIndex); }

    public void setLista(List<Orcamento> novaLista) {
        lista = novaLista == null ? new ArrayList<>() : new ArrayList<>(novaLista);
        fireTableDataChanged();
    }

    public List<Orcamento> getLista() { return lista; }
}
