import api from './api';

const API_URL = '/admin/usuarios';

export const getAllUsuarios = async () => {
  const response = await api.get(API_URL);
  return response.data;
};

export const createUsuario = async (usuario) => {
  const response = await api.post(API_URL, usuario);
  return response.data;
};

export const updateUsuario = async (id, usuario) => {
  const response = await api.put(`${API_URL}/${id}`, usuario);
  return response.data;
};

export const deleteUsuario = async (id) => {
  const response = await api.delete(`${API_URL}/${id}`);
  return response.data;
};