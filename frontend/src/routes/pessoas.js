const express = require('express');
const router = express.Router();
const Pessoa = require('../models/Pessoa');
const { protect } = require('../middleware/auth');

// GET all pessoas
router.get('/', protect, async (req, res) => {
  try {
    const pessoas = await Pessoa.find();
    res.json(pessoas);
  } catch (err) {
    res.status(500).json({ message: 'Erro ao buscar pessoas' });
  }
});

// POST create pessoa
router.post('/', protect, async (req, res) => {
  try {
    const pessoa = new Pessoa(req.body);
    await pessoa.save();
    res.status(201).json(pessoa);
  } catch (err) {
    res.status(400).json({ message: 'Erro ao criar pessoa' });
  }
});

// PUT update pessoa
router.put('/:id', protect, async (req, res) => {
  try {
    const pessoa = await Pessoa.findByIdAndUpdate(req.params.id, req.body, { new: true });
    res.json(pessoa);
  } catch (err) {
    res.status(400).json({ message: 'Erro ao atualizar pessoa' });
  }
});

// DELETE pessoa
router.delete('/:id', protect, async (req, res) => {
  try {
    await Pessoa.findByIdAndDelete(req.params.id);
    res.json({ message: 'Pessoa removida' });
  } catch (err) {
    res.status(400).json({ message: 'Erro ao remover pessoa' });
  }
});

module.exports = router; 