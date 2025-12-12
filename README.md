# TrabalhoFinalEstruturaDeDados# ⛏️ Jogo de Mineração 2D (Trabalho Final - EDOO)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)

Um jogo de plataforma e mineração ("Sandbox") desenvolvido em Java como Projeto Final da disciplina de Estrutura de Dados Orientada a Objetos.

O objetivo principal deste projeto foi aplicar estruturas de dados complexas (Tabela Hash Personalizada) e persistência em nuvem em um cenário prático e interativo, fugindo da teoria pura.

## 👥 Integrantes e Responsabilidades

| Aluno | Foco | Classes Principais |
| :--- | :--- | :--- |
| **Josué** | Game Design, Lógica e Interface Gráfica | `Main.java`, `JogoMineracao.java` |
| **Victor Hugo** | Estrutura de Dados (Hash Table) | `MeuHashTable.java` |
| **Guilherme** | Persistência e Banco de Dados | `SupabaseManager.java` |

---

## ✨ Funcionalidades

- **Mecânica de Jogo:**
  - Movimentação livre (W, A, S, D) e sistema de física básica.
  - Interação com o terreno: minerar (clique esquerdo) e colocar blocos (clique direito).
  - Feedback visual imediato via Java Swing.

- **Estrutura de Dados (Backend Local):**
  - Implementação manual de **Tabela Hash** (`MeuHashTable`) para gerenciamento de inventário e dados.
  - Tratamento de colisões e busca com complexidade O(1) para alta performance.
  - Não utilização de coleções prontas (como `HashMap` do Java) para fins acadêmicos.

- **Persistência na Nuvem:**
  - Integração com **Supabase** (PostgreSQL).
  - Salvamento e carregamento de estado do mundo via API REST.
  - Conversão de dados locais para JSON antes do envio.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java (JDK 17 ou superior).
- **Gerenciamento de Dependências:** Maven.
- **Interface Gráfica:** Java Swing / AWT (Nativo).
- **Banco de Dados:** Supabase.
- **Bibliotecas Externas:**
  - `org.json` (Manipulação de JSON).
  - `java.net.http` (Cliente HTTP Nativo).


---

## 🎮 Controles

| Tecla / Ação | Função |
| :---: | :--- |
| **W, A, S, D** | Movimentar o personagem |
| **Espaço** | Pular |
| **Clique Esquerdo** | Quebrar Bloco (Minerar) |
| **Clique Direito** | Colocar Bloco |
| **Tecla S** | Salvar Progresso (Upload para Supabase) |
| **Tecla L** | Carregar Progresso (Download do Supabase) |

---

## 📂 Estrutura do Código
TrabalhoFinalEstruturaDeDados/
## 📂 Estrutura do Código

```plaintext
TrabalhoFinalEstruturaDeDados/
│
├── .idea/                      # Configurações da IDE
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── projetofinal/
│                   ├── JogoMineracao.java    # Lógica do Jogo (Josué)
│                   ├── Main.java             # Ponto de entrada (Josué)
│                   ├── MeuHashTable.java     # Estrutura de dados (Victor)
│                   └── SupabaseManager.java  # Banco de Dados (Guilherme)
│
├── target/                     # Arquivos compilados (automático)
├── inventario-db.mv.db         # Banco de dados local H2 
├── pom.xml                     # Gerenciador Maven
└── README.md                   # Documentação
---
