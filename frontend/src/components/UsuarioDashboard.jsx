import React, { useState, useEffect } from 'react';
import { getAllUsuarios, createUsuario, updateUsuario, deleteUsuario } from '../services/userService';
import UsuarioForm from './UsuarioForm';
import Swal from 'sweetalert2';

const UsuarioDashboard = () => {
  const [usuarios, setUsuarios] = useState([]);
  const [editing, setEditing] = useState(null);

  const carregarUsuarios = async () => {
    try {
      const data = await getAllUsuarios();
      setUsuarios(data);
    } catch (err) {
      console.error("Erro ao carregar usuários:", err);
    }
  };

  useEffect(() => {
    carregarUsuarios();
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
        await deleteUsuario(id);
        await Swal.fire('Excluído!', 'O usuário foi removido.', 'success');
        carregarUsuarios();
      } catch (err) {
        Swal.fire('Erro!', 'Erro ao excluir o usuário.', 'error');
      }
    }
  };

  const handleEdit = (usuario) => {
    setEditing(usuario);
  };

  const handleSave = async (usuario) => {
    try {
      if (editing) {
        await updateUsuario(editing.id, usuario);
        await Swal.fire('Atualizado!', 'Usuário atualizado com sucesso.', 'success');
        setEditing(null);
      } else {
        await createUsuario(usuario);
        await Swal.fire('Criado!', 'Usuário criado com sucesso.', 'success');
      }
      carregarUsuarios();
    } catch (err) {
      Swal.fire('Erro', 'Erro ao salvar usuário.', 'error');
    }
  };

  return (
    <div style={{ padding: '20px', color: 'white' }}>
      <h1>Usuários</h1>
      <UsuarioForm onSubmit={handleSave} usuario={editing} />

      <table style={{ width: '100%', borderCollapse: 'collapse', backgroundColor: '#1e1e2f', color: '#e0e0e0' }}>
        <thead>
          <tr>
            <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Nome</th>
            <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Email</th>
            <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Nível</th>
            <th style={{ borderBottom: '1px solid #555', padding: '10px' }}>Ações</th>
          </tr>
        </thead>
        <tbody>
          {usuarios.map((u) => (
            <tr key={u.id}>
              <td style={{ padding: '10px' }}>{u.nome}</td>
              <td style={{ padding: '10px' }}>{u.email}</td>
              <td style={{ padding: '10px' }}>{u.nivel}</td>
              <td style={{ padding: '10px' }}>
                <button onClick={() => handleEdit(u)} style={{ marginRight: '8px' }}>Editar</button>
                <button onClick={() => handleDelete(u.id)} style={{ backgroundColor: '#dc3545', color: 'white' }}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UsuarioDashboard;