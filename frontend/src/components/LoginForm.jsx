import React, { useState } from 'react';
import authService from '../services/authService';
import './LoginForm.css';
// import { useNavigate } from 'react-router-dom'; // Descomente se usar React Router

const LoginForm = () => {
    const [email, setEmail] = useState('');
    const [senha, setSenha] = useState('');
    const [rememberMe, setRememberMe] = useState(false);
    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    // const navigate = useNavigate(); // Para React Router

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setErrorMessage('');

        try {
            const data = await authService.login(email, senha);
            console.log('Login bem-sucedido, token:', data.token);
            // O token já foi salvo no localStorage pelo authService
            setLoading(false);
            alert('Login bem-sucedido!'); // Substituir por redirecionamento ou atualização de estado
            // navigate('/dashboard'); // Exemplo de redirecionamento
        } catch (error) {
            // O authService agora lança error.detail ou error.message
            const message = error.detail || error.message || 'Falha no login. Verifique suas credenciais.';
            setErrorMessage(message);
            setLoading(false);
            console.error("Erro no login:", error);
        }
    };

    const handleForgotPassword = (e) => {
        e.preventDefault();
        alert('Funcionalidade "Esqueci a senha" a ser implementada!');
        // navigate('/recuperar-senha');
    };

    return (
        <div className="login-card">
            <div className="profile-icon-area">
                {/* Ícone de usuário pode ser um SVG, <img>, ou ::before como no CSS */}
            </div>
            {/* O título "Login" pode vir do App.js ou ser fixo aqui,
                a imagem de referência não tem um título "Login" explícito dentro do card,
                mas sim acima ou implícito. Ajustei para remover se não estiver na imagem.
            */}
            <form onSubmit={handleLogin}>
                <div className="form-group">
                    <span className="input-icon">✉️</span> {/* Pode substituir por SVG/Font Icon */}
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
                    <span className="input-icon">🔒</span> {/* Pode substituir por SVG/Font Icon */}
                    <input
                        type="password"
                        id="senha"
                        placeholder="Password" // Ajustado de "Senda"
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