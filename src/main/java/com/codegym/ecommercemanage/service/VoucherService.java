package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.request.VoucherRequest;
import com.codegym.ecommercemanage.dto.response.VoucherResponse;
import com.codegym.ecommercemanage.model.Category;
import com.codegym.ecommercemanage.model.Voucher;
import com.codegym.ecommercemanage.repository.CategoryRepository;
import com.codegym.ecommercemanage.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // ================= CREATE =================
    public VoucherResponse createVoucher(VoucherRequest request) {

        if (voucherRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Voucher code đã tồn tại");
        }

        Voucher voucher = new Voucher();
        mapRequestToVoucher(voucher, request);

        return mapToResponse(voucherRepository.save(voucher));
    }

    // ================= UPDATE =================
    public VoucherResponse updateVoucher(Long id, VoucherRequest request) {

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));

        mapRequestToVoucher(voucher, request);

        return mapToResponse(voucherRepository.save(voucher));
    }

    // ================= GET ALL =================
    public List<VoucherResponse> getAllVouchers() {
        return voucherRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= GET ONE =================
    public VoucherResponse getVoucherById(Long id) {
        return voucherRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));
    }

    // ================= ENABLE / DISABLE =================
    public void toggleVoucher(Long id, boolean active) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher"));

        voucher.setActive(active);
        voucherRepository.save(voucher);
    }

    // ================= MAP =================
    private void mapRequestToVoucher(Voucher voucher, VoucherRequest request) {

        voucher.setCode(request.getCode());
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setQuantity(request.getQuantity());
        voucher.setActive(request.getActive());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());

        Set<Category> categories = categoryRepository.findAllById(request.getCategoryIds())
                .stream().collect(Collectors.toSet());

        voucher.setCategories(categories);
    }

    private VoucherResponse mapToResponse(Voucher voucher) {

        VoucherResponse res = new VoucherResponse();
        res.setId(voucher.getId());
        res.setCode(voucher.getCode());
        res.setDiscountPercent(voucher.getDiscountPercent());
        res.setMaxDiscount(voucher.getMaxDiscount());
        res.setMinOrderValue(voucher.getMinOrderValue());
        res.setQuantity(voucher.getQuantity());
        res.setActive(voucher.getActive());
        res.setStartDate(voucher.getStartDate());
        res.setEndDate(voucher.getEndDate());

        res.setCategories(
                voucher.getCategories()
                        .stream()
                        .map(Category::getCategoryName)
                        .collect(Collectors.toSet())
        );

        return res;
    }
}
