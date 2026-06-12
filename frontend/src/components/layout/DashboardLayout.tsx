import { Outlet, Link, useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/store/auth'
import { LogOut, Home, Briefcase, FileText, PenTool, Settings } from 'lucide-react'

export default function DashboardLayout() {
  const navigate = useNavigate()
  const logout = useAuthStore((state) => state.logout)

  function handleLogout() {
    logout()
    navigate('/login')
  }

  const navItems = [
    { path: '/', icon: Home, label: 'Dashboard' },
    { path: '/jobs', icon: Briefcase, label: 'Jobs' },
    { path: '/applications', icon: FileText, label: 'Applications' },
    { path: '/resume', icon: FileText, label: 'Resume' },
    { path: '/content', icon: PenTool, label: 'Content' },
    { path: '/settings', icon: Settings, label: 'Settings' },
  ]

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <div className="w-64 bg-slate-900 text-white flex flex-col">
        <div className="p-6 border-b border-slate-700">
          <h1 className="text-2xl font-bold">Aegis</h1>
          <p className="text-sm text-gray-400">LinkedIn Agent</p>
        </div>

        <nav className="flex-1 p-4 space-y-2">
          {navItems.map(({ path, icon: Icon, label }) => (
            <Link
              key={path}
              to={path}
              className="flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-slate-800 transition-colors"
            >
              <Icon size={20} />
              <span>{label}</span>
            </Link>
          ))}
        </nav>

        <div className="p-4 border-t border-slate-700">
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-red-900 transition-colors"
          >
            <LogOut size={20} />
            <span>Logout</span>
          </button>
        </div>
      </div>

      {/* Main content */}
      <div className="flex-1 flex flex-col">
        {/* Top bar */}
        <div className="bg-white border-b border-gray-200 p-4">
          <h2 className="text-xl font-semibold text-gray-800">Autonomous LinkedIn Intelligence</h2>
        </div>

        {/* Page content */}
        <div className="flex-1 overflow-auto">
          <Outlet />
        </div>
      </div>
    </div>
  )
}
