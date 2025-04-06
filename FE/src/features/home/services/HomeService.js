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

  // 챌린지 성공시 분배
  postChallengeSettle: async (challengeId) => {
    try {
      const response = await Api.post(`api/challenge/${challengeId}/settle`);
      console.log(response);
      return response;
    } catch (error) {
      console.error(error);
    }
  },
};
