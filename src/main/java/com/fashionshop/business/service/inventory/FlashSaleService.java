package com.fashionshop.business.service.inventory;

import com.fashionshop.business.entity.FlashSaleCampaign;
import com.fashionshop.business.entity.FlashSaleItem;
import com.fashionshop.business.entity.ProductVariant;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.common.response.PagedResponse;
import com.fashionshop.dataaccess.dao.inventory.FlashSaleCampaignDAO;
import com.fashionshop.presentation.dto.inventory.CreateFlashSaleDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleItemDTO;
import com.fashionshop.presentation.dto.inventory.FlashSaleQueryDTO;
import com.fashionshop.presentation.dto.inventory.UpdateFlashSaleDTO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FlashSaleService {
    private final FlashSaleCampaignDAO flashSaleDAO = new FlashSaleCampaignDAO();

    public PagedResponse<FlashSaleCampaign> list(FlashSaleQueryDTO query) {
        List<FlashSaleCampaign> data = flashSaleDAO.list(query);
        long total = flashSaleDAO.count(query);
        return new PagedResponse<>(data, total, query.getPage(), query.getLimit());
    }

    public List<FlashSaleCampaign> findActiveCampaigns() {
        return flashSaleDAO.findActiveCampaigns();
    }

    public FlashSaleCampaign findById(int campaignId) {
        return flashSaleDAO.findDetailById(campaignId)
                .orElseThrow(() -> new AppException("Chiến dịch Flash Sale không tồn tại hoặc đã bị xóa", 404));
    }

    private void validateUniqueVariantIds(List<FlashSaleItemDTO> items) {
        Set<Integer> seen = new HashSet<>();
        for (FlashSaleItemDTO item : items) {
            if (!seen.add(item.getVariantId())) {
                throw new AppException("Không được có sản phẩm trùng trong Flash Sale", 422);
            }
        }
    }

    private void validateVariants(List<FlashSaleItemDTO> items) {
        validateUniqueVariantIds(items);

        List<Integer> variantIds = items.stream().map(FlashSaleItemDTO::getVariantId).toList();
        List<ProductVariant> variants = flashSaleDAO.findVariantsForFlashSale(variantIds);

        Map<Integer, ProductVariant> variantMap = variants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        for (FlashSaleItemDTO item : items) {
            ProductVariant variant = variantMap.get(item.getVariantId());

            if (variant == null) {
                throw new NotFoundException("Biến thể SKU ID " + item.getVariantId() + " không tồn tại");
            }

            if (variant.getStockQuantity() < item.getPromoQuantity()) {
                throw new AppException("Tồn kho SKU " + variant.getSku() + " không đủ. Tồn kho hiện có: "
                        + variant.getStockQuantity() + ", cấu hình sale: " + item.getPromoQuantity(), 422);
            }

            if (item.getFlashSalePrice().compareTo(variant.getSellingPrice()) >= 0) {
                throw new AppException("Giá Flash Sale của SKU " + variant.getSku() + " phải thấp hơn giá bán hiện tại", 422);
            }
        }
    }

    public FlashSaleCampaign create(CreateFlashSaleDTO body) {
        if (!body.getStartTime().isBefore(body.getEndTime())) {
            throw new AppException("Thời gian kết thúc phải sau thời gian bắt đầu", 422);
        }

        validateVariants(body.getItems());
        return flashSaleDAO.create(body);
    }

    public FlashSaleCampaign update(int campaignId, UpdateFlashSaleDTO body) {
        FlashSaleCampaign campaign = findById(campaignId);

        LocalDateTime startTime = body.getStartTime() != null ? body.getStartTime() : campaign.getStartTime();
        LocalDateTime endTime = body.getEndTime() != null ? body.getEndTime() : campaign.getEndTime();

        if (!startTime.isBefore(endTime)) {
            throw new AppException("Thời gian kết thúc phải sau thời gian bắt đầu", 422);
        }

        if (body.getItems() != null) {
            validateVariants(body.getItems());

            Map<Integer, FlashSaleItem> existingItemMap = campaign.getItems().stream()
                    .collect(Collectors.toMap(
                            item -> item.getVariant().getId(),
                            item -> item,
                            (a, b) -> a
                    ));

            for (FlashSaleItemDTO item : body.getItems()) {
                FlashSaleItem existing = existingItemMap.get(item.getVariantId());
                if (existing != null && item.getPromoQuantity() < existing.getSoldQuantity()) {
                    throw new AppException("Không thể giảm số lượng khuyến mãi của variant "
                            + item.getVariantId() + " xuống " + item.getPromoQuantity()
                            + " vì đã bán " + existing.getSoldQuantity(), 422);
                }
            }
        }

        return flashSaleDAO.update(campaignId, body);
    }

    public void delete(int campaignId) {
        FlashSaleCampaign campaign = findById(campaignId);
        campaign.setDeletedAt(LocalDateTime.now());
        flashSaleDAO.update(campaign);
    }
}