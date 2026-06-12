import { useState } from 'react'
import { Zap, TrendingUp, Users, FileText } from 'lucide-react'

export default function Dashboard() {
  const [command, setCommand] = useState('')

  return (
    <div className="p-8 max-w-6xl mx-auto">
      {/* Welcome section */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold mb-2">Welcome to Aegis</h1>
        <p className="text-gray-600">Your autonomous LinkedIn intelligence agent</p>
      </div>

      {/* Command input */}
      <div className="bg-white rounded-lg shadow mb-8 p-6">
        <h2 className="text-lg font-semibold mb-4">Give me a command</h2>
        <div className="flex gap-2">
          <input
            type="text"
            value={command}
            onChange={(e) => setCommand(e.target.value)}
            placeholder="e.g., 'Find 10 AI Engineer jobs in Bangalore'"
            className="flex-1 px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
          />
          <button className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium">
            Send
          </button>
        </div>
        <p className="text-sm text-gray-500 mt-2">
          Try: "Send connection requests to 5 recruiters" or "Generate a LinkedIn post about AI"
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <StatCard
          icon={<FileText size={24} />}
          label="Applications"
          value="12"
          trend="+3 this week"
        />
        <StatCard
          icon={<Users size={24} />}
          label="Connection Requests"
          value="42"
          trend="+8 pending"
        />
        <StatCard
          icon={<Zap size={24} />}
          label="Tasks Completed"
          value="234"
          trend="This month"
        />
        <StatCard
          icon={<TrendingUp size={24} />}
          label="Profile Strength"
          value="92%"
          trend="+5% up"
        />
      </div>

      {/* Recent activity */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold mb-4">Recent Activity</h2>
        <div className="space-y-4">
          <ActivityItem
            action="Searched for jobs"
            details="AI Engineer, Bangalore"
            time="2 hours ago"
          />
          <ActivityItem
            action="Sent connection request"
            details="Sarah Chen at Google"
            time="4 hours ago"
          />
          <ActivityItem
            action="Applied to job"
            details="Senior ML Engineer at Meta"
            time="1 day ago"
          />
        </div>
      </div>
    </div>
  )
}

function StatCard({ icon, label, value, trend }: any) {
  return (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex items-center justify-between mb-4">
        <span className="text-gray-400">{icon}</span>
      </div>
      <p className="text-gray-600 text-sm mb-1">{label}</p>
      <p className="text-3xl font-bold mb-2">{value}</p>
      <p className="text-xs text-gray-500">{trend}</p>
    </div>
  )
}

function ActivityItem({ action, details, time }: any) {
  return (
    <div className="flex items-start gap-4 pb-4 border-b last:border-0">
      <div className="w-2 h-2 bg-blue-600 rounded-full mt-1.5" />
      <div>
        <p className="font-medium text-gray-800">{action}</p>
        <p className="text-sm text-gray-600">{details}</p>
        <p className="text-xs text-gray-500 mt-1">{time}</p>
      </div>
    </div>
  )
}
