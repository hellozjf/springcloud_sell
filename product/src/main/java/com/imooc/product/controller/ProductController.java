package com.imooc.product.controller;

import com.imooc.product.dataobject.ProductCategory;
import com.imooc.product.dataobject.ProductInfo;
import com.imooc.product.service.CategoryService;
import com.imooc.product.service.ProductService;
import com.imooc.product.util.ResultVOUtils;
import com.imooc.product.util.TypeUtils;
import com.imooc.product.vo.ProductInfoVO;
import com.imooc.product.vo.ProductVO;
import com.imooc.product.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hellozjf
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 1. 查询所有在架的商品
     * 2. 查询类目type列表
     * 3. 查询类目
     * 4. 构造数据
     */
    @GetMapping("/list")
    public ResultVO<ProductVO> list() {

        // 1. 查询所有在架的商品
        List<ProductInfo> productInfoList = productService.findUpAll();

        // 2. 查询类目type列表
        List<Integer> categoryTypeList = productInfoList.stream()
                .map(e -> e.getCategoryType())
                .collect(Collectors.toList());

        // 3. 查询类目
        List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn(categoryTypeList);

        // 4. 构造数据
        List<ProductVO> productVOList = new ArrayList<>();
        for (ProductCategory productCategory : productCategoryList) {
            ProductVO productVO = TypeUtils.productCategory2ProductVO(productCategory);
            List<ProductInfoVO> productInfoVOList = new ArrayList<>();
            for (ProductInfo productInfo : productInfoList) {
                if (productInfo.getCategoryType().equals(productCategory.getCategoryType())) {
                    productInfoVOList.add(TypeUtils.productInfo2ProductInfoVO(productInfo));
                }
            }
            productVO.setProductInfoVOList(productInfoVOList);
            productVOList.add(productVO);
        }
        return ResultVOUtils.success(productVOList);
    }
}