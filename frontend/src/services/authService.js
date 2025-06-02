// src/services/authService.js
import axios from 'axios';

const API_URL = '/api/auth/'; // O proxy do Vite vai para http://localhost:8080/api/auth/

const login = async (email, senha) => {
  try {
    const response = await axios.post(API_URL + 'login', { // Endpoint de login
      email,
      senha,
    });
    if (response.data.token) { // Supondo que o backend retorna { token: "..." }
      localStorage.setItem('jwtToken', response.data.token);
      // Opcional: armazenar outros dados do usuário se o backend retornar
    }
    return response.data; // Retorna os dados completos da resposta (incluindo o token)
  } catch (error) {
    // Axios encapsula o erro da resposta em error.response.data
    const errorData = error.response?.data || { message: 'Erro de rede ou servidor não respondeu' };
    throw errorData; // Lança o objeto de erro do backend ou um erro genérico
  }
};

const logout = () => {
  localStorage.removeItem('jwtToken');
  // Opcional: chamar um endpoint de logout no backend se houver
};

const getCurrentUserToken = () => {
  return localStorage.getItem('jwtToken');
};

// Você pode adicionar outras funções aqui, como register, forgotPassword, etc.

const authService = {
  login,
  logout,
  getCurrentUserToken,
};

export default authService;