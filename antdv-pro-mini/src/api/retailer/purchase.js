/**
 * 列表
 * @param {*} data
 * @returns
 */
export const publishedPage = (data) => {
  return useGet("/productP/published/page", data);
};
