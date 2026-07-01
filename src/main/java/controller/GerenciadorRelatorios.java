package controller;

import dao.ConexaoHibernate;
import java.awt.BorderLayout;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.hibernate.Session;

public class GerenciadorRelatorios {

    private static final String PASTA_RELATORIOS = "/relatorios/";

    public void relComLista(List<?> lista, String nomeRelatorio) {
        relComListaParametros(lista, nomeRelatorio, new HashMap<>());
    }

    public void relComListaParametros(List<?> lista, String nomeRelatorio, Map<String, Object> parametros) {
        if (lista == null || lista.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nao ha dados para gerar o relatorio.");
            return;
        }
        try {
            JRDataSource dataSource = new JRBeanCollectionDataSource(lista);
            JasperPrint print = JasperFillManager.fillReport(carregarRelatorio(nomeRelatorio),
                    parametrosSeguros(parametros), dataSource);
            abrirVisualizador(print);
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    public void relComConexao(String nomeRelatorio) {
        relComConexaoParametros(nomeRelatorio, new HashMap<>());
    }

    public void relComConexaoParametros(String nomeRelatorio, Map<String, Object> parametros) {
        try (Session session = ConexaoHibernate.getSessionFactory().openSession()) {
            Connection connection = session.doReturningWork(conn -> conn);
            JasperPrint print = JasperFillManager.fillReport(carregarRelatorio(nomeRelatorio),
                    parametrosSeguros(parametros), connection);
            abrirVisualizador(print);
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private JasperReport carregarRelatorio(String nomeRelatorio) throws JRException {
        String nomeBase = normalizarNome(nomeRelatorio);
        try (InputStream jasper = getClass().getResourceAsStream(PASTA_RELATORIOS + nomeBase + ".jasper")) {
            if (jasper != null) {
                return (JasperReport) JRLoader.loadObject(jasper);
            }
        } catch (Exception ex) {
            throw new JRException("Erro ao carregar arquivo compilado do relatorio.", ex);
        }

        InputStream jrxml = getClass().getResourceAsStream(PASTA_RELATORIOS + nomeBase + ".jrxml");
        if (jrxml == null) {
            throw new JRException("Arquivo de relatorio nao encontrado: " + nomeBase + ".jrxml");
        }
        return JasperCompileManager.compileReport(jrxml);
    }

    private String normalizarNome(String nomeRelatorio) {
        if (nomeRelatorio == null || nomeRelatorio.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do relatorio.");
        }
        String nome = nomeRelatorio.trim();
        if (nome.endsWith(".jasper")) {
            return nome.substring(0, nome.length() - ".jasper".length());
        }
        if (nome.endsWith(".jrxml")) {
            return nome.substring(0, nome.length() - ".jrxml".length());
        }
        return nome;
    }

    private Map<String, Object> parametrosSeguros(Map<String, Object> parametros) {
        Map<String, Object> seguros = parametros == null ? new HashMap<>() : new HashMap<>(parametros);
        seguros.putIfAbsent("SISTEMA", "Glinicore");
        return seguros;
    }

    private void abrirVisualizador(JasperPrint print) {
        JasperViewer jrViewer = new JasperViewer(print, false);
        JDialog viewer = new JDialog(new javax.swing.JFrame(), "Visualizacao do Relatorio", true);
        viewer.setLayout(new BorderLayout());
        viewer.setSize(900, 650);
        viewer.setLocationRelativeTo(null);
        viewer.getContentPane().add(jrViewer.getContentPane(), BorderLayout.CENTER);
        viewer.setVisible(true);
    }

    private void mostrarErro(Exception ex) {
        JOptionPane.showMessageDialog(null,
                "Erro ao gerar relatorio.\n" + ex.getMessage(),
                "Relatorio", JOptionPane.ERROR_MESSAGE);
    }
}
