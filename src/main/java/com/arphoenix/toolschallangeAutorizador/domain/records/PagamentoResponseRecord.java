package com.arphoenix.toolschallangeAutorizador.domain.records;

import com.arphoenix.toolschallangeAutorizador.domain.enums.StatusTransacao;

public record PagamentoResponseRecord(TransacaoOutput transacao) {

        public record TransacaoOutput(
                        String id,
                        String cartao,
                        DescricaoOutput descricao,
                        FormaPagamentoOutput formaPagamento) {
        }

        public record DescricaoOutput(
                        String valor,
                        String dataHora,
                        String estabelecimento,
                        String nsu,
                        String codigoAutorizacao,
                        StatusTransacao status) {
        }

        public record FormaPagamentoOutput(
                        String tipo,
                        String parcelas) {
        }
}
