package com.arphoenix.toolschallangeAutorizador.messaging.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoResponseRecord;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PagamentoProducer {

    private final KafkaTemplate<String, PagamentoResponseRecord> kafkaTemplate;

    public void enviarPagamentoParaTopico(PagamentoResponseRecord pagamentoResponse) {
        kafkaTemplate.send("vendas-finalizadas", pagamentoResponse);
    }

}