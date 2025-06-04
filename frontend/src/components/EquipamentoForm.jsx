import React, { useState, useEffect } from 'react';

const EquipamentoForm = ({ onSubmit, equipamento }) => {
  const [nome, setNome] = useState('');
  const [numeroDeSerie, setNumeroDeSerie] = useState('');
  const [descricaoDetalhada, setDescricaoDetalhada] = useState('');

  useEffect(() => {
    if (equipamento) {
      setNome(equipamento.nome || '');
      setNumeroDeSerie(equipamento.numeroDeSerie || '');
      setDescricaoDetalhada(equipamento.descricaoDetalhada || '');
    } else {
      setNome('');
      setNumeroDeSerie('');
      setDescricaoDetalhada('');
    }
  }, [equipamento]);

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({ nome, numeroDeSerie, descricaoDetalhada });
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: '30px' }}>
      <h3>{equipamento ? 'Editar Equipamento' : 'Novo Equipamento'}</h3>
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
          type="text"
          placeholder="Número de Série"
          value={numeroDeSerie}
          onChange={(e) => setNumeroDeSerie(e.target.value)}
          required
          style={{ padding: '8px', width: '100%' }}
        />
      </div>
      <div style={{ marginBottom: '10px' }}>
        <textarea
          placeholder="Descrição detalhada"
          value={descricaoDetalhada}
          onChange={(e) => setDescricaoDetalhada(e.target.value)}
          rows="3"
          style={{ padding: '8px', width: '100%' }}
        />
      </div>
      <button type="submit" style={{ padding: '10px 15px' }}>
        {equipamento ? 'Atualizar' : 'Salvar'}
      </button>
    </form>
  );
};

export default EquipamentoForm;