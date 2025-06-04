import React, { useState, useEffect } from 'react';

const UsuarioForm = ({ onSubmit, usuario }) => {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [nivel, setNivel] = useState('PADRAO');

  useEffect(() => {
    if (usuario) {
      setNome(usuario.nome || '');
      setEmail(usuario.email || '');
      setNivel(usuario.nivel || 'PADRAO');
    } else {
      setNome('');
      setEmail('');
      setNivel('PADRAO');
    }
  }, [usuario]);

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({ nome, email, nivel });
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: '30px' }}>
      <h3>{usuario ? 'Editar Usuário' : 'Novo Usuário'}</h3>
      <div style={{ marginBottom: '10px' }}>
        <input
          type="text"
          placeholder="Nome"
          value={nome}
          onChange={(e) => setNome(e.target.value)}
          required
          style={{ padding: '8px', width: '100%' }}
        />
      </div>
      <div style={{ marginBottom: '10px' }}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          style={{ padding: '8px', width: '100%' }}
        />
      </div>
      <div style={{ marginBottom: '10px' }}>
        <select
          value={nivel}
          onChange={(e) => setNivel(e.target.value)}
          style={{ padding: '8px', width: '100%' }}
        >
          <option value="PADRAO">Padrão</option>
          <option value="ADMIN">Administrador</option>
        </select>
      </div>
      <button type="submit" style={{ padding: '10px 15px' }}>
        {usuario ? 'Atualizar' : 'Salvar'}
      </button>
    </form>
  );
};

export default UsuarioForm;