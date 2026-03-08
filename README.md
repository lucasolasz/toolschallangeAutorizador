# Desafio Arphoenix - Microsserviço Autorizador

## 📋 Descrição do Projeto

Este projeto é o componente **Autorizador** da solução de processamento de pagamentos. Ele atua como um simulador de uma instituição financeira ou adquirente, responsável por receber solicitações de transação, processar regras de aprovação e retornar o resultado (Aprovado ou Negado).

O serviço opera de forma totalmente reativa, consumindo mensagens de um tópico Kafka, processando a lógica de negócio e publicando a resposta em outro tópico, sem expor APIs REST diretamente para o cliente final.

### Objetivo

O objetivo deste microsserviço é desacoplar a lógica de autorização do núcleo de pagamentos, permitindo:

- Processamento assíncrono de transações.
- Simulação de latência e regras de aprovação bancária.
- Geração de dados de confirmação (NSU, Código de Autorização).
- Mascaramento de dados sensíveis antes do retorno.

## 🛠 Tecnologias Utilizadas

- **Java 21**: Linguagem base do projeto.
- **Spring Boot 3.5.11**: Framework para configuração e injeção de dependências.
- **Spring Kafka**: Para consumo e produção de mensagens no Apache Kafka.
- **Lombok**: Para redução de código boilerplate.
- **JUnit 5 & Mockito**: Para testes unitários e mocks.
- **Maven**: Gerenciamento de dependências.

## 📁 Estrutura do Projeto

```
toolschallangeAutorizador/
├── src/main/java/com/arphoenix/toolschallangeAutorizador/
│   ├── domain/
│   │   ├── enums/                 # StatusTransacao, TipoPagamento
│   │   └── records/               # DTOs imutáveis (Request/Response)
│   ├── messaging/
│   │   ├── consumer/              # Consumidor do tópico de vendas pendentes
│   │   └── producer/              # Produtor do tópico de vendas finalizadas
│   ├── service/                   # Lógica de decisão (AutorizadorService)
│   └── util/                      # Utilitários (Gerador de NSU, Máscara de Cartão)
└── pom.xml                        # Dependências Maven
```

## 🚀 Como Executar

### Pré-requisitos
- Java 21 instalado
- Maven instalado
- Instância do Kafka rodando (via Docker Compose no projeto raiz)

### Passos

1. **Configurar Variáveis de Ambiente**:
   Certifique-se de que o arquivo `.env` existe na raiz deste módulo com o IP do Kafka.
   ```bash
   cp .env.example .env
   ```

2. **Executar a Aplicação**:
   ```bash
   mvn spring-boot:run
   ```

A aplicação iniciará e ficará aguardando mensagens no tópico `vendas-pendentes`.

## 🔄 Fluxo de Processamento e Mensageria

Este serviço não possui endpoints REST públicos. Toda a comunicação é feita via **Apache Kafka**.

### Tópicos Kafka

| Ação | Tópico | Descrição |
|---|---|---|
| **Consumo** | `vendas-pendentes` | Recebe o `PagamentoRequestRecord` enviado pelo serviço de pagamentos. |
| **Produção** | `vendas-finalizadas` | Envia o `PagamentoResponseRecord` com o status atualizado da transação. |

### Lógica de Autorização (`AutorizadorService`)

O serviço simula a aprovação de crédito de forma aleatória para fins de teste:

1. **Recebimento**: O consumidor lê a mensagem do Kafka.
2. **Decisão**: O método `isPagamentoAutorizado()` utiliza `ThreadLocalRandom` para gerar um booleano aleatório.
3. **Cenário Aprovado**:
   - Status definido como `AUTORIZADO`.
   - Gera um **NSU** (Número Sequencial Único) aleatório.
   - Gera um **Código de Autorização** (UUID parcial).
4. **Cenário Negado**:
   - Status definido como `NEGADO`.
   - NSU e Código de Autorização retornam nulos.
5. **Segurança**: O número do cartão é mascarado (ex: `**** **** **** 1234`) antes de enviar a resposta.

## 🧪 Testes

O projeto foca em testes unitários para garantir a integridade da lógica de decisão e transformação de dados.

### Executando os Testes
```bash
mvn test
```

### Cobertura Principal (`AutorizadorServiceTest`)

Os testes utilizam **Mockito** e **AssertJ** para validar os comportamentos do serviço:

#### 1. Autorização de Pagamento (Sucesso)
- **Cenário**: O sorteio aleatório retorna `true`.
- **Verificação**:
  - O status da transação deve ser `AUTORIZADO`.
  - Deve haver um NSU e Código de Autorização preenchidos.
  - O ID da transação deve ser preservado.
  - O produtor Kafka deve ser chamado uma vez.

#### 2. Negação de Pagamento (Recusa)
- **Cenário**: O sorteio aleatório retorna `false`.
- **Verificação**:
  - O status da transação deve ser `NEGADO`.
  - NSU e Código de Autorização devem ser nulos.
  - O produtor Kafka deve ser chamado uma vez.

```java
// Exemplo de Teste (Simplificado)
@Test
void processarAutorizacaoPagamento_deveAutorizarPagamento() {
    // Arrange
    doReturn(true).when(autorizadorService).isPagamentoAutorizado();

    // Act
    autorizadorService.processarAutorizacaoPagamento(request);

    // Assert
    verify(pagamentoProducer).enviarPagamentoParaTopico(responseCaptor.capture());
    assertThat(response.transacao().descricao().status()).isEqualTo(StatusTransacao.AUTORIZADO);
}
```

## ⚙️ Configurações Importantes

### Jackson Config
O projeto possui uma configuração personalizada do Jackson (`JacksonConfig`) para lidar com a desserialização de Enums, convertendo strings vazias para `null` automaticamente, o que aumenta a resiliência no consumo de mensagens.

### Utilitários
A classe `ObjetoResponseUtil` centraliza a lógica de construção das respostas, garantindo que regras como o mascaramento de cartão sejam aplicadas consistentemente tanto para transações aprovadas quanto negadas.

---

**Desenvolvido como parte do Desafio Arphoenix.**