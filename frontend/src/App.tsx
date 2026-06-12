import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from '@/store/auth'
import LoginPage from '@/pages/LoginPage'
import RegisterPage from '@/pages/RegisterPage'
import DashboardLayout from '@/components/layout/DashboardLayout'
import Dashboard from '@/pages/Dashboard'
import JobsPage from '@/pages/JobsPage'
import ApplicationsPage from '@/pages/ApplicationsPage'
import ResumeAnalyzer from '@/pages/ResumeAnalyzer'
import ContentCreator from '@/pages/ContentCreator'
import SettingsPage from '@/pages/SettingsPage'

function App() {
  const { user } = useAuthStore()

  return (
    <Router>
      <Routes>
        {!user ? (
          <>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
          </>
        ) : (
          <>
            <Route element={<DashboardLayout />}>
              <Route path="/" element={<Dashboard />} />
              <Route path="/jobs" element={<JobsPage />} />
              <Route path="/applications" element={<ApplicationsPage />} />
              <Route path="/resume" element={<ResumeAnalyzer />} />
              <Route path="/content" element={<ContentCreator />} />
              <Route path="/settings" element={<SettingsPage />} />
            </Route>
            <Route path="*" element={<Navigate to="/" replace />} />
          </>
        )}
      </Routes>
    </Router>
  )
}

export default App
