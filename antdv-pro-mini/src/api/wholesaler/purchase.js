/**
 * 列表
 * @param {*} data
 * @returns
 */
export const publishedPage = (data) => {
  return useGet("/product/published/page", data);
};
