package br.com.gcbank.banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.gcbank.model.Conta;
import br.com.gcbank.model.ContaCorrente;

public class contaDAO {
      public void inserir(Conta conta) {
        String sql = "INSERT INTO conta (numero, titular, saldo) VALUES (?, ?, ?)";
        try (Connection conn = conexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, conta.getNumero());
            stmt.setString(2, conta.getTitular());
            stmt.setDouble(3, conta.getSaldo());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Conta> listar() {
        List<Conta> contas = new ArrayList<>();
        String sql = "SELECT * FROM conta";
        try (Connection conn = conexaoBanco.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Conta c = new ContaCorrente(
                        rs.getInt("numero"),
                        rs.getString("titular"),
                        rs.getDouble("saldo")
                );
                contas.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contas;
    }

    public Conta buscarPorNumero(int numero) {
        String sql = "SELECT * FROM conta WHERE numero = ?";
        try (Connection conn = conexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ContaCorrente(
                        rs.getInt("numero"),
                        rs.getString("titular"),
                        rs.getDouble("saldo")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void atualizarSaldo(int numero, double novoSaldo) {
        String sql = "UPDATE conta SET saldo= ? WHERE numero = ?";
        try (Connection conn = conexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, novoSaldo);
            stmt.setInt(2, numero);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remover(int numero) { 
        String sql = "DELETE FROM conta WHERE numero = ?";
        try (Connection conn = conexaoBanco.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numero);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void transferir(int numeroOrigem, int numeroDestino, double valor) {
    String debitoSQL = "UPDATE conta SET saldo = saldo - ? WHERE numero = ?";
    String creditoSQL = "UPDATE conta SET saldo = saldo + ? WHERE numero = ?";

    try (Connection conn = conexaoBanco.getConnection()) {
        conn.setAutoCommit(false); 

        try (PreparedStatement debitoStmt = conn.prepareStatement(debitoSQL);
             PreparedStatement creditoStmt = conn.prepareStatement(creditoSQL)) {

            debitoStmt.setDouble(1, valor);
            debitoStmt.setInt(2, numeroOrigem);
            debitoStmt.executeUpdate();

            creditoStmt.setDouble(1, valor);
            creditoStmt.setInt(2, numeroDestino);
            creditoStmt.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            conn.rollback(); 
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}

