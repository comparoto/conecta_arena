package com.conectaarena.service;

import com.conectaarena.model.Usuario;
import com.conectaarena.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario cadastrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado!");
        }
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new IllegalArgumentException("Este CPF já está cadastrado!");
        }

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent() && passwordEncoder.matches(senha, usuarioOpt.get().getSenha())) {
            return usuarioOpt;
        }

        return Optional.empty();
    }
    @Transactional
    public void anonimizarUsuario(int usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado!"));

        usuario.setNome("Usuário Anonimizado (LGPD)");
        usuario.setEmail("anonimo_" + java.util.UUID.randomUUID().toString().substring(0, 8) + "@lgpd.conectaarena.com");
        usuario.setCpf("000.000.000-00");
        usuario.setSenha("ANONIMIZADO_" + java.util.UUID.randomUUID());
        usuario.setTelefone("00000000000");

        usuarioRepository.save(usuario);
    }
}