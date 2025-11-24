package io.sf.admin.api;

import io.sf.modules.merchant.entity.Merchant;
import io.sf.modules.merchant.repository.MerchantRepository;
import io.sf.utils.crud.CrudApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("merchant")
@Tag(name = "商户管理", description = "商户(Merchant)管理接口")
public class MerchantController extends CrudApiController<Merchant, Long, MerchantRepository> {
    protected MerchantController(MerchantRepository repository) {
        super(repository);
    }

    @Override
    protected Class<Merchant> entityClass() {
        return Merchant.class;
    }
}

