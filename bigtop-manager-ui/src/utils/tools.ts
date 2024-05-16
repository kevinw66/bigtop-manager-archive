/**
 * copy text
 * @param {string} text
 */
export const copyText = (text: string): Promise<any> => {
  if (navigator.clipboard) {
    return navigator.clipboard.writeText(text)
  }
  return new Promise(async (resolve, reject) => {
    try {
      const { default: ClipboardJS } = await import('clipboard')
      if (!ClipboardJS.isSupported()) {
        reject(new Error('ClipboardJS not support!'))
        return
      }
      const btn = document.createElement('button')
      btn.innerText = text
      const clipboard = new ClipboardJS(btn, {
        text: () => text
      })
      clipboard.on('success', () => {
        resolve(true)
        clipboard.destroy()
      })
      clipboard.on('error', (err) => {
        reject(err)
        clipboard.destroy()
      })
      btn.click()
    } catch (error) {
      console.log('copytext :>> ', error)
    }
  })
}
