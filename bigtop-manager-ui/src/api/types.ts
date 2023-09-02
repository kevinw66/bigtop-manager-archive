export interface ResponseEntity<T = any> {
  code: number
  data?: T
  message: string
}
