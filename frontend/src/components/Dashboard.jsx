import React from 'react';
    import authService from '../services/authService'; // Para o botão de logout
    // import { useNavigate } from 'react-router-dom'; // Se precisar redirecionar no logout

    const Dashboard = () => {
      // const navigate = useNavigate(); // Para React Router

      const handleLogout = () => {
        authService.logout();
        // Redirecionar para a página de login
        // navigate('/login'); // Se usar useNavigate
        window.location.reload(); // Simplesmente recarrega a página, o que deve levar ao login se o token sumiu
        alert('Você foi desconectado.');
      };

      // Obter informações do usuário (exemplo, se o backend retornar no login ou se tiver um endpoint /me)
      // const userInfo = authService.getCurrentUserInfo(); // Você precisaria implementar isso

      return (
        <div style={{ padding: '20px', color: 'white', textAlign: 'center' }}>
          <h1>Dashboard</h1>
          <p>Bem-vindo ao seu painel de controle!</p>
          {/* {userInfo && <p>Logado como: {userInfo.email}</p>} */}
          <button 
            onClick={handleLogout} 
            style={{ 
              padding: '10px 20px', 
              fontSize: '16px', 
              color: 'white', 
              backgroundColor: '#dc3545', 
              border: 'none', 
              borderRadius: '5px', 
              cursor: 'pointer' 
            }}
          >
            Logout
          </button>
          {/* Aqui você adicionará o conteúdo real do seu dashboard */}
        </div>
      );
    };

    export default Dashboard;