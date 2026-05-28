package com.conectaarena.controller;

import com.conectaarena.model.Usuario;
import com.conectaarena.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AutenticacaoController {

    private final UsuarioService usuarioService;

    public AutenticacaoController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String telaLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String efectuarLogin(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.autenticar(email, senha);

        if (usuarioOpt.isPresent()){
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuarioLogado", usuario);

            if ("ADMIN".equalsIgnoreCase(usuario.getPerfil())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/";
            }
        }

        model.addAttribute("erro", "E-mail ou senha inválidos.");
        return "login";
    }

    @GetMapping("/cadastro")
    public String telaCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String efectuarCadastro(jakarta.servlet.http.HttpServletRequest request, Model model) {
        try {
            String perfil = request.getParameter("perfil");
            String email = request.getParameter("email");
            String senha = request.getParameter("senha");
            String aceitouTermosStr = request.getParameter("aceitouTermos");
            boolean aceitouTermos = "on".equals(aceitouTermosStr) || "true".equals(aceitouTermosStr);

            if (senha == null || senha.isBlank()) {
                throw new IllegalArgumentException("Senha é obrigatória!");
            }
            if (senha.length() < 8) {
                throw new IllegalArgumentException("A senha deve conter no mínimo 8 caracteres!");
            }

            Usuario novoUsuario = new Usuario();
            novoUsuario.setPerfil(perfil);
            novoUsuario.setEmail(email);
            novoUsuario.setSenha(senha);
            novoUsuario.setId(0);

            if ("ADMIN".equalsIgnoreCase(perfil)) {
                String chaveAdmin = request.getParameter("chaveAdmin");
                if (!"ARENA2026".equals(chaveAdmin)) {
                    throw new IllegalArgumentException("Chave de acesso corporativo inválida!");
                }
                novoUsuario.setNome("Organizador Arena");
                novoUsuario.setCpf("00000000000");
                novoUsuario.setDataNascimento(LocalDate.now().minusDays(1));
                novoUsuario.setAceitouTermos(true);
            }
            else {
                String nome = request.getParameter("nome");
                String cpf = request.getParameter("cpf");
                String dataNascStr = request.getParameter("dataNascimento");
                String telefone = request.getParameter("telefone");

                if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório!");
                if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("CPF é obrigatório!");
                if (dataNascStr == null || dataNascStr.isBlank()) throw new IllegalArgumentException("Data de nascimento é obrigatória!");

                LocalDate dataNascimento = LocalDate.parse(dataNascStr);
                if (dataNascimento.isBefore(LocalDate.now().minusYears(100))) {
                    throw new IllegalArgumentException("Data de nascimento inválida. A idade não pode ser superior a 100 anos.");
                }

                novoUsuario.setNome(nome);
                novoUsuario.setCpf(cpf);
                novoUsuario.setDataNascimento(dataNascimento);
                novoUsuario.setTelefone(telefone);
                novoUsuario.setAceitouTermos(aceitouTermos);
            }

            if (Boolean.TRUE.equals(novoUsuario.getAceitouTermos())) {
                novoUsuario.setDataHoraConsentimento(LocalDateTime.now());
            }

            usuarioService.cadastrarUsuario(novoUsuario);
            return "redirect:/login?sucesso=true";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", new Usuario());
            return "cadastro";
        } catch (Exception e) {
            Throwable causa = e;
            while (causa != null) {
                if (causa instanceof jakarta.validation.ConstraintViolationException) {
                    jakarta.validation.ConstraintViolationException cve = (jakarta.validation.ConstraintViolationException) causa;
                    String mensagemLimpa = cve.getConstraintViolations().iterator().next().getMessage();
                    model.addAttribute("erro", mensagemLimpa);
                    model.addAttribute("usuario", new Usuario());
                    return "cadastro";
                }
                causa = causa.getCause();
            }

            model.addAttribute("erro", "Erro ao processar o cadastro: " + e.getMessage());
            model.addAttribute("usuario", new Usuario());
            return "cadastro";
        }
    }

    @GetMapping("/termos")
    public String exibirTermosDeUso() {
        return "termos";
    }

    @GetMapping("/privacidade")
    public String exibirPoliticaPrivacidade() {
        return "privacidade";
    }

    @GetMapping("/logout")
    public String efectuarLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/perfil/excluir")
    public String solicitarExclusaoDados(HttpSession session, Model model) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado != null) {
            usuarioService.anonimizarUsuario(usuarioLogado.getId());
            session.invalidate();
            return "redirect:/login?excluido=true";
        }

        return "redirect:/login";
    }
}