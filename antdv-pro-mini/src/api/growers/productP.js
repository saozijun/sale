/**
 * 列表
 * @param {*} data
 * @returns
 */
export const list = (data) => {
  return useGet("/productP/page", data);
};

/**
 * 新增编辑
 * @param {*} data
 * @returns
 */
export const save = (data) => {
  return usePost("/productP/save", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const del = (data) => {
  return usePost("/productP/delete", data);
};

/**
 * 删除
 * @param {*} data
 * @returns
 */
export const publish = (id, status) => {
  return usePost(`/productP/publish/${id}/${status}`);
};