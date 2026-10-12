package com.fashionshop.business.service.fulfillment;

import com.fashionshop.business.entity.OrderItem;
import com.fashionshop.business.entity.ReturnRequest;
import com.fashionshop.common.exception.AppException;
import com.fashionshop.common.exception.NotFoundException;
import com.fashionshop.dataaccess.dao.cartorder.OrderItemDAO;
import com.fashionshop.dataaccess.dao.fulfillment.ReturnRequestDAO;
import com.fashionshop.presentation.dto.fulfillment.CreateReturnDTO;
import com.fashionshop.presentation.dto.fulfillment.ReturnQueryDTO;
import com.fashionshop.presentation.dto.fulfillment.UpdateReturnDTO;

import java.util.List;

public class ReturnService {
    private final ReturnRequestDAO returnRepo = new ReturnRequestDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    public List<ReturnRequest> findAll(ReturnQueryDTO query) {
        return returnRepo.findAllWithFilter(query);
    }

    public ReturnRequest findById(int id) {
        return returnRepo.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("Yêu cầu trả hàng không tồn tại"));
    }

    public ReturnRequest create(CreateReturnDTO data) {
        OrderItem orderItem = orderItemDAO.findById(data.getOrderItemId())
                .orElseThrow(() -> new NotFoundException("Dòng đơn hàng không tồn tại"));

        if (data.getQuantity() > orderItem.getQuantity()) {
            throw new AppException("Số lượng trả không được vượt quá số lượng đã mua", 422);
        }

        return returnRepo.create(data);
    }

    public ReturnRequest update(int id, UpdateReturnDTO data) {
        ReturnRequest existing = findById(id);

        if (data.getQuantity() != null && data.getQuantity() > existing.getOrderItem().getQuantity()) {
            throw new AppException("Số lượng trả không được vượt quá số lượng đã mua", 422);
        }

        return returnRepo.update(id, data);
    }

    public void delete(int id) {
        ReturnRequest existing = findById(id);

        if (existing.getStatus() != ReturnRequest.ReturnStatus.PENDING) {
            throw new AppException("Chỉ có thể xóa yêu cầu trả hàng đang chờ xử lý", 422);
        }

        returnRepo.softDelete(id);
    }
}