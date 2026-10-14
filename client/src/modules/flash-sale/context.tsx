'use client';

import React, { createContext, useContext, useState, ReactNode } from 'react';
import { FlashSaleCampaign, FlashSaleItem } from '@/types/inventory-sale';

interface FlashSaleContextType {
  campaign: FlashSaleCampaign | null;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  selectedItem: FlashSaleItem | null;
  setSelectedItem: (item: FlashSaleItem | null) => void;
  filteredItems: FlashSaleItem[];
}

const FlashSaleContext = createContext<FlashSaleContextType | undefined>(undefined);

export function FlashSaleProvider({
  campaign,
  children,
}: {
  campaign: FlashSaleCampaign | null;
  children: ReactNode;
}) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedItem, setSelectedItem] = useState<FlashSaleItem | null>(null);

  const rawItems = campaign?.items || [];
  const filteredItems = rawItems.filter((item) => {
    if (!searchQuery.trim()) return true;
    const name = item.variant?.product?.defaultName?.toLowerCase() || '';
    const sku = item.variant?.sku?.toLowerCase() || '';
    const q = searchQuery.toLowerCase();
    return name.includes(q) || sku.includes(q);
  });

  return (
    <FlashSaleContext.Provider
      value={{
        campaign,
        searchQuery,
        setSearchQuery,
        selectedItem,
        setSelectedItem,
        filteredItems,
      }}
    >
      {children}
    </FlashSaleContext.Provider>
  );
}

export function useFlashSaleContext() {
  const context = useContext(FlashSaleContext);
  if (!context) {
    throw new Error('useFlashSaleContext phải được dùng bên trong FlashSaleProvider');
  }
  return context;
}
