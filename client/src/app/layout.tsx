import type { Metadata } from 'next';
import { QueryProvider } from '@/components/providers/query-provider';
import { AuthProvider } from '@/components/providers/auth-provider';
import { SocketProvider } from '@/components/providers/socket-provider';
import { LiveChatWidget } from './LiveChatWidget';
import { Toaster } from 'sonner';
import './globals.css';

export const metadata: Metadata = {
  title: 'Fashion Store — Considered Goods',
  description: 'A curated edit of fashion and everyday objects.',
};

export default function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="vi">
      <body className="bg-[#FBF9F4] text-[#1D1F20] antialiased">
        <QueryProvider>
          <AuthProvider>
            <SocketProvider>
              {children}
              <LiveChatWidget />
              <Toaster position="top-right" richColors />
            </SocketProvider>
          </AuthProvider>
        </QueryProvider>
      </body>
    </html>
  );
}