export const ChallengeUtils = {
  // `2024-01-01`과 같은 문자열을 `01,01`로 바꾸어주는 메서드
  formatDate: (dateString) => {
    const parts = dateString.split("-");
    const month = parts[1];
    const day = parts[2];

    return `${month}.${day}`;
  },
};
