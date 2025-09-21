package br.com.gcbank.banco;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.gcbank.exceptions.SaldoInsuficienteException;
import br.com.gcbank.model.ContaCorrente;

public class contaDAO {
 public void inserir(ContaCorrente c) throws SQLException {
        String sql = "INSERT INTO contas (numero, titular, saldo) VALUES (?, ?, ?)";
        try (Connection con = conexaoBanco.abrir();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getNumero());
            ps.setString(2, c.getTitular());
            ps.setBigDecimal(3, BigDecimal.valueOf(c.getSaldo()));
            ps.executeUpdate();
        }
    }

    public List<ContaCorrente> listar() throws SQLException {
        String sql = "SELECT numero, titular, saldo FROM contas ORDER BY numero";
        List<ContaCorrente> lista = new ArrayList<>();
        try (Connection con = conexaoBanco.abrir();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new ContaCorrente(
                        rs.getInt("numero"),
                        rs.getString("titular"),
                        rs.getBigDecimal("saldo").doubleValue()
                ));
            }
        }
        return lista;
    }

    public Optional<ContaCorrente> buscarPorNumero(int numero) throws SQLException {
        String sql = "SELECT numero, titular, saldo FROM contas WHERE numero = ?";
        try (Connection con = conexaoBanco.abrir();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ContaCorrente(
                            rs.getInt("numero"),
                            rs.getString("titular"),
                            rs.getBigDecimal("saldo").doubleValue()
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public boolean atualizarSaldo(int numero, double novoSaldo) throws SQLException {
        String sql = "UPDATE contas SET saldo = ? WHERE numero = ?";
        try (Connection con = conexaoBanco.abrir();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, BigDecimal.valueOf(novoSaldo));
            ps.setInt(2, numero);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean remover(int numero) throws SQLException {
        String sql = "DELETE FROM contas WHERE numero = ?";
        try (Connection con = conexaoBanco.abrir();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, numero);
            return ps.executeUpdate() > 0;
        }
    }

    // ===== Transferência com transação =====
    public void transferir(int numeroOrigem, int numeroDestino, double valor)
            throws SQLException, IllegalArgumentException, SaldoInsuficienteException {

        if (numeroOrigem == numeroDestino) throw new IllegalArgumentException("Contas de origem e destino devem ser diferentes.");
        if (valor <= 0) throw new IllegalArgumentException("Valor de transferência inválido.");

        String lockSql = "SELECT numero, saldo FROM contas WHERE numero IN (?, ?) FOR UPDATE";
        String updSql  = "UPDATE contas SET saldo = ? WHERE numero = ?";

        try (Connection con = conexaoBanco.abrir()) {
            con.setAutoCommit(false);
            try (PreparedStatement lock = con.prepareStatement(lockSql);
                PreparedStatement upd  = con.prepareStatement(updSql)) {

                lock.setInt(1, numeroOrigem);
                lock.setInt(2, numeroDestino);

                Double saldoOrigem = null, saldoDestino = null;
                try (ResultSet rs = lock.executeQuery()) {
                    while (rs.next()) {
                        int num = rs.getInt("numero");
                        double s = rs.getBigDecimal("saldo").doubleValue();
                        if (num == numeroOrigem)  saldoOrigem = s;
                        if (num == numeroDestino) saldoDestino = s;
                    }
                }

                if (saldoOrigem == null || saldoDestino == null) {
                    throw new IllegalArgumentException("Conta de origem ou destino inexistente.");
                }
                if (saldoOrigem < valor) {
                    throw new SaldoInsuficienteException(
                            String.format("Saldo insuficiente na conta %d (saldo=%.2f, valor=%.2f).",
                                    numeroOrigem, saldoOrigem, valor));
                }

                // debita origem
                upd.setBigDecimal(1, BigDecimal.valueOf(saldoOrigem - valor));
                upd.setInt(2, numeroOrigem);
                upd.executeUpdate();

                // credita destino
                upd.setBigDecimal(1, BigDecimal.valueOf(saldoDestino + valor));
                upd.setInt(2, numeroDestino);
                upd.executeUpdate();

                con.commit();
            } catch (Exception e) {
                con.rollback();
                if (e instanceof SQLException se) throw se;
                if (e instanceof IllegalArgumentException iae) throw iae;
                if (e instanceof SaldoInsuficienteException sie) throw sie;
                throw new SQLException("Erro: Falha na transferência!", e);
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
