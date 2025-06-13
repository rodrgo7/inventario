import React, { useState, useEffect } from 'react';
import { getAllPessoas, createPessoa, updatePessoa, deletePessoa } from '../services/pessoaService';
import Swal from 'sweetalert2';

const PessoasDashboard = () => {
  const [pessoas, setPessoas] = useState([]);
  const [editing, setEditing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    telefone: '',
    tipo: 'CLIENTE', // CLIENTE, FORNECEDOR, FUNCIONARIO
    endereco: ''
  });

  const carregarPessoas = async () => {
    try {
      const data = await getAllPessoas();
      setPessoas(data);
    } catch (err) {
      console.error("Erro ao carregar pessoas:", err);
      Swal.fire('Erro!', 'Erro ao carregar lista de pessoas.', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarPessoas();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editing) {
        await updatePessoa(editing.id, formData);
        await Swal.fire('Atualizado!', 'Pessoa atualizada com sucesso.', 'success');
      } else {
        await createPessoa(formData);
        await Swal.fire('Criado!', 'Pessoa cadastrada com sucesso.', 'success');
      }
      setEditing(null);
      setFormData({
        nome: '',
        email: '',
        telefone: '',
        tipo: 'CLIENTE',
        endereco: ''
      });
      carregarPessoas();
    } catch (err) {
      Swal.fire('Erro!', 'Erro ao salvar pessoa.', 'error');
    }
  };

  const handleEdit = (pessoa) => {
    setEditing(pessoa);
    setFormData(pessoa);
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
        await deletePessoa(id);
        await Swal.fire('Excluído!', 'Pessoa removida com sucesso.', 'success');
        carregarPessoas();
      } catch (err) {
        Swal.fire('Erro!', 'Erro ao excluir pessoa.', 'error');
      }
    }
  };

  return (
    <div style={{ padding: '20px', color: 'white' }}>
      <h1>Pessoas</h1>

      <form onSubmit={handleSubmit} style={{ marginBottom: '20px', backgroundColor: '#1e1e2f', padding: '20px', borderRadius: '5px' }}>
        <h3>{editing ? 'Editar Pessoa' : 'Nova Pessoa'}</h3>
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
            <label>Email:</label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              required
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            />
          </div>
          <div>
            <label>Telefone:</label>
            <input
              type="tel"
              value={formData.telefone}
              onChange={(e) => setFormData({ ...formData, telefone: e.target.value })}
              required
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            />
          </div>
          <div>
            <label>Tipo:</label>
            <select
              value={formData.tipo}
              onChange={(e) => setFormData({ ...formData, tipo: e.target.value })}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            >
              <option value="CLIENTE">Cliente</option>
              <option value="FORNECEDOR">Fornecedor</option>
              <option value="FUNCIONARIO">Funcionário</option>
            </select>
          </div>
          <div style={{ gridColumn: '1 / -1' }}>
            <label>Endereço:</label>
            <input
              type="text"
              value={formData.endereco}
              onChange={(e) => setFormData({ ...formData, endereco: e.target.value })}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
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
                  email: '',
                  telefone: '',
                  tipo: 'CLIENTE',
                  endereco: ''
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
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Nome</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Email</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Telefone</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Tipo</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {pessoas.map((p) => (
              <tr key={p.id}>
                <td style={{ padding: '10px' }}>{p.nome}</td>
                <td style={{ padding: '10px' }}>{p.email}</td>
                <td style={{ padding: '10px' }}>{p.telefone}</td>
                <td style={{ padding: '10px' }}>{p.tipo}</td>
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

export default PessoasDashboard; 