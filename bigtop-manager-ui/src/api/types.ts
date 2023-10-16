export interface ResponseEntity<T = any> {
  code: number
  data?: T
  message: string
}

export type CommandVO = {
  id: number
  state: string
}
