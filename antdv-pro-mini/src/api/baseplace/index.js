
/**
 * 列表
 * @param {*} data 
 * @returns 
 */
export const  list = (data) => {
  return useGet('/basePlace/page', data)
}

/**
 * 新增编辑
 * @param {*} data 
 * @returns 
 */
export const  save = (data) => {
  return usePost('/basePlace/save', data)
}

/**
 * 删除
 * @param {*} data 
 * @returns 
 */
export const del = (data) => {
  return usePost('/basePlace/delete', data)
}

/**
 * 字典项
 * @param {*} data 
 * @returns 
 */
export const basePlaceList = (data) => {
  return useGet('/basePlace/basePlaceList', data)
}


