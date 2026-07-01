package dao;

import java.time.LocalDate;
import java.util.List;
import model.Agendamento;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class AgendamentoDAO extends GenericDAO {

    public List<Agendamento> listarProximosAgendamentos() throws HibernateException {
        Session sessao = null;
        try {
            sessao = ConexaoHibernate.getSession();
            return sessao.createQuery(
                    "select a from Agendamento a join fetch a.paciente "
                    + "where a.data >= :hoje and (a.status is null or lower(a.status) <> 'cancelado') "
                    + "order by a.data asc, a.hora asc",
                    Agendamento.class)
                    .setParameter("hoje", LocalDate.now())
                    .setMaxResults(10)
                    .getResultList();
        } finally {
            if (sessao != null) {
                sessao.close();
            }
        }
    }
}
