export default function ChallengeDetailImg({ imageInfo }) {
  return (
    <>
      <div
        className="w-[372px] aspect-[372/286] overflow-hidden rounded-md flex-none order-0 flex-grow-0"
        style={{
          backgroundImage: `url(${imageInfo})`,
          backgroundSize: "cover",
        }}
      ></div>
    </>
  );
}
