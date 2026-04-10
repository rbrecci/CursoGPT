package modelo;

public class Cliente {
    private String nome;
    private String email;

    // Construtor para instanciar o objeto com dados
    public Cliente(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    // Getters e Setters (Boas práticas de encapsulamento)
    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "--- Cliente Cadastrado ---\n" +
                "Nome: " + nome + "\n" +
                "E-mail: " + email + "\n" +
                "--------------------------";
    }
}