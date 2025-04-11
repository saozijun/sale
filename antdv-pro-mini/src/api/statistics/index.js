
/**
 * 列表
 * @param {*} data 
 * @returns 
 */
export const statisticsPost = (data) => {
  return useGet('/user/statistics', data)
}
