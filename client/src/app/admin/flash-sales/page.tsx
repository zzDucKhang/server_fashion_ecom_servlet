'use client';

import * as React from 'react';
import { useEffect, useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Flame,
  Plus,
  Pencil,
  Trash2,
  Calendar,
  Layers,
  CheckCircle2,
  Clock,
  Search,
  RefreshCcw,
  AlertCircle,
  Eye,
  X,
  Sparkles,
  Loader2,
  Package,
} from 'lucide-react';
import {
  FlashSaleCampaign,
  FlashSaleItemPayload,
  CreateFlashSaleInput,
  UpdateFlashSaleInput,
} from '@/types/inventory-sale';
import { flashSaleApi } from '@/api/flash-sale.api';
import { catalogApi } from '@/api/catalog.api';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { formatCurrency } from '@/lib/utils/format-currency';

export default function AdminFlashSalesPage() {
  const [campaigns, setCampaigns] = useState<FlashSaleCampaign[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalItems, setTotalItems] = useState(0);
  const [page, setPage] = useState(1);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeFilter, setActiveFilter] = useState<'all' | 'active' | 'inactive'>('all');

  // Modal states
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingCampaign, setEditingCampaign] = useState<FlashSaleCampaign | null>(null);
  const [detailCampaign, setDetailCampaign] = useState<FlashSaleCampaign | null>(null);
  const [deleteConfirmId, setDeleteConfirmId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Form states
  const [formData, setFormData] = useState<{
    name: string;
    bannerUrl: string;
    startTime: string;
    endTime: string;
    isActive: boolean;
    items: FlashSaleItemPayload[];
  }>({
    name: '',
    bannerUrl: '',
    startTime: '',
    endTime: '',
    isActive: true,
    items: [{ variantId: 0, flashSalePrice: 100000, promoQuantity: 10 }],
  });

  // 1. Tải danh sách biến thể sản phẩm từ Catalog để đưa vào dropdown lựa chọn
  const { data: prodRes, isLoading: isLoadingProds } = useQuery({
    queryKey: ['admin-products-variants-select'],
    queryFn: () => catalogApi.getProducts({ limit: 100 }),
  });

  const products = (prodRes?.data as any) || [];

  // Tạo danh sách phẳng biến thể với đầy đủ thông tin: Tên, Phân loại, SKU, Giá, Tồn kho
  const availableVariants = useMemo(() => {
    const list: Array<{
      id: number;
      productName: string;
      variantValue: string;
      sku: string;
      stockQuantity: number;
      sellingPrice: number;
      originalPrice: number;
      label: string;
    }> = [];

    products.forEach((p: any) => {
      p.variants?.forEach((v: any) => {
        const sellPrice = Number(v.sellingPrice || v.originalPrice || 0);
        list.push({
          id: v.id,
          productName: p.defaultName,
          variantValue: v.variantValue || 'Mặc định',
          sku: v.sku,
          stockQuantity: v.stockQuantity,
          sellingPrice: sellPrice,
          originalPrice: Number(v.originalPrice || 0),
          label: `${p.defaultName} — [${v.variantValue || v.sku}] (Tồn: ${v.stockQuantity} | Giá gốc: ${formatCurrency(sellPrice)})`,
        });
      });
    });
    return list;
  }, [products]);

  // Map nhanh ID biến thể sang Object để tiện hiển thị
  const variantMap = useMemo(() => {
    const map = new Map<number, typeof availableVariants[0]>();
    availableVariants.forEach((v) => map.set(v.id, v));
    return map;
  }, [availableVariants]);

  // 2. Tải danh sách chiến dịch Flash Sale
  const fetchCampaigns = async () => {
    setLoading(true);
    try {
      const res = await flashSaleApi.getCampaigns({
        page,
        limit: 10,
        isActive: activeFilter === 'all' ? undefined : activeFilter === 'active',
      });
      setCampaigns(res.data || []);
      setTotalItems(res.totalItems || 0);
    } catch (err: any) {
      console.error('Lỗi khi tải danh sách Flash Sale:', err);
      setErrorMessage(err?.response?.data?.message || 'Không thể kết nối đến máy chủ');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCampaigns();
  }, [page, activeFilter]);

  // Chuyển đổi định dạng ngày giờ sang YYYY-MM-DDTHH:mm không bị lệch múi giờ
  const toLocalInputString = (d: Date | string) => {
    try {
      const date = typeof d === 'string' ? new Date(d) : d;
      if (isNaN(date.getTime())) return '';
      const pad = (n: number) => n.toString().padStart(2, '0');
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    } catch {
      return '';
    }
  };

  // Mở modal tạo mới
  const handleOpenCreate = () => {
    setEditingCampaign(null);
    const now = new Date();
    const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);

    const firstVariant = availableVariants[0];
    const initialPrice = firstVariant ? Math.round(firstVariant.sellingPrice * 0.8) : 100000;
    const initialQty = firstVariant ? Math.min(10, firstVariant.stockQuantity || 10) : 10;

    setFormData({
      name: `Chiến Dịch Flash Sale Hot ${campaigns.length + 1}`,
      bannerUrl: '',
      startTime: toLocalInputString(now),
      endTime: toLocalInputString(tomorrow),
      isActive: true,
      items: [{
        variantId: firstVariant ? firstVariant.id : 0,
        flashSalePrice: initialPrice,
        promoQuantity: initialQty,
      }],
    });
    setErrorMessage(null);
    setIsFormOpen(true);
  };

  // Mở modal chỉnh sửa
  const handleOpenEdit = (campaign: FlashSaleCampaign) => {
    setEditingCampaign(campaign);

    setFormData({
      name: campaign.name,
      bannerUrl: campaign.bannerUrl || '',
      startTime: toLocalInputString(campaign.startTime),
      endTime: toLocalInputString(campaign.endTime),
      isActive: campaign.isActive,
      items:
        campaign.items && campaign.items.length > 0
          ? campaign.items.map((i) => ({
              variantId: i.variantId,
              flashSalePrice: Number(i.flashSalePrice),
              promoQuantity: i.promoQuantity,
            }))
          : [{ variantId: availableVariants[0]?.id || 0, flashSalePrice: 100000, promoQuantity: 10 }],
    });
    setErrorMessage(null);
    setIsFormOpen(true);
  };

  // Thêm dòng sản phẩm mới vào chiến dịch
  const handleAddItemRow = () => {
    const selectedIds = new Set(formData.items.map((i) => i.variantId));
    const nextVariant = availableVariants.find((v) => !selectedIds.has(v.id)) || availableVariants[0];

    setFormData((prev) => ({
      ...prev,
      items: [
        ...prev.items,
        {
          variantId: nextVariant ? nextVariant.id : 0,
          flashSalePrice: nextVariant ? Math.round(nextVariant.sellingPrice * 0.8) : 100000,
          promoQuantity: nextVariant ? Math.min(10, nextVariant.stockQuantity || 10) : 10,
        },
      ],
    }));
  };

  // Xóa dòng sản phẩm
  const handleRemoveItemRow = (index: number) => {
    if (formData.items.length <= 1) {
      setErrorMessage('Chiến dịch Flash Sale phải có ít nhất 1 sản phẩm tham gia');
      return;
    }
    setFormData((prev) => ({
      ...prev,
      items: prev.items.filter((_, i) => i !== index),
    }));
  };

  // Cập nhật khi chọn biến thể từ dropdown
  const handleVariantChange = (index: number, newVariantId: number) => {
    const variant = variantMap.get(newVariantId);
    setFormData((prev) => {
      const updated = [...prev.items];
      const basePrice = variant ? variant.sellingPrice : 100000;
      updated[index] = {
        ...updated[index],
        variantId: newVariantId,
        flashSalePrice: Math.round(basePrice * 0.8), // Gợi ý giảm 20%
        promoQuantity: variant ? Math.min(10, variant.stockQuantity || 10) : 10,
      };
      return { ...prev, items: updated };
    });
  };

  // Cập nhật giá bán hoặc số lượng
  const handleItemFieldChange = (index: number, field: 'flashSalePrice' | 'promoQuantity', value: number) => {
    setFormData((prev) => {
      const updated = [...prev.items];
      updated[index] = { ...updated[index], [field]: value };
      return { ...prev, items: updated };
    });
  };

  // Submit Form Tạo / Sửa
  const handleSubmitForm = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!formData.name.trim()) {
      setErrorMessage('Tên chiến dịch không được để trống');
      return;
    }
    if (!formData.startTime || !formData.endTime) {
      setErrorMessage('Vui lòng chọn thời gian bắt đầu và kết thúc');
      return;
    }
    if (new Date(formData.startTime) >= new Date(formData.endTime)) {
      setErrorMessage('Thời gian kết thúc phải sau thời gian bắt đầu');
      return;
    }

    // Kiểm tra chưa chọn biến thể
    const hasUnselected = formData.items.some((i) => !i.variantId || i.variantId === 0);
    if (hasUnselected) {
      setErrorMessage('Vui lòng chọn sản phẩm cụ thể cho tất cả các dòng');
      return;
    }

    // Kiểm tra trùng lặp sản phẩm
    const variantIds = formData.items.map((i) => i.variantId);
    if (new Set(variantIds).size !== variantIds.length) {
      setErrorMessage('Không được có sản phẩm trùng lặp trong cùng một chiến dịch');
      return;
    }

    // Kiểm tra logic giá và tồn kho
    for (const item of formData.items) {
      const v = variantMap.get(item.variantId);
      if (v) {
        if (item.flashSalePrice >= v.sellingPrice) {
          setErrorMessage(`Giá Flash Sale của "${v.productName} (${v.sku})" phải thấp hơn giá bán hiện tại (${formatCurrency(v.sellingPrice)})`);
          return;
        }
        if (item.promoQuantity > v.stockQuantity) {
          setErrorMessage(`Số lượng khuyến mãi của "${v.sku}" vượt quá tồn kho thực tế (${v.stockQuantity})`);
          return;
        }
      }
    }

    setSubmitting(true);

    try {
      const payload: CreateFlashSaleInput | UpdateFlashSaleInput = {
        name: formData.name.trim(),
        bannerUrl: formData.bannerUrl || undefined,
        startTime: new Date(formData.startTime).toISOString(),
        endTime: new Date(formData.endTime).toISOString(),
        isActive: formData.isActive,
        items: formData.items,
      };

      if (editingCampaign) {
        await flashSaleApi.updateCampaign(editingCampaign.id, payload);
        setSuccessMessage('Cập nhật chiến dịch Flash Sale thành công!');
      } else {
        await flashSaleApi.createCampaign(payload as CreateFlashSaleInput);
        setSuccessMessage('Tạo chiến dịch Flash Sale thành công!');
      }

      setIsFormOpen(false);
      fetchCampaigns();
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err: any) {
      console.error('Submit error:', err);
      setErrorMessage(err?.response?.data?.message || err?.message || 'Có lỗi xảy ra khi lưu chiến dịch');
    } finally {
      setSubmitting(false);
    }
  };

  // Xóa chiến dịch
  const handleDelete = async (id: number) => {
    setSubmitting(true);
    try {
      await flashSaleApi.deleteCampaign(id);
      setSuccessMessage('Xóa chiến dịch thành công!');
      setDeleteConfirmId(null);
      fetchCampaigns();
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err: any) {
      setErrorMessage(err?.response?.data?.message || 'Xóa chiến dịch thất bại');
    } finally {
      setSubmitting(false);
    }
  };

  const getCampaignStatus = (c: FlashSaleCampaign) => {
    if (!c.isActive) {
      return { label: 'Đã tắt', variant: 'outline' as const };
    }
    const now = new Date().getTime();
    const start = new Date(c.startTime).getTime();
    const end = new Date(c.endTime).getTime();

    if (now < start) {
      return { label: 'Sắp diễn ra', variant: 'warning' as const };
    }
    if (now > end) {
      return { label: 'Đã kết thúc', variant: 'secondary' as const };
    }
    return { label: 'Đang diễn ra', variant: 'success' as const };
  };

  const filteredCampaigns = campaigns.filter((c) =>
    c.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const formatDateTime = (str: string) => {
    try {
      return new Date(str).toLocaleString('vi-VN', {
        hour: '2-digit',
        minute: '2-digit',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
      });
    } catch {
      return str;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between border-b border-[#EAE6DF] pb-5">
        <div>
          <span className="eyebrow block mb-1">MODULE INVENTORY SALE</span>
          <h1 className="font-display text-3xl sm:text-4xl text-[#1D1F20] tracking-tight">
            Quản Lý Flash Sale
          </h1>
          <p className="text-xs text-neutral-500 mt-1">
            Tạo và cấu hình các chiến dịch giảm giá theo khung giờ chớp nhoáng.
          </p>
        </div>

        <Button
          onClick={handleOpenCreate}
          className="h-10 px-4 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-medium flex items-center gap-2 shrink-0 shadow-xs"
        >
          <Plus className="size-4" />
          <span>Tạo chiến dịch mới</span>
        </Button>
      </div>

      {/* Notifications */}
      {successMessage && (
        <div className="flex items-center gap-2 rounded-xl bg-emerald-50 border border-emerald-200 p-3.5 text-xs font-semibold text-emerald-800">
          <CheckCircle2 className="size-4 text-emerald-600" />
          <span>{successMessage}</span>
        </div>
      )}

      {errorMessage && (
        <div className="flex items-center gap-2 rounded-xl bg-rose-50 border border-rose-200 p-3.5 text-xs font-semibold text-rose-800">
          <AlertCircle className="size-4 text-rose-600" />
          <span>{errorMessage}</span>
        </div>
      )}

      {/* Quick Stats Grid */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 shadow-xs">
          <div className="flex items-center justify-between text-neutral-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Tổng chiến dịch</span>
            <Layers className="size-4 text-neutral-700" />
          </div>
          <div className="mt-2 text-2xl font-bold text-neutral-900 font-display">
            {totalItems}
          </div>
        </div>

        <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 shadow-xs">
          <div className="flex items-center justify-between text-neutral-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Đang hoạt động</span>
            <Sparkles className="size-4 text-emerald-600" />
          </div>
          <div className="mt-2 text-2xl font-bold text-emerald-600 font-display">
            {campaigns.filter((c) => c.isActive).length}
          </div>
        </div>

        <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 shadow-xs">
          <div className="flex items-center justify-between text-neutral-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Tổng SP Sale</span>
            <Flame className="size-4 text-red-500" />
          </div>
          <div className="mt-2 text-2xl font-bold text-neutral-900 font-display">
            {campaigns.reduce((acc, c) => acc + (c.items?.length || 0), 0)}
          </div>
        </div>

        <div className="rounded-2xl border border-[#EAE6DF] bg-white p-5 shadow-xs">
          <div className="flex items-center justify-between text-neutral-500">
            <span className="text-xs font-semibold uppercase tracking-wider">Đã bán</span>
            <CheckCircle2 className="size-4 text-neutral-700" />
          </div>
          <div className="mt-2 text-2xl font-bold text-neutral-900 font-display">
            {campaigns.reduce(
              (acc, c) => acc + (c.items?.reduce((iAcc, item) => iAcc + (item.soldQuantity || 0), 0) || 0),
              0
            )}
          </div>
        </div>
      </div>

      {/* Toolbar */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between rounded-2xl bg-white p-4 shadow-xs border border-[#EAE6DF]">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 size-4 text-neutral-400" />
          <input
            type="text"
            placeholder="Tìm theo tên chiến dịch..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-sm rounded-xl border border-[#EAE6DF] bg-white outline-none focus:border-neutral-900 text-neutral-900"
          />
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1 rounded-xl bg-neutral-100 p-1">
            <button
              onClick={() => setActiveFilter('all')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
                activeFilter === 'all' ? 'bg-white text-neutral-900 shadow-xs' : 'text-neutral-500'
              }`}
            >
              Tất cả
            </button>
            <button
              onClick={() => setActiveFilter('active')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
                activeFilter === 'active' ? 'bg-white text-neutral-900 shadow-xs' : 'text-neutral-500'
              }`}
            >
              Hoạt động
            </button>
            <button
              onClick={() => setActiveFilter('inactive')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
                activeFilter === 'inactive' ? 'bg-white text-neutral-900 shadow-xs' : 'text-neutral-500'
              }`}
            >
              Đã tắt
            </button>
          </div>

          <Button onClick={fetchCampaigns} variant="outline" size="sm" className="gap-1.5 rounded-xl border-[#EAE6DF]">
            <RefreshCcw className="size-3.5" />
          </Button>
        </div>
      </div>

      {/* Campaign Table */}
      <div className="overflow-hidden rounded-2xl border border-[#EAE6DF] bg-white shadow-xs">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-neutral-50 border-b border-[#EAE6DF] text-[11px] font-bold uppercase tracking-wider text-neutral-500">
              <tr>
                <th className="px-6 py-4">Mã</th>
                <th className="px-6 py-4">Tên chiến dịch</th>
                <th className="px-6 py-4">Thời gian</th>
                <th className="px-6 py-4">Sản phẩm</th>
                <th className="px-6 py-4">Trạng thái</th>
                <th className="px-6 py-4 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#EAE6DF]">
              {loading ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-neutral-500">
                    <Loader2 className="mx-auto size-5 animate-spin mb-1 text-neutral-400" />
                    Đang tải dữ liệu chiến dịch Flash Sale...
                  </td>
                </tr>
              ) : filteredCampaigns.length === 0 ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-neutral-500">
                    Chưa có chiến dịch Flash Sale nào phù hợp.
                  </td>
                </tr>
              ) : (
                filteredCampaigns.map((c) => {
                  const status = getCampaignStatus(c);
                  return (
                    <tr key={c.id} className="hover:bg-neutral-50/70 transition-colors">
                      <td className="px-6 py-4 font-mono font-bold text-neutral-500">#{c.id}</td>
                      <td className="px-6 py-4 font-semibold text-neutral-900 text-sm">
                        {c.name}
                      </td>
                      <td className="px-6 py-4 text-xs text-neutral-500">
                        <div className="flex items-center gap-1.5 font-medium text-neutral-700">
                          <Calendar className="size-3.5 text-neutral-400" />
                          <span>{formatDateTime(c.startTime)}</span>
                        </div>
                        <div className="flex items-center gap-1.5 mt-1 text-neutral-400">
                          <Clock className="size-3.5" />
                          <span>đến {formatDateTime(c.endTime)}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <span className="inline-flex items-center gap-1 rounded-full bg-neutral-100 px-2.5 py-1 text-xs font-semibold text-neutral-800">
                          <Flame className="size-3 text-red-500" />
                          {c.items?.length || 0} biến thể
                        </span>
                      </td>
                      <td className="px-6 py-4">
                        <Badge variant={status.variant} className="text-[10px]">
                          {status.label}
                        </Badge>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            onClick={() => setDetailCampaign(c)}
                            className="p-1.5 text-neutral-400 hover:text-neutral-900 hover:bg-neutral-100 rounded-lg transition"
                            title="Xem chi tiết"
                          >
                            <Eye className="size-4" />
                          </button>
                          <button
                            onClick={() => handleOpenEdit(c)}
                            className="p-1.5 text-neutral-400 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition"
                            title="Chỉnh sửa chiến dịch"
                          >
                            <Pencil className="size-4" />
                          </button>
                          <button
                            onClick={() => setDeleteConfirmId(c.id)}
                            className="p-1.5 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                            title="Xóa chiến dịch"
                          >
                            <Trash2 className="size-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* ============================================================== */}
      {/* MODAL TẠO & CHỈNH SỬA CHIẾN DỊCH (GIAO DIỆN SÁNG, RÕ NÉT, DỄ DÙNG) */}
      {/* ============================================================== */}
      {isFormOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs animate-in fade-in duration-200">
          <div className="relative w-full max-w-4xl max-h-[90vh] overflow-y-auto rounded-3xl bg-white p-6 md:p-8 shadow-2xl border border-[#EAE6DF] text-neutral-900 animate-in zoom-in-95 duration-200">
            {/* Header Modal */}
            <div className="flex items-center justify-between border-b border-[#EAE6DF] pb-4">
              <h2 className="text-xl font-bold text-neutral-900 flex items-center gap-2">
                <Flame className="size-5 text-red-600" />
                <span>{editingCampaign ? 'Chỉnh Sửa Flash Sale' : 'Tạo Chiến Dịch Flash Sale'}</span>
              </h2>
              <button
                type="button"
                onClick={() => setIsFormOpen(false)}
                className="rounded-full p-1.5 text-neutral-400 hover:bg-neutral-100 hover:text-neutral-700 transition cursor-pointer"
              >
                <X className="size-5" />
              </button>
            </div>

            <form onSubmit={handleSubmitForm} className="mt-6 space-y-6">
              {errorMessage && (
                <div className="rounded-xl bg-rose-50 p-3.5 text-xs font-semibold text-rose-700 border border-rose-200 flex items-center gap-2">
                  <AlertCircle className="size-4 shrink-0 text-rose-600" />
                  <span>{errorMessage}</span>
                </div>
              )}

              {/* Thông tin chung */}
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div className="sm:col-span-2 space-y-1.5">
                  <label className="text-xs font-semibold uppercase tracking-wider text-neutral-700">
                    Tên chiến dịch *
                  </label>
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder="VD: Flash Sale Giờ Vàng 12h-14h"
                    required
                    className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm text-neutral-900 font-medium outline-none focus:border-neutral-900 shadow-xs"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold uppercase tracking-wider text-neutral-700">
                    Thời gian bắt đầu *
                  </label>
                  <input
                    type="datetime-local"
                    value={formData.startTime}
                    onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                    required
                    className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold uppercase tracking-wider text-neutral-700">
                    Thời gian kết thúc *
                  </label>
                  <input
                    type="datetime-local"
                    value={formData.endTime}
                    onChange={(e) => setFormData({ ...formData, endTime: e.target.value })}
                    required
                    className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                  />
                </div>

                <div className="sm:col-span-2 space-y-1.5">
                  <label className="text-xs font-semibold uppercase tracking-wider text-neutral-700">
                    Banner URL (Tùy chọn)
                  </label>
                  <input
                    type="text"
                    value={formData.bannerUrl}
                    onChange={(e) => setFormData({ ...formData, bannerUrl: e.target.value })}
                    placeholder="https://images.unsplash.com/... hoặc đường dẫn ảnh banner"
                    className="w-full rounded-xl border border-[#EAE6DF] bg-white px-4 py-2.5 text-sm text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                  />
                </div>

                <div className="sm:col-span-2 flex items-center gap-2.5 pt-1">
                  <input
                    type="checkbox"
                    id="isActive"
                    checked={formData.isActive}
                    onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
                    className="size-4 rounded border-[#EAE6DF] accent-[#1D1F20] cursor-pointer"
                  />
                  <label htmlFor="isActive" className="text-xs font-semibold text-neutral-800 cursor-pointer">
                    Kích hoạt chiến dịch ngay lập tức
                  </label>
                </div>
              </div>

              {/* Danh sách biến thể tham gia Sale */}
              <div className="border-t border-[#EAE6DF] pt-6 space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-sm font-bold uppercase tracking-wider text-neutral-900 flex items-center gap-1.5">
                      <Package className="size-4 text-neutral-700" />
                      Danh sách sản phẩm tham gia sale ({formData.items.length})
                    </h3>
                    <p className="text-xs text-neutral-500 mt-0.5">
                      Chọn sản phẩm và cấu hình giá ưu đãi cùng số lượng xuất kho cho sự kiện.
                    </p>
                  </div>

                  <Button
                    type="button"
                    onClick={handleAddItemRow}
                    className="h-9 px-3.5 rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white text-xs font-semibold flex items-center gap-1.5 shadow-xs"
                  >
                    <Plus className="size-3.5" />
                    <span>Thêm sản phẩm</span>
                  </Button>
                </div>

                {isLoadingProds ? (
                  <div className="py-8 text-center text-xs text-neutral-400">
                    <Loader2 className="mx-auto size-4 animate-spin mb-1" />
                    Đang tải danh sách sản phẩm từ kho...
                  </div>
                ) : (
                  <div className="space-y-3">
                    {formData.items.map((item, idx) => {
                      const currentVariant = variantMap.get(item.variantId);
                      const basePrice = currentVariant ? currentVariant.sellingPrice : 0;
                      const discountPercent = basePrice > item.flashSalePrice
                        ? Math.round(((basePrice - item.flashSalePrice) / basePrice) * 100)
                        : 0;

                      return (
                        <div
                          key={idx}
                          className="rounded-2xl border border-[#EAE6DF] bg-neutral-50/50 p-4 space-y-3 transition hover:border-neutral-300"
                        >
                          <div className="flex items-start justify-between gap-3">
                            {/* Chọn biến thể từ Dropdown */}
                            <div className="flex-1 space-y-1">
                              <label className="text-[11px] font-bold uppercase tracking-wider text-neutral-600">
                                Sản phẩm & Phân loại SKU *
                              </label>
                              <select
                                value={item.variantId}
                                onChange={(e) => handleVariantChange(idx, Number(e.target.value))}
                                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3.5 py-2.5 text-xs font-medium text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                                required
                              >
                                <option value={0}>-- Chọn sản phẩm tham gia --</option>
                                {availableVariants.map((v) => (
                                  <option key={v.id} value={v.id}>
                                    {v.label}
                                  </option>
                                ))}
                              </select>
                            </div>

                            {/* Nút xóa dòng */}
                            <button
                              type="button"
                              onClick={() => handleRemoveItemRow(idx)}
                              className="p-2 text-neutral-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition mt-5 shrink-0"
                              title="Xóa sản phẩm này khỏi Flash Sale"
                            >
                              <Trash2 className="size-4" />
                            </button>
                          </div>

                          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
                            {/* Giá Flash Sale */}
                            <div className="space-y-1">
                              <div className="flex items-center justify-between">
                                <label className="text-[11px] font-bold uppercase tracking-wider text-neutral-600">
                                  Giá Flash Sale (VNĐ) *
                                </label>
                                {discountPercent > 0 && (
                                  <span className="text-[10px] font-bold text-red-600 bg-red-50 px-1.5 py-0.5 rounded border border-red-200">
                                    Giảm {discountPercent}%
                                  </span>
                                )}
                              </div>
                              <input
                                type="number"
                                min={1000}
                                step={1000}
                                value={item.flashSalePrice}
                                onChange={(e) => handleItemFieldChange(idx, 'flashSalePrice', Number(e.target.value))}
                                required
                                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3.5 py-2 text-xs font-bold text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                              />
                              {basePrice > 0 && (
                                <p className="text-[10px] text-neutral-500">
                                  Giá bán gốc hiện tại: <strong className="text-neutral-700">{formatCurrency(basePrice)}</strong>
                                </p>
                              )}
                            </div>

                            {/* Số lượng KM */}
                            <div className="space-y-1">
                              <label className="text-[11px] font-bold uppercase tracking-wider text-neutral-600">
                                Số lượng khuyến mãi *
                              </label>
                              <input
                                type="number"
                                min={1}
                                max={currentVariant?.stockQuantity || 9999}
                                value={item.promoQuantity}
                                onChange={(e) => handleItemFieldChange(idx, 'promoQuantity', Number(e.target.value))}
                                required
                                className="w-full rounded-xl border border-[#EAE6DF] bg-white px-3.5 py-2 text-xs font-mono font-semibold text-neutral-900 outline-none focus:border-neutral-900 shadow-xs"
                              />
                              {currentVariant && (
                                <p className="text-[10px] text-neutral-500">
                                  Tồn kho sẵn có: <strong className="text-neutral-700">{currentVariant.stockQuantity} sản phẩm</strong>
                                </p>
                              )}
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>

              {/* Action Buttons */}
              <div className="flex items-center justify-end gap-3 border-t border-[#EAE6DF] pt-5">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setIsFormOpen(false)}
                  className="rounded-xl border-[#EAE6DF] text-xs h-10 px-5"
                >
                  Hủy bỏ
                </Button>
                <Button
                  type="submit"
                  disabled={submitting}
                  className="rounded-xl bg-[#1D1F20] hover:bg-neutral-800 !text-white [&_*]:!text-white font-semibold text-xs h-10 px-6 shadow-xs"
                >
                  {submitting ? (
                    <>
                      <Loader2 className="size-4 animate-spin mr-1.5" />
                      Đang lưu...
                    </>
                  ) : editingCampaign ? (
                    'Lưu thay đổi chiến dịch'
                  ) : (
                    'Khởi tạo chiến dịch'
                  )}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Detail Modal */}
      {detailCampaign && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs animate-in fade-in duration-200">
          <div className="relative w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-3xl bg-white p-6 md:p-8 shadow-2xl border border-[#EAE6DF] text-neutral-900">
            <div className="flex items-center justify-between border-b border-[#EAE6DF] pb-4">
              <h2 className="text-lg font-bold text-neutral-900 flex items-center gap-2">
                <Flame className="size-5 text-red-600" />
                <span>Chi tiết chiến dịch: {detailCampaign.name}</span>
              </h2>
              <button
                onClick={() => setDetailCampaign(null)}
                className="rounded-full p-1.5 text-neutral-400 hover:bg-neutral-100 transition cursor-pointer"
              >
                <X className="size-5" />
              </button>
            </div>

            <div className="mt-6 space-y-4 text-xs">
              <div className="grid grid-cols-2 gap-4 rounded-xl bg-neutral-50 p-4 border border-[#EAE6DF]">
                <div>
                  <span className="text-neutral-500 block mb-1">Thời gian bắt đầu:</span>
                  <span className="font-semibold text-neutral-900">
                    {formatDateTime(detailCampaign.startTime)}
                  </span>
                </div>
                <div>
                  <span className="text-neutral-500 block mb-1">Thời gian kết thúc:</span>
                  <span className="font-semibold text-neutral-900">
                    {formatDateTime(detailCampaign.endTime)}
                  </span>
                </div>
              </div>

              <h4 className="font-bold uppercase tracking-wider text-neutral-700 pt-2">
                Danh sách sản phẩm trong sale ({detailCampaign.items?.length || 0})
              </h4>

              <div className="space-y-2.5 max-h-64 overflow-y-auto pr-1">
                {detailCampaign.items?.map((item) => (
                  <div
                    key={item.id || item.variantId}
                    className="flex items-center justify-between rounded-xl border border-[#EAE6DF] p-3.5 bg-white"
                  >
                    <div>
                      <p className="font-semibold text-neutral-900">
                        {item.variant?.product?.defaultName || `Biến thể #${item.variantId}`}
                      </p>
                      <p className="text-[11px] text-neutral-400 font-mono mt-0.5">
                        SKU: {item.variant?.sku || item.variantId}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="font-bold text-red-600 text-sm">
                        {formatCurrency(Number(item.flashSalePrice))}
                      </p>
                      <p className="text-[11px] text-neutral-500 mt-0.5">
                        Đã bán: <strong className="text-neutral-800">{item.soldQuantity}</strong> / {item.promoQuantity}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="mt-6 flex justify-end border-t border-[#EAE6DF] pt-4">
              <Button onClick={() => setDetailCampaign(null)} variant="outline" className="rounded-xl border-[#EAE6DF] text-xs">
                Đóng
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirm Modal */}
      {deleteConfirmId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4 backdrop-blur-xs animate-in fade-in duration-200">
          <div className="w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl border border-[#EAE6DF] text-neutral-900">
            <h3 className="text-base font-bold text-neutral-900">Xác nhận xóa chiến dịch</h3>
            <p className="mt-2 text-xs text-neutral-500 leading-relaxed">
              Bạn có chắc chắn muốn xóa chiến dịch Flash Sale này không? Thao tác này sẽ ngừng áp dụng toàn bộ giá ưu đãi cho các sản phẩm liên quan.
            </p>
            <div className="mt-6 flex justify-end gap-2.5">
              <Button variant="outline" size="sm" onClick={() => setDeleteConfirmId(null)} className="rounded-xl border-[#EAE6DF] text-xs">
                Hủy
              </Button>
              <Button
                size="sm"
                onClick={() => handleDelete(deleteConfirmId)}
                disabled={submitting}
                className="rounded-xl bg-rose-600 hover:bg-rose-700 !text-white [&_*]:!text-white font-semibold text-xs"
              >
                {submitting ? 'Đang xóa...' : 'Xóa vĩnh viễn'}
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}