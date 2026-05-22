package com.conectaarena.controller;

import com.conectaarena.model.Usuario;
import com.conectaarena.model.Evento;
import com.conectaarena.service.ArenaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    private final ArenaService arenaService;

    public DashboardController(ArenaService arenaService) {
        this.arenaService = arenaService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboardAdmin(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || !"ADMIN".equalsIgnoreCase(logado.getPerfil())) {
            return "redirect:/login?erro=Acesso negado.";
        }

        model.addAttribute("sugestoes", arenaService.listarTodasSugestoes());
        model.addAttribute("admin", logado);
        model.addAttribute("paginaAtiva", "dashboard");

        return "dashboard-admin";
    }

    @GetMapping("/admin/eventos/listar")
    public String listarEventosAdmin(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || !"ADMIN".equalsIgnoreCase(logado.getPerfil())) {
            return "redirect:/login?erro=Acesso negado.";
        }

        model.addAttribute("eventos", arenaService.listarEventosPorOrganizador(logado.getId()));
        model.addAttribute("admin", logado);
        model.addAttribute("paginaAtiva", "eventos-admin");

        return "admin-eventos";
    }

    @GetMapping("/admin/eventos/novo")
    public String exibirFormularioEvento(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || !"ADMIN".equalsIgnoreCase(logado.getPerfil())) {
            return "redirect:/login?erro=Acesso negado.";
        }

        model.addAttribute("admin", logado);
        model.addAttribute("paginaAtiva", "cadastro-evento");

        return "cadastro-evento";
    }

    @PostMapping("/admin/eventos/salvar")
    public String salvarEvento(@RequestParam String titulo, @RequestParam String data, @RequestParam String categoria, @RequestParam int ingressosDisponiveis,@RequestParam double preco, @RequestParam String imagem, HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || !"ADMIN".equalsIgnoreCase(logado.getPerfil())) {
            return "redirect:/login?erro=Acesso negado.";
        }

        try {
            Evento novoEvento = new Evento();
            novoEvento.setTitulo(titulo);
            novoEvento.setData(java.time.LocalDateTime.parse(data));
            novoEvento.setCategoria(categoria);
            novoEvento.setIngressosDisponiveis(ingressosDisponiveis);
            novoEvento.setIngressosVendidos(0);
            novoEvento.setPreco(preco);
            novoEvento.setImagem(imagem);

            arenaService.salvarEvento(novoEvento, logado.getId());
            return "redirect:/admin/eventos/listar?sucessoEvento=true";
        } catch (Exception exec) {
            model.addAttribute("erro", "Erro ao salvar evento: " + exec.getMessage());
            model.addAttribute("admin", logado);
            model.addAttribute("paginaAtiva", "cadastro-evento");
            return "cadastro-evento";
        }
    }
    @PostMapping("/admin/eventos/excluir")
    public String excluirEvento(@RequestParam int eventoId, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || !"ADMIN".equalsIgnoreCase(logado.getPerfil())) {
            redirectAttributes.addFlashAttribute("erro", "Acesso negado.");
            return "redirect:/login";
        }

        try {
            arenaService.excluirEventoDoSistema(eventoId);
            redirectAttributes.addFlashAttribute("sucesso", "Evento removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Não foi possível excluir o evento: " + e.getMessage());
        }

        return "redirect:/admin/eventos/listar";
    }

    @GetMapping("/admin/eventos/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable int id, HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || !"ADMIN".equalsIgnoreCase(logado.getPerfil())) {
            return "redirect:/login?erro=Acesso negado.";
        }

        Evento evento = arenaService.listarEventosPorOrganizador(logado.getId()).stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Evento não encontrado."));

        model.addAttribute("evento", evento);
        model.addAttribute("admin", logado);
        model.addAttribute("paginaAtiva", "cadastro-evento");

        return "editar-evento";
    }

    @PostMapping("/admin/eventos/atualizar")
    public String atualizarEvento(@RequestParam int id, @RequestParam String titulo, @RequestParam String data, @RequestParam String categoria, @RequestParam int ingressosDisponiveis, @RequestParam double preco, @RequestParam String imagem, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || !"ADMIN".equalsIgnoreCase(logado.getPerfil())) {
            return "redirect:/login?erro=Acesso negado.";
        }

        try {
            Evento eventoExistente = arenaService.listarEventosPorOrganizador(logado.getId()).stream()
                    .filter(e -> e.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Evento não encontrado."));

            eventoExistente.setTitulo(titulo);
            eventoExistente.setData(java.time.LocalDateTime.parse(data));
            eventoExistente.setCategoria(categoria);
            eventoExistente.setIngressosDisponiveis(ingressosDisponiveis);
            eventoExistente.setPreco(preco);
            eventoExistente.setImagem(imagem);

            arenaService.salvarEvento(eventoExistente, logado.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Evento atualizado com sucesso!");
            return "redirect:/admin/eventos/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar evento: " + e.getMessage());
            return "redirect:/admin/eventos/listar";
        }
    }

    @GetMapping("/usuario/dashboard")
    public String dashboardUsuario(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null) {
            return "redirect:/login";
        }

        model.addAttribute("ingressos", arenaService.listarIngressosPorUsuario(logado.getId()));
        model.addAttribute("sugestoes", arenaService.listarSugestoesPorUsuario(logado.getId()));
        model.addAttribute("agendamentos", arenaService.listarAgendamentosPorUsuario(logado.getId()));
        model.addAttribute("paginaAtiva", "dashboard");

        return "dashboard-usuario";
    }

    @PostMapping("/usuario/excluir-conta")
    public String excluirConta(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null) {
            return "redirect:/login";
        }

        try {
            arenaService.excluirUsuarioDoSistema(logado.getId());
            session.invalidate();
            redirectAttributes.addFlashAttribute("sucesso", "Sua conta foi removida com sucesso.");
            return "redirect:/login";
        } catch (Exception excecao) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir conta: " + excecao.getMessage());
            return "redirect:/usuario/dashboard";
        }
    }

    @PostMapping("/usuario/comprar")
    public String comprarIngresso(@RequestParam int eventoId, HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null) {
            return "redirect:/login";
        }

        try {
            arenaService.comprarIngresso(eventoId, logado.getId());
            return "redirect:/usuario/dashboard?sucessoCompra=true";
        } catch (RuntimeException excecao) {
            return "redirect:/usuario/dashboard?erroCompra=" + excecao.getMessage();
        }
    }

    @PostMapping("/visitas/agendar")
    public String processarAgendamento(@RequestParam("dataVisita") String dataVisita, @RequestParam("horario") String horario, @RequestParam("quantidadePessoas") int quantidadePessoas, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null) {
            return "redirect:/login";
        }

        try {
            arenaService.salvarAgendamento(dataVisita, horario, quantidadePessoas, logado.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Visita agendada com sucesso! Acompanhe no seu painel.");
            return "redirect:/usuario/dashboard";
        } catch (Exception excecao) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao agendar visita: " + excecao.getMessage());
            return "redirect:/visitas/agendar";
        }
    }
}