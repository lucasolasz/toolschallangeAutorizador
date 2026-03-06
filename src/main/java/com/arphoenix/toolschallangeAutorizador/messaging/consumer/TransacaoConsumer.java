package com.arphoenix.toolschallangeAutorizador.messaging.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.arphoenix.toolschallangeAutorizador.domain.enums.StatusTransacao;
import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoRequestRecord;
import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoResponseRecord;
import com.arphoenix.toolschallangeAutorizador.util.ObjetoResponseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransacaoConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransacaoConsumer.class);
    // private RestClient restClient;

    @KafkaListener(topics = "endas-pendentes", groupId = "kafka-desafio-arphoenix")
    public void consumirFinalizacaoDePagamento(PagamentoRequestRecord request) {
        LOGGER.info("Recebendo o pagamento para ser autorizado transação ID: {}", request.transacao().id());

        // boolean autorizado = autorizacaoClient.autorizarTransacao(request);
        boolean autorizado = true;

        if (!autorizado) {
            criarRespostaRecusada(request);
        } else {
            criarRespostaAutorizada(request);
        }

    }

    // public boolean NotificationConsumer(RestClient.Builder builder) {
    // this.restClient = builder
    // .baseUrl("https://api.random.org/json-rpc/4/basic")
    // .build();
    // }

    private PagamentoResponseRecord criarRespostaAutorizada(PagamentoRequestRecord request) {

        var input = request.transacao();

        String nsu = ObjetoResponseUtil.gerarNSU();
        String codAutorizacao = ObjetoResponseUtil.gerarCodigoAutorizacao();
        StatusTransacao status = StatusTransacao.AUTORIZADO;

        var descricaoOutput = new PagamentoResponseRecord.DescricaoOutput(
                input.descricao().valor(),
                input.descricao().dataHora(),
                input.descricao().estabelecimento(),
                nsu,
                codAutorizacao,
                status);

        var formaPagamentoOutput = new PagamentoResponseRecord.FormaPagamentoOutput(
                input.formaPagamento().tipo(),
                input.formaPagamento().parcelas());

        var transacaoOutput = new PagamentoResponseRecord.TransacaoOutput(
                input.id(),
                input.cartao(),
                descricaoOutput,
                formaPagamentoOutput);

        return new PagamentoResponseRecord(transacaoOutput);
    }

    private PagamentoResponseRecord criarRespostaRecusada(PagamentoRequestRecord request) {

        var input = request.transacao();

        var descricaoOutput = new PagamentoResponseRecord.DescricaoOutput(
                input.descricao().valor(),
                input.descricao().dataHora(),
                input.descricao().estabelecimento(),
                null, // nsu nulo ou vazio
                null, // código autorização nulo
                StatusTransacao.NEGADO // ou outro status que você preferir
        );

        var formaPagamentoOutput = new PagamentoResponseRecord.FormaPagamentoOutput(
                input.formaPagamento().tipo(),
                input.formaPagamento().parcelas());

        var transacaoOutput = new PagamentoResponseRecord.TransacaoOutput(
                input.id(),
                input.cartao(),
                descricaoOutput,
                formaPagamentoOutput);

        return new PagamentoResponseRecord(transacaoOutput);
    }
}