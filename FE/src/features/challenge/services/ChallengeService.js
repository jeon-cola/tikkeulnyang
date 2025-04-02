import Api from "@/services/Api";

export const ChallengeService = {
  // 공식 챌린지 조회
  getOfficial: async (page, size) => {
    try {
      const response = await Api.get(
        `api/challenge/official?page=${page}&size=${size}`
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
      const response = await Api.get(`api/challenge/${challengeId}/detail`);
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

      const response = await Api.post(`api/challenge`, challengeData);

      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  // 썸네일 업로드
  postChallengeThumbnail: async (challengeId, file) => {
    try {
      const formData = new FormData();
      formData.append("file", file);

      const response = await Api.post(
        `api/challenge/${challengeId}/thumbnail`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  // 참여중인 챌린지 리스트 조회
  getChallengeParticipated: async () => {
    try {
      const response = await Api.get(`api/challenge/participated`);
      console.log("challengeParticipated", response);
      console.log("참여중인 챌린지 조회");
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  // 챌린지 참여
  postChallengeJoin: async (challengeId) => {
    try {
      const response = await Api.post(`api/challenge/${challengeId}/join`);
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },

  // 추천 챌린지 조회
  getRecommend: async (page, size) => {
    try {
      const response = await Api.get(
        `api/challenge/recommend?page=${page}&size=${size}`
      );

      console.log("recommend", response);
      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  },
};
