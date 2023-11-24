import 'vue-router'
import { VNode } from 'vue'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: VNode
    suffix?: VNode
  }
}
