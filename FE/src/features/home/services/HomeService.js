import Api from "@/services/Api";

export const HomeService = {
  // 메인페이지 조회
  getMain: async () => {
    try {
      const response = await Api.get(`api/main`);
      console.log(response);

      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },
};
