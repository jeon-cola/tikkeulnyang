import { Route, Routes } from "react-router-dom";
import BucketListInformation from "./components/BucketListInformation";
import BucketListStep1 from "./components/BucketListStep1";
import BucketListStep2 from "./components/BucketListStep2";

export default function BucketListMain() {
    return (
        <Routes>
            <Route path="/" element={<BucketListInformation/>}/>
            <Route path="step1" element={<BucketListStep1/>}/>
            <Route path="step2" element={<BucketListStep2/>}/>
        </Routes>
    )
}