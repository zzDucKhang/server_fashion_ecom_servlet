// src/middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('accessToken')?.value || request.headers.get('authorization');
  const { pathname } = request.nextUrl;

  // 1. Bảo vệ các trang yêu cầu đăng nhập (/checkout, /account/*)
  const isProtectedUserRoute = pathname.startsWith('/checkout') || pathname.startsWith('/account');
  if (isProtectedUserRoute && !token) {
    const loginUrl = new URL('/login', request.url);
    loginUrl.searchParams.set('redirectUrl', pathname);
    return NextResponse.redirect(loginUrl);
  }

  // 2. Bảo vệ khu vực Admin (/admin/*)
  if (pathname.startsWith('/admin')) {
    // Với Admin có thể giải mã token hoặc đối chiếu cookie role
    if (!token) {
      return NextResponse.redirect(new URL('/login', request.url));
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/checkout/:path*', '/account/:path*', '/admin/:path*'],
};