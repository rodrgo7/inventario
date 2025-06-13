import api from './api';

const API_URL = '/api/pessoas';

export const getAllPessoas = async () => {
  const response = await api.get(API_URL);
  return response.data;
};

export const getPessoaById = async (id) => {
  const response = await api.get(`${API_URL}/${id}`);
  return response.data;
};

export const createPessoa = async (pessoa) => {
  const response = await api.post(API_URL, pessoa);
  return response.data;
};

export const updatePessoa = async (id, pessoa) => {
  const response = await api.put(`${API_URL}/${id}`, pessoa);
  return response.data;
};

export const deletePessoa = async (id) => {
  const response = await api.delete(`${API_URL}/${id}`);
  return response.data;
}; 