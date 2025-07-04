import React from 'react';

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginForm from './components/LoginForm';
import Dashboard from './components/Dashboard';
import MainLayout from './components/layout/MainLayout';
import UsuarioDashboard from './components/UsuarioDashboard';
import PessoasDashboard from './components/PessoasDashboard';
import ProdutosDashboard from './components/ProdutosDashboard';
import EstoquePainel from './components/EstoquePainel';
import EstoqueMovimentacoes from './components/EstoqueMovimentacoes';
import ProtectedRoute from './routes/ProtectedRoute';

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginForm />} />
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <MainLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Dashboard />} />
          <Route path="pessoas" element={<PessoasDashboard />} />
          <Route path="produtos" element={<ProdutosDashboard />} />
          <Route path="estoque/painel" element={<EstoquePainel />} />
          <Route path="estoque/movimentacoes" element={<EstoqueMovimentacoes />} />
          <Route
            path="usuarios"
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <UsuarioDashboard />
              </ProtectedRoute>
            }
          />
        </Route>
        <Route path="*" element={<p style={{ padding: 20, color: 'white' }}>Página não encontrada</p>} />
        <Route path="/unauthorized" element={<p style={{ padding: 20, color: 'white' }}>Acesso não autorizado</p>} />
      </Routes>
    </Router>
  );
};

export default App;