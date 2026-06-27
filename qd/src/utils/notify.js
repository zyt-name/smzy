import { reactive } from 'vue'

const state = reactive({
  list: [],
  idCounter: 0
})

let notifyInstance = null

export const setNotifyInstance = (instance) => {
  notifyInstance = instance
}

export const handleResponse = (res, silent = false) => {
  if (silent || !res) return res
  if (notifyInstance) {
    notifyInstance.handleResponse(res)
  } else {
    const id = ++state.idCounter
    if (res.code === 200) {
      state.list.push({ id, message: res.msg && res.msg !== '未知错误' ? res.msg : '操作成功！', type: 'success' })
      setTimeout(() => remove(id), 3000)
    } else if (res.code === 500) {
      state.list.push({ id, message: res.msg && res.msg !== '未知错误' ? res.msg : '服务器繁忙，请稍后重试~', type: 'error' })
      setTimeout(() => remove(id), 4000)
    }
  }
  return res
}

export const add = (message, type = 'success') => {
  if (notifyInstance) {
    notifyInstance.add(message, type)
  } else {
    const id = ++state.idCounter
    state.list.push({ id, message, type })
    setTimeout(() => remove(id), 3000)
  }
}

const remove = (id) => {
  const index = state.list.findIndex(n => n.id === id)
  if (index > -1) state.list.splice(index, 1)
}

export const getNotifications = () => state.list
