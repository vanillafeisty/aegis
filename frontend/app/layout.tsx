import type { Metadata, Viewport } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'AEGIS AI v2.0 | LinkedIn & Outreach Automation Agent',
  description: 'Autonomous AI-powered LinkedIn outreach, email automation, profile optimization, and RFC 6202 SSE event orchestration.',
  icons: {
    icon: '/logo.svg',
  },
};

export const viewport: Viewport = {
  themeColor: '#f8fafc',
  width: 'device-width',
  initialScale: 1,
  maximumScale: 1,
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className="bg-[#f8fafc] text-slate-900 min-h-screen antialiased selection:bg-indigo-100 selection:text-indigo-900">
        {children}
      </body>
    </html>
  );
}
