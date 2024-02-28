import { loadEnv, defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())

  return {
    base: env.VITE_APP_BASE,
    plugins: [
      vue({
        script: {
          defineModel: true
        }
      })
    ],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    server: {
      hmr: true,
      proxy: {
        [env.VITE_APP_BASE_API]: {
          target: env.VITE_APP_BASE_URL,
          changeOrigin: true
        },
        [env.VITE_APP_BASE_WS_API]: {
          target: env.VITE_APP_BASE_WS_URL,
          ws: true
        }
      }
    }
  }
})
