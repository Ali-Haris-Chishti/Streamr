import Header from '@/common/Header'
import '../css/globals.css'
import Category from '@/components/Category'

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <head>
        <title>YouTube</title>
        <link rel="icon" type="image/x-icon" href="/page_logo.jpeg" sizes='16%16' />
        <link href="https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700&family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" />
      </head>
      <body>
        {/* <div className='flex justify-center items-start'> */}
        <div>
          <Header />
          {children}
        </div>
        
      </body>
    </html>
  )
}
