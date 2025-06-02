import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginForm from './components/LoginForm'; 
import Dashboard from './components/Dashboard';
import authService from './services/authService';

function PrivateRoute({ children }) {
  const token = authService.getCurrentUserToken();
  return token ? children : <Navigate to="/login" replace />;
}

function App() {
  const token = authService.getCurrentUserToken();

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route 
            path="/login" 
            element={!token ? <LoginForm /> : <Navigate to="/dashboard" replace />} 
          />
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <Dashboard /> 
              </PrivateRoute>
            }
          />
          <Route 
            path="*" 
            element={<Navigate to={token ? "/dashboard" : "/login"} replace />} 
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;