import React, { useState, useEffect } from 'react';
import { createEquipamento, deleteEquipamento, getAllEquipamentos, updateEquipamento } from '../services/equipmentService';
import Swal from 'sweetalert2';
import EquipamentoForm from './EquipamentoForm';

const Dashboard = () => {
  const [equipamentos, setEquipamentos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(null);
  const [filtro, setFiltro] = useState('');

  const carregarEquipamentos = async () => {
    try {
      const data = await getAllEquipamentos();
      setEquipamentos(data);
    } catch (err) {
      console.error("Erro ao carregar equipamentos:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarEquipamentos();
  }, []);

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
        await deleteEquipamento(id);
        await Swal.fire('Excluído!', 'O equipamento foi removido.', 'success');
        carregarEquipamentos();
      } catch (err) {
        Swal.fire('Erro!', 'Erro ao excluir o equipamento.', 'error');
      }
    }
  };

  const handleEdit = (equipamento) => {
    setEditing(equipamento);
  };

  const handleSave = async (data) => {
    try {
      if (editing) {
        await updateEquipamento(editing.id, data);
        await Swal.fire('Atualizado!', 'Equipamento atualizado com sucesso.', 'success');
        setEditing(null);
      } else {
        await createEquipamento(data);
        await Swal.fire('Criado!', 'Equipamento criado com sucesso.', 'success');
      }
      carregarEquipamentos();
    } catch (err) {
      Swal.fire('Erro', 'Erro ao salvar equipamento.', 'error');
    }
  };

  const equipamentosFiltrados = equipamentos.filter((e) =>
    e.nome.toLowerCase().includes(filtro.toLowerCase()) ||
    e.numeroDeSerie.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div style={{ padding: '20px', color: 'white' }}>
      <h1>Equipamentos</h1>

      <input
        type="text"
        placeholder="Filtrar por nome ou número de série"
        value={filtro}
        onChange={(e) => setFiltro(e.target.value)}
        style={{ marginBottom: '20px', padding: '8px', width: '100%' }}
      />

      <EquipamentoForm onSubmit={handleSave} equipamento={editing} />

      {loading ? (
        <p>Carregando...</p>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', backgroundColor: '#1e1e2f', color: '#e0e0e0' }}>
          <thead>
            <tr>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Nome</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Número de Série</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Descrição</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Criado por</th>
              <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {equipamentosFiltrados.map((e) => (
              <tr key={e.id}>
                <td style={{ padding: '10px' }}>{e.nome}</td>
                <td style={{ padding: '10px' }}>{e.numeroDeSerie}</td>
                <td style={{ padding: '10px' }}>{e.descricaoDetalhada}</td>
                <td style={{ padding: '10px' }}>{e.criadoPorUsuario}</td>
                <td style={{ padding: '10px' }}>
                  <button onClick={() => handleEdit(e)} style={{ marginRight: '8px' }}>Editar</button>
                  <button onClick={() => handleDelete(e.id)} style={{ backgroundColor: '#dc3545', color: 'white' }}>Excluir</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default Dashboard;