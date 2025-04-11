/**
 * 列表
 * @param {*} data
 * @returns
 */
export const list = (data) => {
  return useGet("/product/page", data);
};

/**
 * 新增编辑
 * @param {*} data
 * @returns
 */
export const save = (data) => {
  return usePost("/product/save", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const del = (data) => {
  return usePost("/product/delete", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const publish = (id, status) => {
  return usePost(`/product/publish/${id}/${status}`);
};

