import Link from 'next/link';

export function SiteFooter() {
  return (
    <footer className="border-t border-[#EAE6DF] bg-white py-12 mt-auto">
      <div className="mx-auto max-w-[1440px] px-4 sm:px-8 lg:px-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <Link href="/" className="font-display text-2xl font-semibold tracking-[-.06em]">
              common<span className="text-[#84EA53]">.</span>
            </Link>
            <p className="mt-3 text-xs text-neutral-500 leading-relaxed max-w-xs">
              Bộ sưu tập chọn lọc thời trang đương đại và các sản phẩm phong cách sống tối giản.
            </p>
          </div>
          <div>
            <h4 className="eyebrow mb-3">Tài khoản</h4>
            <ul className="space-y-2 text-xs text-neutral-600">
              <li><Link href="/profile" className="hover:text-neutral-900 transition">Hồ sơ cá nhân</Link></li>
              <li><Link href="/orders" className="hover:text-neutral-900 transition">Lịch sử đơn hàng</Link></li>
              <li><Link href="/addresses" className="hover:text-neutral-900 transition">Sổ địa chỉ</Link></li>
              <li><Link href="/security" className="hover:text-neutral-900 transition">Bảo mật tài khoản</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="eyebrow mb-3">Hỗ trợ khách hàng</h4>
            <ul className="space-y-2 text-xs text-neutral-600">
              <li><Link href="#" className="hover:text-neutral-900 transition">Chính sách đổi trả</Link></li>
              <li><Link href="#" className="hover:text-neutral-900 transition">Hướng dẫn chọn size</Link></li>
              <li><Link href="#" className="hover:text-neutral-900 transition">Phương thức thanh toán</Link></li>
              <li><Link href="#" className="hover:text-neutral-900 transition">Câu hỏi thường gặp</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="eyebrow mb-3">Về chúng tôi</h4>
            <p className="text-xs text-neutral-500 leading-relaxed">
              Trải nghiệm mua sắm hiện đại, tinh tế và bền vững.
            </p>
            <p className="text-xs text-neutral-400 mt-4">
              © {new Date().getFullYear()} Common Fashion. All rights reserved.
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
}

