const express = require('express');
const cors = require('cors');
const connectDB = require('./config/database');
require('dotenv').config();

const app = express();

// Connect to MongoDB
connectDB();

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/auth', require('./routes/auth'));
app.use('/api/pessoas', require('./routes/pessoas'));
app.use('/api/produtos', require('./routes/produtos'));
app.use('/api/estoque', require('./routes/estoque'));
app.use('/api/admin/usuarios', require('./routes/usuarios'));

const PORT = process.env.PORT || 8080;

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
}); 