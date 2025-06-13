import api from './api';

const API_URL = '/api/estoque';
const MOVIMENTACOES_URL = '/api/estoque/movimentacoes';

// Painel Estoque
export const getEstoqueAtual = async () => {
  const response = await api.get(`${API_URL}/atual`);
  return response.data;
};

export const getEstoquePorProduto = async (produtoId) => {
  const response = await api.get(`${API_URL}/produto/${produtoId}`);
  return response.data;
};

// Movimentações
export const getAllMovimentacoes = async () => {
  const response = await api.get(MOVIMENTACOES_URL);
  return response.data;
};

export const getMovimentacaoById = async (id) => {
  const response = await api.get(`${MOVIMENTACOES_URL}/${id}`);
  return response.data;
};

export const createMovimentacao = async (movimentacao) => {
  const response = await api.post(MOVIMENTACOES_URL, movimentacao);
  return response.data;
};

export const updateMovimentacao = async (id, movimentacao) => {
  const response = await api.put(`${MOVIMENTACOES_URL}/${id}`, movimentacao);
  return response.data;
};

export const deleteMovimentacao = async (id) => {
  const response = await api.delete(`${MOVIMENTACOES_URL}/${id}`);
  return response.data;
}; 