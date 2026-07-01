package dao;

import java.util.List;
import model.Odontograma;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class OdontogramaDAO extends GenericDAO {

    public List<Odontograma> listarPorProntuario(Long prontuarioId) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            return sessao.createQuery(
                    "select distinct o from Odontograma o left join fetch o.dentes where o.prontuario.id = :prontuarioId order by o.dataRegistro desc, o.id desc",
                    Odontograma.class)
                    .setParameter("prontuarioId", prontuarioId)
                    .getResultList();
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public Odontograma buscarUltimoPorProntuario(Long prontuarioId) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            List<Long> ids = sessao.createQuery(
                    "select o.id from Odontograma o where o.prontuario.id = :prontuarioId order by o.dataRegistro desc, o.id desc",
                    Long.class)
                    .setParameter("prontuarioId", prontuarioId)
                    .setMaxResults(1)
                    .getResultList();
            if (ids.isEmpty()) {
                return null;
            }
            List<Odontograma> lista = sessao.createQuery(
                    "select distinct o from Odontograma o left join fetch o.dentes where o.id = :id",
                    Odontograma.class)
                    .setParameter("id", ids.get(0))
                    .getResultList();
            return lista.isEmpty() ? null : lista.get(0);
        } finally {
            if (sessao != null) sessao.close();
        }
    }
}
