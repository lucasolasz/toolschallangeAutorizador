package com.arphoenix.toolschallangeAutorizador.messaging.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoRequestRecord;
import com.arphoenix.toolschallangeAutorizador.service.AutorizadorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransacaoConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransacaoConsumer.class);
    private final AutorizadorService autorizadorService;
    // private RestClient restClient;

    @KafkaListener(topics = "vendas-pendentes", groupId = "kafka-desafio-autorizador")
    public void consumirFinalizacaoDePagamento(PagamentoRequestRecord request) {
        LOGGER.info("Recebendo o pagamento para ser autorizado transação ID: {}", request.transacao().id());

        autorizadorService.processarAutorizacaoPagamento(request);
    }

}