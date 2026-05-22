package com.conectaarena.model;

import com.conectaarena.util.CryptoConverter;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nome;
    private String email;

    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String senha;

    private String perfil;

    @Convert(converter = CryptoConverter.class)
    @Pattern(regexp = "^\\d{11}$", message = "O CPF deve conter exatamente 11 dígitos numéricos, sem pontos ou hífen.")
    private String cpf;

    @Past(message = "A data de nascimento deve ser uma data passada.")
    private LocalDate dataNascimento;

    @Convert(converter = CryptoConverter.class)
    @Pattern(regexp = "^\\d{11}$", message = "O telefone deve conter exatamente 11 dígitos numéricos (DDD + 9 + número), sem espaços ou traços.")
    private String telefone;

    private Boolean aceitouTermos = false;
    private LocalDateTime dataHoraConsentimento;

    public Usuario() {}

    public Usuario(int id, String nome, String email, String senha, String perfil, String cpf, LocalDate dataNascimento, String telefone, Boolean aceitouTermos, LocalDateTime dataHoraConsentimento) {
        this.setId(id);
        this.setPerfil(perfil);
        this.setNome(nome);
        this.setEmail(email);
        this.setSenha(senha);
        this.setCpf(cpf);
        this.setDataNascimento(dataNascimento);
        this.setTelefone(telefone);
        this.setAceitouTermos(aceitouTermos);
        this.setDataHoraConsentimento(dataHoraConsentimento);
    }

    public int getId() { return id; }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id inválido");
        }
        this.id = id;
    }

    public String getNome() { return nome; }

    public void setNome(String nome) {
        if (!"ADMIN".equalsIgnoreCase(this.perfil)) {
            if (nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("Necessário informar o nome!");
            }
        }
        this.nome = nome;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Necessário informar e-mail!");
        }
        this.email = email;
    }

    public String getSenha() { return senha; }

    public void setSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Necessário informar uma senha!");
        }
        this.senha = senha;
    }

    public String getCpf() { return cpf; }

    public void setCpf(String cpf) {
        if (!"ADMIN".equalsIgnoreCase(this.perfil)) {
            if (cpf == null || cpf.trim().isEmpty()) {
                throw new IllegalArgumentException("Necessário informar o CPF!");
            }
        }
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() { return dataNascimento; }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (!"ADMIN".equalsIgnoreCase(this.perfil)) {
            if (dataNascimento == null) {
                throw new IllegalArgumentException("Necessário informar a data de nascimento!");
            }
        }
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) {
        if (!"ADMIN".equalsIgnoreCase(this.perfil)) {
            if (telefone == null || telefone.trim().isEmpty()) {
                throw new IllegalArgumentException("Necessário informar o telefone!");
            }
        }
        this.telefone = telefone;
    }

    public String getPerfil() { return perfil; }

    public void setPerfil(String perfil) { this.perfil = perfil; }

    public Boolean getAceitouTermos() { return aceitouTermos; }

    public void setAceitouTermos(Boolean aceitouTermos) {
        if (!"ADMIN".equalsIgnoreCase(this.perfil)) {
            if (aceitouTermos == null || !aceitouTermos) {
                throw new IllegalArgumentException("Você precisa aceitar os termos de uso e privacidade!");
            }
        }
        this.aceitouTermos = aceitouTermos;
    }

    public LocalDateTime getDataHoraConsentimento() { return dataHoraConsentimento; }

    public void setDataHoraConsentimento(LocalDateTime dataHoraConsentimento) {
        this.dataHoraConsentimento = dataHoraConsentimento;
    }
}