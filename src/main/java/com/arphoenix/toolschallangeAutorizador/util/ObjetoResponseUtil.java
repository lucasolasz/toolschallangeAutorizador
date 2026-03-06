package com.arphoenix.toolschallangeAutorizador.util;

import java.util.Random;
import java.util.UUID;

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
}
