import { Route, Routes } from "react-router-dom";
import BucketListMain from "../features/bucketlist/BucketListMain";

export default function BucketListRouter() {
  return (
    <Routes>
      <Route path="/*" element={<BucketListMain />} />
    </Routes>
  );
}
//
