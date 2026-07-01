package dao;

import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class GenericDAO {

    public void inserir(Object obj) throws HibernateException {
        executarTransacao(session -> session.save(obj));
    }

    public void alterar(Object obj) throws HibernateException {
        executarTransacao(session -> session.update(obj));
    }

    public void salvarOuAlterar(Object obj) throws HibernateException {
        executarTransacao(session -> session.saveOrUpdate(obj));
    }

    public void excluir(Object obj) throws HibernateException {
        executarTransacao(session -> session.delete(obj));
    }

    public <T> List<T> listar(Class<T> classe) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();
            CriteriaQuery<T> consulta = sessao.getCriteriaBuilder().createQuery(classe);
            Root<T> root = consulta.from(classe);
            consulta.select(root);
            List<T> lista = sessao.createQuery(consulta).getResultList();
            sessao.getTransaction().commit();
            return lista;
        } catch (HibernateException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) {
                sessao.getTransaction().rollback();
            }
            throw ex;
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public <T> T get(Class<T> classe, Long id) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();
            T obj = sessao.get(classe, id);
            sessao.getTransaction().commit();
            return obj;
        } catch (HibernateException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) {
                sessao.getTransaction().rollback();
            }
            throw ex;
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    private void executarTransacao(OperacaoHibernate operacao) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();
            operacao.executar(sessao);
            sessao.getTransaction().commit();
        } catch (HibernateException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) {
                sessao.getTransaction().rollback();
            }
            throw ex;
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    @FunctionalInterface
    private interface OperacaoHibernate {
        void executar(Session session);
    }
}
