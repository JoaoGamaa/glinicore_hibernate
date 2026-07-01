package dao;

import java.util.List;
import model.Anamnese;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class AnamneseDAO extends GenericDAO {

    public List<Anamnese> listarPorPaciente(Long pacienteId) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            return sessao.createQuery(
                    "from Anamnese a where a.paciente.id = :pacienteId order by a.dataRegistro desc, a.id desc",
                    Anamnese.class)
                    .setParameter("pacienteId", pacienteId)
                    .getResultList();
        } finally {
            if (sessao != null) sessao.close();
        }
    }

    public Anamnese buscarUltimaPorPaciente(Long pacienteId) throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            List<Anamnese> lista = sessao.createQuery(
                    "from Anamnese a where a.paciente.id = :pacienteId order by a.dataRegistro desc, a.id desc",
                    Anamnese.class)
                    .setParameter("pacienteId", pacienteId)
                    .setMaxResults(1)
                    .getResultList();
            return lista.isEmpty() ? null : lista.get(0);
        } finally {
            if (sessao != null) sessao.close();
        }
    }
}
