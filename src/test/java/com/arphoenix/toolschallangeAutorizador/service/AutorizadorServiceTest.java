package com.arphoenix.toolschallangeAutorizador.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arphoenix.toolschallangeAutorizador.domain.enums.StatusTransacao;
import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoRequestRecord;
import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoResponseRecord;
import com.arphoenix.toolschallangeAutorizador.messaging.producer.PagamentoProducer;

@ExtendWith(MockitoExtension.class)
class AutorizadorServiceTest {

    @Mock
    private PagamentoProducer pagamentoProducer;

    @Spy
    @InjectMocks
    private AutorizadorService autorizadorService;

    @Captor
    private ArgumentCaptor<PagamentoResponseRecord> responseCaptor;

    private PagamentoRequestRecord requestRecord;

    @BeforeEach
    void setUp() {
        requestRecord = new PagamentoRequestRecord(
                new PagamentoRequestRecord.TransacaoInput(
                        "4444********8189",
                        "71649146213882",
                        new PagamentoRequestRecord.DescricaoInput(
                                "883.03",
                                "LocalDateTime.now().minusDays(1)",
                                "PetShop Mundo Cão"),
                        new PagamentoRequestRecord.FormaPagamentoInput(
                                "PARCELADO_EMISSOR",
                                "2")));
    }

    @Test
    void processarAutorizacaoPagamento_deveAutorizarPagamentoQuandoSorteioForPositivo() {
        // Arrange
        PagamentoRequestRecord request = requestRecord;
        // Spy no método que gera o booleano aleatório para garantir determinismo
        doReturn(true).when(autorizadorService).isPagamentoAutorizado();

        // Act
        autorizadorService.processarAutorizacaoPagamento(request);

        // Assert
        verify(pagamentoProducer).enviarPagamentoParaTopico(responseCaptor.capture());
        PagamentoResponseRecord response = responseCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.transacao().id()).isEqualTo(request.transacao().id());
        assertThat(response.transacao().descricao().status()).isEqualTo(StatusTransacao.AUTORIZADO);

        // Verifica se NSU e Código de Autorização foram gerados (assumindo que
        // ObjetoResponseUtil os gera)
        assertThat(response.transacao().descricao().nsu()).isNotBlank();
        assertThat(response.transacao().descricao().codigoAutorizacao()).isNotBlank();
    }

    @Test
    void processarAutorizacaoPagamento_deveNegarPagamentoQuandoSorteioForNegativo() {
        // Arrange
        PagamentoRequestRecord request = requestRecord;
        // Spy no método que gera o booleano aleatório para garantir determinismo
        doReturn(false).when(autorizadorService).isPagamentoAutorizado();

        // Act
        autorizadorService.processarAutorizacaoPagamento(request);

        // Assert
        verify(pagamentoProducer).enviarPagamentoParaTopico(responseCaptor.capture());
        PagamentoResponseRecord response = responseCaptor.getValue();
        var descricao = response.transacao().descricao();

        assertThat(response).isNotNull();
        assertThat(response.transacao().id()).isEqualTo(request.transacao().id());
        assertThat(descricao.status()).isEqualTo(StatusTransacao.NEGADO);
        assertThat(descricao.nsu()).isNull();
        assertThat(descricao.codigoAutorizacao()).isNull();
    }

    // private PagamentoRequestRecord criarPagamentoRequestRecord() {
    // return new PagamentoRequestRecord(
    // new PagamentoRequestRecord.TransacaoInput(
    // "4444********8189",
    // "71649146213882",
    // new PagamentoRequestRecord.DescricaoInput(
    // "883.03",
    // "LocalDateTime.now().minusDays(1)",
    // "PetShop Mundo Cão"),
    // new PagamentoRequestRecord.FormaPagamentoInput(
    // "PARCELADO_EMISSOR",
    // "2")));
    // }
}