package dao;

import java.util.List;
import model.Orcamento;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class OrcamentoDAO extends GenericDAO {

    public List<Orcamento> listarOrdenadoPorData() throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            return sessao.createQuery(
                    "from Orcamento o "
                    + "order by case when o.dataConsulta is null then 1 else 0 end, "
                    + "o.dataConsulta asc, o.dataCriacao desc, o.id desc",
                    Orcamento.class)
                    .getResultList();
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public List<Orcamento> listarPorPaciente(Long pacienteId) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            return sessao.createQuery(
                    "select distinct o from Orcamento o left join fetch o.itens where o.paciente.id = :pacienteId order by o.dataCriacao desc, o.id desc",
                    Orcamento.class)
                    .setParameter("pacienteId", pacienteId)
                    .getResultList();
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public Orcamento buscarComItens(Long id) throws HibernateException {
        if (id == null) return null;
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            List<Orcamento> lista = sessao.createQuery(
                    "select distinct o from Orcamento o left join fetch o.itens where o.id = :id",
                    Orcamento.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return lista.isEmpty() ? null : lista.get(0);
        } finally {
            if (sessao != null) sessao.close();
        }
    }
}
