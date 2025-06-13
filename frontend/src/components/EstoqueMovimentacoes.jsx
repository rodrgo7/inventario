import React, { useState, useEffect } from 'react';
import { getAllMovimentacoes, createMovimentacao } from '../services/estoqueService';
import { getAllProdutos } from '../services/produtoService';
import Swal from 'sweetalert2';

const EstoqueMovimentacoes = () => {
  const [movimentacoes, setMovimentacoes] = useState([]);
  const [produtos, setProdutos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    produtoId: '',
    tipo: 'ENTRADA', // ENTRADA, SAIDA
    quantidade: '',
    observacao: ''
  });

  const carregarDados = async () => {
    try {
      const [movimentacoesData, produtosData] = await Promise.all([
        getAllMovimentacoes(),
        getAllProdutos()
      ]);
      setMovimentacoes(movimentacoesData);
      setProdutos(produtosData);
    } catch (err) {
      console.error("Erro ao carregar dados:", err);
      Swal.fire('Erro!', 'Erro ao carregar dados.', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await createMovimentacao(formData);
      await Swal.fire('Sucesso!', 'Movimentação registrada com sucesso.', 'success');
      setFormData({
        produtoId: '',
        tipo: 'ENTRADA',
        quantidade: '',
        observacao: ''
      });
      carregarDados();
    } catch (err) {
      Swal.fire('Erro!', 'Erro ao registrar movimentação.', 'error');
    }
  };

  const getTipoMovimentacao = (tipo) => {
    return tipo === 'ENTRADA' ? 'Entrada' : 'Saída';
  };

  const getCorMovimentacao = (tipo) => {
    return tipo === 'ENTRADA' ? '#4CAF50' : '#f44336';
  };

  return (
    <div style={{ padding: '20px', color: 'white' }}>
      <h1>Movimentações de Estoque</h1>

      <form onSubmit={handleSubmit} style={{ marginBottom: '20px', backgroundColor: '#1e1e2f', padding: '20px', borderRadius: '5px' }}>
        <h3>Nova Movimentação</h3>
        <div style={{ display: 'grid', gap: '10px', gridTemplateColumns: '1fr 1fr' }}>
          <div>
            <label>Produto:</label>
            <select
              value={formData.produtoId}
              onChange={(e) => setFormData({ ...formData, produtoId: e.target.value })}
              required
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            >
              <option value="">Selecione um produto</option>
              {produtos.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.codigo} - {p.nome}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label>Tipo:</label>
            <select
              value={formData.tipo}
              onChange={(e) => setFormData({ ...formData, tipo: e.target.value })}
              required
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            >
              <option value="ENTRADA">Entrada</option>
              <option value="SAIDA">Saída</option>
            </select>
          </div>
          <div>
            <label>Quantidade:</label>
            <input
              type="number"
              value={formData.quantidade}
              onChange={(e) => setFormData({ ...formData, quantidade: e.target.value })}
              required
              min="0.01"
              step="0.01"
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            />
          </div>
          <div style={{ gridColumn: '1 / -1' }}>
            <label>Observação:</label>
            <textarea
              value={formData.observacao}
              onChange={(e) => setFormData({ ...formData, observacao: e.target.value })}
              style={{ width: '100%', padding: '8px', marginTop: '5px', minHeight: '100px' }}
            />
          </div>
        </div>
        <div style={{ marginTop: '20px' }}>
          <button
            type="submit"
            style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
          >
            Registrar Movimentação
          </button>
        </div>
      </form>

      {loading ? (
        <p>Carregando...</p>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', backgroundColor: '#1e1e2f', color: '#e0e0e0' }}>
          <thead>
            <tr>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Data/Hora</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Produto</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Tipo</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Quantidade</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Observação</th>
            </tr>
          </thead>
          <tbody>
            {movimentacoes.map((m) => (
              <tr key={m.id}>
                <td style={{ padding: '10px' }}>
                  {new Date(m.dataHora).toLocaleString()}
                </td>
                <td style={{ padding: '10px' }}>
                  {m.produto.codigo} - {m.produto.nome}
                </td>
                <td style={{ padding: '10px' }}>
                  <span style={{
                    padding: '5px 10px',
                    borderRadius: '4px',
                    backgroundColor: getCorMovimentacao(m.tipo),
                    color: 'white'
                  }}>
                    {getTipoMovimentacao(m.tipo)}
                  </span>
                </td>
                <td style={{ padding: '10px' }}>{m.quantidade} {m.produto.unidade}</td>
                <td style={{ padding: '10px' }}>{m.observacao}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default EstoqueMovimentacoes; 