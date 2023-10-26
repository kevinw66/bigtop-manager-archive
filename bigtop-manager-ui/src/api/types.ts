export interface ResponseEntity<T = any> {
  code: number
  data?: T
  message: string
}

export interface PageVO<T = any> {
  total: number
  content: T[]
}
