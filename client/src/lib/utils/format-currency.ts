export function formatCurrency(amount: number | string | undefined | null): string {
  if (amount === undefined || amount === null) return '0 ₫';
  const val = typeof amount === 'string' ? parseFloat(amount) : amount;
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
}