"use client";

import React, { useState, useEffect } from "react";
import {
  Tag,
  CheckCircle2,
  AlertCircle,
  Loader2,
  X,
  Ticket,
  ChevronRight,
  Clock,
  Sparkles,
  Coins,
} from "lucide-react";
import {
  validateVoucher,
  getAvailableVouchers,
  type VoucherValidationData,
  type VoucherEntity,
} from "../api";
import { formatCurrency } from "@/lib/utils/format-currency";

// =========================================================================
// COMPONENT 1: VOUCHER INPUT (GIAO DIỆN CHỌN VOUCHER KIỂU SHOPEE)
// =========================================================================

export interface VoucherInputProps {
  orderTotal: number;
  onValidate: (result: VoucherValidationData) => void;
  onRemove?: () => void;
}

export function VoucherInput({
  orderTotal,
  onValidate,
  onRemove,
}: VoucherInputProps) {
  const [code, setCode] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [appliedVoucher, setAppliedVoucher] = useState<VoucherValidationData | null>(null);

  // State Modal danh sách Voucher
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [voucherList, setVoucherList] = useState<VoucherEntity[]>([]);
  const [isLoadingList, setIsLoadingList] = useState<boolean>(false);

  // Tự động hủy nếu giỏ hàng thay đổi không còn đủ điều kiện minOrderValue
  useEffect(() => {
    if (appliedVoucher) {
      const minRequired = Number(appliedVoucher.voucher.minOrderValue);
      if (orderTotal < minRequired) {
        const timer = setTimeout(() => {
          setAppliedVoucher(null);
          setCode("");
          setErrorMessage(
            `Đơn hàng không còn đủ giá trị tối thiểu (${formatCurrency(minRequired)}) để dùng mã này.`
          );
          onRemove?.();
        }, 0);
        return () => clearTimeout(timer);
      }
    }
  }, [orderTotal, appliedVoucher, onRemove]);

  // Tải danh sách voucher khi mở modal
  const handleOpenModal = async () => {
    setIsModalOpen(true);
    setErrorMessage(null);
    setIsLoadingList(true);
    try {
      const res = await getAvailableVouchers();
      const list = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
      setVoucherList(list);
    } catch (err) {
      console.error("Không thể tải danh sách voucher:", err);
    } finally {
      setIsLoadingList(false);
    }
  };

  // Áp dụng voucher
  const handleApplyCode = async (targetCode: string) => {
    const cleanCode = targetCode.trim().toUpperCase();
    if (!cleanCode) {
      setErrorMessage("Vui lòng nhập mã voucher");
      return;
    }

    setIsLoading(true);
    setErrorMessage(null);

    try {
      const response = await validateVoucher(cleanCode, orderTotal);
      if (response?.data) {
        setAppliedVoucher(response.data);
        setCode(response.data.code);
        setErrorMessage(null);
        onValidate(response.data);
        setIsModalOpen(false);
      }
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Mã voucher không hợp lệ hoặc đã hết hạn";
      setErrorMessage(msg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRemoveVoucher = () => {
    setAppliedVoucher(null);
    setCode("");
    setErrorMessage(null);
    onRemove?.();
  };

  return (
    <div className="w-full bg-[#FFFFFF] border border-[#EAE6DF] rounded-2xl p-5 sm:p-6 space-y-4 shadow-xs">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Tag size={18} className="text-[#1D1F20]" />
          <h4 className="text-base font-bold text-[#1D1F20]">Fashion Voucher</h4>
        </div>

        <button
          type="button"
          onClick={handleOpenModal}
          className="text-xs font-semibold text-emerald-700 hover:text-emerald-800 flex items-center gap-1 cursor-pointer transition"
        >
          <span>Xem tất cả mã</span>
          <ChevronRight size={14} />
        </button>
      </div>

      {appliedVoucher ? (
        <div className="flex items-center justify-between p-4 rounded-xl bg-[#84EA53]/15 border border-[#84EA53]/40 transition">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <CheckCircle2 size={16} className="text-[#16a34a] shrink-0" />
              <span className="font-bold text-sm text-[#1D1F20] tracking-wide font-mono">
                {appliedVoucher.code}
              </span>
              <span className="text-[10px] font-bold bg-[#1D1F20] text-white px-2 py-0.5 rounded-full">
                Đã chọn
              </span>
            </div>
            <p className="text-xs text-[#16a34a] font-semibold pl-6">
              Giảm -{formatCurrency(appliedVoucher.discountAmount)} vào tiền hàng
            </p>
          </div>

          <button
            type="button"
            onClick={handleRemoveVoucher}
            className="p-1.5 rounded-lg text-[#74736e] hover:text-[#dc2626] hover:bg-white/80 transition cursor-pointer"
            aria-label="Xóa mã giảm giá"
          >
            <X size={18} />
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          <div className="flex items-center gap-2">
            <input
              type="text"
              value={code}
              disabled={isLoading}
              onChange={(e) => {
                setCode(e.target.value.toUpperCase());
                if (errorMessage) setErrorMessage(null);
              }}
              onKeyDown={(e) => e.key === "Enter" && handleApplyCode(code)}
              placeholder="Nhập mã voucher hoặc chọn bên dưới"
              className="flex-1 rounded-xl border border-[#EAE6DF] bg-[#FBF9F4] px-4 py-2.5 text-sm text-[#1D1F20] uppercase placeholder:normal-case placeholder-[#74736e]/60 focus:border-[#84EA53] outline-none transition disabled:opacity-50"
            />
            <button
              type="button"
              onClick={() => handleApplyCode(code)}
              disabled={isLoading || !code.trim()}
              className="inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl bg-[#1D1F20] !text-white font-semibold text-sm hover:bg-black active:scale-[0.98] disabled:opacity-40 disabled:cursor-not-allowed transition cursor-pointer"
            >
              {isLoading ? <Loader2 size={15} className="animate-spin" /> : "Áp dụng"}
            </button>
          </div>

          {errorMessage && (
            <div className="flex items-center gap-2 text-xs text-[#dc2626] font-medium pt-1">
              <AlertCircle size={14} className="shrink-0" />
              <span>{errorMessage}</span>
            </div>
          )}

          <div
            onClick={handleOpenModal}
            className="flex items-center justify-between p-3 rounded-xl border border-dashed border-[#EAE6DF] bg-neutral-50/60 hover:bg-neutral-50 hover:border-neutral-400 transition cursor-pointer"
          >
            <div className="flex items-center gap-2 text-xs text-neutral-700 font-medium">
              <Ticket size={16} className="text-emerald-600" />
              <span>Chọn Voucher có sẵn của cửa hàng</span>
            </div>
            <span className="text-[11px] font-bold text-neutral-900 bg-white border border-[#EAE6DF] px-2 py-0.5 rounded-md">
              Chọn ngay
            </span>
          </div>
        </div>
      )}

      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-xs p-4 animate-in fade-in duration-200">
          <div className="relative w-full max-w-lg max-h-[85vh] flex flex-col rounded-3xl bg-white shadow-2xl border border-[#EAE6DF] text-neutral-900 animate-in zoom-in-95 duration-200">
            <div className="flex items-center justify-between border-b border-[#EAE6DF] px-6 py-4">
              <div>
                <h3 className="text-base font-bold text-neutral-900 flex items-center gap-2">
                  <Ticket className="size-5 text-emerald-600" />
                  Chọn Fashion Voucher
                </h3>
                <p className="text-xs text-neutral-500 mt-0.5">
                  Áp dụng mã giảm giá trực tiếp vào tiền hàng
                </p>
              </div>
              <button
                type="button"
                onClick={() => setIsModalOpen(false)}
                className="rounded-full p-1.5 text-neutral-400 hover:bg-neutral-100 hover:text-neutral-700 transition cursor-pointer"
              >
                <X className="size-5" />
              </button>
            </div>

            <div className="p-4 border-b border-[#EAE6DF] bg-neutral-50/50">
              <div className="flex gap-2">
                <input
                  type="text"
                  placeholder="Nhập mã giảm giá..."
                  value={code}
                  onChange={(e) => setCode(e.target.value.toUpperCase())}
                  className="flex-1 rounded-xl border border-[#EAE6DF] bg-white px-3.5 py-2 text-xs uppercase font-mono outline-none focus:border-neutral-900"
                />
                <button
                  type="button"
                  onClick={() => handleApplyCode(code)}
                  disabled={isLoading || !code.trim()}
                  className="px-4 py-2 bg-[#1D1F20] text-white text-xs font-semibold rounded-xl hover:bg-black transition disabled:opacity-40"
                >
                  {isLoading ? <Loader2 size={13} className="animate-spin" /> : "Áp dụng"}
                </button>
              </div>
              {errorMessage && (
                <p className="text-[11px] text-red-600 font-medium mt-2 flex items-center gap-1">
                  <AlertCircle size={12} /> {errorMessage}
                </p>
              )}
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-3">
              <span className="text-[11px] font-bold uppercase tracking-wider text-neutral-400 block px-1">
                Mã giảm giá đơn hàng
              </span>

              {isLoadingList ? (
                <div className="py-12 text-center text-xs text-neutral-400">
                  <Loader2 className="size-5 animate-spin mx-auto mb-2" />
                  Đang tải danh sách voucher khả dụng...
                </div>
              ) : voucherList.length === 0 ? (
                <div className="py-12 text-center text-xs text-neutral-400">
                  Hiện chưa có mã giảm giá nào có sẵn.
                </div>
              ) : (
                voucherList.map((v) => {
                  const minOrder = Number(v.minOrderValue || 0);
                  const isEligible = orderTotal >= minOrder;
                  const discountDisplay =
                    v.discountType === "PERCENTAGE"
                      ? `Giảm ${v.discountValue}% ${v.maxDiscountAmt ? `(Tối đa ${formatCurrency(v.maxDiscountAmt)})` : ""}`
                      : `Giảm ${formatCurrency(v.discountValue)}`;

                  const isCurrentApplied = appliedVoucher?.code === v.code;

                  return (
                    <div
                      key={v.id}
                      className={`relative flex items-stretch rounded-2xl border transition-all overflow-hidden ${
                        isEligible
                          ? "bg-white border-[#EAE6DF] hover:border-emerald-500 shadow-xs"
                          : "bg-neutral-50 border-neutral-200 opacity-60"
                      }`}
                    >
                      <div className={`w-24 shrink-0 flex flex-col items-center justify-center p-3 text-center border-r border-dashed ${
                        isEligible ? "bg-emerald-50 text-emerald-800 border-emerald-200" : "bg-neutral-100 text-neutral-500 border-neutral-200"
                      }`}>
                        <Sparkles size={20} className={isEligible ? "text-emerald-600" : "text-neutral-400"} />
                        <span className="text-[10px] font-black uppercase tracking-wider mt-1">
                          VOUCHER
                        </span>
                      </div>

                      <div className="flex-1 p-3.5 space-y-1 min-w-0">
                        <div className="flex items-center gap-2">
                          <span className="font-mono font-bold text-xs text-neutral-900 bg-neutral-100 px-1.5 py-0.5 rounded">
                            {v.code}
                          </span>
                          <span className="text-xs font-extrabold text-emerald-700 truncate">
                            {discountDisplay}
                          </span>
                        </div>

                        <p className="text-[11px] text-neutral-600">
                          Đơn tối thiểu: <strong>{formatCurrency(minOrder)}</strong>
                        </p>

                        {!isEligible && (
                          <p className="text-[10px] text-red-600 font-medium">
                            Mua thêm {formatCurrency(minOrder - orderTotal)} để sử dụng
                          </p>
                        )}

                        <div className="flex items-center gap-1 text-[10px] text-neutral-400 pt-1">
                          <Clock size={11} />
                          <span>HSD: {new Date(v.endDate).toLocaleDateString("vi-VN")}</span>
                        </div>
                      </div>

                      <div className="flex items-center pr-3.5 pl-1">
                        <button
                          type="button"
                          disabled={!isEligible || isLoading || isCurrentApplied}
                          onClick={() => handleApplyCode(v.code)}
                          className={`px-3 py-1.5 rounded-xl text-xs font-bold transition cursor-pointer ${
                            isCurrentApplied
                              ? "bg-emerald-100 text-emerald-700 border border-emerald-200 cursor-default"
                              : isEligible
                              ? "bg-[#1D1F20] text-white hover:bg-black"
                              : "bg-neutral-200 text-neutral-400 cursor-not-allowed"
                          }`}
                        >
                          {isCurrentApplied ? "Đang chọn" : "Áp dụng"}
                        </button>
                      </div>
                    </div>
                  );
                })
              )}
            </div>

            <div className="p-4 border-t border-[#EAE6DF] flex justify-end">
              <button
                type="button"
                onClick={() => setIsModalOpen(false)}
                className="px-5 py-2 text-xs font-bold rounded-xl border border-[#EAE6DF] hover:bg-neutral-50 transition cursor-pointer"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}


// =========================================================================
// COMPONENT 2: POINT REDEEM SLIDER (SỬ DỤNG ĐIỂM THƯỞNG)
// =========================================================================

export interface PointRedeemSliderProps {
  maxPoints: number;
  orderTotal: number;
  onRedeem: (points: number) => void;
}

export function PointRedeemSlider({
  maxPoints,
  orderTotal,
  onRedeem,
}: PointRedeemSliderProps) {
  const [isEnabled, setIsEnabled] = useState<boolean>(false);
  const [points, setPoints] = useState<number>(0);

  const usableMaxPoints = Math.max(0, Math.min(maxPoints, Math.floor(orderTotal)));

  useEffect(() => {
    if (isEnabled && points > usableMaxPoints) {
      const timer = setTimeout(() => {
        setPoints(usableMaxPoints);
        onRedeem(usableMaxPoints);
      }, 0);
      return () => clearTimeout(timer);
    }
    if (usableMaxPoints <= 0 && isEnabled) {
      const timer = setTimeout(() => {
        setIsEnabled(false);
        setPoints(0);
        onRedeem(0);
      }, 0);
      return () => clearTimeout(timer);
    }
  }, [usableMaxPoints, isEnabled, points, onRedeem]);

  const handleToggle = () => {
    const nextState = !isEnabled;
    setIsEnabled(nextState);

    if (nextState) {
      const initialPoints = usableMaxPoints;
      setPoints(initialPoints);
      onRedeem(initialPoints);
    } else {
      setPoints(0);
      onRedeem(0);
    }
  };

  const handleSliderChange = (newVal: number) => {
    const clamped = Math.max(0, Math.min(newVal, usableMaxPoints));
    setPoints(clamped);
    onRedeem(clamped);
  };

  const handlePreset = (fraction: number) => {
    const calculated = Math.round(usableMaxPoints * fraction);
    handleSliderChange(calculated);
  };

  if (maxPoints <= 0) {
    return null;
  }

  return (
    <div className="w-full bg-[#FFFFFF] border border-[#EAE6DF] rounded-2xl p-5 sm:p-6 space-y-5 shadow-sm">
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-[#84EA53]/20 border border-[#84EA53]/40 flex items-center justify-center text-[#1D1F20]">
            <Coins size={18} />
          </div>
          <div>
            <h4 className="text-base font-bold text-[#1D1F20]">Dùng điểm tích lũy</h4>
            <p className="text-xs text-[#74736e]">
              Bạn có{" "}
              <strong className="text-[#1D1F20]">
                {maxPoints.toLocaleString("vi-VN")} điểm
              </strong>{" "}
              (1 điểm = 1₫)
            </p>
          </div>
        </div>

        <button
          type="button"
          role="switch"
          aria-checked={isEnabled}
          onClick={handleToggle}
          className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
            isEnabled ? "bg-[#84EA53]" : "bg-[#EAE6DF]"
          }`}
        >
          <span
            className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow-md ring-0 transition duration-200 ease-in-out ${
              isEnabled ? "translate-x-5" : "translate-x-0"
            }`}
          />
        </button>
      </div>

      {isEnabled && (
        <div className="space-y-4 pt-2 border-t border-[#EAE6DF] animate-fadeIn">
          <div className="flex items-center justify-between p-3.5 rounded-xl bg-[#FBF9F4] border border-[#EAE6DF]">
            <span className="text-xs font-semibold text-[#74736e]">
              Số tiền giảm giá:
            </span>
            <div className="flex items-center gap-1.5 text-sm font-bold text-[#16a34a]">
              <Sparkles size={14} className="text-[#84EA53]" />
              <span>-{points.toLocaleString("vi-VN")}₫</span>
            </div>
          </div>

          <div className="space-y-2">
            <div className="flex justify-between text-xs text-[#74736e] font-medium">
              <span>0 điểm</span>
              <span className="text-[#1D1F20] font-bold">
                {points.toLocaleString("vi-VN")} / {usableMaxPoints.toLocaleString("vi-VN")} điểm
              </span>
            </div>

            <input
              type="range"
              min={0}
              max={usableMaxPoints}
              step={100}
              value={points}
              onChange={(e) => handleSliderChange(Number(e.target.value))}
              className="w-full h-2 bg-[#EAE6DF] rounded-lg appearance-none cursor-pointer accent-[#84EA53] focus:outline-none"
            />
          </div>

          <div className="flex items-center gap-2 pt-1">
            {[
              { label: "25%", frac: 0.25 },
              { label: "50%", frac: 0.5 },
              { label: "75%", frac: 0.75 },
              { label: "Tối đa", frac: 1 },
            ].map(({ label, frac }) => (
              <button
                key={label}
                type="button"
                onClick={() => handlePreset(frac)}
                className="flex-1 py-1.5 text-xs font-semibold rounded-lg bg-[#FBF9F4] border border-[#EAE6DF] text-[#1D1F20] hover:border-[#84EA53] hover:bg-[#84EA53]/10 transition cursor-pointer"
              >
                {label}
              </button>
            ))}

            <button
              type="button"
              onClick={() => handleSliderChange(0)}
              className="px-3 py-1.5 text-xs font-semibold rounded-lg bg-transparent text-[#74736e] hover:text-[#dc2626] transition cursor-pointer"
            >
              Đặt lại
            </button>
          </div>
        </div>
      )}
    </div>
  );
}