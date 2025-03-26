import Api from "@/services/Api";

export const ChallengeService = {
  // 공식 챌린지 조회
  getOfficial: async (page, size) => {
    try {
      const response = await Api.get(
        `/challenge/official?page=${page}&size=${size}`
        //`/api_challenges_official?page=${page}&size=${size}`
      );

      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  // 유저 챌린지 조회
  getUser: async (page, size) => {
    try {
      const response = await Api.get(
        `/challenge/user?page=${page}&size=${size}`
        //`/challenge/user?page=0&size=4`
        //`/api_challenge_user?page=${page}&size=${size}`
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
        `/challenge/history?page=${page}&size=${size}`
        //`/api_challenge_history?page=${page}&size=${size}`
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
        //`/challenge/current?challenge_id=${challengeId}`
        `/challenge/${challengeId}/detail`
        //`/api_challenge_current?challenge_id=${challengeId}`
      );
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  //TODO: 참여전 챌린지 상세조회

  // 챌린지 생성
  postChallengeCreate: async (challengeData) => {
    try {
      console.log("Sending challenge data:", challengeData);

      const response = await Api.post(
        `/challenge`,
        //`/api_challenge`,
        challengeData
      );

      console.log("challengeData", challengeData);
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },
};
