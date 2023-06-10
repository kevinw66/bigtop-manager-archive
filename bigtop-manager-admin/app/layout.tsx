import './globals.css'
import './antd.css'
import { Inter } from 'next/font/google'

const inter = Inter({ subsets: ['latin'] })

export const metadata = {
  title: 'Bigtop Manager',
  description: 'An easy deployment solution for Bigtop',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body
        className={inter.className}
        // className="h-screen"
      >
      {children}
      </body>
    </html>
  )
}
