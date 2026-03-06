package com.arphoenix.toolschallangeAutorizador.util;

import java.util.Random;
import java.util.UUID;

import com.arphoenix.toolschallangeAutorizador.domain.enums.StatusTransacao;
import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoRequestRecord;
import com.arphoenix.toolschallangeAutorizador.domain.records.PagamentoResponseRecord;

public class ObjetoResponseUtil {

    public static String gerarNSU() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    public static String gerarCodigoAutorizacao() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    // Boa prática: nunca retornar o número completo do cartão na resposta
    public static String mascararCartao(String cartao) {
        if (cartao == null || cartao.length() < 11)
            return cartao;
        return "**** **** **** " + cartao.substring(cartao.length() - 4);
    }

    public static PagamentoResponseRecord criarRespostaAutorizada(PagamentoRequestRecord request) {

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

    public static PagamentoResponseRecord criarRespostaRecusada(PagamentoRequestRecord request) {

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
