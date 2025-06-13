const mongoose = require('mongoose');

const pessoaSchema = new mongoose.Schema({
  nome: {
    type: String,
    required: true
  },
  email: {
    type: String,
    required: true
  },
  telefone: {
    type: String,
    required: true
  },
  tipo: {
    type: String,
    enum: ['CLIENTE', 'FORNECEDOR', 'FUNCIONARIO'],
    required: true
  },
  endereco: {
    type: String,
    required: true
  }
}, {
  timestamps: true
});

module.exports = mongoose.model('Pessoa', pessoaSchema); 