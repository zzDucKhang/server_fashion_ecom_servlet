import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  reactCompiler: true,
  images: {
    remotePatterns: [
      { protocol: 'https', hostname: 'images.unsplash.com', pathname: '/**' },
      // Thêm cấu hình dưới đây để Next.js cho phép load ảnh từ Supabase Storage
      { protocol: 'https', hostname: '*.supabase.co', pathname: '/**' }, 
    ],
  },
};

export default nextConfig;