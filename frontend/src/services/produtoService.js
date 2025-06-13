import api from './api';

const API_URL = '/api/produtos';

export const getAllProdutos = async () => {
  const response = await api.get(API_URL);
  return response.data;
};

export const getProdutoById = async (id) => {
  const response = await api.get(`${API_URL}/${id}`);
  return response.data;
};

export const createProduto = async (produto) => {
  const response = await api.post(API_URL, produto);
  return response.data;
};

export const updateProduto = async (id, produto) => {
  const response = await api.put(`${API_URL}/${id}`, produto);
  return response.data;
};

export const deleteProduto = async (id) => {
  const response = await api.delete(`${API_URL}/${id}`);
  return response.data;
}; 