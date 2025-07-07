"use client"

import type React from "react"
import { useState, useRef, useEffect } from "react"
import { Link } from "react-router-dom"
import { login } from "../api/login.tsx"

const Login = () => {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState("")
  const [showHelp, setShowHelp] = useState(false)


  // États pour le captcha
  const [captchaInput, setCaptchaInput] = useState("")
  const [captchaText, setCaptchaText] = useState("")
  const canvasRef = useRef<HTMLCanvasElement>(null)

  useEffect(() => {
    generateCaptcha()
  }, [])

  const generateCaptcha = () => {
    const captcha = generateRandomCaptcha(6)
    setCaptchaText(captcha)

    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext("2d")
    if (!ctx) return

    // Clear the canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height)

    // Background color
    ctx.fillStyle = "#f0f0f0"
    ctx.fillRect(0, 0, canvas.width, canvas.height)

    // Text properties
    ctx.font = "24px Arial"
    ctx.fillStyle = "#333"
    ctx.textAlign = "center"
    ctx.textBaseline = "middle"

    // Write the captcha
    ctx.fillText(captcha, canvas.width / 2, canvas.height / 2)

    // Noise lines
    for (let i = 0; i < 3; i++) {
      ctx.strokeStyle = "#999"
      ctx.beginPath()
      ctx.moveTo(Math.random() * canvas.width, Math.random() * canvas.height)
      ctx.lineTo(Math.random() * canvas.width, Math.random() * canvas.height)
      ctx.stroke()
    }
  }

  const generateRandomCaptcha = (length: number) => {
    let result = ""
    const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    const charactersLength = characters.length
    for (let i = 0; i < length; i++) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength))
    }
    return result
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!email || !password) {
      setError("Please fill in all fields.")
      return
    }

    // Vérifier le captcha
    if (captchaInput !== captchaText) {
      setError("Invalid CAPTCHA. Please try again.")
      generateCaptcha() // Regenerate captcha on error
      return
    }

    setError("")

    try {
      console.log("email : " + email + " password : " + password)
      const a = await login(email, password)
      setError(a)

      // Si la connexion échoue, réinitialiser le captcha
      if (a) {
        generateCaptcha()
        setCaptchaInput("")
      }
    } catch (error) {
      console.error("Erreur de connexion", error)
      generateCaptcha() // Regenerate captcha on error
      setCaptchaInput("")
    }
  }

  return (
      <div className="w-screen min-h-screen bg-[#384454] flex flex-col items-center justify-center px-4 relative">
        {/* Logo */}
        <img src="/White-Logo-without-bg.png" alt="Logo" className="mx-auto mb-6 w-36 h-auto" />

        {/* Form */}
        <form onSubmit={handleSubmit} className="w-full max-w-sm space-y-4">
          <input
              type="email"
              required
              placeholder="E-mail"
              className="w-full px-4 py-4 bg-[#d3d4dc] text-gray-800 placeholder-white rounded-full border-none focus:outline-none focus:ring-0"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
          />

          <div className="relative">
            <input
                type={showPassword ? "text" : "password"}
                required
                placeholder="Password"
                className="w-full px-4 py-4 pr-12 bg-[#d3d4dc] text-gray-800 placeholder-white rounded-full border-none focus:outline-none focus:ring-0"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute inset-y-0 right-0 pr-4 flex items-center text-gray-500 hover:text-gray-700"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                {showPassword ? (
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21"
                    />
                ) : (
                    <>
                      <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                      />
                      <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                      />
                    </>
                )}
              </svg>
            </button>
          </div>

          {/* CAPTCHA Section */}
          <div className="space-y-3">
            <div className="flex items-center justify-center space-x-3">
              <canvas ref={canvasRef} width={140} height={50} className="border border-gray-300 rounded-lg bg-white" />
              <button
                  type="button"
                  onClick={generateCaptcha}
                  className="px-3 py-2 bg-gradient-to-r from-[#F89BEB] to-[#EA508E]  rounded-lg hover:bg-pink-600 focus:outline-none"
                  title="Generate new CAPTCHA"
              >
                🔄
              </button>
            </div>
            <input
                type="text"
                placeholder="Enter the code above"
                className="w-full px-4 py-3 bg-[#d3d4dc] text-gray-800 placeholder-white rounded-full border-none focus:outline-none focus:ring-0"
                value={captchaInput}
                onChange={(e) => setCaptchaInput(e.target.value)}
                maxLength={6}
            />
          </div>

          <div>
            <Link to="/reset-password" className="text-sm text-[#EA508E] underline">
              Forgot your password?
            </Link>
          </div>

          <button
              type="submit"
              className="w-full bg-gradient-to-b from-[#E1A624] to-[#7B5B14] text-white font-bold py-4 rounded-full hover:from-[#D4991F] hover:to-[#A6851A] focus:outline-none transition-all duration-200 shadow-lg"
          >
            Log-in
          </button>
        </form>

        {/* Error Message */}
        {error && (
            <div className="mt-4 text-center">
              <p className="text-sm text-red-400">{error}</p>
            </div>
        )}

        {/* Bottom Text */}
        <div className="mt-6 text-center text-sm text-gray-400">
          No account?{" "}
          <Link to="/signup" className="text-[#EA508E] underline font-medium">
            Create one
          </Link>
        </div>

        {/* Help Button */}
        <div className="fixed bottom-6 right-6">
          <button
              onClick={() => setShowHelp(!showHelp)}
              className="flex items-center justify-center w-12 h-12 bg-gradient-to-r from-[#F89BEB] to-[#EA508E] text-white rounded-full shadow-lg hover:shadow-xl transition-all duration-200"
          >
            <span className="text-xl font-bold">?</span>
          </button>
        </div>

        {/* Help Modal */}
        {showHelp && (
            <div className="fixed bottom-20 right-6 bg-white rounded-lg shadow-xl p-4 w-80 border border-gray-200">
              <div className="flex justify-between items-start mb-3">
                <h3 className="text-lg font-bold text-gray-800">Log in Help</h3>
                <button onClick={() => setShowHelp(false)} className="text-gray-500 hover:text-gray-700 text-xl font-bold">
                  ×
                </button>
              </div>
              <div className="text-sm text-gray-600 space-y-2">
                <p>
                  <strong>How to log-in:</strong>
                </p>
                <ol className="list-decimal list-inside space-y-1 ml-2">
                  <li>Enter your email address in the field above</li>
                  <li>Enter your password in the field above</li>
                  <li>Write the code in the field above</li>
                  <li>If you forgot your password : click on "forgot your password"</li>
                  <li>If you don't have an account : click on "create one"</li>
                </ol>
              </div>
            </div>
        )}

        {/* Overlay to close help when clicking outside */}
        {showHelp && <div className="fixed inset-0 z-[-1]" onClick={() => setShowHelp(false)} />}
      </div>
  )
}

export default Login
