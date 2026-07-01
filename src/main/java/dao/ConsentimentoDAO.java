package dao;

import java.util.List;
import model.ConsentimentoTratamento;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class ConsentimentoDAO extends GenericDAO {

    public List<ConsentimentoTratamento> listarOrdenadoPorData() throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            return sessao.createQuery(
                    "select c from ConsentimentoTratamento c join fetch c.orcamento o join fetch o.paciente order by c.dataAssinatura desc, c.id desc",
                    ConsentimentoTratamento.class)
                    .getResultList();
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public ConsentimentoTratamento buscarPorOrcamento(Long orcamentoId) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            List<ConsentimentoTratamento> lista = sessao.createQuery(
                    "select c from ConsentimentoTratamento c join fetch c.orcamento o join fetch o.paciente where o.id = :orcamentoId",
                    ConsentimentoTratamento.class)
                    .setParameter("orcamentoId", orcamentoId)
                    .setMaxResults(1)
                    .getResultList();
            return lista.isEmpty() ? null : lista.get(0);
        } finally {
            if (sessao != null) sessao.close();
        }
    }
}
