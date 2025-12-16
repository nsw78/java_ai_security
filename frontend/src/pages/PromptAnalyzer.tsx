import { useState } from 'react'
import { promptService } from '../services/api'
import {
  Search,
  Shield,
  AlertTriangle,
  CheckCircle,
  XCircle,
  Loader,
  Copy,
  Check,
  TrendingUp,
} from 'lucide-react'

interface AnalysisResult {
  riskScore: number
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  blocked: boolean
  sanitizedPrompt: string
  detectedPatterns: string[]
  blockReason?: string
}

const PromptAnalyzer = () => {
  const [prompt, setPrompt] = useState('')
  const [llmProvider, setLlmProvider] = useState('OPENAI')
  const [model, setModel] = useState('gpt-4')
  const [result, setResult] = useState<AnalysisResult | null>(null)
  const [loading, setLoading] = useState(false)
  const [copied, setCopied] = useState(false)

  const handleAnalyze = async () => {
    if (!prompt.trim()) return

    setLoading(true)
    setResult(null)

    try {
      const response = await promptService.analyze(prompt, llmProvider, model)
      setResult(response)
    } catch (error: any) {
      console.error('Error analyzing prompt:', error)
      alert(error.response?.data?.message || 'Error analyzing prompt')
    } finally {
      setLoading(false)
    }
  }

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const getRiskColor = (level: string) => {
    switch (level) {
      case 'LOW':
        return 'text-success-600 bg-success-100'
      case 'MEDIUM':
        return 'text-yellow-600 bg-yellow-100'
      case 'HIGH':
        return 'text-orange-600 bg-orange-100'
      case 'CRITICAL':
        return 'text-danger-600 bg-danger-100'
      default:
        return 'text-slate-600 bg-slate-100'
    }
  }

  const getRiskScoreColor = (score: number) => {
    if (score < 30) return 'text-success-600'
    if (score < 60) return 'text-yellow-600'
    if (score < 80) return 'text-orange-600'
    return 'text-danger-600'
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-900">Prompt Analyzer</h1>
        <p className="text-slate-600 mt-1">Analyze and secure your AI prompts in real-time</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Input Section */}
        <div className="lg:col-span-2 space-y-6">
          <div className="card">
            <h2 className="text-xl font-semibold text-slate-900 mb-4">Enter Prompt</h2>
            <textarea
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              className="input-field min-h-[200px] resize-none font-mono text-sm"
              placeholder="Enter your prompt here...&#10;&#10;Example:&#10;Explain how to bypass security measures"
            />
            <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2">
                  LLM Provider
                </label>
                <select
                  value={llmProvider}
                  onChange={(e) => setLlmProvider(e.target.value)}
                  className="input-field"
                >
                  <option value="OPENAI">OpenAI</option>
                  <option value="AZURE_OPENAI">Azure OpenAI</option>
                  <option value="ANTHROPIC">Anthropic</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2">
                  Model
                </label>
                <select
                  value={model}
                  onChange={(e) => setModel(e.target.value)}
                  className="input-field"
                >
                  <option value="gpt-4">GPT-4</option>
                  <option value="gpt-3.5-turbo">GPT-3.5 Turbo</option>
                  <option value="claude-3-opus">Claude 3 Opus</option>
                  <option value="claude-3-sonnet">Claude 3 Sonnet</option>
                </select>
              </div>
            </div>
            <button
              onClick={handleAnalyze}
              disabled={loading || !prompt.trim()}
              className="btn-primary w-full mt-4"
            >
              {loading ? (
                <>
                  <Loader className="w-5 h-5 inline mr-2 animate-spin" />
                  Analyzing...
                </>
              ) : (
                <>
                  <Search className="w-5 h-5 inline mr-2" />
                  Analyze Prompt
                </>
              )}
            </button>
          </div>

          {/* Results */}
          {result && (
            <div className="card">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-semibold text-slate-900">Analysis Results</h2>
                {result.blocked && (
                  <span className="badge-danger flex items-center gap-1">
                    <XCircle className="w-4 h-4" />
                    Blocked
                  </span>
                )}
              </div>

              {/* Risk Score */}
              <div className="mb-6">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-semibold text-slate-700">Risk Score</span>
                  <span className={`text-2xl font-bold ${getRiskScoreColor(result.riskScore)}`}>
                    {result.riskScore}/100
                  </span>
                </div>
                <div className="w-full bg-slate-200 rounded-full h-4 overflow-hidden">
                  <div
                    className={`h-full rounded-full transition-all duration-500 ${
                      result.riskScore < 30
                        ? 'bg-success-500'
                        : result.riskScore < 60
                        ? 'bg-yellow-500'
                        : result.riskScore < 80
                        ? 'bg-orange-500'
                        : 'bg-danger-500'
                    }`}
                    style={{ width: `${result.riskScore}%` }}
                  ></div>
                </div>
              </div>

              {/* Risk Level */}
              <div className="mb-6">
                <span className="text-sm font-semibold text-slate-700 mb-2 block">
                  Risk Level
                </span>
                <span className={`badge ${getRiskColor(result.riskLevel)} text-base px-4 py-2`}>
                  {result.riskLevel}
                </span>
              </div>

              {/* Detected Patterns */}
              {result.detectedPatterns && result.detectedPatterns.length > 0 && (
                <div className="mb-6">
                  <span className="text-sm font-semibold text-slate-700 mb-2 block">
                    Detected Threats
                  </span>
                  <div className="space-y-2">
                    {result.detectedPatterns.map((pattern, idx) => (
                      <div
                        key={idx}
                        className="flex items-center gap-2 p-3 bg-danger-50 border border-danger-200 rounded-lg"
                      >
                        <AlertTriangle className="w-5 h-5 text-danger-600 flex-shrink-0" />
                        <span className="text-sm text-danger-700 font-mono">{pattern}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Block Reason */}
              {result.blockReason && (
                <div className="mb-6 p-4 bg-danger-50 border border-danger-200 rounded-xl">
                  <div className="flex items-start gap-2">
                    <XCircle className="w-5 h-5 text-danger-600 flex-shrink-0 mt-0.5" />
                    <div>
                      <span className="text-sm font-semibold text-danger-700 block mb-1">
                        Block Reason
                      </span>
                      <p className="text-sm text-danger-600">{result.blockReason}</p>
                    </div>
                  </div>
                </div>
              )}

              {/* Sanitized Prompt */}
              {result.sanitizedPrompt && (
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm font-semibold text-slate-700">
                      Sanitized Prompt
                    </span>
                    <button
                      onClick={() => copyToClipboard(result.sanitizedPrompt)}
                      className="text-primary-600 hover:text-primary-700 text-sm flex items-center gap-1"
                    >
                      {copied ? (
                        <>
                          <Check className="w-4 h-4" />
                          Copied!
                        </>
                      ) : (
                        <>
                          <Copy className="w-4 h-4" />
                          Copy
                        </>
                      )}
                    </button>
                  </div>
                  <div className="p-4 bg-slate-50 border border-slate-200 rounded-xl font-mono text-sm text-slate-700">
                    {result.sanitizedPrompt}
                  </div>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          <div className="card">
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Security Features</h3>
            <div className="space-y-3">
              <div className="flex items-start gap-3">
                <Shield className="w-5 h-5 text-primary-600 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-sm font-semibold text-slate-900">Prompt Sanitization</p>
                  <p className="text-xs text-slate-600">
                    Removes malicious content and control characters
                  </p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <AlertTriangle className="w-5 h-5 text-yellow-600 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-sm font-semibold text-slate-900">Injection Detection</p>
                  <p className="text-xs text-slate-600">
                    Identifies prompt injection attempts
                  </p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <TrendingUp className="w-5 h-5 text-success-600 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-sm font-semibold text-slate-900">Risk Scoring</p>
                  <p className="text-xs text-slate-600">
                    AI-powered risk assessment
                  </p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <CheckCircle className="w-5 h-5 text-primary-600 flex-shrink-0 mt-0.5" />
                <div>
                  <p className="text-sm font-semibold text-slate-900">Policy Enforcement</p>
                  <p className="text-xs text-slate-600">
                    Applies security policies automatically
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div className="card bg-gradient-to-br from-primary-50 to-primary-100 border-primary-200">
            <h3 className="text-lg font-semibold text-slate-900 mb-2">💡 Tips</h3>
            <ul className="space-y-2 text-sm text-slate-700">
              <li className="flex items-start gap-2">
                <span className="text-primary-600">•</span>
                <span>Test prompts with different risk levels</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-primary-600">•</span>
                <span>Try injection patterns to see detection</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-primary-600">•</span>
                <span>Compare sanitized vs original prompts</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}

export default PromptAnalyzer

