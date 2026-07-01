package dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.Paciente;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class PacienteDAO extends GenericDAO {

    public List<Paciente> listarOrdenadoPorNome() throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            return sessao.createQuery("from Paciente p order by lower(p.nome)", Paciente.class).getResultList();
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public Paciente buscarPorId(Long id) throws HibernateException {
        if (id == null) return null;
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            return sessao.get(Paciente.class, id);
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public Paciente buscarPorCpfExato(String cpf) throws HibernateException {
        if (cpf == null || cpf.isBlank()) return null;
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            List<Paciente> lista = sessao.createQuery("from Paciente p where p.cpf = :cpf", Paciente.class)
                    .setParameter("cpf", cpf)
                    .setMaxResults(1)
                    .getResultList();
            return lista.isEmpty() ? null : lista.get(0);
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public List<Paciente> pesquisarPorNome(String texto) throws HibernateException {
        return pesquisar(1, texto);
    }

    public List<Paciente> pesquisarPorCpf(String texto) throws HibernateException {
        return pesquisar(2, texto);
    }

    public List<Paciente> pesquisarPorTelefone(String texto) throws HibernateException {
        return pesquisar(3, texto);
    }

    public long contarAtivos() {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Long> consulta = builder.createQuery(Long.class);
            Root<Paciente> tabela = consulta.from(Paciente.class);
            consulta.select(builder.count(tabela)).where(builder.equal(tabela.get("ativo"), true));
            return sessao.createQuery(consulta).getSingleResult();
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    private List<Paciente> pesquisar(int tipo, String texto) throws HibernateException {
        List<Paciente> lista = new ArrayList<>();
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSessionFactory().openSession();
            sessao.beginTransaction();
            CriteriaBuilder builder = sessao.getCriteriaBuilder();
            CriteriaQuery<Paciente> consulta = builder.createQuery(Paciente.class);
            Root<Paciente> tabela = consulta.from(Paciente.class);
            String pesq = texto == null ? "" : texto.trim();
            Predicate restricao;
            switch (tipo) {
                case 2:
                    restricao = builder.like(tabela.get("cpf"), "%" + pesq + "%");
                    break;
                case 3:
                    restricao = builder.like(tabela.get("telefone"), "%" + pesq + "%");
                    break;
                default:
                    restricao = builder.like(builder.lower(tabela.get("nome")), "%" + pesq.toLowerCase() + "%");
                    break;
            }
            consulta.where(restricao);
            consulta.orderBy(builder.asc(tabela.get("nome")));
            lista = sessao.createQuery(consulta).getResultList();
            sessao.getTransaction().commit();
            return lista;
        } catch (HibernateException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) sessao.getTransaction().rollback();
            throw ex;
        } finally {
            if (sessao != null) sessao.close();
        }
    }
}
