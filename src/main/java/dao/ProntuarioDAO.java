package dao;

import java.util.Date;
import java.util.List;
import model.Atendimento;
import model.HistoricoAlteracao;
import model.Paciente;
import model.Prontuario;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class ProntuarioDAO extends GenericDAO {

    public Prontuario buscarPorPaciente(Long pacienteId) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            List<Prontuario> lista = sessao.createQuery(
                    "select p from Prontuario p join fetch p.paciente where p.paciente.id = :pacienteId",
                    Prontuario.class)
                    .setParameter("pacienteId", pacienteId)
                    .setMaxResults(1)
                    .getResultList();
            return lista.isEmpty() ? null : lista.get(0);
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public Prontuario obterOuCriar(Paciente paciente) throws HibernateException {
        if (paciente == null || paciente.getId() == null) {
            throw new IllegalArgumentException("Paciente salvo e obrigatorio para abrir prontuario.");
        }
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            sessao.beginTransaction();
            Paciente gerenciado = sessao.get(Paciente.class, paciente.getId());
            if (gerenciado == null) {
                throw new IllegalArgumentException("Paciente nao encontrado.");
            }
            Prontuario prontuario = gerenciado.getProntuario();
            if (prontuario == null) {
                prontuario = new Prontuario(new Date(), "Prontuario aberto automaticamente no cadastro do paciente.");
                prontuario.setPaciente(gerenciado);
                gerenciado.setProntuario(prontuario);
                sessao.save(prontuario);
            }
            sessao.getTransaction().commit();
            return prontuario;
        } catch (RuntimeException ex) {
            if (sessao != null && sessao.getTransaction().isActive()) {
                sessao.getTransaction().rollback();
            }
            throw ex;
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public List<Atendimento> listarAtendimentos(Long prontuarioId) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            return sessao.createQuery(
                    "from Atendimento a where a.prontuario.id = :prontuarioId order by a.dataAtendimento desc, a.id desc",
                    Atendimento.class)
                    .setParameter("prontuarioId", prontuarioId)
                    .getResultList();
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public List<HistoricoAlteracao> listarHistorico(Long prontuarioId) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            return sessao.createQuery(
                    "from HistoricoAlteracao h where h.prontuario.id = :prontuarioId order by h.dataAlteracao desc, h.id desc",
                    HistoricoAlteracao.class)
                    .setParameter("prontuarioId", prontuarioId)
                    .getResultList();
        } finally {
            if (sessao != null) sessao.close();
        }
    }
}
