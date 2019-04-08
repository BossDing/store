package com.d2c.store.modules.product.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.store.common.api.Asserts;
import com.d2c.store.common.api.PageModel;
import com.d2c.store.common.api.Response;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.common.api.base.BaseCtrl;
import com.d2c.store.common.utils.QueryUtil;
import com.d2c.store.modules.product.model.ProductDO;
import com.d2c.store.modules.product.model.ProductSkuDO;
import com.d2c.store.modules.product.query.ProductQuery;
import com.d2c.store.modules.product.query.ProductSkuQuery;
import com.d2c.store.modules.product.service.ProductService;
import com.d2c.store.modules.product.service.ProductSkuService;
import com.d2c.store.modules.security.model.UserDO;
import com.d2c.store.modules.security.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author BaiCai
 */
@Api(description = "商品管理")
@RestController
@RequestMapping("/back/product")
public class ProductController extends BaseCtrl<ProductDO, ProductQuery> {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSkuService productSkuService;

    @ApiOperation(value = "P2P查询数据")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<Page<ProductDO>> list(PageModel page, ProductQuery query) {
        UserDO user = userService.findByUsername(loginUserHolder.getUsername());
        Page<ProductDO> pager = (Page<ProductDO>) productService.findByQuery(user.getP2pId(), query, page);
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "新增数据")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<ProductDO> insert(@RequestBody ProductDO entity) {
        Asserts.notNull(ResultCode.REQUEST_PARAM_NULL, entity);
        productService.doCreate(entity);
        return Response.restResult(entity, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "通过ID获取数据")
    @RequestMapping(value = "/select/{id}", method = RequestMethod.GET)
    public R<ProductDO> select(@PathVariable Long id) {
        ProductDO entity = service.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, entity);
        ProductSkuQuery query = new ProductSkuQuery();
        query.setProductId(entity.getId());
        List<ProductSkuDO> skuList = productSkuService.list(QueryUtil.buildWrapper(query));
        entity.setSkuList(skuList);
        return Response.restResult(entity, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "通过ID更新数据")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<ProductDO> update(@RequestBody ProductDO entity) {
        Asserts.notNull(ResultCode.REQUEST_PARAM_NULL, entity);
        productService.doUpdate(entity);
        return Response.restResult(service.getById(entity.getId()), ResultCode.SUCCESS);
    }

    @ApiOperation(value = "更改状态")
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public R status(Long id, Integer status) {
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, id, status);
        ProductDO product = productService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, product);
        ProductDO entity = new ProductDO();
        entity.setId(id);
        entity.setStatus(status);
        productService.updateById(entity);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
