import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx'; // Assumindo que App.jsx está em src/
// Corrigido o caminho para App.css se ele estiver em src/styles/
// Se App.css estiver diretamente em src/, use './App.css'
import './App.css'; 

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)