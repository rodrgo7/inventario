    import React from 'react';

    const Topbar = ({ nomeUsuario, onLogout, tituloPagina }) => {
      const styles = {
        topbar: {
          backgroundColor: '#2c1a4d',
          color: '#e0e0e0',
          padding: '0 20px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          height: '60px',
          boxSizing: 'border-box',
          borderBottom: '1px solid #4f228a',
        },
        logoAndTitle: {
            display: 'flex',
            alignItems: 'center',
        },
        logo: {
          fontSize: '1.4rem',
          fontWeight: 'bold',
          color: '#fff',
          marginRight: '20px',
        },
        pageTitle: {
            fontSize: '1.2rem',
            color: '#c0c0c0',
        },
        userSection: {
          display: 'flex',
          alignItems: 'center',
        },
        userInfo: {
          fontSize: '0.9rem',
          marginRight: '15px',
          color: '#f0f0f0',
        },
        logoutButton: {
          background: 'none',
          border: 'none',
          color: '#f0f0f0',
          cursor: 'pointer',
          fontSize: '0.9rem',
        }
      };

      return (
        <header style={styles.topbar}>
          <div style={styles.logoAndTitle}>
            <div style={styles.logo}>Gerenciador de Estoque</div>
            <div style={styles.pageTitle}>{tituloPagina || 'Painel'}</div>
          </div>
          <div style={styles.userSection}>
            {/* <FaUserCircle size={20} style={{ marginRight: '8px' }} />  Exemplo com ícone */}
            <span style={styles.userInfo}>{nomeUsuario || 'Usuário'}</span>
            <button onClick={onLogout} style={styles.logoutButton}>
              {/* <FaSignOutAlt size={18} style={{ marginRight: '5px' }} /> Exemplo com ícone */}
              Sair
            </button>
          </div>
        </header>
      );
    };

    export default Topbar;    