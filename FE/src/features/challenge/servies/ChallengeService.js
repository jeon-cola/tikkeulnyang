import Api from "@/services/Api";

export const challengeService = {
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
};
