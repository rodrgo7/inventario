import axios from 'axios';
import authService from './authService';

const API_URL = '/api/equipamentos';

const getAuthHeaders = () => {
  const token = authService.getCurrentUserToken();
  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
};

// Lista todos os equipamentos
export const getAllEquipamentos = async () => {
  const response = await axios.get(API_URL, getAuthHeaders());
  return response.data;
};

// Busca equipamento por ID
export const getEquipamentoById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}`, getAuthHeaders());
  return response.data;
};

// Cria novo equipamento
export const createEquipamento = async (equipamento) => {
  const response = await axios.post(API_URL, equipamento, getAuthHeaders());
  return response.data;
};

// Atualiza equipamento existente
export const updateEquipamento = async (id, equipamento) => {
  const response = await axios.put(`${API_URL}/${id}`, equipamento, getAuthHeaders());
  return response.data;
};

// Deleta equipamento
export const deleteEquipamento = async (id) => {
  const response = await axios.delete(`${API_URL}/${id}`, getAuthHeaders());
  return response.data;
};