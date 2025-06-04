import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, requiredRole }) => {
  const token = localStorage.getItem('token');
  const nivel = localStorage.getItem('nivel');

  if (!token) return <Navigate to="/login" replace />;
  if (requiredRole && nivel !== requiredRole) return <Navigate to="/unauthorized" replace />;

  return children;
};

export default ProtectedRoute;