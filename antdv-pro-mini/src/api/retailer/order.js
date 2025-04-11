/**
 * 列表
 * @param {*} data
 * @returns
 */
export const list = (data) => {
  return useGet("/orderP/page", data);
};

/**
 * 新增编辑
 * @param {*} data
 * @returns
 */
export const save = (data) => {
  return usePost("/orderP/save", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const del = (data) => {
  return usePost("/orderP/delete", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const changeStatus = (orderId, newStatus) => {
    return usePost(`/productInventoryP/order/status/change?orderId=${orderId}&newStatus=${newStatus}`);
};