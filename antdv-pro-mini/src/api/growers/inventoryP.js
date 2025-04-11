/**
 * 列表
 * @param {*} data
 * @returns
 */
export const list = (data) => {
  return useGet("/productInventoryP/page", data);
};

/**
 * 新增编辑
 * @param {*} data
 * @returns
 */
export const save = (data) => {
  return usePost("/productInventoryP/save", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const del = (data) => {
  return usePost("/productInventoryP/delete", data);
};

/**
 * 修改库存数量
 * @param {*} data
 * @returns
 */
export const updateStock = (data) => {
    return usePost("/productInventoryP/updateStock", data);
};

/**
 * 列表
 * @param {*} data
 * @returns
 */
export const listAll = (data) => {
  return useGet("/productInventoryP/product/full", data);
};