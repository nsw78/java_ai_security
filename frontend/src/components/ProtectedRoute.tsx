import { Navigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import Layout from './Layout'

export const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth()

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return <Layout>{children}</Layout>
}

