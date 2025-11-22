package io.sf.utils.crud;

import io.sf.utils.response.JsonResult;
import io.swagger.v3.oas.annotations.Operation;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

public abstract class CrudApiController<E, ID, R extends JpaRepository<E, ID> & JpaSpecificationExecutor<E>> {

    protected final R repository;

    protected CrudApiController(R repository) {
        this.repository = repository;
    }

    /** 获取实体类的 Class，用于动态 DSL */
    protected abstract Class<E> entityClass();

    /** 列表 + 动态查询 */
    @GetMapping
    @Operation(summary = "列表 + 动态查询",tags = {"CRUD"})
    public JsonResult<Page<E>> list(
            @NonNull Pageable pageable,
            @RequestParam Map<String, String> params
    ) {
        Specification<E> spec = new EnhancedDynamicDSL<>(entityClass(), params)
                .build();
        Page<E> result = repository.findAll(spec, pageable);
        return new JsonResult<Page<E>>(HttpStatus.OK,result);
    }

    /** 查询单条 */
    @GetMapping("/{id}")
    @Operation(summary = "查询单条",tags = {"CRUD"})
    public JsonResult<E> getById(@NonNull @PathVariable ID id) throws Exception {
        Optional<E> result = repository.findById(id);
        E responseEntity = result.orElseThrow(()->{     
                    return new NotFoundException("没有数据");
                });
        return new JsonResult<E>(HttpStatus.OK,responseEntity);
    }

    /** 创建 */
    @PostMapping
    @Operation(summary = "创建",tags = {"CRUD"})
    public JsonResult<E> create(@NonNull @RequestBody E body) {
        E saved = repository.save(body);
        return new JsonResult<E>(HttpStatus.OK,saved);
    }

    /** 更新（PUT 全量覆盖） */
    @PutMapping("/{id}")
    @Operation(summary = "更新（PUT 全量覆盖）",tags = {"CRUD"})
    public JsonResult<E> update(
            @NonNull @PathVariable ID id,
            @NonNull @RequestBody E body
    ) {
        if (!repository.existsById(id)) {
            return new JsonResult<E>(HttpStatus.NOT_FOUND,null);
        }
        E saved = repository.save(body);
        return new JsonResult<E>(HttpStatus.OK,saved);
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除",tags = {"CRUD"})
    public JsonResult<Void> delete(@NonNull @PathVariable ID id) {
        if (!repository.existsById(id)) {
            return new JsonResult<Void>(HttpStatus.NOT_FOUND,null);
        }
        repository.deleteById(id);
        return new JsonResult<Void>(HttpStatus.NO_CONTENT,null);
    }
}
