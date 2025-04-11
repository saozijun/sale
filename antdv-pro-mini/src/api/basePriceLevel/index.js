
/**
 * 列表
 * @param {*} data 
 * @returns 
 */
export const  list = (data) => {
  return useGet('/basePriceLevel/page', data)
}

/**
 * 新增编辑
 * @param {*} data 
 * @returns 
 */
export const  save = (data) => {
  return usePost('/basePriceLevel/save', data)
}

/**
 * 删除
 * @param {*} data 
 * @returns 
 */
export const del = (data) => {
  return usePost('/basePriceLevel/delete', data)
}

/**
 * 字典项
 * @param {*} data 
 * @returns 
 */
export const levelList = (data) => {
  return useGet('/basePriceLevel/basePriceLevelList', data)
}


