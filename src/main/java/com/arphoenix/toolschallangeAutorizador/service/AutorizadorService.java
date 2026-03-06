package com.arphoenix.toolschallangeAutorizador.service;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoRequestRecord;
import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoResponseRecord;
import com.arphoenix.toolschallangeAutorizador.messaging.producer.PagamentoProducer;
import com.arphoenix.toolschallangeAutorizador.util.ObjetoResponseUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutorizadorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutorizadorService.class);
    private final PagamentoProducer pagamentoProducer;

    public void processarAutorizacaoPagamento(PagamentoRequestRecord request) {
        LOGGER.info("Processando autorização de pagamento para transação ID: {}", request.transacao().id());

        boolean autorizado = isPagamentoAutorizado();
        PagamentoResponseRecord resposta;

        if (!autorizado) {
            resposta = ObjetoResponseUtil.criarRespostaRecusada(request);
        } else {
            resposta = ObjetoResponseUtil.criarRespostaAutorizada(request);
        }

        pagamentoProducer.enviarPagamentoParaTopico(resposta);
        LOGGER.info("Pagamento processado para transação ID: {}. Status: {}. NSU: {}", request.transacao().id(),
                resposta.transacao().descricao().status(), resposta.transacao().descricao().nsu());
    }

    // Retorna um valor booleano aleatório para simular a autorização ou recusa do
    // pagamento
    public boolean isPagamentoAutorizado() {
        return ThreadLocalRandom.current().nextBoolean();
    }

}
