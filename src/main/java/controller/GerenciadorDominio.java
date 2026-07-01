package controller;

import controller.relatorio.AnamneseRelatorioDTO;
import controller.relatorio.ConsentimentoRelatorioDTO;
import controller.relatorio.OdontogramaRelatorioDTO;
import controller.relatorio.OrcamentoRelatorioDTO;
import controller.relatorio.PacienteRelatorioDTO;
import controller.relatorio.ProntuarioRelatorioDTO;
import dao.AnamneseDAO;
import dao.ConsentimentoDAO;
import dao.GenericDAO;
import dao.OdontogramaDAO;
import dao.OrcamentoDAO;
import dao.PacienteDAO;
import dao.ProntuarioDAO;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import model.Anamnese;
import model.Atendimento;
import model.CondicaoDente;
import model.ConsentimentoTratamento;
import model.Dente;
import model.Endereco;
import model.EspecialidadeOdontologica;
import model.HistoricoAlteracao;
import model.ItemOrcamento;
import model.Odontograma;
import model.Orcamento;
import model.Paciente;
import model.Prontuario;
import model.TipoUsuario;
import model.Usuario;
import org.hibernate.HibernateException;

public class GerenciadorDominio {

    private final GenericDAO genDAO;
    private final PacienteDAO pacienteDAO;
    private final OrcamentoDAO orcamentoDAO;
    private final AnamneseDAO anamneseDAO;
    private final OdontogramaDAO odontogramaDAO;
    private final ProntuarioDAO prontuarioDAO;
    private final ConsentimentoDAO consentimentoDAO;
    private final CepService cepService;

    public GerenciadorDominio() {
        genDAO = new GenericDAO();
        pacienteDAO = new PacienteDAO();
        orcamentoDAO = new OrcamentoDAO();
        anamneseDAO = new AnamneseDAO();
        odontogramaDAO = new OdontogramaDAO();
        prontuarioDAO = new ProntuarioDAO();
        consentimentoDAO = new ConsentimentoDAO();
        cepService = new CepService();
    }

    public <T> List<T> listar(Class<T> classe) throws HibernateException {
        return genDAO.listar(classe);
    }

    public <T> List<T> listarSeguro(Class<T> classe) {
        try {
            return genDAO.listar(classe);
        } catch (Throwable ex) {
            System.err.println("Nao foi possivel listar " + classe.getSimpleName() + ": " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public void excluir(Object obj) throws HibernateException {
        genDAO.excluir(obj);
    }

    public void inserir(Object obj) throws HibernateException {
        genDAO.inserir(obj);
    }

    public void salvarOuAlterar(Object obj) throws HibernateException {
        genDAO.salvarOuAlterar(obj);
    }

    public List<Paciente> listarPacientes() throws HibernateException {
        return pacienteDAO.listarOrdenadoPorNome();
    }

    public List<Paciente> listarPacientesSeguro() {
        try {
            return listarPacientes();
        } catch (Throwable ex) {
            System.err.println("Nao foi possivel listar pacientes: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Paciente> pesquisarPacientes(String texto, int tipo) throws HibernateException {
        switch (tipo) {
            case 1: return pacienteDAO.pesquisarPorCpf(FuncoesUteis.somenteNumeros(texto));
            case 2: return pacienteDAO.pesquisarPorTelefone(texto);
            default: return pacienteDAO.pesquisarPorNome(texto);
        }
    }

    public Paciente inserirPaciente(String nome, String cpf, Date dataNascimento, String telefone,
            String email, Icon foto, String rua, String numero, String bairro, String cidade,
            String estado, String cep, String complemento) throws HibernateException {
        String cpfNormalizado = normalizarCpf(cpf);
        validarPaciente(nome, cpfNormalizado);
        verificarCpfUnico(cpfNormalizado, null);
        Endereco endereco = new Endereco(vazioParaNull(rua), vazioParaNull(numero), vazioParaNull(bairro),
                vazioParaNull(cidade), vazioParaNull(estado), vazioParaNull(cep), vazioParaNull(complemento));
        Paciente paciente = new Paciente(nome.trim(), cpfNormalizado, dataNascimento, vazioParaNull(telefone),
                vazioParaNull(email), FuncoesUteis.iconToBytes(foto), endereco);
        paciente.abrirProntuarioSeNecessario();
        genDAO.inserir(paciente);
        return paciente;
    }

    public Paciente inserirPaciente(Paciente paciente) throws HibernateException {
        if (paciente == null) {
            throw new IllegalArgumentException("Informe os dados do paciente.");
        }
        String cpfNormalizado = normalizarCpf(paciente.getCpf());
        validarPaciente(paciente.getNome(), cpfNormalizado);
        verificarCpfUnico(cpfNormalizado, null);
        paciente.setCpf(cpfNormalizado);
        paciente.abrirProntuarioSeNecessario();
        genDAO.inserir(paciente);
        return paciente;
    }

    public Paciente alterarPaciente(Paciente paciente, String nome, String cpf, Date dataNascimento,
            String telefone, String email, Icon foto, String rua, String numero, String bairro,
            String cidade, String estado, String cep, String complemento, Boolean ativo) throws HibernateException {
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um paciente para alterar.");
        }
        String cpfNormalizado = normalizarCpf(cpf);
        validarPaciente(nome, cpfNormalizado);
        verificarCpfUnico(cpfNormalizado, paciente.getId());
        paciente.setNome(nome.trim());
        paciente.setCpf(cpfNormalizado);
        paciente.setDataNascimento(dataNascimento);
        paciente.setTelefone(vazioParaNull(telefone));
        paciente.setEmail(vazioParaNull(email));
        paciente.setAtivo(ativo);
        if (foto != null) {
            paciente.setFotoIdentificacao(FuncoesUteis.iconToBytes(foto));
        }
        if (paciente.getEndereco() == null) {
            paciente.setEndereco(new Endereco());
        }
        paciente.getEndereco().setRua(vazioParaNull(rua));
        paciente.getEndereco().setNumero(vazioParaNull(numero));
        paciente.getEndereco().setBairro(vazioParaNull(bairro));
        paciente.getEndereco().setCidade(vazioParaNull(cidade));
        paciente.getEndereco().setEstado(vazioParaNull(estado));
        paciente.getEndereco().setCep(vazioParaNull(cep));
        paciente.getEndereco().setComplemento(vazioParaNull(complemento));
        paciente.abrirProntuarioSeNecessario();
        genDAO.salvarOuAlterar(paciente);
        return paciente;
    }

    public Paciente alterarPaciente(Paciente paciente) throws HibernateException {
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um paciente para alterar.");
        }
        String cpfNormalizado = normalizarCpf(paciente.getCpf());
        validarPaciente(paciente.getNome(), cpfNormalizado);
        verificarCpfUnico(cpfNormalizado, paciente.getId());
        paciente.setCpf(cpfNormalizado);
        paciente.abrirProntuarioSeNecessario();
        genDAO.salvarOuAlterar(paciente);
        return paciente;
    }

    public void excluirPaciente(Paciente paciente) throws HibernateException {
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um registro para excluir.");
        }
        try {
            genDAO.excluir(paciente);
        } catch (RuntimeException ex) {
            throw new IllegalStateException(
                    "Nao e possivel excluir este paciente porque existem registros vinculados.", ex);
        }
    }

    public Endereco buscarEnderecoPorCep(String cep) {
        return cepService.buscarEnderecoPorCep(cep);
    }

    public Anamnese inserirAnamnese(Paciente paciente, Boolean possuiAlergia, String alergias,
            Boolean usaMedicamento, String medicamentos, Boolean possuiDoenca, String doencas,
            String observacoes) throws HibernateException {
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um paciente para registrar a anamnese.");
        }
        if (textoVazio(alergias) && textoVazio(medicamentos) && textoVazio(doencas) && textoVazio(observacoes)) {
            throw new IllegalArgumentException("Preencha pelo menos uma informacao da anamnese.");
        }
        Anamnese anamnese = new Anamnese(possuiAlergia, vazioParaNull(alergias), usaMedicamento,
                vazioParaNull(medicamentos), possuiDoenca, vazioParaNull(doencas), vazioParaNull(observacoes));
        anamnese.setPaciente(paciente);
        genDAO.inserir(anamnese);
        registrarHistorico(paciente, null, "Anamnese registrada", "ANAMNESE");
        return anamnese;
    }

    public Anamnese inserirAnamnese(Anamnese anamnese) throws HibernateException {
        validarAnamnese(anamnese);
        genDAO.inserir(anamnese);
        registrarHistorico(anamnese.getPaciente(), null, "Anamnese registrada", "ANAMNESE");
        return anamnese;
    }

    public Anamnese inserirAnamneseClinica(Paciente paciente, String hda, String hpp,
            String historiaFisiologica, String historiaFamiliar) throws HibernateException {
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um paciente para registrar a anamnese.");
        }
        if (textoVazio(hda) && textoVazio(hpp) && textoVazio(historiaFisiologica) && textoVazio(historiaFamiliar)) {
            throw new IllegalArgumentException("Preencha pelo menos uma informacao da anamnese.");
        }
        Anamnese anamnese = new Anamnese(false, null, false, null, !textoVazio(hpp), vazioParaNull(hpp), vazioParaNull(hda));
        anamnese.setHistoriaDoencaAtual(vazioParaNull(hda));
        anamnese.setHistoriaPatologicaPregressa(vazioParaNull(hpp));
        anamnese.setHistoriaFisiologica(vazioParaNull(historiaFisiologica));
        anamnese.setHistoriaFamiliar(vazioParaNull(historiaFamiliar));
        anamnese.setPaciente(paciente);
        genDAO.inserir(anamnese);
        registrarHistorico(paciente, null, "Anamnese registrada", "ANAMNESE");
        return anamnese;
    }

    public Anamnese alterarAnamneseClinica(Anamnese anamnese, Paciente paciente, String hda, String hpp,
            String historiaFisiologica, String historiaFamiliar) throws HibernateException {
        if (anamnese == null) {
            throw new IllegalArgumentException("Selecione uma anamnese para editar.");
        }
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um paciente para editar a anamnese.");
        }
        if (textoVazio(hda) && textoVazio(hpp) && textoVazio(historiaFisiologica) && textoVazio(historiaFamiliar)) {
            throw new IllegalArgumentException("Preencha pelo menos uma informacao da anamnese.");
        }
        anamnese.setPaciente(paciente);
        anamnese.setHistoriaDoencaAtual(vazioParaNull(hda));
        anamnese.setHistoriaPatologicaPregressa(vazioParaNull(hpp));
        anamnese.setHistoriaFisiologica(vazioParaNull(historiaFisiologica));
        anamnese.setHistoriaFamiliar(vazioParaNull(historiaFamiliar));
        anamnese.setObservacoes(vazioParaNull(hda));
        anamnese.setDoencas(vazioParaNull(hpp));
        anamnese.setMedicamentos(vazioParaNull(historiaFisiologica));
        anamnese.setAlergias(vazioParaNull(historiaFamiliar));
        anamnese.setPossuiDoenca(!textoVazio(hpp));
        anamnese.setUsaMedicamento(!textoVazio(historiaFisiologica));
        anamnese.setPossuiAlergia(!textoVazio(historiaFamiliar));
        genDAO.salvarOuAlterar(anamnese);
        registrarHistorico(paciente, null, "Anamnese atualizada", "ANAMNESE");
        return anamnese;
    }

    public Anamnese alterarAnamnese(Anamnese anamnese) throws HibernateException {
        validarAnamnese(anamnese);
        genDAO.salvarOuAlterar(anamnese);
        registrarHistorico(anamnese.getPaciente(), null, "Anamnese atualizada", "ANAMNESE");
        return anamnese;
    }

    public void excluirAnamnese(Anamnese anamnese) throws HibernateException {
        if (anamnese == null) {
            throw new IllegalArgumentException("Selecione uma anamnese para excluir.");
        }
        genDAO.excluir(anamnese);
    }

    public Prontuario obterOuCriarProntuario(Paciente paciente) throws HibernateException {
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente e obrigatorio.");
        }
        return prontuarioDAO.obterOuCriar(paciente);
    }

    public Atendimento inserirAtendimento(Paciente paciente, Usuario usuario, EspecialidadeOdontologica especialidade,
            Date data, String descricao, String procedimento, String observacoes) throws HibernateException {
        if (textoVazio(descricao) && textoVazio(procedimento) && textoVazio(observacoes)) {
            throw new IllegalArgumentException("Preencha os dados do atendimento antes de salvar.");
        }
        Prontuario prontuario = obterOuCriarProntuario(paciente);
        Atendimento atendimento = new Atendimento(data, vazioParaNull(descricao), vazioParaNull(procedimento), vazioParaNull(observacoes));
        atendimento.setProntuario(prontuario);
        atendimento.setUsuario(usuario);
        atendimento.setEspecialidade(especialidade);
        genDAO.inserir(atendimento);
        registrarHistorico(paciente, usuario, "Atendimento registrado", "ATENDIMENTO");
        return atendimento;
    }

    public Odontograma inserirOdontograma(Paciente paciente, Map<Integer, CondicaoDente> dentes,
            String observacoes) throws HibernateException {
        return salvarOdontograma(paciente, dentes, observacoes);
    }

    public Odontograma inserirOdontograma(Odontograma odontograma) throws HibernateException {
        if (odontograma == null || odontograma.getProntuario() == null) {
            throw new IllegalArgumentException("Selecione um paciente antes de salvar o odontograma.");
        }
        genDAO.inserir(odontograma);
        return odontograma;
    }

    public Odontograma salvarOdontograma(Paciente paciente, Map<Integer, CondicaoDente> dentes,
            String observacoes) throws HibernateException {
        Prontuario prontuario = obterOuCriarProntuario(paciente);
        boolean possuiAlgumaInformacao = !textoVazio(observacoes)
                || (dentes != null && dentes.values().stream().anyMatch(cond -> cond != null && cond != CondicaoDente.NORMAL));
        if (!possuiAlgumaInformacao) {
            throw new IllegalArgumentException("Altere ao menos um dente ou informe uma observacao antes de salvar.");
        }

        Odontograma odontograma = odontogramaDAO.buscarUltimoPorProntuario(prontuario.getId());
        boolean novo = odontograma == null;
        if (novo) {
            odontograma = new Odontograma(vazioParaNull(observacoes));
            odontograma.setProntuario(prontuario);
        } else {
            odontograma.setObservacoes(vazioParaNull(observacoes));
            odontograma.getDentes().clear();
        }

        if (dentes != null) {
            for (Map.Entry<Integer, CondicaoDente> entrada : dentes.entrySet()) {
                odontograma.adicionarDente(new Dente(entrada.getKey(), entrada.getValue(), null));
            }
        }

        if (novo) {
            genDAO.inserir(odontograma);
            registrarHistorico(paciente, null, "Odontograma registrado", "ODONTOGRAMA");
        } else {
            genDAO.salvarOuAlterar(odontograma);
            registrarHistorico(paciente, null, "Odontograma atualizado", "ODONTOGRAMA");
        }
        return odontograma;
    }

    public Odontograma alterarOdontograma(Odontograma odontograma) throws HibernateException {
        if (odontograma == null) {
            throw new IllegalArgumentException("Selecione um odontograma para alterar.");
        }
        genDAO.salvarOuAlterar(odontograma);
        return odontograma;
    }

    public void excluirOdontograma(Odontograma odontograma) throws HibernateException {
        if (odontograma == null) {
            throw new IllegalArgumentException("Selecione um registro para excluir.");
        }
        genDAO.excluir(odontograma);
    }

    public Anamnese buscarUltimaAnamnesePaciente(Paciente paciente) throws HibernateException {
        if (paciente == null || paciente.getId() == null) return null;
        return anamneseDAO.buscarUltimaPorPaciente(paciente.getId());
    }

    public List<Anamnese> listarAnamnesesPaciente(Paciente paciente) throws HibernateException {
        if (paciente == null || paciente.getId() == null) return new ArrayList<>();
        return anamneseDAO.listarPorPaciente(paciente.getId());
    }

    public List<Anamnese> listarAnamnesesPorPaciente(Paciente paciente) throws HibernateException {
        return listarAnamnesesPaciente(paciente);
    }

    public Odontograma buscarUltimoOdontogramaPaciente(Paciente paciente) throws HibernateException {
        if (paciente == null || paciente.getId() == null) return null;
        Prontuario prontuario = obterOuCriarProntuario(paciente);
        return odontogramaDAO.buscarUltimoPorProntuario(prontuario.getId());
    }

    public List<Odontograma> listarOdontogramasPaciente(Paciente paciente) throws HibernateException {
        if (paciente == null || paciente.getId() == null) return new ArrayList<>();
        Prontuario prontuario = obterOuCriarProntuario(paciente);
        return odontogramaDAO.listarPorProntuario(prontuario.getId());
    }

    public List<Atendimento> listarAtendimentosPaciente(Paciente paciente) throws HibernateException {
        if (paciente == null || paciente.getId() == null) return new ArrayList<>();
        Prontuario prontuario = obterOuCriarProntuario(paciente);
        return prontuarioDAO.listarAtendimentos(prontuario.getId());
    }

    public List<Orcamento> listarOrcamentosPaciente(Paciente paciente) throws HibernateException {
        if (paciente == null || paciente.getId() == null) return new ArrayList<>();
        return orcamentoDAO.listarPorPaciente(paciente.getId());
    }

    public List<Orcamento> listarOrcamentosSeguro() {
        try {
            return orcamentoDAO.listarOrdenadoPorData();
        } catch (Throwable ex) {
            System.err.println("Nao foi possivel listar orcamentos: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public Orcamento inserirOrcamento(Paciente paciente, Usuario usuario, String observacoes,
            List<ItemOrcamento> itens) throws HibernateException {
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um paciente para criar o orcamento.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O orcamento precisa ter pelo menos um item.");
        }
        Orcamento orcamento = new Orcamento(paciente, usuario, vazioParaNull(observacoes));
        for (ItemOrcamento item : itens) {
            validarItemOrcamento(item);
            orcamento.adicionarItem(item);
        }
        orcamento.recalcularValorTotal();
        genDAO.inserir(orcamento);
        return orcamento;
    }

    public Orcamento inserirOrcamento(Orcamento orcamento) throws HibernateException {
        validarOrcamento(orcamento);
        for (ItemOrcamento item : orcamento.getItens()) {
            validarItemOrcamento(item);
            item.setOrcamento(orcamento);
        }
        orcamento.recalcularValorTotal();
        genDAO.inserir(orcamento);
        return orcamento;
    }

    public Orcamento alterarOrcamento(Orcamento orcamento) throws HibernateException {
        validarOrcamento(orcamento);
        for (ItemOrcamento item : orcamento.getItens()) {
            validarItemOrcamento(item);
            item.setOrcamento(orcamento);
        }
        orcamento.recalcularValorTotal();
        genDAO.salvarOuAlterar(orcamento);
        return orcamento;
    }

    public void excluirOrcamento(Orcamento orcamento) throws HibernateException {
        if (orcamento == null) {
            throw new IllegalArgumentException("Selecione um registro para excluir.");
        }
        genDAO.excluir(orcamento);
    }

    public ConsentimentoTratamento gerarConsentimento(Orcamento orcamento, String assinaturaDigital, String termo) throws HibernateException {
        if (orcamento == null) {
            throw new IllegalArgumentException("Selecione um orcamento para gerar o consentimento.");
        }
        if (textoVazio(assinaturaDigital)) {
            throw new IllegalArgumentException("Informe a assinatura ou nome do paciente.");
        }
        if (textoVazio(termo)) {
            throw new IllegalArgumentException("O texto do consentimento nao pode ficar vazio.");
        }
        ConsentimentoTratamento existente = consentimentoDAO.buscarPorOrcamento(orcamento.getId());
        if (existente != null) {
            throw new IllegalArgumentException("Ja existe consentimento para este orcamento.");
        }
        ConsentimentoTratamento consentimento = new ConsentimentoTratamento(new Date(), assinaturaDigital.trim(), termo.trim(), orcamento);
        orcamento.setConsentimentoTratamento(consentimento);
        genDAO.inserir(consentimento);
        return consentimento;
    }

    public ConsentimentoTratamento inserirConsentimento(ConsentimentoTratamento consentimento) throws HibernateException {
        validarConsentimento(consentimento);
        genDAO.inserir(consentimento);
        return consentimento;
    }

    public ConsentimentoTratamento alterarConsentimento(ConsentimentoTratamento consentimento) throws HibernateException {
        validarConsentimento(consentimento);
        genDAO.salvarOuAlterar(consentimento);
        return consentimento;
    }

    public void excluirConsentimento(ConsentimentoTratamento consentimento) throws HibernateException {
        if (consentimento == null) {
            throw new IllegalArgumentException("Selecione um registro para excluir.");
        }
        genDAO.excluir(consentimento);
    }

    public List<ConsentimentoTratamento> listarConsentimentosSeguro() {
        try {
            return consentimentoDAO.listarOrdenadoPorData();
        } catch (Throwable ex) {
            System.err.println("Nao foi possivel listar consentimentos: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<PacienteRelatorioDTO> listarPacientesParaRelatorio() {
        List<PacienteRelatorioDTO> dados = new ArrayList<>();
        for (Paciente paciente : listarPacientes()) {
            Endereco endereco = paciente.getEndereco();
            dados.add(new PacienteRelatorioDTO(
                    paciente.getId(),
                    nulo(paciente.getNome()),
                    nulo(paciente.getCpf()),
                    paciente.getDataNascimentoFormatada(),
                    nulo(paciente.getTelefone()),
                    nulo(paciente.getEmail()),
                    endereco == null ? "" : nulo(endereco.getCidade()),
                    endereco == null ? "" : nulo(endereco.getBairro()),
                    Boolean.TRUE.equals(paciente.getAtivo()) ? "Ativo" : "Inativo"));
        }
        return dados;
    }

    public List<AnamneseRelatorioDTO> listarAnamnesesParaRelatorio() {
        return montarAnamnesesParaRelatorio(genDAO.listar(Anamnese.class));
    }

    public List<AnamneseRelatorioDTO> listarAnamnesesPorPacienteParaRelatorio(Paciente paciente) {
        return montarAnamnesesParaRelatorio(listarAnamnesesPaciente(paciente));
    }

    public List<OdontogramaRelatorioDTO> listarOdontogramasParaRelatorio() {
        List<OdontogramaRelatorioDTO> dados = new ArrayList<>();
        for (Odontograma odontograma : genDAO.listar(Odontograma.class)) {
            adicionarOdontogramaRelatorio(dados, odontograma);
        }
        return dados;
    }

    public List<OdontogramaRelatorioDTO> listarOdontogramasPorPacienteParaRelatorio(Paciente paciente) {
        List<OdontogramaRelatorioDTO> dados = new ArrayList<>();
        for (Odontograma odontograma : listarOdontogramasPaciente(paciente)) {
            adicionarOdontogramaRelatorio(dados, odontograma);
        }
        return dados;
    }

    public List<OrcamentoRelatorioDTO> listarOrcamentosParaRelatorio() {
        List<OrcamentoRelatorioDTO> dados = new ArrayList<>();
        for (Orcamento orcamento : orcamentoDAO.listarOrdenadoPorData()) {
            dados.add(montarOrcamentoRelatorio(orcamento));
        }
        return dados;
    }

    public List<ConsentimentoRelatorioDTO> listarConsentimentosParaRelatorio() {
        List<ConsentimentoRelatorioDTO> dados = new ArrayList<>();
        for (ConsentimentoTratamento consentimento : consentimentoDAO.listarOrdenadoPorData()) {
            Orcamento orcamento = consentimento.getOrcamento();
            Paciente paciente = orcamento == null ? null : orcamento.getPaciente();
            dados.add(new ConsentimentoRelatorioDTO(
                    consentimento.getId(),
                    paciente == null ? "" : nulo(paciente.getNome()),
                    orcamento == null ? null : orcamento.getId(),
                    consentimento.getDataAssinaturaFormatada(),
                    resumo(consentimento.getTermo(), 120),
                    nulo(consentimento.getAssinaturaDigital())));
        }
        return dados;
    }

    public List<ProntuarioRelatorioDTO> montarProntuarioParaRelatorio(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um paciente para gerar o relatorio do prontuario.");
        }
        List<ProntuarioRelatorioDTO> dados = new ArrayList<>();
        Prontuario prontuario = obterOuCriarProntuario(paciente);
        dados.add(new ProntuarioRelatorioDTO("Prontuario", prontuario.getDataAberturaFormatada(),
                nulo(prontuario.getObservacoesGerais())));
        for (Anamnese anamnese : listarAnamnesesPaciente(paciente)) {
            dados.add(new ProntuarioRelatorioDTO("Anamnese", anamnese.getDataRegistroFormatada(),
                    primeiroTexto(anamnese.getHistoriaDoencaAtual(), anamnese.getObservacoes(), anamnese.getDoencas())));
        }
        for (Odontograma odontograma : listarOdontogramasPaciente(paciente)) {
            dados.add(new ProntuarioRelatorioDTO("Odontograma", odontograma.getDataRegistroFormatada(),
                    nulo(odontograma.getObservacoes())));
        }
        for (Atendimento atendimento : listarAtendimentosPaciente(paciente)) {
            dados.add(new ProntuarioRelatorioDTO("Atendimento", atendimento.getDataAtendimentoFormatada(),
                    primeiroTexto(atendimento.getProcedimentoRealizado(), atendimento.getDescricao(), atendimento.getObservacoes())));
        }
        for (Orcamento orcamento : listarOrcamentosPaciente(paciente)) {
            dados.add(new ProntuarioRelatorioDTO("Orcamento", orcamento.getDataCriacaoFormatada(),
                    orcamento.getValorTotalFormatado() + " - " + orcamento.getStatus()));
            ConsentimentoTratamento consentimento = consentimentoDAO.buscarPorOrcamento(orcamento.getId());
            if (consentimento != null) {
                dados.add(new ProntuarioRelatorioDTO("Consentimento", consentimento.getDataAssinaturaFormatada(),
                        resumo(consentimento.getTermo(), 120)));
            }
        }
        return dados;
    }

    public Map<String, Object> parametrosProntuarioPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um paciente para gerar o relatorio do prontuario.");
        }
        Map<String, Object> parametros = new LinkedHashMap<>();
        Endereco endereco = paciente.getEndereco();
        parametros.put("PACIENTE", nulo(paciente.getNome()));
        parametros.put("CPF", nulo(paciente.getCpf()));
        parametros.put("TELEFONE", nulo(paciente.getTelefone()));
        parametros.put("EMAIL", nulo(paciente.getEmail()));
        parametros.put("DATA_NASCIMENTO", paciente.getDataNascimentoFormatada());
        parametros.put("ENDERECO", endereco == null ? "" : endereco.getEnderecoCompleto());
        return parametros;
    }

    public List<Object[]> contarPacientesPorCidade() {
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Paciente paciente : listarPacientes()) {
            Endereco endereco = paciente.getEndereco();
            String chave = endereco == null ? "Nao informado" : textoOuPadrao(endereco.getCidade(), "Nao informado");
            mapa.put(chave, mapa.getOrDefault(chave, 0L) + 1L);
        }
        return mapaQuantidade(mapa);
    }

    public List<Object[]> contarPacientesPorBairro() {
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Paciente paciente : listarPacientes()) {
            Endereco endereco = paciente.getEndereco();
            String chave = endereco == null ? "Nao informado" : textoOuPadrao(endereco.getBairro(), "Nao informado");
            mapa.put(chave, mapa.getOrDefault(chave, 0L) + 1L);
        }
        return mapaQuantidade(mapa);
    }

    public List<Object[]> contarOrcamentosPorStatus() {
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Orcamento orcamento : orcamentoDAO.listarOrdenadoPorData()) {
            String chave = orcamento.getStatus() == null ? "Nao informado" : orcamento.getStatus().name();
            mapa.put(chave, mapa.getOrDefault(chave, 0L) + 1L);
        }
        return mapaQuantidade(mapa);
    }

    public List<Object[]> somarOrcamentosPorPaciente() {
        Map<String, BigDecimal> mapa = new LinkedHashMap<>();
        for (Orcamento orcamento : orcamentoDAO.listarOrdenadoPorData()) {
            String chave = orcamento.getPaciente() == null ? "Nao informado" : nulo(orcamento.getPaciente().getNome());
            BigDecimal atual = mapa.getOrDefault(chave, BigDecimal.ZERO);
            mapa.put(chave, atual.add(orcamento.getValorTotal() == null ? BigDecimal.ZERO : orcamento.getValorTotal()));
        }
        List<Object[]> dados = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entrada : mapa.entrySet()) {
            dados.add(new Object[]{entrada.getKey(), null, entrada.getValue()});
        }
        return dados;
    }

    public List<Object[]> contarConsentimentosPorMes() {
        SimpleDateFormat formato = new SimpleDateFormat("MM/yyyy");
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (ConsentimentoTratamento consentimento : consentimentoDAO.listarOrdenadoPorData()) {
            String chave = consentimento.getDataAssinatura() == null
                    ? "Nao informado" : formato.format(consentimento.getDataAssinatura());
            mapa.put(chave, mapa.getOrDefault(chave, 0L) + 1L);
        }
        return mapaQuantidade(mapa);
    }

    public List<Object[]> contarAnamnesesPorPaciente() {
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Anamnese anamnese : genDAO.listar(Anamnese.class)) {
            Paciente paciente = anamnese.getPaciente();
            String chave = paciente == null ? "Nao informado" : nulo(paciente.getNome());
            mapa.put(chave, mapa.getOrDefault(chave, 0L) + 1L);
        }
        return mapaQuantidade(mapa);
    }

    public Usuario criarUsuarioPadraoSeNecessario() {
        try {
            List<Usuario> usuarios = genDAO.listar(Usuario.class);
            if (!usuarios.isEmpty()) {
                return usuarios.get(0);
            }
            Usuario usuario = new Usuario("Administrador", "admin@glinicore.local", "admin", TipoUsuario.RECEPCAO);
            genDAO.inserir(usuario);
            return usuario;
        } catch (Throwable ex) {
            return null;
        }
    }

    private void registrarHistorico(Paciente paciente, Usuario usuario, String descricao, String tipo) {
        try {
            Prontuario prontuario = obterOuCriarProntuario(paciente);
            HistoricoAlteracao hist = new HistoricoAlteracao(descricao, tipo);
            hist.setProntuario(prontuario);
            hist.setUsuario(usuario);
            genDAO.inserir(hist);
        } catch (Throwable ex) {
            System.err.println("Historico nao registrado: " + ex.getMessage());
        }
    }

    private List<AnamneseRelatorioDTO> montarAnamnesesParaRelatorio(List<Anamnese> anamneses) {
        List<AnamneseRelatorioDTO> dados = new ArrayList<>();
        for (Anamnese anamnese : anamneses) {
            Paciente paciente = anamnese.getPaciente();
            dados.add(new AnamneseRelatorioDTO(
                    anamnese.getId(),
                    paciente == null ? "" : nulo(paciente.getNome()),
                    anamnese.getDataRegistroFormatada(),
                    primeiroTexto(anamnese.getAlergias(), anamnese.getHistoriaFamiliar()),
                    primeiroTexto(anamnese.getDoencas(), anamnese.getHistoriaPatologicaPregressa()),
                    primeiroTexto(anamnese.getMedicamentos(), anamnese.getHistoriaFisiologica()),
                    primeiroTexto(anamnese.getObservacoes(), anamnese.getHistoriaDoencaAtual())));
        }
        return dados;
    }

    private void adicionarOdontogramaRelatorio(List<OdontogramaRelatorioDTO> dados, Odontograma odontograma) {
        String paciente = nomePacienteOdontograma(odontograma);
        if (odontograma.getDentes() == null || odontograma.getDentes().isEmpty()) {
            dados.add(new OdontogramaRelatorioDTO(odontograma.getId(), paciente,
                    odontograma.getDataRegistroFormatada(), null, "", nulo(odontograma.getObservacoes())));
            return;
        }
        for (Dente dente : odontograma.getDentes()) {
            dados.add(new OdontogramaRelatorioDTO(
                    odontograma.getId(),
                    paciente,
                    odontograma.getDataRegistroFormatada(),
                    dente.getNumero(),
                    dente.getCondicao() == null ? "" : dente.getCondicao().name(),
                    primeiroTexto(dente.getObservacao(), odontograma.getObservacoes())));
        }
    }

    private String nomePacienteOdontograma(Odontograma odontograma) {
        if (odontograma == null || odontograma.getProntuario() == null
                || odontograma.getProntuario().getPaciente() == null) {
            return "";
        }
        return nulo(odontograma.getProntuario().getPaciente().getNome());
    }

    private OrcamentoRelatorioDTO montarOrcamentoRelatorio(Orcamento orcamento) {
        Paciente paciente = orcamento.getPaciente();
        return new OrcamentoRelatorioDTO(
                orcamento.getId(),
                paciente == null ? "" : nulo(paciente.getNome()),
                orcamento.getDataCriacaoFormatada(),
                orcamento.getStatus() == null ? "" : orcamento.getStatus().name(),
                orcamento.getValorTotalFormatado(),
                nulo(orcamento.getObservacoes()),
                itensResumo(orcamento.getItens()));
    }

    private String itensResumo(List<ItemOrcamento> itens) {
        if (itens == null || itens.isEmpty()) {
            return "";
        }
        StringBuilder resumo = new StringBuilder();
        for (ItemOrcamento item : itens) {
            if (resumo.length() > 0) {
                resumo.append("; ");
            }
            resumo.append(item.getDescricaoProcedimento())
                    .append(" (")
                    .append(item.getQuantidade())
                    .append(" x ")
                    .append(FuncoesUteis.moeda(item.getValorUnitario()))
                    .append(")");
        }
        return resumo.toString();
    }

    private List<Object[]> mapaQuantidade(Map<String, Long> mapa) {
        List<Object[]> dados = new ArrayList<>();
        for (Map.Entry<String, Long> entrada : mapa.entrySet()) {
            dados.add(new Object[]{entrada.getKey(), entrada.getValue(), null});
        }
        return dados;
    }

    private String textoOuPadrao(String texto, String padrao) {
        return texto == null || texto.isBlank() ? padrao : texto;
    }

    private String primeiroTexto(String... textos) {
        if (textos == null) {
            return "";
        }
        for (String texto : textos) {
            if (texto != null && !texto.isBlank()) {
                return texto;
            }
        }
        return "";
    }

    private String resumo(String texto, int limite) {
        String valor = nulo(texto);
        if (valor.length() <= limite) {
            return valor;
        }
        return valor.substring(0, limite - 3) + "...";
    }

    private String nulo(String texto) {
        return texto == null ? "" : texto;
    }

    private void validarPaciente(String nome, String cpf) {
        if (nome == null || nome.trim().length() < 3) {
            throw new IllegalArgumentException("Nome do paciente deve ter pelo menos 3 caracteres.");
        }
        if (cpf != null && !FuncoesUteis.cpfBasicoValido(cpf)) {
            throw new IllegalArgumentException("CPF deve ter 11 numeros.");
        }
    }

    private void verificarCpfUnico(String cpf, Long idAtual) {
        if (cpf == null) return;
        Paciente existente = pacienteDAO.buscarPorCpfExato(cpf);
        if (existente != null && (idAtual == null || !idAtual.equals(existente.getId()))) {
            throw new IllegalArgumentException("Ja existe paciente cadastrado com este CPF.");
        }
    }

    private void validarItemOrcamento(ItemOrcamento item) {
        if (item == null) {
            throw new IllegalArgumentException("Item de orcamento invalido.");
        }
        if (item.getDescricaoProcedimento() == null || item.getDescricaoProcedimento().trim().length() < 3) {
            throw new IllegalArgumentException("Descricao do procedimento e obrigatoria.");
        }
        if (item.getQuantidade() == null || item.getQuantidade() < 1) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        BigDecimal valor = item.getValorUnitario();
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor unitario nao pode ser negativo.");
        }
    }

    private void validarAnamnese(Anamnese anamnese) {
        if (anamnese == null) {
            throw new IllegalArgumentException("Informe os dados da anamnese.");
        }
        if (anamnese.getPaciente() == null) {
            throw new IllegalArgumentException("Nao salvar anamnese sem paciente vinculado.");
        }
        if (textoVazio(anamnese.getAlergias()) && textoVazio(anamnese.getMedicamentos())
                && textoVazio(anamnese.getDoencas()) && textoVazio(anamnese.getObservacoes())
                && textoVazio(anamnese.getHistoriaDoencaAtual())
                && textoVazio(anamnese.getHistoriaPatologicaPregressa())
                && textoVazio(anamnese.getHistoriaFisiologica())
                && textoVazio(anamnese.getHistoriaFamiliar())) {
            throw new IllegalArgumentException("Preencha pelo menos uma informacao da anamnese.");
        }
    }

    private void validarOrcamento(Orcamento orcamento) {
        if (orcamento == null) {
            throw new IllegalArgumentException("Informe os dados do orcamento.");
        }
        if (orcamento.getPaciente() == null) {
            throw new IllegalArgumentException("Selecione um paciente para criar o orcamento.");
        }
        if (orcamento.getItens() == null || orcamento.getItens().isEmpty()) {
            throw new IllegalArgumentException("O orcamento precisa ter pelo menos um item.");
        }
    }

    private void validarConsentimento(ConsentimentoTratamento consentimento) {
        if (consentimento == null) {
            throw new IllegalArgumentException("Informe os dados do consentimento.");
        }
        if (consentimento.getOrcamento() == null) {
            throw new IllegalArgumentException("Selecione um orcamento para o consentimento.");
        }
        if (textoVazio(consentimento.getTermo())) {
            throw new IllegalArgumentException("O texto do consentimento nao pode ficar vazio.");
        }
        if (textoVazio(consentimento.getAssinaturaDigital())) {
            throw new IllegalArgumentException("Informe a assinatura ou nome do paciente.");
        }
    }

    private String normalizarCpf(String cpf) {
        String numeros = FuncoesUteis.somenteNumeros(cpf);
        return numeros.isBlank() ? null : numeros;
    }

    private String vazioParaNull(String texto) {
        return texto == null || texto.trim().isBlank() || texto.contains("_") ? null : texto.trim();
    }

    private boolean textoVazio(String texto) {
        return texto == null || texto.trim().isBlank();
    }
}
