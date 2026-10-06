# LoginSeguro

Sistema acadêmico de login e gerenciamento de usuários com Java 25, Spring Boot, Thymeleaf e MongoDB Atlas.

## Funcionalidades

- Cadastro, login e logout.
- Senhas protegidas com BCrypt e validação dos formulários.
- Autenticação com JWT em cookie HttpOnly e proteção CSRF.
- Usuários e sessões armazenados no MongoDB Atlas.
- Perfis `USER`, `MANAGER` e `ADMIN`.
- Listagem paginada de usuários, alteração de perfil e ativação ou desativação de contas.

O usuário cadastrado recebe o perfil `USER`. O `MANAGER` pode consultar usuários e o `ADMIN` também pode gerenciar seus perfis e status. Para configurar o primeiro administrador, altere o campo `role` da sua conta para `ADMIN` no Atlas.

## Como executar

É necessário ter o JDK 25, o IntelliJ IDEA e um cluster no MongoDB Atlas. O projeto inclui o Maven Wrapper.

1. Abra o projeto no IntelliJ e aguarde a importação das dependências Maven.
2. Configure o JDK 25 como SDK do projeto.
3. No Atlas, crie um usuário de banco com acesso ao banco `login_seguro`, autorize seu IP em **Network Access** e copie a conexão em **Connect → Drivers**.
4. Em **Run → Edit Configurations**, selecione a configuração da aplicação e preencha **Environment variables**:

| Variável | Valor |
| --- | --- |
| `MONGODB_URI` | URI do Atlas: `mongodb+srv://<usuario>:<senha>@<cluster>/?retryWrites=true&w=majority` |
| `JWT_SECRET` | Chave aleatória em Base64 com pelo menos 32 bytes antes da codificação |
| `JWT_COOKIE_SECURE` | `false` para executar localmente com HTTP; `true` ao usar HTTPS |
| `JWT_EXPIRATION` | Opcional: duração do token em segundos. Padrão: `3600` |

Substitua os campos da URI pelos dados do seu cluster. Se a senha tiver caracteres especiais, codifique-os para uso na URI. Mantenha as credenciais somente na configuração local do IntelliJ, sem publicá-las no GitHub.

Para gerar o `JWT_SECRET`, execute no JShell do JDK e copie o resultado para o IntelliJ:

```java
byte[] key = new byte[32];
new java.security.SecureRandom().nextBytes(key);
System.out.println(java.util.Base64.getEncoder().encodeToString(key));
```

5. Execute a classe `LoginseguroApplication`.
6. Acesse [http://localhost:8080/login](http://localhost:8080/login) e clique em **Criar conta**.

O banco configurado é `login_seguro`, com as coleções `users` e `sessions`. Ao iniciar, a aplicação cria o índice único de e-mail para impedir cadastros duplicados.

## Estrutura

Os pacotes ficam em `src/main/java/com/loginseguro`:

| Pacote | Responsabilidade |
| --- | --- |
| `config` | Configurações da aplicação |
| `controller` | Rotas e páginas Thymeleaf |
| `domain` | Entidades e perfis de usuário |
| `dto` | Dados de entrada e saída |
| `exception` | Tratamento de erros |
| `mapper` | Conversão entre DTOs e entidades |
| `repository` | Acesso ao MongoDB |
| `security` | Autenticação e autorização |
| `service` | Regras de negócio e suas implementações |

Os templates ficam em `src/main/resources/templates`. Os estilos ficam em `src/main/resources/static/css`, e as cores do tema em `themes/default.css`. A configuração da conexão e do JWT fica em `src/main/resources/application.yaml`, que utiliza as variáveis de ambiente acima.
