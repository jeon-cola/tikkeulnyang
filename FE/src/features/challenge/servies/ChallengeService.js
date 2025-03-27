import Api from "@/services/Api";

export const ChallengeService = {
  // 공식 챌린지 조회
  getOfficial: async (page, size) => {
    try {
      const response = await Api.get(
        `api/challenge/official?page=${page}&size=${size}`
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
        `api/challenge/user?page=${page}&size=${size}`
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
  getPast: async () => {
    try {
      const response = await Api.get(`api/challenge/past`);
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  // 챌린지 상세조회
  getCurrChallenge: async (challengeId) => {
    try {
      const response = await Api.get(
        //`/challenge/current?challenge_id=${challengeId}`
        `api/challenge/${challengeId}/detail`
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
        `api/challenge`,
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
