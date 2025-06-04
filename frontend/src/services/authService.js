import axios from 'axios';

const API_URL = '/api/auth/'; // O proxy do Vite vai para http://localhost:8080/api/auth/

const login = async (email, senha) => {
  try {
    const response = await axios.post(API_URL + 'login', { // Endpoint de login
      email,
      senha,
    });
    if (response.data.token) {
      localStorage.setItem('jwtToken', response.data.token);
    }
    return response.data;
  } catch (error) {
    const errorData = error.response?.data || { message: 'Erro de rede ou servidor não respondeu' };
    throw errorData; 
  }
};

const logout = () => {
  localStorage.removeItem('jwtToken');
};

const getCurrentUserToken = () => {
  return localStorage.getItem('jwtToken');
};

const authService = {
  login,
  logout,
  getCurrentUserToken,
};

export default authService;