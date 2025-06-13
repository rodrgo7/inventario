const express = require('express');
const router = express.Router();
const Usuario = require('../models/Usuario');
const { protect, admin } = require('../middleware/auth');

// GET all usuarios (admin only)
router.get('/', protect, admin, async (req, res) => {
  try {
    const usuarios = await Usuario.find().select('-senha');
    res.json(usuarios);
  } catch (err) {
    res.status(500).json({ message: 'Erro ao buscar usuários' });
  }
});

// POST create usuario (admin only)
router.post('/', protect, admin, async (req, res) => {
  try {
    const usuario = new Usuario(req.body);
    await usuario.save();
    res.status(201).json(usuario);
  } catch (err) {
    res.status(400).json({ message: 'Erro ao criar usuário' });
  }
});

// PUT update usuario (admin only)
router.put('/:id', protect, admin, async (req, res) => {
  try {
    const usuario = await Usuario.findByIdAndUpdate(req.params.id, req.body, { new: true }).select('-senha');
    res.json(usuario);
  } catch (err) {
    res.status(400).json({ message: 'Erro ao atualizar usuário' });
  }
});

// DELETE usuario (admin only)
router.delete('/:id', protect, admin, async (req, res) => {
  try {
    await Usuario.findByIdAndDelete(req.params.id);
    res.json({ message: 'Usuário removido' });
  } catch (err) {
    res.status(400).json({ message: 'Erro ao remover usuário' });
  }
});

module.exports = router; 