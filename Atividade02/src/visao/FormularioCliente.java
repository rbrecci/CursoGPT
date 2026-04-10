package visao;

import modelo.Cliente;
import java.util.Scanner;

public class FormularioCliente {

    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("=== Cadastro de Novo Cliente ===");

        System.out.print("Digite o nome completo: ");
        String nome = leitor.nextLine();

        System.out.print("Digite o e-mail: ");
        String email = leitor.nextLine();

        // Validação simples conforme solicitado
        if (validarEmail(email)) {
            // Se válido, instanciamos o objeto (Modelo)
            Cliente novoCliente = new Cliente(nome, email);

            // "Salvando" e imprimindo no console
            salvar(novoCliente);
        } else {
            System.err.println("Erro: E-mail inválido! O campo deve conter '@'.");
        }

        leitor.close();
    }

    /**
     * Valida se o e-mail possui os requisitos mínimos.
     */
    private static boolean validarEmail(String email) {
        return email != null && email.contains("@");
    }

    /**
     * Simula a persistência dos dados.
     */
    private static void salvar(Cliente cliente) {
        System.out.println("\n[Sistema] Salvando dados...");
        // Imprime os dados usando o método toString da classe Cliente
        System.out.println(cliente);
        System.out.println("[Sistema] Cliente salvo com sucesso!");
    }
}