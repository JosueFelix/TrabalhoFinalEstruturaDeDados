package com.projetofinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SupabaseManager {

    private static final String URL = "jdbc:h2:file:./inventario-db;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        criarTabelaSeNecessario();
    }

    private static void criarTabelaSeNecessario() {
        final String ddl = """
                CREATE TABLE IF NOT EXISTS inventario_jogador (
                    item_nome VARCHAR(120) PRIMARY KEY,
                    quantidade INT NOT NULL
                )
                """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
            System.out.println("Banco H2 preparado em inventario-db (arquivo local).");
        } catch (SQLException e) {
            System.err.println("Não foi possível preparar a tabela no H2: " + e.getMessage());
        }
    }

    public static void salvarInventario(String item, int quantidade) {
        final String sql = "MERGE INTO inventario_jogador (item_nome, quantidade) KEY (item_nome) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item);
            pstmt.setInt(2, quantidade);
            pstmt.executeUpdate();
            System.out.println("Salvo no banco local H2: " + item + " = " + quantidade);

        } catch (SQLException e) {
            System.err.println("Erro ao salvar no banco H2: " + e.getMessage());
        }
    }
}

