const mongoose = require('mongoose');

const produtoSchema = new mongoose.Schema({
  nome: {
    type: String,
    required: true
  },
  descricao: {
    type: String,
    required: true
  },
  preco: {
    type: Number,
    required: true
  },
  codigo: {
    type: String,
    required: true,
    unique: true
  },
  unidade: {
    type: String,
    enum: ['UN', 'KG', 'L', 'M'],
    required: true
  },
  estoqueMinimo: {
    type: Number,
    required: true,
    default: 0
  }
}, {
  timestamps: true
});

module.exports = mongoose.model('Produto', produtoSchema); 