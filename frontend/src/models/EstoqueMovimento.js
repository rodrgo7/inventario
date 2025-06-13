const mongoose = require('mongoose');

const estoqueMovimentoSchema = new mongoose.Schema({
  produto: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Produto',
    required: true
  },
  tipo: {
    type: String,
    enum: ['ENTRADA', 'SAIDA'],
    required: true
  },
  quantidade: {
    type: Number,
    required: true
  },
  data: {
    type: Date,
    default: Date.now
  },
  pessoa: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Pessoa',
    required: true
  },
  observacao: {
    type: String
  }
}, {
  timestamps: true
});

module.exports = mongoose.model('EstoqueMovimento', estoqueMovimentoSchema); 