/**
 * 列表
 * @param {*} data
 * @returns
 */
export const list = (data) => {
  return useGet("/productInventory/page", data);
};

/**
 * 列表
 * @param {*} data
 * @returns
 */
export const listAll = (data) => {
  return useGet("/productInventory/product/full", data);
};

/**
 * 新增编辑
 * @param {*} data
 * @returns
 */
export const save = (data) => {
  return usePost("/productInventory/save", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const del = (data) => {
  return usePost("/productInventory/delete", data);
};

/**
 * 修改库存数量
 * @param {*} data
 * @returns
 */
export const updateStock = (data) => {
    return usePost("/productInventory/updateStock", data);
};