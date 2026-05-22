package com.conectaarena.service;

import com.conectaarena.model.*; // importa as entidades
import com.conectaarena.repository.*; // importa todas as interfaces
import org.springframework.stereotype.Service; // anotação service
import org.springframework.transaction.annotation.Transactional; // anotação transactional
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // gerar ids únicos (qrCodes)
import java.util.stream.Collectors; //listagem

@Service
public class ArenaService {

    private final EventoRepository eventoRepository;
    private final IngressoRepository ingressoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SugestaoRepository sugestaoRepository;
    private final AgendamentoRepository agendamentoRepository;

    public ArenaService(EventoRepository eventoRepository, IngressoRepository ingressoRepository, UsuarioRepository usuarioRepository, SugestaoRepository sugestaoRepository, AgendamentoRepository agendamentoRepository) {
        this.eventoRepository = eventoRepository;
        this.ingressoRepository = ingressoRepository;
        this.usuarioRepository = usuarioRepository;
        this.sugestaoRepository = sugestaoRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    public List<Evento> listarEventos(String categoria) {
        if (categoria == null || categoria.isBlank() || categoria.equalsIgnoreCase("Todos")) {
            return eventoRepository.findByDataAfterOrderByDataAsc(LocalDateTime.now());
        }
        return eventoRepository.findByCategoriaAndDataAfterOrderByDataAsc(categoria, LocalDateTime.now());
    }

    public List<Evento> listarEventosPorOrganizador(int organizadorId) {
        return eventoRepository.findByOrganizadorIdOrderByDataAsc(organizadorId);
    }


    @Transactional
    public Evento salvarEvento(Evento evento, int organizadorId) {
        if (evento == null) {
            throw new IllegalArgumentException("O evento não pode ser nulo!");
        }

        Optional<Usuario> orgOpt = usuarioRepository.findById(organizadorId);
        if (orgOpt.isEmpty()) {
            throw new RuntimeException("Organizador não localizado para criar o evento.");
        }

        evento.setOrganizador(orgOpt.get());
        return eventoRepository.save(evento);
    }

    @Transactional
    public Ingresso comprarIngresso(int eventoId, int usuarioId) {
        Optional<Evento> eventoOptional = eventoRepository.findById(eventoId);
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);

        if (eventoOptional.isEmpty()) {
            throw new RuntimeException("Evento não encontrado: " + eventoId);
        }
        Evento evento = eventoOptional.get();

        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuário inexistente: " + usuarioId);
        }
        Usuario usuario = usuarioOptional.get();

        if (evento.getIngressosVendidos() >= evento.getIngressosDisponiveis()) {
            throw new RuntimeException("Ingressos esgotados para este evento!");
        }

        evento.setIngressosVendidos(evento.getIngressosVendidos() + 1);
        eventoRepository.save(evento);

        String qrCode = "ARENA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Ingresso novoIngresso = new Ingresso(0, usuario, evento, LocalDateTime.now(), qrCode);

        return ingressoRepository.save(novoIngresso);
    }

    public List<Ingresso> listarIngressosPorUsuario(int usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return ingressoRepository.findByComprador(usuarioOpt.get());
    }

    public List<Sugestao> listarTodasSugestoes() {
        return sugestaoRepository.findAllByOrderByDataCriacaoDesc();
    }

    public List<Sugestao> listarSugestoesPorUsuario(int usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        String marcadorEmail = "[" + usuarioOpt.get().getEmail() + "]";
        return sugestaoRepository.findAllByOrderByDataCriacaoDesc().stream()
                .filter(s -> s.getTexto() != null && s.getTexto().contains(marcadorEmail))
                .collect(Collectors.toList());
    }



    @Transactional
    public void salvarSugestaoComUsuario(String texto, int usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado para vincular a sugestão.");
        }

        String textoComTag = texto + " [" + usuarioOpt.get().getEmail() + "]";
        Sugestao novaSugestao = new Sugestao(textoComTag);
        sugestaoRepository.save(novaSugestao);
    }

    public List<Agendamento> listarAgendamentosPorUsuario(int usuarioId) {
        return agendamentoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public void salvarAgendamento(String dataVisita, String horario, int quantidadePessoas, int usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado para realizar o agendamento.");
        }

        Agendamento novoAgendamento = new Agendamento(0, usuarioOpt.get(), dataVisita, horario, quantidadePessoas);
        agendamentoRepository.save(novoAgendamento);
    }

    @Transactional
    public void excluirUsuarioDoSistema(int usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não localizado no sistema.");
        }
        Usuario usuario = usuarioOpt.get();

        List<Ingresso> ingressosDoUsuario = ingressoRepository.findByComprador(usuario);
        ingressoRepository.deleteAll(ingressosDoUsuario);

        String marcadorEmail = "[" + usuario.getEmail() + "]";
        List<Sugestao> sugestoesDoUsuario = interstateSugestoesParaDelecao(marcadorEmail);
        sugestaoRepository.deleteAll(sugestoesDoUsuario);

        agendamentoRepository.deleteByUsuarioId(usuarioId);

        usuarioRepository.deleteById(usuarioId);
    }

    private List<Sugestao> interstateSugestoesParaDelecao(String marcador) {
        return sugestaoRepository.findAll().stream()
                .filter(s -> s.getTexto() != null && s.getTexto().contains(marcador))
                .collect(Collectors.toList());
    }
    @Transactional
    public void excluirEventoDoSistema(int eventoId) {
        Optional<Evento> eventoOpt = eventoRepository.findById(eventoId);
        if (eventoOpt.isEmpty()) {
            throw new RuntimeException("O evento informado já foi removido ou não existe.");
        }

        eventoRepository.deleteById(eventoId);
    }
}