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

  // // 챌린지 성공시 분배
  // postChallengeSettle: async (challengeId) => {
  //   try {
  //     const response = await Api.post(`api/challenge/${challengeId}/settle`);
  //     console.log(response);
  //     return response;
  //   } catch (error) {
  //     console.error(error);
  //   }
  // },

  // 성공한 챌린지가 있는지 알람
  getSettlementAlert: async () => {
    try {
      const response = await Api.get(`api/challenge/settlement-alert`);
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  // 챌린지 알람 수정(결과 반영)
  patchSettlementAlert: async () => {
    try {
      const response = await Api.patch(`api/challenge/settlement-alert`);
      console.log("챌린지 알람 수정", response);
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },
};
