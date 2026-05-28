# Conecta Arena 🏟️
![logo](logo-conecta-arena-readme.png)

O projeto Conecta Arena foi desenvolvido com o objetivo de criar uma aplicação web voltada para a Arena Pernambuco, visando solucionar o problema de baixa ocupação efetiva e subutilização do espaço. O sistema possibilitará a conexão entre a administração do equipamento público e os cidadãos, facilitando a divulgação de eventos culturais, esportivos e corporativos.

## 📋 Índice

* [Entrega 1](#entrega-1)
* [Entrega 2](#entrega-2)
* [Entrega 3](#entrega-3)
* [Entrega 4](#entrega-4)
---

## Entrega 1

- [Histórias de Usuário - Google Docs](https://docs.google.com/document/d/14mVCGJEfeiR7oN51TRB3UKwZIKDrzhR2z3nLTI25dKo/edit?usp=sharing)
- [Protótipo Figma](https://www.figma.com/design/MF9wEsv166XqiNlBZGceJP/Conecta-Arena?node-id=3-485&t=LgbOso7bPJNhc88U-1)
- [Screencast Protótipo Figma](https://youtu.be/1O8QA4T2W_Q?si=C8w6k2fqdcu9Mezj)

##  Entrega 2

- Implementação de 2 HU✔️
- [Screencast 2 HU](https://youtu.be/ehbncqpZdrE)
- Issues atualizado ✔️

##  Entrega 3

- Implementação de 2 HU✔️
- [Screencast 2 HU](https://www.youtube.com/watch?si=iUFOywEshCSOe54E&v=_nZ3VLmfrmI&feature=youtu.be)
- Issues atualizado ✔️
  
##  Entrega 4

- Implementação de 2 HU✔️
- [Screencast semifinal](https://www.youtube.com/watch?si=iUFOywEshCSOe54E&v=_nZ3VLmfrmI&feature=youtu.be)
- Issues atualizado ✔️
- Documentação final

### Issues/Bug Tracker
[issues](https://github.com/comparoto/conecta_arena/issues)

#### Relatório Evolutivo
- [Relatório](https://docs.google.com/document/d/1HQrfCskFHfdq33UlZEy46uq7AfxOLlP3F9qMNpJru8o/edit?usp=sharing)


## 👩‍💻 Equipe 
- [Iza Malafaia](https://github.com/Iza-Malafaia) 
- [Juliana Comparoto](https://github.com/comparoto) 
- [Joanna Farias](https://github.com/Joanna-Farias) 
- [Maria Luiza](https://github.com/alumiria)

  ## ⚙️ Guia 
Montando o ambiente corretamente para execução do projeto:

<details>
<summary>🌐 <b>1. Tecnologias Utilizadas</b></summary>

* **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA
* **Banco de Dados:** PostgreSQL
* **Frontend:** HTML5, CSS3, Thymeleaf
* **Gerenciador de Dependências:** Maven
</details>

<details>
<summary>🛠️ <b>2. Como Configurar e Executar o Projeto</b></summary>

Siga os passos abaixo para montar o ambiente e rodar a aplicação localmente na sua máquina.

### 1. Pré-requisitos
Antes de começar, certifique-se de ter instalado em sua máquina:
* **Java JDK 17** (ou superior)
* **Git**
* **PostgreSQL 16** (ou superior)
* **pgAdmin 4** (ou ferramenta similar como DBeaver) 
* Uma IDE de sua preferência (Recomendado: **IntelliJ IDEA** ou VS Code com extensões Java)

  Após clonar, crie um banco de dados vazio chamado conecta_db no seu PostgreSQL e ajuste a propriedade spring.datasource.password no arquivo application-dev.properties com a sua senha mestre 
### 2. Clonar o Repositório
Abra o seu terminal/prompt de comando e execute o comando abaixo para clonar o projeto:

git clone [https://github.com/SEU_USUARIO/conecta-arena.git](https://github.com/SEU_USUARIO/conecta-arena.git)

Em seguida, entre na pasta do projeto:

cd conecta-arena

### 3. Abrir e Compilar na IDE (IntelliJ IDEA)
Abra o IntelliJ IDEA.

Clique em Open (Abrir) e selecione a pasta raiz do projeto conecta-arena.

Aguarde o Maven baixar todas as dependências automáticas do projeto (acompanhe a barra de progresso no canto inferior direito).

Para garantir que tudo foi importado sem erros, vá no menu superior e clique em Build -> Rebuild Project.

### 4. Executar a Aplicação
Localize a classe principal do sistema em:
src/main/java/com/conectaarena/ConectaArenaApplication.java

Clique com o botão direito em cima dela e selecione Run 'ConectaArenaApplication', ou clique no botão de "Play" verde no topo da IDE.

O console deverá exibir os logs de inicialização. Quando aparecer a mensagem do Tomcat rodando na porta 8080, o sistema estará online.
</details>

<details>
<summary>🌐 <b>3. URLs de acesso</b></summary>

Com o sistema executando, abra o seu navegador e acesse a rota abaixo:

- Aplicação (Home Pública): http://localhost:8080/
- Deploy (Não precisa rodar a aplicação): https://conecta-arena.onrender.com
</details>
