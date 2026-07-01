package controller;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.ConsentimentoTratamento;

public class TableModelConsentimento extends AbstractTableModel {

    private List<ConsentimentoTratamento> lista = new ArrayList<>();
    private final String[] colunas = {"Paciente", "Orçamento", "Data", "Assinatura"};

    @Override
    public int getRowCount() { return lista.size(); }

    @Override
    public int getColumnCount() { return colunas.length; }

    @Override
    public String getColumnName(int column) { return colunas[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ConsentimentoTratamento c = lista.get(rowIndex);
        switch (columnIndex) {
            case 0: return c.getOrcamento() == null ? "" : c.getOrcamento().getPaciente();
            case 1: return c.getOrcamento() == null ? "" : c.getOrcamento().getId();
            case 2: return c.getDataAssinaturaFormatada();
            case 3: return c.getAssinaturaDigital();
            default: return null;
        }
    }

    public ConsentimentoTratamento getItem(int rowIndex) { return lista.get(rowIndex); }

    public void setLista(List<ConsentimentoTratamento> novaLista) {
        lista = novaLista == null ? new ArrayList<>() : new ArrayList<>(novaLista);
        fireTableDataChanged();
    }
}
