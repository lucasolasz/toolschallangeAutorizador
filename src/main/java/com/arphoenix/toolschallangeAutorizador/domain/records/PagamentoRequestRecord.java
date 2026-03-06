package com.arphoenix.toolschallangeAutorizador.domain.records;

public record PagamentoRequestRecord(
                TransacaoInput transacao) {

        public record TransacaoInput(
                        String cartao,
                        String id,
                        DescricaoInput descricao,
                        FormaPagamentoInput formaPagamento) {
        }

        public record DescricaoInput(
                        String valor,
                        String dataHora,
                        String estabelecimento) {
        }

        public record FormaPagamentoInput(
                        String tipo,
                        String parcelas) {
        }
}