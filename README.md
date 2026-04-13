## 1. Atividade: Registro de Clientes com Banco de Dados (`Atv1.java`)
**Foco:** Swing, JDBC (MySQL) e Validação de CPF.

> **Prompt:** "Crie uma aplicação Java Swing para cadastro de clientes utilizando `GridBagLayout`. O formulário deve conter campos para Nome, E-mail, Telefone (com máscara) e CPF (com máscara). Implemente uma função robusta para validar o algoritmo do CPF (dígitos verificadores). Ao clicar em 'Salvar', o sistema deve validar o CPF, limpar caracteres não numéricos e persistir os dados em um banco de dados MySQL chamado `sistema_clientes` na tabela `clientes` (colunas: nome, email, telefone, cpf). Inclua um botão 'Limpar' para resetar o formulário e use `JOptionPane` para feedbacks."

---

## 2. Atividade: Console App com Validação Simples (`FormularioCliente.java`)
**Foco:** Lógica básica, POO e Scanner.

> **Prompt:** "Desenvolva uma aplicação Java simples de console para cadastro de clientes. O programa deve pertencer ao pacote `visao` e interagir com uma classe `Cliente` no pacote `modelo`. Use a classe `Scanner` para ler o Nome e o E-mail do usuário. Implemente uma regra de validação que verifique se o e-mail contém o caractere '@'. Se for válido, instancie o objeto e exiba uma mensagem confirmando o salvamento (simulado via console); caso contrário, exiba uma mensagem de erro."

---

## 3. Atividade: Listagem em Tabela com FlatLaf (`CadastroClientes.java`)
**Foco:** Interface Moderna, JTable e ArrayList.

> **Prompt:** "Crie um programa em Java Swing para gerenciar um cadastro de clientes, utilizando a biblioteca `FlatLaf` (FlatLightLaf) para um visual moderno. A interface deve ser dividida em duas partes: um formulário no topo (`GridLayout`) e uma tabela (`JTable`) na parte inferior para listagem. Armazene os dados em um `ArrayList` de objetos `Cliente`. Campos necessários: Nome, Nascimento, CPF, Email e Celular (todos os campos de data e números devem ter máscaras). O sistema deve validar se o CPF é real e se nenhum campo está vazio antes de adicionar o cliente à lista e atualizar a tabela."

---

## 4. Atividade: Controle de Movimentação em Arquivo TXT (`FormularioAppChallenge.java`)
**Foco:** ComboBox, File I/O (Append) e Persistência em Texto.

> **Prompt:** "Desenvolva um 'Sistema de Controle de Movimentação - Versão Beta' em Java Swing. Utilize um `GridLayout` para organizar campos de: Data (com máscara), Operador (ComboBox), Fornecedor (ComboBox), Produto (ComboBox), Quantidade (JTextField) e Tipo de Movimentação (ComboBox: Entrada/Saída). Implemente a persistência em um arquivo chamado `dados_movimentacao.txt`. Importante: o arquivo não deve ser sobrescrito a cada salvamento (use o modo append). Adicione um botão 'Ver Registros' que leia o conteúdo do arquivo e o exiba em um `JTextArea` dentro de um `JScrollPane` em uma janela de diálogo."

---

## 5. Atividade: Formulário de Cadastro e Máscaras (`CadastroCliente.java`)
**Foco:** Validação de interface e Componentes Formatados.

> **Prompt:** "Construa um formulário de 'Cadastro de Cliente' em Java Swing usando `GridBagLayout`. O foco principal deve ser a experiência do usuário com campos formatados. Utilize `MaskFormatter` para os campos de Telefone `(##) #####-####` e CPF `###.###.###-##`. Implemente a lógica completa de validação de CPF (cálculo de 1º e 2º dígito). Ao clicar em salvar, se o CPF for válido, exiba todos os dados digitados em um `JOptionPane`. O botão 'Limpar' deve limpar todos os campos e resetar os valores dos campos formatados."
