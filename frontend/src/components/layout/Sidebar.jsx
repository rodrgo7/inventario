import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Sidebar = () => {
  const location = useLocation();

  const menuItems = [
    { nome: 'Painel', path: '/', icone: '🏠' },
    { nome: 'Pessoas', path: '/pessoas', icone: '👥' },
    { nome: 'Produtos', path: '/produtos', icone: '📦' },
    { nome: 'Painel Estoque', path: '/estoque/painel', icone: '📊' },
    { nome: 'Movimentações Estoque', path: '/estoque/movimentacoes', icone: '📈' },
  ];

  const nivel = localStorage.getItem('nivel');
  const nome = localStorage.getItem('nome');

  if (nivel === 'ADMIN') {
    menuItems.push({ nome: 'Usuários', path: '/usuarios', icone: '🧑‍💼' });
  }

  const isActive = (path) => location.pathname === path;

  return (
    <div style={{ width: '220px', backgroundColor: '#1f1f2e', height: '100vh', padding: '20px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
      <div>
        <h2 style={{ color: 'white', marginBottom: '10px' }}>Menu</h2>
        <h3 style={{ color: '#bbb', marginBottom: '30px' }}>{nome} ({nivel})</h3>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {menuItems.map((item, index) => (
            <li key={index} style={{ marginBottom: '15px' }}>
              <Link to={item.path} style={{ color: isActive(item.path) ? '#61dafb' : 'white', textDecoration: 'none' }}>
                <span style={{ marginRight: '8px' }}>{item.icone}</span>
                {item.nome}
              </Link>
            </li>
          ))}
        </ul>
      </div>
      <button
        onClick={() => {
          localStorage.clear();
          window.location.href = '/login';
        }}
        style={{ background: '#dc3545', color: 'white', padding: '8px', border: 'none', width: '100%' }}
      >
        Sair
      </button>
    </div>
  );
};

export default Sidebar;