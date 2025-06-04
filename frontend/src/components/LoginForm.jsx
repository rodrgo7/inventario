import React, { useState } from 'react';
import './LoginForm.css';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const LoginForm = () => {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage('');

    try {
      const data = await login(email, senha);
      console.log('Login bem-sucedido, token:', data.token);
      setLoading(false);
      navigate('/dashboard');
    } catch (error) {
      const message = error.detail || error.message || 'Falha no login. Verifique suas credenciais.';
      setErrorMessage(message);
      setLoading(false);
      console.error("Erro no login:", error);
    }
  };

  const handleForgotPassword = (e) => {
    e.preventDefault();
    alert('Funcionalidade "Esqueci a senha" a ser implementada!');
  };

  return (
    <div className="login-card">
      <div className="profile-icon-area" />
      <form onSubmit={handleLogin}>
        <div className="form-group">
          <span className="input-icon">✉️</span>
          <input
            type="email"
            id="email"
            placeholder="Email ID"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <span className="input-icon">🔒</span>
          <input
            type="password"
            id="senha"
            placeholder="Password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
          />
        </div>

        {errorMessage && (
          <div className="error-message-box">
            {errorMessage}
          </div>
        )}

        <div className="form-options">
          <label className="remember-me">
            <input
              type="checkbox"
              name="remember"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
            />
            Remember me
          </label>
          <a href="#" onClick={handleForgotPassword} className="forgot-password">
            Forgot Password?
          </a>
        </div>

        <button type="submit" className="login-button" disabled={loading}>
          {loading ? 'ENTRANDO...' : 'LOGIN'}
        </button>
      </form>
    </div>
  );
};

export default LoginForm;