import React, { useState, useEffect } from 'react';
import { getAllProdutos, createProduto, updateProduto, deleteProduto } from '../services/produtoService';
import Swal from 'sweetalert2';

const ProdutosDashboard = () => {
  const [produtos, setProdutos] = useState([]);
  const [editing, setEditing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    nome: '',
    descricao: '',
    preco: '',
    codigo: '',
    unidade: 'UN', // UN, KG, L, M
    estoqueMinimo: 0
  });

  const carregarProdutos = async () => {
    try {
      const data = await getAllProdutos();
      setProdutos(data);
    } catch (err) {
      console.error("Erro ao carregar produtos:", err);
      Swal.fire('Erro!', 'Erro ao carregar lista de produtos.', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarProdutos();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editing) {
        await updateProduto(editing.id, formData);
        await Swal.fire('Atualizado!', 'Produto atualizado com sucesso.', 'success');
      } else {
        await createProduto(formData);
        await Swal.fire('Criado!', 'Produto cadastrado com sucesso.', 'success');
      }
      setEditing(null);
      setFormData({
        nome: '',
        descricao: '',
        preco: '',
        codigo: '',
        unidade: 'UN',
        estoqueMinimo: 0
      });
      carregarProdutos();
    } catch (err) {
      Swal.fire('Erro!', 'Erro ao salvar produto.', 'error');
    }
  };

  const handleEdit = (produto) => {
    setEditing(produto);
    setFormData(produto);
  };

  const handleDelete = async (id) => {
    const result = await Swal.fire({
      title: 'Confirmar exclusão?',
      text: 'Essa ação não poderá ser desfeita.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sim, excluir',
      cancelButtonText: 'Cancelar'
    });

    if (result.isConfirmed) {
      try {
        await deleteProduto(id);
        await Swal.fire('Excluído!', 'Produto removido com sucesso.', 'success');
        carregarProdutos();
      } catch (err) {
        Swal.fire('Erro!', 'Erro ao excluir produto.', 'error');
      }
    }
  };

  return (
    <div style={{ padding: '20px', color: 'white' }}>
      <h1>Produtos</h1>

      <form onSubmit={handleSubmit} style={{ marginBottom: '20px', backgroundColor: '#1e1e2f', padding: '20px', borderRadius: '5px' }}>
        <h3>{editing ? 'Editar Produto' : 'Novo Produto'}</h3>
        <div style={{ display: 'grid', gap: '10px', gridTemplateColumns: '1fr 1fr' }}>
          <div>
            <label>Nome:</label>
            <input
              type="text"
              value={formData.nome}
              onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
              required
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            />
          </div>
          <div>
            <label>Código:</label>
            <input
              type="text"
              value={formData.codigo}
              onChange={(e) => setFormData({ ...formData, codigo: e.target.value })}
              required
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            />
          </div>
          <div>
            <label>Preço:</label>
            <input
              type="number"
              step="0.01"
              value={formData.preco}
              onChange={(e) => setFormData({ ...formData, preco: e.target.value })}
              required
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            />
          </div>
          <div>
            <label>Unidade:</label>
            <select
              value={formData.unidade}
              onChange={(e) => setFormData({ ...formData, unidade: e.target.value })}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            >
              <option value="UN">Unidade</option>
              <option value="KG">Quilograma</option>
              <option value="L">Litro</option>
              <option value="M">Metro</option>
            </select>
          </div>
          <div>
            <label>Estoque Mínimo:</label>
            <input
              type="number"
              value={formData.estoqueMinimo}
              onChange={(e) => setFormData({ ...formData, estoqueMinimo: parseInt(e.target.value) })}
              required
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            />
          </div>
          <div style={{ gridColumn: '1 / -1' }}>
            <label>Descrição:</label>
            <textarea
              value={formData.descricao}
              onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
              style={{ width: '100%', padding: '8px', marginTop: '5px', minHeight: '100px' }}
            />
          </div>
        </div>
        <div style={{ marginTop: '20px', display: 'flex', gap: '10px' }}>
          <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
            {editing ? 'Atualizar' : 'Cadastrar'}
          </button>
          {editing && (
            <button
              type="button"
              onClick={() => {
                setEditing(null);
                setFormData({
                  nome: '',
                  descricao: '',
                  preco: '',
                  codigo: '',
                  unidade: 'UN',
                  estoqueMinimo: 0
                });
              }}
              style={{ padding: '10px 20px', backgroundColor: '#f44336', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
            >
              Cancelar
            </button>
          )}
        </div>
      </form>

      {loading ? (
        <p>Carregando...</p>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', backgroundColor: '#1e1e2f', color: '#e0e0e0' }}>
          <thead>
            <tr>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Código</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Nome</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Preço</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Unidade</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Estoque Mínimo</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {produtos.map((p) => (
              <tr key={p.id}>
                <td style={{ padding: '10px' }}>{p.codigo}</td>
                <td style={{ padding: '10px' }}>{p.nome}</td>
                <td style={{ padding: '10px' }}>R$ {parseFloat(p.preco).toFixed(2)}</td>
                <td style={{ padding: '10px' }}>{p.unidade}</td>
                <td style={{ padding: '10px' }}>{p.estoqueMinimo}</td>
                <td style={{ padding: '10px' }}>
                  <button
                    onClick={() => handleEdit(p)}
                    style={{ marginRight: '8px', padding: '5px 10px', backgroundColor: '#2196F3', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                  >
                    Editar
                  </button>
                  <button
                    onClick={() => handleDelete(p.id)}
                    style={{ padding: '5px 10px', backgroundColor: '#f44336', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                  >
                    Excluir
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ProdutosDashboard; 