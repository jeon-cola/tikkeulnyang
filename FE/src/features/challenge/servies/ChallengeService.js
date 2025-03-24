import Api from "@/services/Api";

export const ChallengeService = {
  getOfficial: async (page, size) => {
    try {
      const response = await Api.get(
        //`/challenge/official?page=${page}&size=${size}`
        `/api_challenges_official?page=${page}&size=${size}`
      );

      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  // 참여이력 전체조회
  getHistory: async (page, size) => {
    try {
      const response = await Api.get(
        //`/challenge/history?page=${page}&size=${size}`
        `/api_challenge_history?page=${page}&size=${size}`
      );
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  // 현재 참여중인 챌린지 상세조회
  getCurrChallenge: async (challengeId) => {
    try {
      const response = await Api.get(
        //`/api/challenge/current?challenge_id=${challengeId}`
        `/api_challenge_current?challenge_id=${challengeId}`
      );
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },
};
