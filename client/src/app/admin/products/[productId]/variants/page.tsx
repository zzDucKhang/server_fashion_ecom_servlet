'use client';

import * as React from 'react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { ArrowLeft, Plus, Trash2, Layers, Loader2, Save } from 'lucide-react';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';
import { formatCurrency } from '@/lib/utils/format-currency';

interface VariantRow {
  id?: number | null;
  sku: string;
  variant: string;
  variantValue: string;
  originalPrice: number;
  sellingPrice: number;
  stockQuantity: number;
  weightGram: number;
}

export default function AdminProductVariantsPage() {
  const params = useParams<{ productId: string }>();
  const router = useRouter();
  const queryClient = useQueryClient();
  const productId = Number(params.productId);

  const { data: prodRes, isLoading } = useQuery({
    queryKey: ['admin-product-detail-variants', productId],
    queryFn: () => catalogApi.getProductById(productId),
    enabled: !!productId,
  });

  const product = prodRes?.data;
  const [variants, setVariants] = React.useState<VariantRow[]>([]);

  React.useEffect(() => {
    if (product?.variants && product.variants.length > 0) {
      setVariants(
        product.variants.map((v) => ({
          id: v.id,
          sku: v.sku,
          variant: v.variant || 'Màu sắc - Kích cỡ',
          variantValue: v.variantValue || 'Mặc định',
          originalPrice: Number(v.originalPrice || 0),
          sellingPrice: Number(v.sellingPrice || 0),
          stockQuantity: Number(v.stockQuantity || 0),
          weightGram: Number(v.weightGram || 200),
        }))
      );
    } else if (product) {
      setVariants([
        {
          id: null,
          sku: `SKU-${productId}-01`,
          variant: 'Kích cỡ',
          variantValue: 'Tiêu chuẩn',
          originalPrice: 200000,
          sellingPrice: 180000,
          stockQuantity: 50,
          weightGram: 200,
        },
      ]);
    }
  }, [product, productId]);

  const syncMutation = useMutation({
    mutationFn: (rows: VariantRow[]) => catalogApi.syncVariants({
      productId,
      variants: rows.map(r => ({
        ...r,
        id: r.id || undefined,
      })),
    }),
    onSuccess: () => {
      toast.success('Đồng bộ ma trận biến thể thành công!');
      queryClient.invalidateQueries({ queryKey: ['admin-product-detail-variants', productId] });
    },
    onError: (err: any) => {
      toast.error(err.response?.data?.message || 'Lỗi khi lưu biến thể');
    },
  });

  const handleAddRow = () => {
    setVariants((prev) => [
      ...prev,
      {
        id: null,
        sku: `SKU-${productId}-0${prev.length + 1}`,
        variant: 'Màu sắc - Kích cỡ',
        variantValue: 'Đen - L',
        originalPrice: 200000,
        sellingPrice: 180000,
        stockQuantity: 10,
        weightGram: 200,
      },
    ]);
  };

  const handleRemoveRow = (idx: number) => {
    if (variants.length <= 1) {
      toast.error('Sản phẩm phải có ít nhất một biến thể (SKU)');
      return;
    }
    setVariants((prev) => prev.filter((_, i) => i !== idx));
  };

  const handleFieldChange = (idx: number, field: keyof VariantRow, value: any) => {
    setVariants((prev) => {
      const updated = [...prev];
      updated[idx] = { ...updated[idx], [field]: value };
      return updated;
    });
  };

  if (isLoading) {
    return (
      <div className="flex justify-center py-20">
        <Loader2 className="size-8 animate-spin text-neutral-400" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#EAE6DF] pb-4">
        <div className="flex items-center gap-4">
          <Link href="/admin/products" className="p-2 border border-[#EAE6DF] rounded-xl hover:bg-neutral-50 transition">
            <ArrowLeft className="size-4 text-neutral-600" />
          </Link>
          <div>
            <span className="eyebrow block mb-0.5">QUẢN LÝ BIẾN THỂ SKU</span>
            <h1 className="text-xl sm:text-2xl font-bold text-neutral-900 truncate max-w-lg">
              {product?.defaultName}
            </h1>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <Button onClick={handleAddRow} variant="outline" className="rounded-xl border-[#EAE6DF] text-xs">
            <Plus className="size-4 mr-1.5" /> Thêm SKU
          </Button>
          <Button
            onClick={() => syncMutation.mutate(variants)}
            disabled={syncMutation.isPending}
            className="rounded-xl bg-[#1D1F20] !text-white [&_*]:!text-white hover:bg-neutral-800 text-xs"
          >
            {syncMutation.isPending ? (
              <>
                <Loader2 className="size-4 animate-spin mr-1.5" /> Đang lưu...
              </>
            ) : (
              <>
                <Save className="size-4 mr-1.5" /> Lưu danh sách biến thể
              </>
            )}
          </Button>
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-[#EAE6DF] shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-neutral-50 border-b border-[#EAE6DF] text-[11px] font-bold uppercase tracking-wider text-neutral-500">
              <tr>
                <th className="px-4 py-3.5">Mã SKU *</th>
                <th className="px-4 py-3.5">Nhóm thuộc tính</th>
                <th className="px-4 py-3.5">Giá trị phân loại *</th>
                <th className="px-4 py-3.5">Giá gốc (₫)</th>
                <th className="px-4 py-3.5">Giá bán (₫) *</th>
                <th className="px-4 py-3.5">Tồn kho *</th>
                <th className="px-4 py-3.5 text-right">Xóa</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#EAE6DF]">
              {variants.map((row, idx) => (
                <tr key={idx} className="hover:bg-neutral-50/50">
                  <td className="px-4 py-3">
                    <input
                      type="text"
                      value={row.sku}
                      onChange={(e) => handleFieldChange(idx, 'sku', e.target.value)}
                      className="w-32 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 font-mono text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="text"
                      value={row.variant}
                      placeholder="VD: Màu sắc"
                      onChange={(e) => handleFieldChange(idx, 'variant', e.target.value)}
                      className="w-32 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="text"
                      value={row.variantValue}
                      placeholder="VD: Trắng - M"
                      onChange={(e) => handleFieldChange(idx, 'variantValue', e.target.value)}
                      className="w-32 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 font-semibold text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="number"
                      value={row.originalPrice}
                      onChange={(e) => handleFieldChange(idx, 'originalPrice', Number(e.target.value))}
                      className="w-24 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="number"
                      value={row.sellingPrice}
                      onChange={(e) => handleFieldChange(idx, 'sellingPrice', Number(e.target.value))}
                      className="w-24 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 font-bold text-xs focus:border-neutral-900 outline-none"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="number"
                      value={row.stockQuantity}
                      onChange={(e) => handleFieldChange(idx, 'stockQuantity', Number(e.target.value))}
                      className="w-20 rounded-lg border border-[#EAE6DF] px-2.5 py-1.5 text-xs focus:border-neutral-900 outline-none font-mono"
                    />
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      onClick={() => handleRemoveRow(idx)}
                      className="p-1.5 text-neutral-400 hover:text-rose-600 rounded-lg hover:bg-rose-50 transition"
                    >
                      <Trash2 className="size-4" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}