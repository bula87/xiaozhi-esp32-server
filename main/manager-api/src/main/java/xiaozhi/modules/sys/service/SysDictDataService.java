package xiaozhi.modules.sys.service;

import java.util.List;
import java.util.Map;

import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.sys.dto.SysDictDataDTO;
import xiaozhi.modules.sys.entity.SysDictDataEntity;
import xiaozhi.modules.sys.vo.SysDictDataItem;
import xiaozhi.modules.sys.vo.SysDictDataVO;

/**
 * Data Dictionary
 */
public interface SysDictDataService extends BaseService<SysDictDataEntity> {

    /**
     * Paginate to query data dictionary information
     *
     * @param params Query parameters, including pagination information and query conditions
     * @return Returns the paginated query result of the data dictionary
     */
    PageData<SysDictDataVO> page(Map<String, Object> params);

    /**
     * Get data dictionary entity by ID
     *
     * @param id Unique identifier of the data dictionary entity
     * @return Returns detailed information of the data dictionary entity
     */
    SysDictDataVO get(Long id);

    /**
     * Save new data dictionary item
     *
     * @param dto Data transfer object for saving data dictionary items
     */
    void save(SysDictDataDTO dto);

    /**
     * Update data dictionary item
     *
     * @param dto Data transfer object for updating data dictionary items
     */
    void update(SysDictDataDTO dto);

    /**
     * Delete data dictionary item
     *
     * @param ids Array of IDs of the data dictionary items to be deleted
     */
    void delete(Long[] ids);

    /**
     * Delete corresponding dictionary data by dictionary type ID
     *
     * @param dictTypeId Dictionary type ID
     */
    void deleteByTypeId(Long dictTypeId);

    /**
     * Get dictionary data list by dictionary type
     *
     * @param dictType Dictionary type
     * @return Returns the dictionary data list
     */
    List<SysDictDataItem> getDictDataByType(String dictType);

}
 