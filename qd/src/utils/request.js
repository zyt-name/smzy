import axios from 'axios'
import { handleResponse } from './notify'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['token'] = token
    }
    // 注意：id 和 username 由 Gateway 从 token 解析并设置到请求头
    // 前端不需要传递，避免覆盖 Gateway 设置的值
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res && typeof res === 'object' && 'code' in res) {
      const silent = res._silent
      delete res._silent
      // 登录接口的401错误（账号被封禁）不处理，让业务层处理
      const isLoginApi = response.config.url && (
        response.config.url.includes('/login') ||
        response.config.url.includes('/adminLogin') ||
        response.config.url.includes('/merchantLogin')
      )
      if (res.code === 401 && !isLoginApi) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.setItem('currentPage', 'login')
        window.location.reload()
        return res
      }
      handleResponse(res, silent)
    }
    return res
  },
  error => {
    // 登录接口的401错误（账号被封禁）不刷新页面，让业务层处理
    const isLoginApi = error.config && error.config.url && (
      error.config.url.includes('/login') ||
      error.config.url.includes('/adminLogin') ||
      error.config.url.includes('/merchantLogin')
    )
    if (error.response && error.response.status === 401 && !isLoginApi) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.setItem('currentPage', 'login')
      window.location.reload()
      return Promise.reject(error)
    }
    // 其他错误正常抛出，让业务层处理
    return Promise.reject(error)
  }
)

export default service
