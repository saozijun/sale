/**
 * 列表
 * @param {*} data
 * @returns
 */
export const list = (data) => {
  return useGet("/order/page", data);
};

/**
 * 新增编辑
 * @param {*} data
 * @returns
 */
export const save = (data) => {
  return usePost("/order/save", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const del = (data) => {
  return usePost("/order/delete", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const changeStatus = (orderId, newStatus) => {
    return usePost(`/productInventory/order/status/change?orderId=${orderId}&newStatus=${newStatus}`);
};