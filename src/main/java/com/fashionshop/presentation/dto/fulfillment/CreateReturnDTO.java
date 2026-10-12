package com.fashionshop.presentation.dto.fulfillment;

<<<<<<< HEAD
import java.util.ArrayList;
=======
>>>>>>> feature/checkout
import java.util.List;

public class CreateReturnDTO {
    private Integer orderItemId;
    private Integer quantity;
    private String reason;
    private String description;
<<<<<<< HEAD
    private List<ReturnMediaDTO> medias = new ArrayList<>();
=======
    private List<ReturnMediaDTO> medias;
>>>>>>> feature/checkout

    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ReturnMediaDTO> getMedias() { return medias; }
    public void setMedias(List<ReturnMediaDTO> medias) { this.medias = medias; }
<<<<<<< HEAD
}
=======
}

>>>>>>> feature/checkout
