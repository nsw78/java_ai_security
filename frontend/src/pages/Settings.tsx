import { useState } from 'react'
import { Shield, Bell, Key, User, Save } from 'lucide-react'

const Settings = () => {
  const [notifications, setNotifications] = useState({
    email: true,
    critical: true,
    weekly: false,
  })

  const [security, setSecurity] = useState({
    twoFactor: false,
    sessionTimeout: '30',
  })

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-900">Settings</h1>
        <p className="text-slate-600 mt-1">Manage your account and preferences</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          {/* Profile Settings */}
          <div className="card">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-primary-100 rounded-xl">
                <User className="w-5 h-5 text-primary-600" />
              </div>
              <h2 className="text-xl font-semibold text-slate-900">Profile</h2>
            </div>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2">
                  Email
                </label>
                <input
                  type="email"
                  className="input-field"
                  defaultValue="user@example.com"
                  disabled
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2">
                  Plan
                </label>
                <input
                  type="text"
                  className="input-field"
                  defaultValue="FREE"
                  disabled
                />
              </div>
            </div>
          </div>

          {/* Security Settings */}
          <div className="card">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-danger-100 rounded-xl">
                <Shield className="w-5 h-5 text-danger-600" />
              </div>
              <h2 className="text-xl font-semibold text-slate-900">Security</h2>
            </div>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-4 bg-slate-50 rounded-xl">
                <div>
                  <p className="font-semibold text-slate-900">Two-Factor Authentication</p>
                  <p className="text-sm text-slate-600">Add an extra layer of security</p>
                </div>
                <label className="relative inline-flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={security.twoFactor}
                    onChange={(e) =>
                      setSecurity({ ...security, twoFactor: e.target.checked })
                    }
                    className="sr-only peer"
                  />
                  <div className="w-11 h-6 bg-slate-300 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-primary-500 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
                </label>
              </div>
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2">
                  Session Timeout (minutes)
                </label>
                <select
                  value={security.sessionTimeout}
                  onChange={(e) =>
                    setSecurity({ ...security, sessionTimeout: e.target.value })
                  }
                  className="input-field"
                >
                  <option value="15">15 minutes</option>
                  <option value="30">30 minutes</option>
                  <option value="60">60 minutes</option>
                  <option value="120">2 hours</option>
                </select>
              </div>
            </div>
          </div>

          {/* Notifications */}
          <div className="card">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-yellow-100 rounded-xl">
                <Bell className="w-5 h-5 text-yellow-600" />
              </div>
              <h2 className="text-xl font-semibold text-slate-900">Notifications</h2>
            </div>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-4 bg-slate-50 rounded-xl">
                <div>
                  <p className="font-semibold text-slate-900">Email Notifications</p>
                  <p className="text-sm text-slate-600">Receive updates via email</p>
                </div>
                <label className="relative inline-flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={notifications.email}
                    onChange={(e) =>
                      setNotifications({ ...notifications, email: e.target.checked })
                    }
                    className="sr-only peer"
                  />
                  <div className="w-11 h-6 bg-slate-300 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-primary-500 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
                </label>
              </div>
              <div className="flex items-center justify-between p-4 bg-slate-50 rounded-xl">
                <div>
                  <p className="font-semibold text-slate-900">Critical Alerts</p>
                  <p className="text-sm text-slate-600">Notify on critical security events</p>
                </div>
                <label className="relative inline-flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={notifications.critical}
                    onChange={(e) =>
                      setNotifications({ ...notifications, critical: e.target.checked })
                    }
                    className="sr-only peer"
                  />
                  <div className="w-11 h-6 bg-slate-300 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-primary-500 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
                </label>
              </div>
              <div className="flex items-center justify-between p-4 bg-slate-50 rounded-xl">
                <div>
                  <p className="font-semibold text-slate-900">Weekly Reports</p>
                  <p className="text-sm text-slate-600">Receive weekly security summaries</p>
                </div>
                <label className="relative inline-flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={notifications.weekly}
                    onChange={(e) =>
                      setNotifications({ ...notifications, weekly: e.target.checked })
                    }
                    className="sr-only peer"
                  />
                  <div className="w-11 h-6 bg-slate-300 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-primary-500 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
                </label>
              </div>
            </div>
          </div>

          <button className="btn-primary w-full">
            <Save className="w-5 h-5 inline mr-2" />
            Save Changes
          </button>
        </div>

        {/* API Keys */}
        <div className="space-y-6">
          <div className="card">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-2 bg-indigo-100 rounded-xl">
                <Key className="w-5 h-5 text-indigo-600" />
              </div>
              <h2 className="text-lg font-semibold text-slate-900">API Keys</h2>
            </div>
            <p className="text-sm text-slate-600 mb-4">
              Manage your API keys for programmatic access
            </p>
            <button className="btn-secondary w-full">Generate New Key</button>
          </div>

          <div className="card bg-gradient-to-br from-primary-50 to-primary-100 border-primary-200">
            <h3 className="text-lg font-semibold text-slate-900 mb-2">📊 Usage</h3>
            <div className="space-y-3">
              <div>
                <div className="flex justify-between text-sm mb-1">
                  <span className="text-slate-600">Requests Today</span>
                  <span className="font-semibold text-slate-900">156</span>
                </div>
                <div className="w-full bg-white/50 rounded-full h-2">
                  <div className="bg-primary-500 h-2 rounded-full" style={{ width: '52%' }}></div>
                </div>
              </div>
              <div>
                <div className="flex justify-between text-sm mb-1">
                  <span className="text-slate-600">Rate Limit</span>
                  <span className="font-semibold text-slate-900">50 / 50</span>
                </div>
                <div className="w-full bg-white/50 rounded-full h-2">
                  <div className="bg-success-500 h-2 rounded-full" style={{ width: '100%' }}></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Settings

