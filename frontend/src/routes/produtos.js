const express = require('express');
const router = express.Router();
const Produto = require('../models/Produto');
const { protect } = require('../middleware/auth');

// GET all produtos
router.get('/', protect, async (req, res) => {
  try {
    const produtos = await Produto.find();
    res.json(produtos);
  } catch (err) {
    res.status(500).json({ message: 'Erro ao buscar produtos' });
  }
});

// POST create produto
router.post('/', protect, async (req, res) => {
  try {
    const produto = new Produto(req.body);
    await produto.save();
    res.status(201).json(produto);
  } catch (err) {
    res.status(400).json({ message: 'Erro ao criar produto' });
  }
});

// PUT update produto
router.put('/:id', protect, async (req, res) => {
  try {
    const produto = await Produto.findByIdAndUpdate(req.params.id, req.body, { new: true });
    res.json(produto);
  } catch (err) {
    res.status(400).json({ message: 'Erro ao atualizar produto' });
  }
});

// DELETE produto
router.delete('/:id', protect, async (req, res) => {
  try {
    await Produto.findByIdAndDelete(req.params.id);
    res.json({ message: 'Produto removido' });
  } catch (err) {
    res.status(400).json({ message: 'Erro ao remover produto' });
  }
});

module.exports = router; 