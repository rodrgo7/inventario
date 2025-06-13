import React, { useState, useEffect } from 'react';
import { getEstoqueAtual } from '../services/estoqueService';
import Swal from 'sweetalert2';

const EstoquePainel = () => {
  const [estoque, setEstoque] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filtro, setFiltro] = useState('');

  const carregarEstoque = async () => {
    try {
      const data = await getEstoqueAtual();
      setEstoque(data);
    } catch (err) {
      console.error("Erro ao carregar estoque:", err);
      Swal.fire('Erro!', 'Erro ao carregar dados do estoque.', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarEstoque();
  }, []);

  const estoqueFiltrado = estoque.filter((item) =>
    item.produto.nome.toLowerCase().includes(filtro.toLowerCase()) ||
    item.produto.codigo.toLowerCase().includes(filtro.toLowerCase())
  );

  const getStatusEstoque = (quantidade, minimo) => {
    if (quantidade <= 0) return { texto: 'Sem Estoque', cor: '#f44336' };
    if (quantidade <= minimo) return { texto: 'Estoque Baixo', cor: '#ff9800' };
    return { texto: 'Normal', cor: '#4CAF50' };
  };

  return (
    <div style={{ padding: '20px', color: 'white' }}>
      <h1>Painel de Estoque</h1>

      <div style={{ marginBottom: '20px' }}>
        <input
          type="text"
          placeholder="Filtrar por nome ou código do produto"
          value={filtro}
          onChange={(e) => setFiltro(e.target.value)}
          style={{ width: '100%', padding: '8px', marginBottom: '20px' }}
        />
      </div>

      {loading ? (
        <p>Carregando...</p>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', backgroundColor: '#1e1e2f', color: '#e0e0e0' }}>
          <thead>
            <tr>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Código</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Produto</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Quantidade</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Unidade</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Mínimo</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {estoqueFiltrado.map((item) => {
              const status = getStatusEstoque(item.quantidade, item.produto.estoqueMinimo);
              return (
                <tr key={item.id}>
                  <td style={{ padding: '10px' }}>{item.produto.codigo}</td>
                  <td style={{ padding: '10px' }}>{item.produto.nome}</td>
                  <td style={{ padding: '10px' }}>{item.quantidade}</td>
                  <td style={{ padding: '10px' }}>{item.produto.unidade}</td>
                  <td style={{ padding: '10px' }}>{item.produto.estoqueMinimo}</td>
                  <td style={{ padding: '10px' }}>
                    <span style={{
                      padding: '5px 10px',
                      borderRadius: '4px',
                      backgroundColor: status.cor,
                      color: 'white'
                    }}>
                      {status.texto}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default EstoquePainel; 