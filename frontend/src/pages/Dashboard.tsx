import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  Shield,
  AlertTriangle,
  CheckCircle,
  TrendingUp,
  Activity,
  Zap,
  FileText,
  Settings,
} from 'lucide-react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'

interface Stats {
  totalRequests: number
  blockedRequests: number
  averageRiskScore: number
  requestsToday: number
  riskDistribution: { name: string; value: number }[]
  requestsOverTime: { date: string; requests: number; blocked: number }[]
}

const Dashboard = () => {
  const [stats, setStats] = useState<Stats | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadStats()
  }, [])

  const loadStats = async () => {
    try {
      // Mock data for demo - replace with actual API call
      setStats({
        totalRequests: 1247,
        blockedRequests: 89,
        averageRiskScore: 42,
        requestsToday: 156,
        riskDistribution: [
          { name: 'Low', value: 856 },
          { name: 'Medium', value: 302 },
          { name: 'High', value: 89 },
        ],
        requestsOverTime: [
          { date: 'Mon', requests: 120, blocked: 8 },
          { date: 'Tue', requests: 145, blocked: 12 },
          { date: 'Wed', requests: 132, blocked: 9 },
          { date: 'Thu', requests: 156, blocked: 11 },
          { date: 'Fri', requests: 178, blocked: 15 },
          { date: 'Sat', requests: 98, blocked: 5 },
          { date: 'Sun', requests: 156, blocked: 10 },
        ],
      })
    } catch (error) {
      console.error('Error loading stats:', error)
    } finally {
      setLoading(false)
    }
  }

  const COLORS = ['#22c55e', '#eab308', '#ef4444']

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Dashboard</h1>
          <p className="text-slate-600 mt-1">Monitor your AI security in real-time</p>
        </div>
        <Link to="/analyzer" className="btn-primary">
          <Zap className="w-5 h-5 inline mr-2" />
          Analyze Prompt
        </Link>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-slate-600 mb-1">Total Requests</p>
              <p className="text-3xl font-bold text-slate-900">{stats?.totalRequests.toLocaleString()}</p>
            </div>
            <div className="p-3 bg-primary-100 rounded-xl">
              <Activity className="w-6 h-6 text-primary-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center text-sm">
            <TrendingUp className="w-4 h-4 text-success-500 mr-1" />
            <span className="text-success-600 font-semibold">+12.5%</span>
            <span className="text-slate-500 ml-1">vs last week</span>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-slate-600 mb-1">Blocked Requests</p>
              <p className="text-3xl font-bold text-slate-900">{stats?.blockedRequests}</p>
            </div>
            <div className="p-3 bg-danger-100 rounded-xl">
              <Shield className="w-6 h-6 text-danger-600" />
            </div>
          </div>
          <div className="mt-4">
            <div className="flex items-center justify-between text-sm mb-1">
              <span className="text-slate-600">Block Rate</span>
              <span className="font-semibold text-slate-900">
                {stats ? ((stats.blockedRequests / stats.totalRequests) * 100).toFixed(1) : 0}%
              </span>
            </div>
            <div className="w-full bg-slate-200 rounded-full h-2">
              <div
                className="bg-danger-500 h-2 rounded-full transition-all"
                style={{
                  width: `${stats ? (stats.blockedRequests / stats.totalRequests) * 100 : 0}%`,
                }}
              ></div>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-slate-600 mb-1">Avg Risk Score</p>
              <p className="text-3xl font-bold text-slate-900">{stats?.averageRiskScore}</p>
            </div>
            <div className="p-3 bg-yellow-100 rounded-xl">
              <AlertTriangle className="w-6 h-6 text-yellow-600" />
            </div>
          </div>
          <div className="mt-4">
            <div className="w-full bg-slate-200 rounded-full h-2">
              <div
                className="bg-yellow-500 h-2 rounded-full transition-all"
                style={{ width: `${stats?.averageRiskScore || 0}%` }}
              ></div>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-slate-600 mb-1">Requests Today</p>
              <p className="text-3xl font-bold text-slate-900">{stats?.requestsToday}</p>
            </div>
            <div className="p-3 bg-success-100 rounded-xl">
              <CheckCircle className="w-6 h-6 text-success-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center text-sm">
            <TrendingUp className="w-4 h-4 text-success-500 mr-1" />
            <span className="text-success-600 font-semibold">Active</span>
          </div>
        </div>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-slate-900 mb-4">Requests Over Time</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={stats?.requestsOverTime}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
              <XAxis dataKey="date" stroke="#64748b" />
              <YAxis stroke="#64748b" />
              <Tooltip
                contentStyle={{
                  backgroundColor: 'white',
                  border: '1px solid #e2e8f0',
                  borderRadius: '8px',
                }}
              />
              <Line
                type="monotone"
                dataKey="requests"
                stroke="#0ea5e9"
                strokeWidth={3}
                dot={{ fill: '#0ea5e9', r: 4 }}
              />
              <Line
                type="monotone"
                dataKey="blocked"
                stroke="#ef4444"
                strokeWidth={3}
                dot={{ fill: '#ef4444', r: 4 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-slate-900 mb-4">Risk Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={stats?.riskDistribution}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {stats?.riskDistribution.map((_, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="card">
        <h3 className="text-lg font-semibold text-slate-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Link
            to="/analyzer"
            className="p-4 bg-gradient-to-br from-primary-50 to-primary-100 rounded-xl border border-primary-200 hover:shadow-lg transition-all"
          >
            <Zap className="w-6 h-6 text-primary-600 mb-2" />
            <h4 className="font-semibold text-slate-900">Analyze Prompt</h4>
            <p className="text-sm text-slate-600 mt-1">Test prompt security</p>
          </Link>
          <Link
            to="/audit"
            className="p-4 bg-gradient-to-br from-slate-50 to-slate-100 rounded-xl border border-slate-200 hover:shadow-lg transition-all"
          >
            <FileText className="w-6 h-6 text-slate-600 mb-2" />
            <h4 className="font-semibold text-slate-900">View Audit Logs</h4>
            <p className="text-sm text-slate-600 mt-1">Check activity history</p>
          </Link>
          <Link
            to="/settings"
            className="p-4 bg-gradient-to-br from-indigo-50 to-indigo-100 rounded-xl border border-indigo-200 hover:shadow-lg transition-all"
          >
            <Settings className="w-6 h-6 text-indigo-600 mb-2" />
            <h4 className="font-semibold text-slate-900">Settings</h4>
            <p className="text-sm text-slate-600 mt-1">Configure preferences</p>
          </Link>
        </div>
      </div>
    </div>
  )
}

export default Dashboard

