import React, { useState, useEffect } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import Topbar from './Topbar';
import Sidebar from './Sidebar'; 
import authService from '../../services/authService';

  const MainLayout = () => {
  const [nomeUsuario, setNomeUsuario] = useState('Usuário');
  const navigate = useNavigate();

  useEffect(() => {
    const token = authService.getCurrentUserToken();
      if (token) {
        try {
          const payloadBase64 = token.split('.')[1];
          const decodedPayload = JSON.parse(atob(payloadBase64));

          setNomeUsuario(decodedPayload.sub || 'Usuário Logado'); 
        } catch (e) {
          console.error("Erro ao decodificar token para nome de usuário:", e);
          authService.logout();
          navigate('/login');
        }
      }
    }, [navigate]);
      
    const handleLogout = () => {
      authService.logout();
      navigate('/login');
    };

    const styles = {
      mainLayout: {
        display: 'flex',
        flexDirection: 'column',
        height: '100vh',
        width: '100vw',
        overflow: 'hidden',
        backgroundColor: '#1e1e2f', 
      },
      contentAreaWrapper: {
      display: 'flex',
      flex: 1,
      overflow: 'hidden',
      },
      content: {
        flex: 1,
        padding: '20px',
        overflowY: 'auto',
        height: 'calc(100vh - 60px)',
        boxSizing: 'border-box',
        backgroundColor: '#27293d',
        color: '#e0e0e0',
      }
    };

    return (
      <div style={styles.mainLayout}>
        <Topbar nomeUsuario={nomeUsuario} onLogout={handleLogout} tituloPagina="Painel" />
        <div style={styles.contentAreaWrapper}>
          <Sidebar />
          <main style={styles.content}>
            <Outlet /> {/* Aqui o Dashboard ou outros componentes de rota serão renderizados */}
          </main>
        </div>
      </div>
      );
  };

export default MainLayout;
    