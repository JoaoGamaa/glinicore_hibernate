package controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.CondicaoDente;
import model.ConsentimentoTratamento;
import model.Endereco;
import model.EspecialidadeOdontologica;
import model.ItemOrcamento;
import model.Orcamento;
import model.Paciente;
import model.StatusOrcamento;
import model.Usuario;

public final class DatabaseSeeder {

    private static final String TERMO_PADRAO = "Declaro estar ciente do tratamento odontologico proposto, "
            + "seus beneficios, riscos e alternativas.";

    private DatabaseSeeder() {
    }

    public static void executarSeNecessario(GerenciadorDominio gerenciador) {
        if (gerenciador == null) {
            return;
        }

        Usuario usuario = gerenciador.criarUsuarioPadraoSeNecessario();
        EspecialidadeOdontologica clinicaGeral = garantirEspecialidade(gerenciador,
                "Clinica Geral", "Consultas, limpezas, restauracoes e acompanhamento preventivo.");
        EspecialidadeOdontologica ortodontia = garantirEspecialidade(gerenciador,
                "Ortodontia", "Acompanhamento de alinhamento dental e aparelhos.");
        EspecialidadeOdontologica endodontia = garantirEspecialidade(gerenciador,
                "Endodontia", "Tratamentos de canal e controle de dor.");
        EspecialidadeOdontologica periodontia = garantirEspecialidade(gerenciador,
                "Periodontia", "Tratamento gengival, raspagem e controle periodontal.");
        EspecialidadeOdontologica cirurgia = garantirEspecialidade(gerenciador,
                "Cirurgia Odontologica", "Extracoes e pequenos procedimentos cirurgicos.");

        Map<String, Paciente> pacientesExistentes = pacientesPorCpf(gerenciador.listarPacientes());
        List<PacienteSeed> pacientes = Arrays.asList(
                new PacienteSeed("Joao Silva", "11111111111", 1985, 3, 14, "(27) 99911-0001",
                        "joao.silva@email.com", "Rua das Flores", "120", "Centro", "Vitoria", "ES", "29010-000"),
                new PacienteSeed("Maria Oliveira", "22222222222", 1990, 7, 22, "(27) 99922-0002",
                        "maria.oliveira@email.com", "Avenida Brasil", "455", "Praia do Canto", "Vitoria", "ES", "29055-000"),
                new PacienteSeed("Carlos Santos", "33333333333", 1978, 11, 5, "(27) 99933-0003",
                        "carlos.santos@email.com", "Rua Sao Jose", "87", "Itapoa", "Vila Velha", "ES", "29101-120"),
                new PacienteSeed("Ana Pereira", "44444444444", 1996, 1, 18, "(27) 99944-0004",
                        "ana.pereira@email.com", "Rua Sete de Setembro", "301", "Campo Grande", "Cariacica", "ES", "29146-010"),
                new PacienteSeed("Pedro Almeida", "55555555555", 1982, 9, 9, "(27) 99955-0005",
                        "pedro.almeida@email.com", "Avenida Central", "52", "Jardim Camburi", "Vitoria", "ES", "29090-050"),
                new PacienteSeed("Fernanda Rocha", "66666666666", 1988, 5, 27, "(27) 99966-0006",
                        "fernanda.rocha@email.com", "Rua Guarapari", "201", "Itaparica", "Vila Velha", "ES", "29102-030"),
                new PacienteSeed("Roberto Lima", "77777777777", 1972, 12, 2, "(27) 99977-0007",
                        "roberto.lima@email.com", "Rua das Palmeiras", "44", "Serra Centro", "Serra", "ES", "29176-020"),
                new PacienteSeed("Juliana Costa", "88888888888", 1998, 8, 16, "(27) 99988-0008",
                        "juliana.costa@email.com", "Avenida Expedito Garcia", "900", "Campo Grande", "Cariacica", "ES", "29146-200"),
                new PacienteSeed("Lucas Martins", "99999999999", 2001, 4, 8, "(27) 99999-0009",
                        "lucas.martins@email.com", "Rua Alecrim", "15", "Maruipe", "Vitoria", "ES", "29043-020"),
                new PacienteSeed("Beatriz Nunes", "10101010101", 1993, 10, 30, "(27) 99810-1010",
                        "beatriz.nunes@email.com", "Rua Santa Luzia", "88", "Praia da Costa", "Vila Velha", "ES", "29101-340")
        );

        for (int i = 0; i < pacientes.size(); i++) {
            PacienteSeed seed = pacientes.get(i);
            if (pacientesExistentes.containsKey(seed.cpf)) {
                continue;
            }
            Paciente paciente = gerenciador.inserirPaciente(seed.toPaciente());
            cadastrarDadosClinicos(gerenciador, paciente, usuario, i, clinicaGeral,
                    ortodontia, endodontia, periodontia, cirurgia);
            pacientesExistentes.put(seed.cpf, paciente);
        }
    }

    private static void cadastrarDadosClinicos(GerenciadorDominio gerenciador, Paciente paciente,
            Usuario usuario, int indice, EspecialidadeOdontologica clinicaGeral,
            EspecialidadeOdontologica ortodontia, EspecialidadeOdontologica endodontia,
            EspecialidadeOdontologica periodontia, EspecialidadeOdontologica cirurgia) {

        switch (indice) {
            case 0:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente relata alergia a dipirona.",
                        "Sem cirurgias recentes.", "Sono regular e boa alimentacao.",
                        "Familia sem historico odontologico relevante.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(11, CondicaoDente.NORMAL,
                        12, CondicaoDente.RESTAURADO, 13, CondicaoDente.MANUTENCAO,
                        14, CondicaoDente.CARIADO, 15, CondicaoDente.AUSENTE),
                        "Odontograma inicial de exemplo.");
                gerenciador.inserirAtendimento(paciente, usuario, clinicaGeral, data(2026, 5, 20),
                        "Consulta inicial com profilaxia.", "Limpeza e orientacao de higiene.",
                        "Retorno semestral indicado.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.APROVADO,
                        "Plano inicial para limpeza e restauracao.", true,
                        item("Limpeza", 1, "180.00"), item("Restauracao", 2, "250.00"));
                break;
            case 1:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente relata hipertensao controlada.",
                        "Uso continuo de losartana.", "Pratica atividade fisica leve.",
                        "Mae com historico de hipertensao.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(21, CondicaoDente.NORMAL,
                        22, CondicaoDente.RESTAURADO, 23, CondicaoDente.CARIADO,
                        24, CondicaoDente.MANUTENCAO, 25, CondicaoDente.AUSENTE),
                        "Retorno para avaliacao em 30 dias.");
                gerenciador.inserirAtendimento(paciente, usuario, periodontia, data(2026, 5, 25),
                        "Avaliacao periodontal.", "Raspagem supragengival.",
                        "Monitorar sangramento gengival.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.PENDENTE,
                        "Tratamento com avaliacao e clareamento.", true,
                        item("Avaliacao", 1, "120.00"), item("Clareamento", 1, "900.00"));
                break;
            case 2:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente nega doencas sistemicas.",
                        "Sem patologias pregressas declaradas.", "Nao faz uso de medicamentos.",
                        "Sem observacoes familiares.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(31, CondicaoDente.NORMAL,
                        32, CondicaoDente.CARIADO, 36, CondicaoDente.RESTAURADO,
                        37, CondicaoDente.MANUTENCAO), "Queixa de sensibilidade no lado inferior esquerdo.");
                gerenciador.inserirAtendimento(paciente, usuario, cirurgia, data(2026, 6, 4),
                        "Avaliacao de terceiro molar.", "Solicitada radiografia panoramica.",
                        "Aguardar exame antes de extracao.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.PENDENTE,
                        "Procedimento cirurgico simples.", false,
                        item("Extracao", 1, "350.00"));
                break;
            case 3:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente gestante informa acompanhamento medico.",
                        "Sem alergias medicamentosas conhecidas.", "Evitar radiografia sem necessidade.",
                        "Historico familiar sem alteracoes.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(41, CondicaoDente.NORMAL,
                        42, CondicaoDente.NORMAL, 46, CondicaoDente.CARIADO,
                        47, CondicaoDente.RESTAURADO), "Acompanhamento preventivo durante gestacao.");
                gerenciador.inserirAtendimento(paciente, usuario, clinicaGeral, data(2026, 6, 10),
                        "Consulta preventiva.", "Orientacao de higiene e avaliacao visual.",
                        "Sem intervencoes invasivas neste atendimento.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.APROVADO,
                        "Plano conservador de restauracao.", true,
                        item("Restauracao", 1, "260.00"), item("Aplicacao de fluor", 1, "90.00"));
                break;
            case 4:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente relata bruxismo noturno.",
                        "Ja utilizou placa antiga.", "Sono irregular em periodos de estresse.",
                        "Pai tambem relata bruxismo.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(16, CondicaoDente.RESTAURADO,
                        17, CondicaoDente.MANUTENCAO, 26, CondicaoDente.RESTAURADO,
                        27, CondicaoDente.NORMAL), "Sinais de desgaste incisal.");
                gerenciador.inserirAtendimento(paciente, usuario, clinicaGeral, data(2026, 6, 12),
                        "Avaliacao de bruxismo.", "Moldagem para placa miorrelaxante.",
                        "Orientado retorno para ajuste.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.PENDENTE,
                        "Placa e ajustes oclusais.", false,
                        item("Placa miorrelaxante", 1, "650.00"), item("Ajuste oclusal", 1, "180.00"));
                break;
            case 5:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente relata sensibilidade ao frio.",
                        "Historico de restauracoes antigas.", "Nao usa medicamento continuo.",
                        "Sem historico familiar importante.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(12, CondicaoDente.CARIADO,
                        13, CondicaoDente.RESTAURADO, 22, CondicaoDente.NORMAL,
                        24, CondicaoDente.CARIADO), "Necessaria reavaliacao de restauracoes antigas.");
                gerenciador.inserirAtendimento(paciente, usuario, endodontia, data(2026, 6, 18),
                        "Teste de vitalidade pulpar.", "Teste termico e percussao.",
                        "Monitorar dente 24.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.APROVADO,
                        "Restauracoes posteriores e controle de sensibilidade.", true,
                        item("Restauracao", 2, "270.00"), item("Dessensibilizacao", 1, "140.00"));
                break;
            case 6:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente diabetico controlado.",
                        "Uso de metformina.", "Alimentacao controlada.",
                        "Irmao com diabetes tipo 2.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(33, CondicaoDente.NORMAL,
                        34, CondicaoDente.RESTAURADO, 35, CondicaoDente.CARIADO,
                        38, CondicaoDente.AUSENTE), "Atenção a cicatrizacao por diabetes.");
                gerenciador.inserirAtendimento(paciente, usuario, periodontia, data(2026, 6, 21),
                        "Controle periodontal em paciente diabetico.", "Raspagem e polimento.",
                        "Solicitado retorno em 90 dias.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.PENDENTE,
                        "Terapia periodontal de manutencao.", false,
                        item("Raspagem periodontal", 2, "220.00"), item("Profilaxia", 1, "160.00"));
                break;
            case 7:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente procura avaliacao ortodontica.",
                        "Sem doencas sistemicas.", "Boa higiene oral.",
                        "Mae utilizou aparelho fixo.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(11, CondicaoDente.NORMAL,
                        21, CondicaoDente.NORMAL, 31, CondicaoDente.NORMAL,
                        41, CondicaoDente.NORMAL), "Apinhamento leve em incisivos inferiores.");
                gerenciador.inserirAtendimento(paciente, usuario, ortodontia, data(2026, 6, 22),
                        "Triagem ortodontica.", "Documentacao solicitada.",
                        "Aguardar exames para planejamento.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.PENDENTE,
                        "Planejamento ortodontico inicial.", false,
                        item("Documentacao ortodontica", 1, "300.00"), item("Aparelho fixo inicial", 1, "750.00"));
                break;
            case 8:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente relata trauma antigo em incisivo.",
                        "Canal realizado ha 3 anos.", "Sem dor no momento.",
                        "Sem historico familiar relevante.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(11, CondicaoDente.MANUTENCAO,
                        12, CondicaoDente.NORMAL, 21, CondicaoDente.RESTAURADO,
                        22, CondicaoDente.NORMAL), "Acompanhar dente 11 tratado endodonticamente.");
                gerenciador.inserirAtendimento(paciente, usuario, endodontia, data(2026, 6, 24),
                        "Controle radiografico de canal.", "Avaliacao clinica e solicitacao de RX.",
                        "Sem sinais agudos.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.RECUSADO,
                        "Retratamento somente se houver alteracao radiografica.", false,
                        item("Radiografia periapical", 1, "70.00"), item("Retratamento endodontico", 1, "850.00"));
                break;
            default:
                gerenciador.inserirAnamneseClinica(paciente, "Paciente busca clareamento e check-up.",
                        "Sem alergias conhecidas.", "Nao faz uso de medicamentos.",
                        "Historico familiar sem alteracoes.");
                gerenciador.salvarOdontograma(paciente, dentesExemplo(14, CondicaoDente.NORMAL,
                        15, CondicaoDente.RESTAURADO, 24, CondicaoDente.NORMAL,
                        25, CondicaoDente.RESTAURADO), "Boa condicao geral, foco estetico.");
                gerenciador.inserirAtendimento(paciente, usuario, clinicaGeral, data(2026, 6, 27),
                        "Check-up estetico.", "Avaliacao de cor e profilaxia.",
                        "Apta para clareamento supervisionado.");
                criarOrcamento(gerenciador, paciente, usuario, StatusOrcamento.APROVADO,
                        "Clareamento supervisionado.", true,
                        item("Clareamento caseiro supervisionado", 1, "780.00"),
                        item("Profilaxia previa", 1, "160.00"));
                break;
        }
    }

    private static Orcamento criarOrcamento(GerenciadorDominio gerenciador, Paciente paciente,
            Usuario usuario, StatusOrcamento status, String observacoes, boolean gerarConsentimento,
            ItemOrcamento... itens) {
        Orcamento orcamento = gerenciador.inserirOrcamento(paciente, usuario, observacoes, Arrays.asList(itens));
        orcamento.setStatus(status);
        gerenciador.alterarOrcamento(orcamento);

        if (gerarConsentimento) {
            ConsentimentoTratamento consentimento = gerenciador.gerarConsentimento(
                    orcamento, paciente.getNome(), TERMO_PADRAO);
            consentimento.setDataAssinatura(data(2026, 6, 1 + (paciente.getId() == null ? 0 : paciente.getId().intValue() % 20)));
            gerenciador.alterarConsentimento(consentimento);
        }
        return orcamento;
    }

    private static EspecialidadeOdontologica garantirEspecialidade(GerenciadorDominio gerenciador,
            String nome, String descricao) {
        for (EspecialidadeOdontologica especialidade : gerenciador.listarSeguro(EspecialidadeOdontologica.class)) {
            if (especialidade.getNome() != null && especialidade.getNome().equalsIgnoreCase(nome)) {
                return especialidade;
            }
        }
        EspecialidadeOdontologica especialidade = new EspecialidadeOdontologica(nome, descricao);
        gerenciador.inserir(especialidade);
        return especialidade;
    }

    private static Map<String, Paciente> pacientesPorCpf(List<Paciente> pacientes) {
        Map<String, Paciente> porCpf = new LinkedHashMap<>();
        for (Paciente paciente : pacientes) {
            if (paciente.getCpf() != null) {
                porCpf.put(paciente.getCpf(), paciente);
            }
        }
        return porCpf;
    }

    private static ItemOrcamento item(String descricao, int quantidade, String valorUnitario) {
        return new ItemOrcamento(descricao, quantidade, new BigDecimal(valorUnitario));
    }

    private static Map<Integer, CondicaoDente> dentesExemplo(Object... pares) {
        Map<Integer, CondicaoDente> dentes = new LinkedHashMap<>();
        for (int i = 0; i < pares.length; i += 2) {
            dentes.put((Integer) pares[i], (CondicaoDente) pares[i + 1]);
        }
        return dentes;
    }

    private static java.util.Date data(int ano, int mes, int dia) {
        Calendar calendario = Calendar.getInstance();
        calendario.clear();
        calendario.set(ano, mes - 1, dia);
        return calendario.getTime();
    }

    private static final class PacienteSeed {
        private final String nome;
        private final String cpf;
        private final int ano;
        private final int mes;
        private final int dia;
        private final String telefone;
        private final String email;
        private final String rua;
        private final String numero;
        private final String bairro;
        private final String cidade;
        private final String uf;
        private final String cep;

        private PacienteSeed(String nome, String cpf, int ano, int mes, int dia,
                String telefone, String email, String rua, String numero, String bairro,
                String cidade, String uf, String cep) {
            this.nome = nome;
            this.cpf = cpf;
            this.ano = ano;
            this.mes = mes;
            this.dia = dia;
            this.telefone = telefone;
            this.email = email;
            this.rua = rua;
            this.numero = numero;
            this.bairro = bairro;
            this.cidade = cidade;
            this.uf = uf;
            this.cep = cep;
        }

        private Paciente toPaciente() {
            Paciente paciente = new Paciente(nome, cpf, data(ano, mes, dia), telefone, email, null,
                    new Endereco(rua, numero, bairro, cidade, uf, cep, null));
            paciente.setAtivo(true);
            return paciente;
        }
    }
}
