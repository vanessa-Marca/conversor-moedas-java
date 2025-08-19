import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Scanner;

public class ConversorMoedas {

    public static String buscarCotacoes(String apiKey) throws Exception {
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("Erro ao buscar cotação: " + response.statusCode());
        }
    }

    public static JsonObject extrairRates(String jsonString) {
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");
        return rates;
    }

    public static double converter(double valor, double taxaOrigem, double taxaDestino) {
        double valorEmUSD = valor / taxaOrigem;
        return valorEmUSD * taxaDestino;
    }

    public static void menu(JsonObject rates) {
        Scanner sc = new Scanner(System.in);
        String[] moedas = {"ARS", "BOB", "BRL", "CLP", "COP", "USD"};

        while (true) {
            System.out.println("Escolha a moeda de origem:");
            for (int i = 0; i < moedas.length; i++) {
                System.out.println((i + 1) + " - " + moedas[i]);
            }
            int opOrigem = sc.nextInt();

            System.out.println("Escolha a moeda de destino:");
            for (int i = 0; i < moedas.length; i++) {
                System.out.println((i + 1) + " - " + moedas[i]);
            }
            int opDestino = sc.nextInt();

            System.out.println("Digite o valor a ser convertido:");
            double valor = sc.nextDouble();

            double taxaOrigem = rates.get(moedas[opOrigem - 1]).getAsDouble();
            double taxaDestino = rates.get(moedas[opDestino - 1]).getAsDouble();

            double resultado = converter(valor, taxaOrigem, taxaDestino);

            System.out.printf("Resultado: %.2f %s = %.2f %s%n", valor, moedas[opOrigem - 1], resultado, moedas[opDestino - 1]);

            System.out.println("Deseja fazer outra conversão? (1-Sim / 2-Não)");
            int continuar = sc.nextInt();

            if (continuar != 1) break;
        }
        sc.close();
    }

    public static void main(String[] args) {
        String suaApiKey = "64de44d00a8272980b883a04"; // substitua pela sua chave real da API

        try {
            String jsonResposta = buscarCotacoes(suaApiKey);
            JsonObject rates = extrairRates(jsonResposta);
            menu(rates);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
