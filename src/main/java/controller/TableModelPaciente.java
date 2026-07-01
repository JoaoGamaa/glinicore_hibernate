package controller;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.Paciente;

public class TableModelPaciente extends AbstractTableModel {

    private List<Paciente> lista = new ArrayList<>();
    private final String[] colunas = {"Paciente", "CPF", "Contato", "Cadastro", "Ativo"};

    @Override
    public int getRowCount() { return lista.size(); }

    @Override
    public int getColumnCount() { return colunas.length; }

    @Override
    public String getColumnName(int column) { return colunas[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Paciente p = lista.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getNome();
            case 1: return p.getCpf();
            case 2: return p.getTelefone();
            case 3: return p.getDataCadastroFormatada();
            case 4: return Boolean.TRUE.equals(p.getAtivo()) ? "Sim" : "Não";
            default: return null;
        }
    }

    public Paciente getItem(int rowIndex) { return lista.get(rowIndex); }

    public void setLista(List<Paciente> novaLista) {
        lista = novaLista == null ? new ArrayList<>() : new ArrayList<>(novaLista);
        fireTableDataChanged();
    }

    public List<Paciente> getLista() { return lista; }
}
